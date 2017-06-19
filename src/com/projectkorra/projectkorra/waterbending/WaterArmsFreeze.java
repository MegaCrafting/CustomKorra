/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.util.TempPotionEffect;
import com.projectkorra.projectkorra.waterbending.WaterArms;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class WaterArmsFreeze {
    private static FileConfiguration config = ProjectKorra.plugin.getConfig();
    public static ConcurrentHashMap<Integer, WaterArmsFreeze> instances = new ConcurrentHashMap();
    private Player player;
    private WaterArms waterArms;
    private int iceRange = config.getInt("Abilities.Water.WaterArms.Freeze.Range");
    private double iceDamage = config.getInt("Abilities.Water.WaterArms.Freeze.Damage");
    private boolean usageCooldownEnabled = config.getBoolean("Abilities.Water.WaterArms.Arms.Cooldowns.UsageCooldownEnabled");
    private long usageCooldown = config.getLong("Abilities.Water.WaterArms.Arms.Cooldowns.UsageCooldown");
    private Location location;
    private Vector direction;
    private int distanceTravelled;
    private WaterArms.Arm arm;
    private boolean cancelled;
    private int id;
    private static int ID = Integer.MIN_VALUE;

    public WaterArmsFreeze(Player player) {
        this.player = player;
        this.direction = player.getEyeLocation().getDirection();
        this.createInstance();
    }

    private void createInstance() {
        if (WaterArms.instances.containsKey((Object)this.player)) {
            this.waterArms = WaterArms.instances.get((Object)this.player);
            this.waterArms.switchPreferredArm();
            this.arm = this.waterArms.getActiveArm();
            BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(this.player.getName());
            if (this.arm.equals((Object)WaterArms.Arm.Left)) {
                if (this.waterArms.isLeftArmCooldown() || bPlayer.isOnCooldown("WaterArms_LEFT")) {
                    return;
                }
                if (this.usageCooldownEnabled) {
                    bPlayer.addCooldown("WaterArms_LEFT", this.usageCooldown);
                }
                this.waterArms.setLeftArmCooldown(true);
            }
            if (this.arm.equals((Object)WaterArms.Arm.Right)) {
                if (this.waterArms.isRightArmCooldown() || bPlayer.isOnCooldown("WaterArms_RIGHT")) {
                    return;
                }
                if (this.usageCooldownEnabled) {
                    bPlayer.addCooldown("WaterArms_RIGHT", this.usageCooldown);
                }
                this.waterArms.setRightArmCooldown(true);
            }
        } else {
            return;
        }
        Vector dir = this.player.getLocation().getDirection();
        this.location = this.waterArms.getActiveArmEnd().add(dir.normalize().multiply(1));
        this.direction = GeneralMethods.getDirection(this.location, GeneralMethods.getTargetedLocation(this.player, this.iceRange, 8, 9, 79, 174)).normalize();
        this.id = ID;
        instances.put(this.id, this);
        if (ID == Integer.MAX_VALUE) {
            ID = Integer.MIN_VALUE;
        }
        ++ID;
    }

    private void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (this.distanceTravelled > this.iceRange) {
            this.remove();
            return;
        }
        if (this.distanceTravelled >= 5 && !this.cancelled) {
            this.cancelled = true;
            if (WaterArms.instances.containsKey((Object)this.player)) {
                if (this.arm.equals((Object)WaterArms.Arm.Left)) {
                    this.waterArms.setLeftArmCooldown(false);
                } else {
                    this.waterArms.setRightArmCooldown(false);
                }
                this.waterArms.setMaxIceBlasts(this.waterArms.getMaxIceBlasts() - 1);
            }
        }
        if (!this.canPlaceBlock(this.location.getBlock())) {
            this.remove();
            return;
        }
        this.progressIce();
    }

    private boolean canPlaceBlock(Block block) {
        if (!(EarthMethods.isTransparentToEarthbending(this.player, block) || (WaterMethods.isWater(block) || WaterMethods.isIcebendable(block)) && TempBlock.isTempBlock(block))) {
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterArms", block.getLocation())) {
            return false;
        }
        return true;
    }

    private void progressIce() {
        ParticleEffect.SNOW_SHOVEL.display(this.location, (float)Math.random(), (float)Math.random(), (float)Math.random(), 0.05f, 5);
        new com.projectkorra.projectkorra.util.TempBlock(this.location.getBlock(), Material.ICE, (byte)0);
        WaterArms.revert.put(this.location.getBlock(), System.currentTimeMillis() + 10);
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.5)) {
            if (!(entity instanceof LivingEntity) || entity.getEntityId() == this.player.getEntityId() || entity instanceof ArmorStand) continue;
            GeneralMethods.damageEntity(this.player, entity, this.iceDamage, SubElement.Icebending, "WaterArms Freeze");
            PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 40, 2);
            new com.projectkorra.projectkorra.util.TempPotionEffect((LivingEntity)entity, effect);
            this.remove();
            return;
        }
        int i = 0;
        while (i < 2) {
            this.location = this.location.add(this.direction.clone().multiply(1));
            if (!this.canPlaceBlock(this.location.getBlock())) {
                return;
            }
            ++this.distanceTravelled;
            ++i;
        }
    }

    private void remove() {
        if (WaterArms.instances.containsKey((Object)this.player) && !this.cancelled) {
            if (this.arm.equals((Object)WaterArms.Arm.Left)) {
                this.waterArms.setLeftArmCooldown(false);
            } else {
                this.waterArms.setRightArmCooldown(false);
            }
            this.waterArms.setMaxIceBlasts(this.waterArms.getMaxIceBlasts() - 1);
        }
        instances.remove(this.id);
    }

    public static void progressAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            instances.get(ID).progress();
        }
    }

    public static void removeAll() {
        instances.clear();
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean getCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

