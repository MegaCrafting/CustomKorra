/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.airbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirBlast;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.firebending.Combustion;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.firebending.Illumination;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class AirSwipe
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, AirSwipe> instances = new ConcurrentHashMap();
    private static int stepsize = 4;
    private static int ARC = config.get().getInt("Abilities.Air.AirSwipe.Arc");
    private static int defaultdamage = config.get().getInt("Abilities.Air.AirSwipe.Damage");
    private static double PUSH_FACTOR = config.get().getDouble("Abilities.Air.AirSwipe.Push");
    private static double AFFECTING_RADIUS = config.get().getDouble("Abilities.Air.AirSwipe.Radius");
    private static double RANGE = config.get().getDouble("Abilities.Air.AirSwipe.Range");
    private static double SPEED = config.get().getDouble("Abilities.Air.AirSwipe.Speed");
    private static double MAX_FACTOR = config.get().getDouble("Abilities.Air.AirSwipe.ChargeFactor");
    private static byte full = AirBlast.full;
    private static long MAX_CHARGE_TIME = config.get().getLong("Abilities.Air.AirSwipe.MaxChargeTime");
    private static Integer[] breakables = new Integer[]{6, 31, 32, 37, 38, 39, 40, 59, 81, 83, 106, 175};
    private final int MAX_AFFECTABLE_ENTITIES = 10;
    private double speedfactor;
    private Location origin;
    private Player player;
    private boolean charging = false;
    private long time;
    private double damage = defaultdamage;
    private double pushfactor = PUSH_FACTOR;
    private double speed = SPEED;
    private double range = RANGE;
    private double maxfactor = MAX_FACTOR;
    private double affectingradius = AFFECTING_RADIUS;
    private int arc = ARC;
    private long maxchargetime = MAX_CHARGE_TIME;
    private ConcurrentHashMap<Vector, Location> elements = new ConcurrentHashMap();
    private ArrayList<Entity> affectedentities = new ArrayList();

    public AirSwipe(Player player) {
        this(player, false);
    }

    public AirSwipe(Player player, boolean charging) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("AirSwipe")) {
            return;
        }
        if (player.getEyeLocation().getBlock().isLiquid()) {
            return;
        }
        this.player = player;
        this.charging = charging;
        this.origin = player.getEyeLocation();
        this.time = System.currentTimeMillis();
        instances.put(player, this);
        bPlayer.addCooldown("AirSwipe", ProjectKorra.plugin.getConfig().getLong("Abilities.Air.AirSwipe.Cooldown"));
        if (!charging) {
            this.launch();
        }
    }

    public static void charge(Player player) {
        new com.projectkorra.projectkorra.airbending.AirSwipe(player, true);
    }

    public static boolean removeSwipesAroundPoint(Location loc, double radius) {
        boolean removed = false;
        for (AirSwipe aswipe : instances.values()) {
            for (Vector vec : aswipe.elements.keySet()) {
                Location vectorLoc = aswipe.elements.get((Object)vec);
                if (vectorLoc == null || !vectorLoc.getWorld().equals((Object)loc.getWorld()) || vectorLoc.distance(loc) > radius) continue;
                aswipe.remove();
                removed = true;
            }
        }
        return removed;
    }

    private void advanceSwipe() {
        this.affectedentities.clear();
        for (Vector direction : this.elements.keySet()) {
            Location location = this.elements.get((Object)direction);
            if (direction == null || location == null) continue;
            location = location.clone().add(direction.clone().multiply(this.speedfactor));
            this.elements.replace(direction, location);
            if (location.distance(this.origin) > this.range || GeneralMethods.isRegionProtectedFromBuild(this.player, "AirSwipe", location)) {
                this.elements.remove((Object)direction);
                continue;
            }
            AirMethods.removeAirSpouts(location, this.player);
            WaterMethods.removeWaterSpouts(location, this.player);
            double radius = FireBlast.AFFECTING_RADIUS;
            Player source = this.player;
            if (EarthBlast.annihilateBlasts(location, radius, source) || WaterManipulation.annihilateBlasts(location, radius, source) || FireBlast.annihilateBlasts(location, radius, source) || Combustion.removeAroundPoint(location, radius)) {
                this.elements.remove((Object)direction);
                this.damage = 0.0;
                this.remove();
                continue;
            }
            Block block = location.getBlock();
            for (Block testblock : GeneralMethods.getBlocksAroundPoint(location, this.affectingradius)) {
                if (testblock.getType() == Material.FIRE) {
                    testblock.setType(Material.AIR);
                }
                if (!this.isBlockBreakable(testblock)) continue;
                GeneralMethods.breakBlock(testblock);
            }
            if (block.getType() != Material.AIR) {
                if (this.isBlockBreakable(block)) {
                    GeneralMethods.breakBlock(block);
                } else {
                    this.elements.remove((Object)direction);
                }
                if (block.getType() != Material.LAVA && block.getType() != Material.STATIONARY_LAVA) continue;
                if (block.getData() == full) {
                    block.setType(Material.OBSIDIAN);
                    continue;
                }
                block.setType(Material.COBBLESTONE);
                continue;
            }
            AirMethods.playAirbendingParticles(location, 3, 0.2f, 0.2f, 0.0f);
            if (GeneralMethods.rand.nextInt(4) == 0) {
                AirMethods.playAirbendingSound(location);
            }
            this.affectPeople(location, direction);
        }
        if (this.elements.isEmpty()) {
            this.remove();
        }
    }

    private void affectPeople(Location location, Vector direction) {
        WaterMethods.removeWaterSpouts(location, this.player);
        AirMethods.removeAirSpouts(location, this.player);
        List<Entity> entities = GeneralMethods.getEntitiesAroundPoint(location, this.affectingradius);
        final List<Entity> surroundingEntities = GeneralMethods.getEntitiesAroundPoint(location, 4.0);
        final Vector fDirection = direction;
        int i = 0;
        while (i < entities.size()) {
            final Entity entity = entities.get(i);
            new BukkitRunnable(){

                public void run() {
                    if (GeneralMethods.isRegionProtectedFromBuild(AirSwipe.this.player, "AirSwipe", entity.getLocation())) {
                        return;
                    }
                    if (entity.getEntityId() != AirSwipe.this.player.getEntityId()) {
                        if (entity instanceof Player && Commands.invincible.contains(((Player)entity).getName())) {
                            return;
                        }
                        if (surroundingEntities.size() < 10) {
                            if (AvatarState.isAvatarState(AirSwipe.this.player)) {
                                GeneralMethods.setVelocity(entity, fDirection.multiply(AvatarState.getValue(AirSwipe.this.pushfactor)));
                            } else {
                                GeneralMethods.setVelocity(entity, fDirection.multiply(AirSwipe.this.pushfactor));
                            }
                        }
                        if (entity instanceof LivingEntity && !AirSwipe.this.affectedentities.contains((Object)entity)) {
                            if (AirSwipe.this.damage != 0.0) {
                                GeneralMethods.damageEntity(AirSwipe.this.player, entity, AirSwipe.this.damage, "AirSwipe");
                            }
                            AirSwipe.this.affectedentities.add(entity);
                        }
                        if (entity instanceof Player) {
                            new com.projectkorra.projectkorra.util.Flight((Player)entity, AirSwipe.this.player);
                        }
                        AirMethods.breakBreathbendingHold(entity);
                        if (AirSwipe.this.elements.containsKey((Object)fDirection)) {
                            AirSwipe.this.elements.remove((Object)fDirection);
                        }
                    }
                }
            }.runTaskLater((Plugin)ProjectKorra.plugin, (long)(i / 10));
            ++i;
        }
    }

    public double getAffectingradius() {
        return this.affectingradius;
    }

    public int getArc() {
        return this.arc;
    }

    public double getDamage() {
        return this.damage;
    }

    public long getMaxchargetime() {
        return this.maxchargetime;
    }

    public double getMaxfactor() {
        return this.maxfactor;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getPushfactor() {
        return this.pushfactor;
    }

    public double getRange() {
        return this.range;
    }

    public double getSpeed() {
        return this.speed;
    }

    private boolean isBlockBreakable(Block block) {
        Integer id = block.getTypeId();
        if (Arrays.asList(breakables).contains(id) && !Illumination.blocks.containsKey((Object)block)) {
            return true;
        }
        return false;
    }

    private void launch() {
        this.origin = this.player.getEyeLocation();
        int i = - this.arc;
        while (i <= this.arc) {
            double angle = Math.toRadians(i);
            Vector direction = this.player.getEyeLocation().getDirection().clone();
            double x = direction.getX();
            double z = direction.getZ();
            double vx = x * Math.cos(angle) - z * Math.sin(angle);
            double vz = x * Math.sin(angle) + z * Math.cos(angle);
            direction.setX(vx);
            direction.setZ(vz);
            this.elements.put(direction, this.origin);
            i += stepsize;
        }
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        this.speedfactor = this.speed * ((double)ProjectKorra.time_step / 1000.0);
        if (!this.charging) {
            if (this.elements.isEmpty()) {
                this.remove();
                return false;
            }
            this.advanceSwipe();
        } else {
            if (GeneralMethods.getBoundAbility(this.player) == null) {
                this.remove();
                return false;
            }
            if (!GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("AirSwipe") || !GeneralMethods.canBend(this.player.getName(), "AirSwipe")) {
                this.remove();
                return false;
            }
            if (!this.player.isSneaking()) {
                double factor = 1.0;
                factor = System.currentTimeMillis() >= this.time + this.maxchargetime ? this.maxfactor : (AvatarState.isAvatarState(this.player) ? AvatarState.getValue(factor) : this.maxfactor * (double)(System.currentTimeMillis() - this.time) / (double)this.maxchargetime);
                this.charging = false;
                this.launch();
                if (factor < 1.0) {
                    factor = 1.0;
                }
                this.damage *= factor;
                this.pushfactor *= factor;
                return true;
            }
            if (System.currentTimeMillis() >= this.time + this.maxchargetime) {
                AirMethods.playAirbendingParticles(this.player.getEyeLocation(), 3);
            }
        }
        return true;
    }

    public static void progressAll() {
        for (AirSwipe ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (AirSwipe ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        ARC = config.get().getInt("Abilities.Air.AirSwipe.Arc");
        defaultdamage = config.get().getInt("Abilities.Air.AirSwipe.Damage");
        PUSH_FACTOR = config.get().getDouble("Abilities.Air.AirSwipe.Push");
        AFFECTING_RADIUS = config.get().getDouble("Abilities.Air.AirSwipe.Radius");
        RANGE = config.get().getDouble("Abilities.Air.AirSwipe.Range");
        SPEED = config.get().getDouble("Abilities.Air.AirSwipe.Speed");
        MAX_FACTOR = config.get().getDouble("Abilities.Air.AirSwipe.ChargeFactor");
        MAX_CHARGE_TIME = config.get().getLong("Abilities.Air.AirSwipe.MaxChargeTime");
    }

    public void setAffectingradius(double affectingradius) {
        this.affectingradius = affectingradius;
    }

    public void setArc(int arc) {
        this.arc = arc;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setMaxchargetime(long maxchargetime) {
        this.maxchargetime = maxchargetime;
    }

    public void setMaxfactor(double maxfactor) {
        this.maxfactor = maxfactor;
    }

    public void setPushfactor(double pushfactor) {
        this.pushfactor = pushfactor;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}

