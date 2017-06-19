/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.object.HorizontalVelocityTracker;
import com.projectkorra.projectkorra.util.TempPotionEffect;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Bloodbending {
    public static ConcurrentHashMap<Player, Bloodbending> instances = new ConcurrentHashMap();
    ConcurrentHashMap<Entity, Location> targetentities = new ConcurrentHashMap();
    private static final double FACTOR = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Bloodbending.ThrowFactor");
    private static final boolean onlyUsableAtNight = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Water.Bloodbending.CanOnlyBeUsedAtNight");
    private static boolean canBeUsedOnUndead = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Water.Bloodbending.CanBeUsedOnUndeadMobs");
    private static final boolean onlyUsableDuringMoon = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Water.Bloodbending.CanOnlyBeUsedDuringFullMoon");
    private boolean canBloodbendBloodbenders = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Water.Bloodbending.CanBloodbendOtherBloodbenders");
    private int RANGE = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.Bloodbending.Range");
    private long HOLD_TIME = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.Bloodbending.HoldTime");
    private long COOLDOWN = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.Bloodbending.Cooldown");
    private Player player;
    private long time;
    private double factor = FACTOR;
    private int range = this.RANGE;
    private long holdTime = this.HOLD_TIME;
    private long cooldown = this.COOLDOWN;

    public Bloodbending(Player player) {
        if (instances.containsKey((Object)player)) {
            Bloodbending.remove(player);
            return;
        }
        if (onlyUsableAtNight && !WaterMethods.isNight(player.getWorld()) && !WaterMethods.canBloodbendAtAnytime(player)) {
            return;
        }
        if (onlyUsableDuringMoon && !WaterMethods.isFullMoon(player.getWorld()) && !WaterMethods.canBloodbendAtAnytime(player)) {
            return;
        }
        BendingPlayer bplayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bplayer.isOnCooldown("Bloodbending") && !AvatarState.isAvatarState(player)) {
            return;
        }
        this.range = (int)WaterMethods.waterbendingNightAugment(this.range, player.getWorld());
        if (AvatarState.isAvatarState(player)) {
            this.range = (int)((double)this.range + AvatarState.getValue(1.5));
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), this.range)) {
                if (!(entity instanceof LivingEntity) || entity instanceof Player && (GeneralMethods.isRegionProtectedFromBuild(player, "Bloodbending", entity.getLocation()) || AvatarState.isAvatarState((Player)entity) || entity.getEntityId() == player.getEntityId() || GeneralMethods.canBend(((Player)entity).getName(), "Bloodbending"))) continue;
                GeneralMethods.damageEntity(player, entity, 0.0, "Bloodbending");
                AirMethods.breakBreathbendingHold(entity);
                this.targetentities.put(entity, entity.getLocation().clone());
            }
        } else {
            Entity target = GeneralMethods.getTargetedEntity(player, this.range, new ArrayList<Entity>());
            if (target == null) {
                return;
            }
            if (!(target instanceof LivingEntity) || GeneralMethods.isRegionProtectedFromBuild(player, "Bloodbending", target.getLocation())) {
                return;
            }
            if (target instanceof Player && (GeneralMethods.canBend(((Player)target).getName(), "Bloodbending") && !this.canBloodbendBloodbenders || AvatarState.isAvatarState((Player)target)) && (!FireMethods.isDay(target.getWorld()) || WaterMethods.canBloodbendAtAnytime((Player)target))) {
                return;
            }
            if (!canBeUsedOnUndead && Bloodbending.isUndead(target)) {
                return;
            }
            GeneralMethods.damageEntity(player, target, 0.0, "Bloodbending");
            HorizontalVelocityTracker.remove(target);
            AirMethods.breakBreathbendingHold(target);
            this.targetentities.put(target, target.getLocation().clone());
        }
        if (this.targetentities.size() > 0) {
            bplayer.addCooldown("Bloodbending", this.cooldown);
        }
        this.player = player;
        this.time = System.currentTimeMillis();
        instances.put(player, this);
    }

    public static void launch(Player player) {
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).launch();
        }
    }

    private void launch() {
        Location location = this.player.getLocation();
        for (Entity entity : this.targetentities.keySet()) {
            Location target = entity.getLocation().clone();
            Vector vector = GeneralMethods.getDirection(location, GeneralMethods.getTargetedLocation(this.player, location.distance(target), new Integer[0]));
            vector.normalize();
            entity.setVelocity(vector.multiply(this.factor));
            new com.projectkorra.projectkorra.object.HorizontalVelocityTracker(entity, this.player, 200, "Bloodbending", Element.Air, SubElement.Bloodbending);
        }
        Bloodbending.remove(this.player);
    }

    private void progress() {
        PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 60, 1);
        if (!this.player.isSneaking()) {
            Bloodbending.remove(this.player);
            return;
        }
        if (this.holdTime > 0 && System.currentTimeMillis() - this.time > this.holdTime) {
            Bloodbending.remove(this.player);
            return;
        }
        if (!canBeUsedOnUndead) {
            for (Entity entity : this.targetentities.keySet()) {
                if (!Bloodbending.isUndead(entity)) continue;
                this.targetentities.remove((Object)entity);
            }
        }
        if (onlyUsableDuringMoon && !WaterMethods.isFullMoon(this.player.getWorld())) {
            Bloodbending.remove(this.player);
            return;
        }
        if (onlyUsableAtNight && !WaterMethods.isNight(this.player.getWorld())) {
            Bloodbending.remove(this.player);
            return;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "Bloodbending")) {
            Bloodbending.remove(this.player);
            return;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            Bloodbending.remove(this.player);
            return;
        }
        if (!GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("Bloodbending")) {
            Bloodbending.remove(this.player);
            return;
        }
        if (AvatarState.isAvatarState(this.player)) {
            ArrayList<Entity> entities = new ArrayList<Entity>();
            for (Entity entity2 : GeneralMethods.getEntitiesAroundPoint(this.player.getLocation(), this.range)) {
                if (GeneralMethods.isRegionProtectedFromBuild(this.player, "Bloodbending", entity2.getLocation()) || entity2 instanceof Player && (!WaterMethods.canBeBloodbent((Player)entity2) || entity2.getEntityId() == this.player.getEntityId())) continue;
                entities.add(entity2);
                if (!this.targetentities.containsKey((Object)entity2) && entity2 instanceof LivingEntity) {
                    GeneralMethods.damageEntity(this.player, entity2, 0.0, "Bloodbending");
                    this.targetentities.put(entity2, entity2.getLocation().clone());
                }
                if (!(entity2 instanceof LivingEntity)) continue;
                Location newlocation = entity2.getLocation();
                if (this.player.getWorld() != newlocation.getWorld()) {
                    this.targetentities.remove((Object)entity2);
                    continue;
                }
                Location location = this.targetentities.get((Object)entity2);
                double distance = location.distance(newlocation);
                double dx = location.getX() - newlocation.getX();
                double dy = location.getY() - newlocation.getY();
                double dz = location.getZ() - newlocation.getZ();
                Vector vector = new Vector(dx, dy, dz);
                if (distance > 0.5) {
                    entity2.setVelocity(vector.normalize().multiply(0.5));
                } else {
                    entity2.setVelocity(new Vector(0, 0, 0));
                }
                new com.projectkorra.projectkorra.util.TempPotionEffect((LivingEntity)entity2, effect);
                entity2.setFallDistance(0.0f);
                if (entity2 instanceof Creature) {
                    ((Creature)entity2).setTarget(null);
                }
                AirMethods.breakBreathbendingHold(entity2);
            }
            for (Entity entity2 : this.targetentities.keySet()) {
                if (entities.contains((Object)entity2)) continue;
                this.targetentities.remove((Object)entity2);
            }
        } else {
            for (Entity entity : this.targetentities.keySet()) {
                if (entity instanceof Player && !WaterMethods.canBeBloodbent((Player)entity)) {
                    this.targetentities.remove((Object)entity);
                    continue;
                }
                Location newlocation = entity.getLocation();
                if (this.player.getWorld() != newlocation.getWorld()) {
                    this.targetentities.remove((Object)entity);
                    continue;
                }
                Location location = GeneralMethods.getTargetedLocation(this.player, 6);
                double distance = location.distance(newlocation);
                double dx = location.getX() - newlocation.getX();
                double dy = location.getY() - newlocation.getY();
                double dz = location.getZ() - newlocation.getZ();
                Vector vector = new Vector(dx, dy, dz);
                if (distance > 0.5) {
                    entity.setVelocity(vector.normalize().multiply(0.5));
                } else {
                    entity.setVelocity(new Vector(0, 0, 0));
                }
                new com.projectkorra.projectkorra.util.TempPotionEffect((LivingEntity)entity, effect);
                entity.setFallDistance(0.0f);
                if (entity instanceof Creature) {
                    ((Creature)entity).setTarget(null);
                }
                AirMethods.breakBreathbendingHold(entity);
            }
        }
    }

    public static void progressAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).progress();
        }
    }

    public static void remove(Player player) {
        if (instances.containsKey((Object)player)) {
            instances.remove((Object)player);
        }
    }

    public static boolean isBloodbended(Entity entity) {
        for (Player player : instances.keySet()) {
            if (!Bloodbending.instances.get((Object)player).targetentities.containsKey((Object)entity)) continue;
            return true;
        }
        return false;
    }

    public static boolean isUndead(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.BLAZE || entity.getType() == EntityType.GIANT || entity.getType() == EntityType.IRON_GOLEM || entity.getType() == EntityType.MAGMA_CUBE || entity.getType() == EntityType.PIG_ZOMBIE || entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.SLIME || entity.getType() == EntityType.SNOWMAN || entity.getType() == EntityType.ZOMBIE) {
            return true;
        }
        return false;
    }

    public static Location getBloodbendingLocation(Entity entity) {
        for (Player player : instances.keySet()) {
            if (!Bloodbending.instances.get((Object)player).targetentities.containsKey((Object)entity)) continue;
            return Bloodbending.instances.get((Object)player).targetentities.get((Object)entity);
        }
        return null;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getFactor() {
        return this.factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public long getHoldTime() {
        return this.holdTime;
    }

    public void setHoldTime(long holdTime) {
        this.holdTime = holdTime;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
        if (this.player != null) {
            GeneralMethods.getBendingPlayer(this.player.getName()).addCooldown("Bloodbending", cooldown);
        }
    }
}

