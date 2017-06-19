/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.ArmorStand
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.WaterArms;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class WaterArmsSpear {
    private static FileConfiguration config = ProjectKorra.plugin.getConfig();
    public static ConcurrentHashMap<Integer, WaterArmsSpear> instances = new ConcurrentHashMap();
    private Player player;
    private WaterArms waterArms;
    private List<Location> spearLocations = new ArrayList<Location>();
    private int spearRange = config.getInt("Abilities.Water.WaterArms.Spear.Range");
    private double spearDamage = config.getDouble("Abilities.Water.WaterArms.Spear.Damage");
    private boolean spearDamageEnabled = config.getBoolean("Abilities.Water.WaterArms.Spear.DamageEnabled");
    private int spearSphere = config.getInt("Abilities.Water.WaterArms.Spear.Sphere");
    private long spearDuration = config.getLong("Abilities.Water.WaterArms.Spear.Duration");
    private int spearLength = config.getInt("Abilities.Water.WaterArms.Spear.Length");
    private int spearRangeNight = config.getInt("Abilities.Water.WaterArms.Spear.NightAugments.Range.Normal");
    private int spearRangeFullMoon = config.getInt("Abilities.Water.WaterArms.Spear.NightAugments.Range.FullMoon");
    private int spearSphereNight = config.getInt("Abilities.Water.WaterArms.Spear.NightAugments.Sphere.Normal");
    private int spearSphereFullMoon = config.getInt("Abilities.Water.WaterArms.Spear.NightAugments.Sphere.FullMoon");
    private long spearDurationNight = config.getLong("Abilities.Water.WaterArms.Spear.NightAugments.Duration.Normal");
    private long spearDurationFullMoon = config.getLong("Abilities.Water.WaterArms.Spear.NightAugments.Duration.FullMoon");
    private boolean usageCooldownEnabled = config.getBoolean("Abilities.Water.WaterArms.Arms.Cooldowns.UsageCooldownEnabled");
    private long usageCooldown = config.getLong("Abilities.Water.WaterArms.Arms.Cooldowns.UsageCooldown");
    private Location location;
    private Location initLocation;
    private int distanceTravelled;
    private WaterArms.Arm arm;
    private int layer;
    private boolean hitEntity;
    private boolean canFreeze;
    private int id;
    private static int ID = Integer.MIN_VALUE;
    Random rand = new Random();

    public WaterArmsSpear(Player player, boolean freeze) {
        this.player = player;
        this.canFreeze = freeze;
        this.getNightAugments();
        this.createInstance();
    }

    private void getNightAugments() {
        World world = this.player.getWorld();
        if (WaterMethods.isNight(world)) {
            if (WaterMethods.isFullMoon(world)) {
                this.spearRange = this.spearRangeFullMoon;
                this.spearSphere = this.spearSphereFullMoon;
                this.spearDuration = this.spearDurationFullMoon;
            } else {
                this.spearRange = this.spearRangeNight;
                this.spearSphere = this.spearSphereNight;
                this.spearDuration = this.spearDurationNight;
            }
        }
    }

    private void createInstance() {
        if (WaterArms.instances.containsKey((Object)this.player)) {
            this.waterArms = WaterArms.instances.get((Object)this.player);
            this.waterArms.switchPreferredArm();
            this.arm = this.waterArms.getActiveArm();
            BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(this.player.getName());
            if (this.arm.equals((Object)WaterArms.Arm.Left)) {
                if (this.waterArms.isLeftArmCooldown() || bPlayer.isOnCooldown("WaterArms_LEFT") || !this.waterArms.displayLeftArm()) {
                    return;
                }
                if (this.usageCooldownEnabled) {
                    bPlayer.addCooldown("WaterArms_LEFT", this.usageCooldown);
                }
                this.waterArms.setLeftArmConsumed(true);
                this.waterArms.setLeftArmCooldown(true);
            }
            if (this.arm.equals((Object)WaterArms.Arm.Right)) {
                if (this.waterArms.isRightArmCooldown() || bPlayer.isOnCooldown("WaterArms_RIGHT") || !this.waterArms.displayRightArm()) {
                    return;
                }
                if (this.usageCooldownEnabled) {
                    bPlayer.addCooldown("WaterArms_RIGHT", this.usageCooldown);
                }
                this.waterArms.setRightArmConsumed(true);
                this.waterArms.setRightArmCooldown(true);
            }
        } else {
            return;
        }
        Vector dir = this.player.getLocation().getDirection();
        this.location = this.waterArms.getActiveArmEnd().add(dir.normalize().multiply(1));
        this.initLocation = this.location.clone();
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
        if (this.distanceTravelled > this.spearRange) {
            this.remove();
            return;
        }
        if (!this.hitEntity) {
            this.progressSpear();
        } else {
            this.createIceBall();
        }
        if (this.layer >= this.spearSphere) {
            this.remove();
            return;
        }
        if (!this.canPlaceBlock(this.location.getBlock())) {
            if (this.canFreeze) {
                this.createSpear();
            }
            this.remove();
            return;
        }
    }

    private void progressSpear() {
        int i = 0;
        while (i < 2) {
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.0)) {
                if (!(entity instanceof LivingEntity) || entity.getEntityId() == this.player.getEntityId() || entity instanceof ArmorStand) continue;
                this.hitEntity = true;
                this.location = entity.getLocation();
                if (this.spearDamageEnabled) {
                    GeneralMethods.damageEntity(this.player, entity, this.spearDamage, Element.Water, "WaterArms Spear");
                }
                return;
            }
            new com.projectkorra.projectkorra.util.TempBlock(this.location.getBlock(), Material.STATIONARY_WATER, (byte)8);
            WaterArms.revert.put(this.location.getBlock(), System.currentTimeMillis() + 600);
            Vector direction = GeneralMethods.getDirection(this.initLocation, GeneralMethods.getTargetedLocation(this.player, this.spearRange, 8, 9, 79, 174)).normalize();
            this.location = this.location.add(direction.clone().multiply(1));
            this.spearLocations.add(this.location.clone());
            if (!this.canPlaceBlock(this.location.getBlock())) {
                return;
            }
            ++this.distanceTravelled;
            ++i;
        }
    }

    private void createSpear() {
        int i = this.spearLocations.size() - this.spearLength;
        while (i < this.spearLocations.size()) {
            Block block;
            if (i >= 0 && this.canPlaceBlock(block = this.spearLocations.get(i).getBlock())) {
                WaterMethods.playIcebendingSound(block.getLocation());
                if (WaterArms.revert.containsKey((Object)block)) {
                    WaterArms.revert.remove((Object)block);
                }
                new com.projectkorra.projectkorra.util.TempBlock(block, Material.ICE, (byte)0);
                WaterArms.revert.put(block, System.currentTimeMillis() + this.spearDuration + (long)(Math.random() * 500.0));
            }
            ++i;
        }
    }

    private void createIceBall() {
        ++this.layer;
        for (Block block : GeneralMethods.getBlocksAroundPoint(this.location, this.layer)) {
            if (!EarthMethods.isTransparentToEarthbending(this.player, block) || block.getType() == Material.ICE || WaterArms.isUnbreakable(block)) continue;
            WaterMethods.playIcebendingSound(block.getLocation());
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.ICE, (byte)0);
            WaterArms.revert.put(block, System.currentTimeMillis() + this.spearDuration + (long)(Math.random() * 500.0));
        }
    }

    private boolean canPlaceBlock(Block block) {
        if (!(EarthMethods.isTransparentToEarthbending(this.player, block) || (WaterMethods.isWater(block) || WaterMethods.isIcebendable(block)) && TempBlock.isTempBlock(block) && !WaterArms.revert.containsKey((Object)block))) {
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterArms", block.getLocation())) {
            return false;
        }
        if (WaterArms.isUnbreakable(block) && !WaterMethods.isWater(block)) {
            return false;
        }
        return true;
    }

    private void remove() {
        if (WaterArms.instances.containsKey((Object)this.player)) {
            if (this.arm.equals((Object)WaterArms.Arm.Left)) {
                this.waterArms.setLeftArmCooldown(false);
            } else {
                this.waterArms.setRightArmCooldown(false);
            }
            this.waterArms.setMaxUses(this.waterArms.getMaxUses() - 1);
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

    public boolean getCanFreeze() {
        return this.canFreeze;
    }

    public void setCanFreeze(boolean freeze) {
        this.canFreeze = freeze;
    }

    public boolean getHasHitEntity() {
        return this.hitEntity;
    }

    public void setHitEntity(boolean hit) {
        this.hitEntity = hit;
    }
}

