/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.material.Button
 *  org.bukkit.material.Lever
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.airbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.airbending.AirBurst;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.object.HorizontalVelocityTracker;
import com.projectkorra.projectkorra.util.Flight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class AirBlast
implements ConfigLoadable {
    public static ConcurrentHashMap<Integer, AirBlast> instances = new ConcurrentHashMap();
    private static ConcurrentHashMap<Player, Location> origins = new ConcurrentHashMap();
    public static double speed = config.get().getDouble("Abilities.Air.AirBlast.Speed");
    public static double defaultrange = config.get().getDouble("Abilities.Air.AirBlast.Range");
    public static double affectingradius = config.get().getDouble("Abilities.Air.AirBlast.Radius");
    public static double defaultpushfactor = config.get().getDouble("Abilities.Air.AirBlast.Push.Entities");
    public static double otherpushfactor = config.get().getDouble("Abilities.Air.AirBlast.Push.Self");
    public static boolean flickLevers = config.get().getBoolean("Abilities.Air.AirBlast.CanFlickLevers");
    public static boolean openDoors = config.get().getBoolean("Abilities.Air.AirBlast.CanOpenDoors");
    public static boolean pressButtons = config.get().getBoolean("Abilities.Air.AirBlast.CanPressButtons");
    public static boolean coolLava = config.get().getBoolean("Abilities.Air.AirBlast.CanCoolLava");
    private static double originselectrange = 10.0;
    private static int idCounter = 0;
    private static final int maxticks = 10000;
    static double maxspeed = 1.0 / defaultpushfactor;
    public static byte full = 0;
    Location location;
    private Location origin;
    private Vector direction;
    private Player player;
    private double speedfactor;
    private double range = defaultrange;
    private double pushfactor = defaultpushfactor;
    private double damage = 0.0;
    private boolean otherorigin = false;
    private boolean showParticles = true;
    private int ticks = 0;
    private int id = 0;
    private ArrayList<Block> affectedlevers = new ArrayList();
    private ArrayList<Entity> affectedentities = new ArrayList();
    private AirBurst source = null;

    public AirBlast(Location location, Vector direction, Player player, double factorpush, AirBurst burst) {
        if (location.getBlock().isLiquid()) {
            return;
        }
        this.source = burst;
        this.player = player;
        this.origin = location.clone();
        this.direction = direction.clone();
        this.location = location.clone();
        this.pushfactor *= factorpush;
        instances.put(idCounter, this);
        this.id = idCounter;
        idCounter = (idCounter + 1) % Integer.MAX_VALUE;
    }

    public AirBlast(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("AirBlast")) {
            return;
        }
        if (player.getEyeLocation().getBlock().isLiquid()) {
            return;
        }
        this.player = player;
        if (origins.containsKey((Object)player)) {
            this.otherorigin = true;
            this.origin = origins.get((Object)player);
            origins.remove((Object)player);
            Entity entity = GeneralMethods.getTargetedEntity(player, this.range, new ArrayList<Entity>());
            this.direction = entity != null ? GeneralMethods.getDirection(this.origin, entity.getLocation()).normalize() : GeneralMethods.getDirection(this.origin, GeneralMethods.getTargetedLocation(player, this.range, new Integer[0])).normalize();
        } else {
            this.origin = player.getEyeLocation();
            this.direction = player.getEyeLocation().getDirection().normalize();
        }
        this.location = this.origin.clone();
        instances.put(idCounter, this);
        this.id = idCounter;
        idCounter = (idCounter + 1) % Integer.MAX_VALUE;
        bPlayer.addCooldown("AirBlast", GeneralMethods.getGlobalCooldown());
    }

    private static void playOriginEffect(Player player) {
        if (!origins.containsKey((Object)player)) {
            return;
        }
        Location origin = origins.get((Object)player);
        if (!origin.getWorld().equals((Object)player.getWorld())) {
            origins.remove((Object)player);
            return;
        }
        if (GeneralMethods.getBoundAbility(player) == null) {
            origins.remove((Object)player);
            return;
        }
        if (!GeneralMethods.getBoundAbility(player).equalsIgnoreCase("AirBlast") || !GeneralMethods.canBend(player.getName(), "AirBlast")) {
            origins.remove((Object)player);
            return;
        }
        if (origin.distance(player.getEyeLocation()) > originselectrange) {
            origins.remove((Object)player);
            return;
        }
        AirMethods.playAirbendingParticles(origin, 4);
    }

    public static void progressAll() {
        for (AirBlast blast : instances.values()) {
            blast.progress();
        }
        for (Player player : origins.keySet()) {
            AirBlast.playOriginEffect(player);
        }
    }

    public static void setOrigin(Player player) {
        Location location = GeneralMethods.getTargetedLocation(player, originselectrange, GeneralMethods.nonOpaque);
        if (location.getBlock().isLiquid() || GeneralMethods.isSolid(location.getBlock())) {
            return;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "AirBlast", location)) {
            return;
        }
        if (origins.containsKey((Object)player)) {
            origins.replace(player, location);
        } else {
            origins.put(player, location);
        }
    }

    private void advanceLocation() {
        if (this.showParticles) {
            AirMethods.playAirbendingParticles(this.location, 6, 0.275f, 0.275f, 0.275f);
        }
        if (GeneralMethods.rand.nextInt(4) == 0) {
            AirMethods.playAirbendingSound(this.location);
        }
        this.location = this.location.add(this.direction.clone().multiply(this.speedfactor));
    }

    private void affect(Entity entity) {
        boolean isUser;
        boolean bl = isUser = entity.getUniqueId() == this.player.getUniqueId();
        if (!isUser || this.otherorigin) {
            Vector push;
            double comp;
            this.pushfactor = otherpushfactor;
            Vector velocity = entity.getVelocity();
            double max = maxspeed;
            double factor = this.pushfactor;
            if (AvatarState.isAvatarState(this.player)) {
                max = AvatarState.getValue(maxspeed);
                factor = AvatarState.getValue(factor);
            }
            if (Math.abs((push = this.direction.clone()).getY()) > max && !isUser) {
                if (push.getY() < 0.0) {
                    push.setY(- max);
                } else {
                    push.setY(max);
                }
            }
            factor *= 1.0 - this.location.distance(this.origin) / (2.0 * this.range);
            if (isUser && GeneralMethods.isSolid(this.player.getLocation().add(0.0, -0.5, 0.0).getBlock())) {
                factor *= 0.5;
            }
            if ((comp = velocity.dot(push.clone().normalize())) > factor) {
                velocity.multiply(0.5);
                velocity.add(push.clone().normalize().multiply(velocity.clone().dot(push.clone().normalize())));
            } else if (comp + factor * 0.5 > factor) {
                velocity.add(push.clone().multiply(factor - comp));
            } else {
                velocity.add(push.clone().multiply(factor * 0.5));
            }
            if (entity instanceof Player && Commands.invincible.contains(((Player)entity).getName())) {
                return;
            }
            if (Double.isNaN(velocity.length())) {
                return;
            }
            GeneralMethods.setVelocity(entity, velocity);
            if (this.source != null) {
                new com.projectkorra.projectkorra.object.HorizontalVelocityTracker(entity, this.player, 200, "AirBurst", Element.Air, null);
            } else {
                new com.projectkorra.projectkorra.object.HorizontalVelocityTracker(entity, this.player, 200, "AirBlast", Element.Air, null);
            }
            entity.setFallDistance(0.0f);
            if (!isUser && entity instanceof Player) {
                new com.projectkorra.projectkorra.util.Flight((Player)entity, this.player);
            }
            if (entity.getFireTicks() > 0) {
                entity.getWorld().playEffect(entity.getLocation(), Effect.EXTINGUISH, 0);
            }
            entity.setFireTicks(0);
            AirMethods.breakBreathbendingHold(entity);
            if (this.damage > 0.0 && entity instanceof LivingEntity && !entity.equals((Object)this.player) && !this.affectedentities.contains((Object)entity)) {
                GeneralMethods.damageEntity(this.player, entity, this.damage, "AirBlast");
                this.affectedentities.add(entity);
            }
        }
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

    public boolean getShowParticles() {
        return this.showParticles;
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirBlast", this.location)) {
            this.remove();
            return false;
        }
        this.speedfactor = speed * ((double)ProjectKorra.time_step / 1000.0);
        ++this.ticks;
        if (this.ticks > 10000) {
            this.remove();
            return false;
        }
        Block block = this.location.getBlock();
        for (Block testblock : GeneralMethods.getBlocksAroundPoint(this.location, affectingradius)) {
            Block btBlock;
            BlockState initialSupportState;
            BlockState supportState;
            Button button;
            Block supportBlock;
            if (testblock.getType() == Material.FIRE) {
                testblock.setType(Material.AIR);
                testblock.getWorld().playEffect(testblock.getLocation(), Effect.EXTINGUISH, 0);
            }
            if (GeneralMethods.isRegionProtectedFromBuild(this.getPlayer(), "AirBlast", block.getLocation())) continue;
            Material[] doorTypes = new Material[]{Material.WOODEN_DOOR, Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR};
            if (Arrays.asList(doorTypes).contains((Object)block.getType()) && openDoors) {
                if (block.getData() >= 8) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                if (block.getData() < 4) {
                    block.setData((byte)(block.getData() + 4));
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 10.0f, 1.0f);
                } else {
                    block.setData((byte)(block.getData() - 4));
                    block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 10.0f, 1.0f);
                }
            }
            if (block.getType() == Material.LEVER && !this.affectedlevers.contains((Object)block) && flickLevers) {
            	Lever lever = new Lever(Material.LEVER, block.getData());
				lever.setPowered(!lever.isPowered());
				block.setData(lever.getData());
                
                supportBlock = block.getRelative(lever.getAttachedFace());
                if (supportBlock != null && supportBlock.getType() != Material.AIR) {
                    initialSupportState = supportBlock.getState();
                    supportState = supportBlock.getState();
                    supportState.setType(Material.AIR);
                    supportState.update(true, false);
                    initialSupportState.update(true);
                }
                this.affectedlevers.add(block);
                continue;
            }
            if (block.getType() == Material.STONE_BUTTON && !this.affectedlevers.contains((Object)block) && pressButtons) {
            	button = new Button(Material.STONE_BUTTON, block.getData());
				button.setPowered(!button.isPowered());
				block.setData(button.getData());

                supportBlock = block.getRelative(button.getAttachedFace());
                if (supportBlock != null && supportBlock.getType() != Material.AIR) {
                    initialSupportState = supportBlock.getState();
                    supportState = supportBlock.getState();
                    supportState.setType(Material.AIR);
                    supportState.update(true, false);
                    initialSupportState.update(true);
                }
                btBlock = block;
                new BukkitRunnable(){

                    public void run() {
                        button.setPowered(!button.isPowered());
                        btBlock.setData(button.getData());
                        Block supportBlock = btBlock.getRelative(button.getAttachedFace());
                        if (supportBlock != null && supportBlock.getType() != Material.AIR) {
                            BlockState initialSupportState = supportBlock.getState();
                            BlockState supportState = supportBlock.getState();
                            supportState.setType(Material.AIR);
                            supportState.update(true, false);
                            initialSupportState.update(true);
                        }
                    }
                }.runTaskLater((Plugin)ProjectKorra.plugin, 10);
                this.affectedlevers.add(block);
                continue;
            }
            if (block.getType() != Material.WOOD_BUTTON || this.affectedlevers.contains((Object)block) || !pressButtons) continue;
            button = new Button(Material.WOOD_BUTTON, block.getData());
			button.setPowered(!button.isPowered());
			block.setData(button.getData());            supportBlock = block.getRelative(button.getAttachedFace());
            if (supportBlock != null && supportBlock.getType() != Material.AIR) {
                initialSupportState = supportBlock.getState();
                supportState = supportBlock.getState();
                supportState.setType(Material.AIR);
                supportState.update(true, false);
                initialSupportState.update(true);
            }
            btBlock = block;
            new BukkitRunnable(){

                public void run() {
                    button.setPowered(!button.isPowered());
                    btBlock.setData(button.getData());
                    Block supportBlock = btBlock.getRelative(button.getAttachedFace());
                    if (supportBlock != null && supportBlock.getType() != Material.AIR) {
                        BlockState initialSupportState = supportBlock.getState();
                        BlockState supportState = supportBlock.getState();
                        supportState.setType(Material.AIR);
                        supportState.update(true, false);
                        initialSupportState.update(true);
                    }
                }
            }.runTaskLater((Plugin)ProjectKorra.plugin, 15);
            this.affectedlevers.add(block);
        }
        if ((GeneralMethods.isSolid(block) || block.isLiquid()) && !this.affectedlevers.contains((Object)block) && coolLava) {
            if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
                if (block.getData() == full) {
                    block.setType(Material.OBSIDIAN);
                } else {
                    block.setType(Material.COBBLESTONE);
                }
            }
            this.remove();
            return false;
        }
        double dist = this.location.distance(this.origin);
        if (Double.isNaN(dist) || dist > this.range) {
            this.remove();
            return false;
        }
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, affectingradius)) {
            this.affect(entity);
        }
        this.advanceLocation();
        return true;
    }

    public static boolean removeAirBlastsAroundPoint(Location location, double radius) {
        boolean removed = false;
        for (AirBlast airBlast : instances.values()) {
            Location airBlastlocation = airBlast.location;
            if (location.getWorld() != airBlastlocation.getWorld()) continue;
            if (location.distance(airBlastlocation) <= radius) {
                airBlast.remove();
            }
            removed = true;
        }
        return removed;
    }

    public void remove() {
        instances.remove(this.id);
    }

    public static void removeAll() {
        for (AirBlast ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        speed = config.get().getDouble("Abilities.Air.AirBlast.Speed");
        defaultrange = config.get().getDouble("Abilities.Air.AirBlast.Range");
        affectingradius = config.get().getDouble("Abilities.Air.AirBlast.Radius");
        defaultpushfactor = config.get().getDouble("Abilities.Air.AirBlast.Push");
        flickLevers = config.get().getBoolean("Abilities.Air.AirBlast.CanFlickLevers");
        openDoors = config.get().getBoolean("Abilities.Air.AirBlast.CanOpenDoors");
        pressButtons = config.get().getBoolean("Abilities.Air.AirBlast.CanPressButtons");
        coolLava = config.get().getBoolean("Abilities.Air.AirBlast.CanCoolLava");
        maxspeed = 1.0 / defaultpushfactor;
        this.range = defaultrange;
        this.pushfactor = defaultpushfactor;
    }

    public void setDamage(double dmg) {
        this.damage = dmg;
    }

    public void setPushfactor(double pushfactor) {
        this.pushfactor = pushfactor;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setShowParticles(boolean show) {
        this.showParticles = show;
    }

}

