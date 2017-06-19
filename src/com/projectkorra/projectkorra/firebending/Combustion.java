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
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.util.ParticleEffect;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Combustion
implements ConfigLoadable {
    public static long chargeTime = config.get().getLong("Abilities.Fire.Combustion.ChargeTime");
    public static long cooldown = config.get().getLong("Abilities.Fire.Combustion.Cooldown");
    public static double speed = config.get().getDouble("Abilities.Fire.Combustion.Speed");
    public static double defaultrange = config.get().getDouble("Abilities.Fire.Combustion.Range");
    public static double defaultpower = config.get().getDouble("Abilities.Fire.Combustion.Power");
    public static boolean breakblocks = config.get().getBoolean("Abilities.Fire.Combustion.BreakBlocks");
    public static double radius = config.get().getDouble("Abilities.Fire.Combustion.Radius");
    public static double defaultdamage = config.get().getDouble("Abilities.Fire.Combustion.Damage");
    public static ConcurrentHashMap<Player, Combustion> instances = new ConcurrentHashMap();
    private static final int maxticks = 10000;
    private Location location;
    private Location origin;
    private Player player;
    private Vector direction;
    private double range = defaultrange;
    private double speedfactor;
    private int ticks = 0;
    private float power;
    private double damage;
    private long starttime;
    private boolean charged = false;

    public Combustion(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (instances.containsKey((Object)player)) {
            return;
        }
        if (bPlayer.isOnCooldown("Combustion")) {
            return;
        }
        this.player = player;
        this.starttime = System.currentTimeMillis();
        this.origin = player.getEyeLocation();
        this.direction = player.getEyeLocation().getDirection().normalize();
        this.location = this.origin.clone();
        if (AvatarState.isAvatarState(player)) {
            this.range = AvatarState.getValue(defaultrange);
            this.damage = AvatarState.getValue(defaultdamage);
        } else if (FireMethods.isDay(player.getWorld())) {
            this.range = FireMethods.getFirebendingDayAugment(defaultrange, player.getWorld());
            this.damage = FireMethods.getFirebendingDayAugment(defaultdamage, player.getWorld());
        } else {
            this.range = defaultrange;
            this.damage = defaultdamage;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "Combustion", GeneralMethods.getTargetedLocation(player, this.range, new Integer[0]))) {
            return;
        }
        instances.put(player, this);
        bPlayer.addCooldown("Combustion", cooldown);
    }

    public static void explode(Player player) {
        if (instances.containsKey((Object)player)) {
            Combustion combustion = instances.get((Object)player);
            combustion.createExplosion(combustion.location, combustion.power, breakblocks);
            ParticleEffect.EXPLOSION_NORMAL.display(combustion.location, (float)Math.random(), (float)Math.random(), (float)Math.random(), 0.0f, 3);
            
            
        }
    }

    public static boolean removeAroundPoint(Location loc, double radius) {
        for (Combustion combustion : instances.values()) {
            if (combustion.location.getWorld() != loc.getWorld() || combustion.location.distance(loc) > radius) continue;
            Combustion.explode(combustion.getPlayer());
            combustion.remove();
            return true;
        }
        return false;
    }

    private void advanceLocation() {
        ParticleEffect.FIREWORKS_SPARK.display(this.location, (float)Math.random() / 2.0f, (float)Math.random() / 2.0f, (float)Math.random() / 2.0f, 0.0f, 5);
        ParticleEffect.FLAME.display(this.location, (float)Math.random() / 2.0f, (float)Math.random() / 2.0f, (float)Math.random() / 2.0f, 0.0f, 2);
        FireMethods.playCombustionSound(this.location);
        this.location = this.location.add(this.direction.clone().multiply(this.speedfactor));
    }

    private void createExplosion(Location block, float power, boolean breakblocks) {
        block.getWorld().createExplosion(block.getX(), block.getY(), block.getZ(), (float)defaultpower, true, breakblocks);
        for (Entity entity : block.getWorld().getEntities()) {
            if (!(entity instanceof LivingEntity) || entity.getLocation().distance(block) >= radius) continue;
            GeneralMethods.damageEntity(this.player, entity, this.damage, "Combustion");
            AirMethods.breakBreathbendingHold(entity);
        }
        this.remove();
    }

    public boolean progress() {
        if (!instances.containsKey((Object)this.player)) {
            return false;
        }
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "Combustion")) {
            this.remove();
            return false;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null || !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("Combustion")) {
            this.remove();
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "Combustion", this.location)) {
            this.remove();
            return false;
        }
        this.speedfactor = speed * ((double)ProjectKorra.time_step / 1000.0);
        ++this.ticks;
        if (this.ticks > 10000) {
            this.remove();
            return false;
        }
        if (this.location.distance(this.origin) > this.range) {
            this.remove();
            return false;
        }
        Block block = this.location.getBlock();
        if (block != null && block.getType() != Material.AIR && block.getType() != Material.WATER && block.getType() != Material.STATIONARY_WATER) {
            this.createExplosion(block.getLocation(), this.power, breakblocks);
        }
        for (Entity entity : this.location.getWorld().getEntities()) {
            if (!(entity instanceof LivingEntity) || entity.getLocation().distance(this.location) > 2.0 || entity.equals((Object)this.player)) continue;
            this.createExplosion(this.location, this.power, breakblocks);
        }
        this.advanceLocation();
        return true;
    }

    public static void progressAll() {
        for (Combustion ability : instances.values()) {
            ability.progress();
        }
    }

    @Override
    public void reloadVariables() {
        chargeTime = config.get().getLong("Abilities.Fire.Combustion.ChargeTime");
        cooldown = config.get().getLong("Abilities.Fire.Combustion.Cooldown");
        speed = config.get().getDouble("Abilities.Fire.Combustion.Speed");
        defaultrange = config.get().getDouble("Abilities.Fire.Combustion.Range");
        defaultpower = config.get().getDouble("Abilities.Fire.Combustion.Power");
        breakblocks = config.get().getBoolean("Abilities.Fire.Combustion.BreakBlocks");
        radius = config.get().getDouble("Abilities.Fire.Combustion.Radius");
        defaultdamage = config.get().getDouble("Abilities.Fire.Combustion.Damage");
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public Player getPlayer() {
        return this.player;
    }
}

