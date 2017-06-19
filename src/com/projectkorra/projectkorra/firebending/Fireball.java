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
 *  org.bukkit.block.BlockState
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.material.MaterialData
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.FireStream;
import com.projectkorra.projectkorra.util.ParticleEffect;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class Fireball
implements ConfigLoadable {
    public static ConcurrentHashMap<Integer, Fireball> instances = new ConcurrentHashMap();
    private static ConcurrentHashMap<Entity, Fireball> explosions = new ConcurrentHashMap();
    private static long defaultchargetime = config.get().getLong("Abilities.Fire.FireBlast.Charged.ChargeTime");
    private static long interval = 25;
    private static double radius = 1.5;
    private static int idCounter = 0;
    private static double MAX_DAMAGE = config.get().getDouble("Abilities.Fire.FireBlast.Charged.Damage");
    private static double DAMAGE_RADIUS = config.get().getDouble("Abilities.Fire.FireBlast.Charged.DamageRadius");
    private static double RANGE = config.get().getDouble("Abilities.Fire.FireBlast.Charged.Range");
    private static double EXPLOSIONRADIUS = config.get().getDouble("Abilities.Fire.FireBlast.Charged.ExplosionRadius");
    private static boolean DAMAGEBLOCKS = config.get().getBoolean("Abilities.Fire.FireBlast.Charged.DamageBlocks");
    private static double fireticks = config.get().getDouble("Abilities.Fire.FireBlast.Charged.FireTicks");
    private int id;
    private double maxdamage = MAX_DAMAGE;
    private double range = RANGE;
    private double damageradius = DAMAGE_RADIUS;
    private double explosionradius = EXPLOSIONRADIUS;
    private double innerradius = this.damageradius / 2.0;
    private long starttime;
    private long time;
    private long chargetime = defaultchargetime;
    private boolean charged = false;
    private boolean launched = false;
    private Player player;
    private Location origin;
    private Location location;
    private Vector direction;
    private TNTPrimed explosion = null;
    private boolean damage_blocks;

    public Fireball(Player player) {
        this.player = player;
        this.starttime = this.time = System.currentTimeMillis();
        if (FireMethods.isDay(player.getWorld())) {
            this.chargetime = (long)((double)this.chargetime / config.get().getDouble("Properties.Fire.DayFactor"));
        }
        if (AvatarState.isAvatarState(player)) {
            this.chargetime = 0;
            this.maxdamage = AvatarState.getValue(this.maxdamage);
        }
        this.range = FireMethods.getFirebendingDayAugment(this.range, player.getWorld());
        if (!player.getEyeLocation().getBlock().isLiquid()) {
            instances.put(idCounter, this);
            this.id = idCounter;
            idCounter = (idCounter + 1) % Integer.MAX_VALUE;
        }
    }

    public static boolean annihilateBlasts(Location location, double radius, Player source) {
        boolean broke = false;
        for (Fireball fireball : instances.values()) {
            if (!fireball.launched) continue;
            Location fireblastlocation = fireball.location;
            if (location.getWorld() != fireblastlocation.getWorld() || source.equals((Object)fireball.player) || location.distance(fireblastlocation) > radius) continue;
            fireball.explode();
            broke = true;
        }
        return broke;
    }

    public static Fireball getFireball(Entity entity) {
        if (explosions.containsKey((Object)entity)) {
            return explosions.get((Object)entity);
        }
        return null;
    }

    public static boolean isCharging(Player player) {
        for (Fireball fireball : instances.values()) {
            if (fireball.player != player || fireball.launched) continue;
            return true;
        }
        return false;
    }

    public static void removeFireballsAroundPoint(Location location, double radius) {
        for (Fireball fireball : instances.values()) {
            if (!fireball.launched) continue;
            Location fireblastlocation = fireball.location;
            if (location.getWorld() != fireblastlocation.getWorld() || location.distance(fireblastlocation) > radius) continue;
            fireball.remove();
        }
    }

    public void dealDamage(Entity entity) {
        if (this.explosion == null) {
            return;
        }
        double distance = entity.getLocation().distance(this.explosion.getLocation());
        if (distance > this.damageradius) {
            return;
        }
        if (distance < this.innerradius) {
            GeneralMethods.damageEntity(this.player, entity, this.maxdamage, "FireBlast");
            return;
        }
        double slope = (- this.maxdamage * 0.5) / (this.damageradius - this.innerradius);
        double damage = slope * (distance - this.innerradius) + this.maxdamage;
        GeneralMethods.damageEntity(this.player, entity, damage, "FireBlast");
        AirMethods.breakBreathbendingHold(entity);
    }

    public void explode() {
        boolean explode = true;
        for (Block block : GeneralMethods.getBlocksAroundPoint(this.location, 3.0)) {
            if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "FireBlast", block.getLocation())) continue;
            explode = false;
            break;
        }
        if (explode) {
            if (this.damage_blocks && this.explosionradius > 0.0) {
                this.explosion = (TNTPrimed)this.player.getWorld().spawn(this.location, TNTPrimed.class);
                this.explosion.setFuseTicks(0);
                float yield = (float)this.explosionradius;
                if (!AvatarState.isAvatarState(this.player)) {
                    if (FireMethods.isDay(this.player.getWorld())) {
                        yield = (float)FireMethods.getFirebendingDayAugment(yield, this.player.getWorld());
                    }
                } else {
                    yield = (float)((double)yield * AvatarState.factor);
                }
                this.explosion.setYield(yield);
                explosions.put((Entity)this.explosion, this);
            } else {
                List<Entity> l = GeneralMethods.getEntitiesAroundPoint(this.location, this.damageradius);
                for (Entity e : l) {
                    if (!(e instanceof LivingEntity)) continue;
                    double slope = (- this.maxdamage * 0.5) / (this.damageradius - this.innerradius);
                    double damage = slope * (e.getLocation().distance(this.location) - this.innerradius) + this.maxdamage;
                    GeneralMethods.damageEntity(this.getPlayer(), e, damage, "FireBlast");
                }
                this.location.getWorld().playSound(this.location, Sound.ENTITY_GENERIC_EXPLODE, 5.0f, 1.0f);
                ParticleEffect.EXPLOSION_HUGE.display(new Vector(0, 0, 0), 0.0f, this.location, 256.0);
            }
        }
        this.ignite(this.location);
        this.remove();
    }

    private void fireball() {
        for (Block block : GeneralMethods.getBlocksAroundPoint(this.location, radius)) {
            ParticleEffect.FLAME.display(block.getLocation(), 0.5f, 0.5f, 0.5f, 0.0f, 5);
            ParticleEffect.SMOKE_NORMAL.display(block.getLocation(), 0.5f, 0.5f, 0.5f, 0.0f, 2);
            if (GeneralMethods.rand.nextInt(4) != 0) continue;
            FireMethods.playFirebendingSound(this.location);
        }
        boolean exploded = false;
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.0 * radius)) {
            if (entity.getEntityId() == this.player.getEntityId()) continue;
            entity.setFireTicks((int)(fireticks * 20.0));
            if (!(entity instanceof LivingEntity)) continue;
            if (!exploded) {
                this.explode();
                exploded = true;
            }
            this.dealDamage(entity);
        }
    }

    public long getChargetime() {
        return this.chargetime;
    }

    public double getDamageRadius() {
        return this.damageradius;
    }

    public double getExplosionRadius() {
        return this.explosionradius;
    }

    public boolean getDamageBlocks() {
        return this.damage_blocks;
    }

    public double getInnerradius() {
        return this.innerradius;
    }

    public double getMaxdamage() {
        return this.maxdamage;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getRange() {
        return this.range;
    }

    private void ignite(Location location) {
        for (Block block : GeneralMethods.getBlocksAroundPoint(location, FireBlast.AFFECTING_RADIUS)) {
            if (!FireStream.isIgnitable(this.player, block)) continue;
            if (block.getType() != Material.FIRE) {
                FireStream.replacedBlocks.put(block.getLocation(), block.getState().getData());
            }
            block.setType(Material.FIRE);
            if (!FireBlast.dissipate) continue;
            FireStream.ignitedblocks.put(block, this.player);
            FireStream.ignitedtimes.put(block, System.currentTimeMillis());
        }
    }

    public boolean progress() {
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            this.remove();
            return false;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "FireBlast") && !this.launched) {
            this.remove();
            return false;
        }
        if (!GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("FireBlast") && !this.launched) {
            this.remove();
            return false;
        }
        if (System.currentTimeMillis() > this.starttime + this.chargetime) {
            this.charged = true;
        }
        if (!this.player.isSneaking() && !this.charged) {
            this.remove();
            return false;
        }
        if (!this.player.isSneaking() && !this.launched) {
            this.launched = true;
            this.location = this.player.getEyeLocation();
            this.origin = this.location.clone();
            this.direction = this.location.getDirection().normalize().multiply(radius);
        }
        if (System.currentTimeMillis() > this.time + interval) {
            if (this.launched && GeneralMethods.isRegionProtectedFromBuild(this.player, "Blaze", this.location)) {
                this.remove();
                return false;
            }
            this.time = System.currentTimeMillis();
            if (!this.launched && !this.charged) {
                return true;
            }
            if (!this.launched) {
                this.player.getWorld().playEffect(this.player.getEyeLocation(), Effect.MOBSPAWNER_FLAMES, 0, 3);
                return true;
            }
            this.location = this.location.clone().add(this.direction);
            if (this.location.distance(this.origin) > this.range) {
                this.remove();
                return false;
            }
            if (GeneralMethods.isSolid(this.location.getBlock())) {
                this.explode();
                return false;
            }
            if (this.location.getBlock().isLiquid()) {
                this.remove();
                return false;
            }
            this.fireball();
        }
        return true;
    }

    public static void progressAll() {
        for (Fireball ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove(this.id);
    }

    public static void removeAll() {
        for (Fireball ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        defaultchargetime = config.get().getLong("Abilities.Fire.FireBlast.Charged.ChargeTime");
        interval = 25;
        radius = 1.5;
        MAX_DAMAGE = config.get().getDouble("Abilities.Fire.FireBlast.Charged.Damage");
        DAMAGE_RADIUS = config.get().getDouble("Abilities.Fire.FireBlast.Charged.DamageRadius");
        RANGE = config.get().getDouble("Abilities.Fire.FireBlast.Charged.Range");
        DAMAGEBLOCKS = config.get().getBoolean("Abilities.Fire.FireBlast.Charged.DamageBlocks");
        EXPLOSIONRADIUS = config.get().getDouble("Abilities.Fire.FireBlast.Charged.ExplosionRadius");
        fireticks = config.get().getDouble("Abilities.Fire.FireBlast.Charged.FireTicks");
        this.maxdamage = MAX_DAMAGE;
        this.range = RANGE;
        this.damageradius = DAMAGE_RADIUS;
        this.explosionradius = EXPLOSIONRADIUS;
        this.damage_blocks = DAMAGEBLOCKS;
        this.chargetime = defaultchargetime;
    }

    public void setChargetime(long chargetime) {
        this.chargetime = chargetime;
    }

    public void setDamageBlocks(boolean damageblocks) {
        this.damage_blocks = damageblocks;
    }

    public void setExplosionRadius(double radius) {
        this.explosionradius = radius;
    }

    public void setDamageRadius(double radius) {
        this.damageradius = radius;
    }

    public void setInnerradius(double innerradius) {
        this.innerradius = innerradius;
    }

    public void setMaxdamage(double maxdamage) {
        this.maxdamage = maxdamage;
    }

    public void setRange(double range) {
        this.range = range;
    }
}

