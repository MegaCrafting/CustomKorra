/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Furnace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.firebending.Enflamed;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.FireStream;
import com.projectkorra.projectkorra.firebending.Fireball;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FireBlast
implements ConfigLoadable {
    public static ConcurrentHashMap<Integer, FireBlast> instances = new ConcurrentHashMap();
    private static double SPEED = config.get().getDouble("Abilities.Fire.FireBlast.Speed");
    private static double PUSH_FACTOR = config.get().getDouble("Abilities.Fire.FireBlast.Push");
    private static double RANGE = config.get().getDouble("Abilities.Fire.FireBlast.Range");
    private static int DAMAGE = config.get().getInt("Abilities.Fire.FireBlast.Damage");
    private static double fireticks = config.get().getDouble("Abilities.Fire.FireBlast.FireTicks");
    private static int idCounter = 0;
    static boolean dissipate = config.get().getBoolean("Abilities.Fire.FireBlast.Dissipate");
    public static double AFFECTING_RADIUS = 2.0;
    public static byte full = 0;
    private static boolean canPowerFurnace = true;
    private static final int maxticks = 10000;
    private long cooldown = config.get().getLong("Abilities.Fire.FireBlast.Cooldown");
    private Location location;
    private List<Block> safe = new ArrayList<Block>();
    private Location origin;
    private Vector direction;
    private Player player;
    private double speedfactor;
    private int ticks = 0;
    private int id = 0;
    private double range = RANGE;
    private double damage = DAMAGE;
    private double speed = SPEED;
    private double pushfactor = PUSH_FACTOR;
    private double affectingradius = AFFECTING_RADIUS;
    private boolean showParticles = true;
    private Random rand = new Random();

    public FireBlast(Location location, Vector direction, Player player, int damage, List<Block> safeblocks) {
        if (location.getBlock().isLiquid()) {
            return;
        }
        this.safe = safeblocks;
        this.range = FireMethods.getFirebendingDayAugment(this.range, player.getWorld());
        this.player = player;
        this.location = location.clone();
        this.origin = location.clone();
        this.direction = direction.clone().normalize();
        this.damage *= 1.5;
        instances.put(idCounter, this);
        this.id = idCounter;
        idCounter = (idCounter + 1) % Integer.MAX_VALUE;
    }

    public FireBlast(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("FireBlast")) {
            return;
        }
        if (player.getEyeLocation().getBlock().isLiquid() || Fireball.isCharging(player)) {
            return;
        }
        this.range = FireMethods.getFirebendingDayAugment(this.range, player.getWorld());
        this.player = player;
        this.location = player.getEyeLocation();
        this.origin = player.getEyeLocation();
        this.direction = player.getEyeLocation().getDirection().normalize();
        this.location = this.location.add(this.direction.clone());
        instances.put(idCounter, this);
        this.id = idCounter;
        idCounter = (idCounter + 1) % Integer.MAX_VALUE;
        bPlayer.addCooldown("FireBlast", this.cooldown);
    }

    public static boolean annihilateBlasts(Location location, double radius, Player source) {
        boolean broke = false;
        for (FireBlast blast : instances.values()) {
            Location fireblastlocation = blast.location;
            if (location.getWorld() != fireblastlocation.getWorld() || blast.player.equals((Object)source) || location.distance(fireblastlocation) > radius) continue;
            blast.remove();
            broke = true;
        }
        if (Fireball.annihilateBlasts(location, radius, source)) {
            broke = true;
        }
        return broke;
    }

    public static ArrayList<FireBlast> getAroundPoint(Location location, double radius) {
        ArrayList<FireBlast> list = new ArrayList<FireBlast>();
        for (FireBlast fireBlast : instances.values()) {
            Location fireblastlocation = fireBlast.location;
            if (location.getWorld() != fireblastlocation.getWorld() || location.distance(fireblastlocation) > radius) continue;
            list.add(fireBlast);
        }
        return list;
    }

    public static String getDescription() {
        return "FireBlast is the most fundamental bending technique of a firebender. To use, simply left-click in a direction. A blast of fire will be created at your fingertips. If this blast contacts an enemy, it will dissipate and engulf them in flames, doing additional damage and knocking them back slightly. If the blast hits terrain, it will ignite the nearby area. Additionally, if you hold sneak, you will charge up the fireblast. If you release it when it's charged, it will instead launch a powerful fireball that explodes on contact.";
    }

    public static void removeFireBlastsAroundPoint(Location location, double radius) {
        for (FireBlast fireBlast : instances.values()) {
            Location fireblastlocation = fireBlast.location;
            if (location.getWorld() != fireblastlocation.getWorld() || location.distance(fireblastlocation) > radius) continue;
            fireBlast.remove();
        }
        Fireball.removeFireballsAroundPoint(location, radius);
    }

    private void advanceLocation() {
        if (this.showParticles) {
            ParticleEffect.FLAME.display(this.location, 0.275f, 0.275f, 0.275f, 0.0f, 6);
            ParticleEffect.SMOKE_NORMAL.display(this.location, 0.3f, 0.3f, 0.3f, 0.0f, 3);
        }
        this.location = this.location.add(this.direction.clone().multiply(this.speedfactor));
        if (this.rand.nextInt(4) == 0) {
            FireMethods.playFirebendingSound(this.location);
        }
    }

    private void affect(Entity entity) {
        if (entity.getUniqueId() != this.player.getUniqueId()) {
            if (AvatarState.isAvatarState(this.player)) {
                GeneralMethods.setVelocity(entity, this.direction.clone().multiply(AvatarState.getValue(this.pushfactor)));
            } else {
                GeneralMethods.setVelocity(entity, this.direction.clone().multiply(this.pushfactor));
            }
            if (entity instanceof LivingEntity) {
                entity.setFireTicks((int)(fireticks * 20.0));
                GeneralMethods.damageEntity(this.player, entity, (int)FireMethods.getFirebendingDayAugment(this.damage, entity.getWorld()), "FireBlast");
                AirMethods.breakBreathbendingHold(entity);
                new com.projectkorra.projectkorra.firebending.Enflamed(entity, this.player);
                this.remove();
            }
        }
    }

    public double getAffectingradius() {
        return this.affectingradius;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public double getDamage() {
        return this.damage;
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

    private void ignite(Location location) {
        for (Block block : GeneralMethods.getBlocksAroundPoint(location, this.affectingradius)) {
            if (!FireStream.isIgnitable(this.player, block) || this.safe.contains((Object)block)) continue;
            if (FireMethods.canFireGrief()) {
                if (WaterMethods.isPlantbendable(block)) {
                    new com.projectkorra.projectkorra.waterbending.Plantbending(block);
                }
                block.setType(Material.FIRE);
            } else {
                FireMethods.createTempFire(block.getLocation());
            }
            if (!dissipate) continue;
            FireStream.ignitedblocks.put(block, this.player);
            FireStream.ignitedtimes.put(block, System.currentTimeMillis());
        }
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "Blaze", this.location)) {
            this.remove();
            return false;
        }
        this.speedfactor = this.speed * ((double)ProjectKorra.time_step / 1000.0);
        ++this.ticks;
        if (this.ticks > 10000) {
            this.remove();
            return false;
        }
        Block block = this.location.getBlock();
        if (GeneralMethods.isSolid(block) || block.isLiquid()) {
            if (block.getType() == Material.FURNACE && canPowerFurnace) {
                Furnace furnace = (Furnace)block.getState();
                furnace.setBurnTime((short) 800);
                furnace.setCookTime((short) 800);
                furnace.update();
            } else if (FireStream.isIgnitable(this.player, block.getRelative(BlockFace.UP))) {
                this.ignite(this.location);
            }
            this.remove();
            return false;
        }
        if (this.location.distance(this.origin) > this.range) {
            this.remove();
            return false;
        }
        WaterMethods.removeWaterSpouts(this.location, this.player);
        AirMethods.removeAirSpouts(this.location, this.player);
        double radius = this.affectingradius;
        Player source = this.player;
        if (EarthBlast.annihilateBlasts(this.location, radius, source) || WaterManipulation.annihilateBlasts(this.location, radius, source) || FireBlast.annihilateBlasts(this.location, radius, source)) {
            this.remove();
            return false;
        }
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.affectingradius)) {
            this.affect(entity);
            if (entity instanceof LivingEntity) break;
        }
        this.advanceLocation();
        return true;
    }

    public static void progressAll() {
        for (FireBlast ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove(this.id);
    }

    public static void removeAll() {
        for (FireBlast ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        SPEED = config.get().getDouble("Abilities.Fire.FireBlast.Speed");
        PUSH_FACTOR = config.get().getDouble("Abilities.Fire.FireBlast.Push");
        RANGE = config.get().getDouble("Abilities.Fire.FireBlast.Range");
        DAMAGE = config.get().getInt("Abilities.Fire.FireBlast.Damage");
        fireticks = config.get().getDouble("Abilities.Fire.FireBlast.FireTicks");
        dissipate = config.get().getBoolean("Abilities.Fire.FireBlast.Dissipate");
        this.cooldown = config.get().getLong("Abilities.Fire.FireBlast.Cooldown");
        this.range = RANGE;
        this.damage = DAMAGE;
        this.speed = SPEED;
        this.pushfactor = PUSH_FACTOR;
        this.affectingradius = AFFECTING_RADIUS;
    }

    public void setAffectingradius(double affectingradius) {
        this.affectingradius = affectingradius;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
        if (this.player != null) {
            GeneralMethods.getBendingPlayer(this.player.getName()).addCooldown("FireBlast", cooldown);
        }
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

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

