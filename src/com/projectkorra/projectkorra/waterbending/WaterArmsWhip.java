/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.WaterArms;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class WaterArmsWhip {
    private static FileConfiguration config = ProjectKorra.plugin.getConfig();
    public static ConcurrentHashMap<Integer, WaterArmsWhip> instances = new ConcurrentHashMap();
    public static HashMap<LivingEntity, Integer> grabbedEntities = new HashMap();
    private Player player;
    private WaterArms waterArms;
    private int whipLength = config.getInt("Abilities.Water.WaterArms.Whip.MaxLength");
    private int whipLengthWeak = config.getInt("Abilities.Water.WaterArms.Whip.MaxLengthWeak");
    private int whipLengthNight = config.getInt("Abilities.Water.WaterArms.Whip.NightAugments.MaxLength.Normal");
    private int whipLengthFullMoon = config.getInt("Abilities.Water.WaterArms.Whip.NightAugments.MaxLength.FullMoon");
    private int initLength = config.getInt("Abilities.Water.WaterArms.Arms.InitialLength");
    private double pullMultiplier = config.getDouble("Abilities.Water.WaterArms.Whip.Pull.Multiplier");
    private double punchDamage = config.getDouble("Abilities.Water.WaterArms.Whip.Punch.PunchDamage");
    private int punchLength = config.getInt("Abilities.Water.WaterArms.Whip.Punch.MaxLength");
    private int punchLengthNight = config.getInt("Abilities.Water.WaterArms.Whip.Punch.NightAugments.MaxLength.Normal");
    private int punchLengthFullMoon = config.getInt("Abilities.Water.WaterArms.Whip.Punch.NightAugments.MaxLength.FullMoon");
    private boolean grappleRespectRegions = config.getBoolean("Abilities.Water.WaterArms.Whip.Grapple.RespectRegions");
    private long holdTime = config.getLong("Abilities.Water.WaterArms.Whip.Grab.HoldTime");
    private boolean usageCooldownEnabled = config.getBoolean("Abilities.Water.WaterArms.Arms.Cooldowns.UsageCooldownEnabled");
    private long usageCooldown = config.getLong("Abilities.Water.WaterArms.Arms.Cooldowns.UsageCooldown");
    private int activeLength = this.initLength;
    private int whipSpeed = 2;
    private boolean reverting = false;
    private boolean hasDamaged = false;
    private boolean grappled = false;
    private boolean grabbed = false;
    private double playerHealth;
    private long time;
    private LivingEntity grabbedEntity;
    private Location end;
    private WaterArms.Arm arm;
    private Whip ability;
    private int id;
    private static int ID = Integer.MIN_VALUE;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArmsWhip$Whip;

    public WaterArmsWhip(Player player, Whip ability) {
        if (instances.containsKey(WaterArmsWhip.getId(player))) {
            WaterArmsWhip waw = instances.get(WaterArmsWhip.getId(player));
            if (waw.grabbed) {
                waw.grabbed = false;
                if (waw.grabbedEntity != null) {
                    grabbedEntities.remove((Object)waw.grabbedEntity);
                    waw.grabbedEntity.setVelocity(waw.grabbedEntity.getVelocity().multiply(2.5));
                }
                return;
            }
            if (!waw.arm.equals((Object)WaterArms.instances.get((Object)player).getActiveArm())) {
                return;
            }
        }
        this.player = player;
        this.ability = ability;
        this.getAugments();
        this.createInstance();
    }

    private void getAugments() {
        World world;
        if (this.ability.equals((Object)Whip.Punch)) {
            this.whipLength = this.punchLength;
        }
        if (WaterMethods.isNight(world = this.player.getWorld())) {
            this.whipLength = WaterMethods.isFullMoon(world) ? (this.ability.equals((Object)Whip.Punch) ? this.punchLengthFullMoon : this.whipLengthFullMoon) : (this.ability.equals((Object)Whip.Punch) ? this.punchLengthNight : this.whipLengthNight);
        }
    }

    private void createInstance() {
        if (WaterArms.instances.containsKey((Object)this.player)) {
            this.waterArms = WaterArms.instances.get((Object)this.player);
            this.waterArms.switchPreferredArm();
            this.arm = this.waterArms.getActiveArm();
            this.time = System.currentTimeMillis() + this.holdTime;
            this.playerHealth = this.player.getHealth();
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
        if (!this.waterArms.isFullSource().booleanValue()) {
            this.whipLength = this.whipLengthWeak;
        }
        this.id = ID;
        instances.put(this.id, this);
        if (ID == Integer.MAX_VALUE) {
            ID = Integer.MIN_VALUE;
        }
        ++ID;
    }

    private void progress() {
        if (!WaterArms.instances.containsKey((Object)this.player)) {
            this.remove();
            return;
        }
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (!MultiAbilityManager.hasMultiAbilityBound(this.player, "WaterArms")) {
            this.remove();
            return;
        }
        if (this.activeLength < this.whipLength && !this.reverting) {
            this.activeLength += this.whipSpeed;
        } else if (this.activeLength > this.initLength) {
            if (!this.grabbed) {
                this.activeLength -= this.whipSpeed;
            }
        } else {
            this.remove();
            return;
        }
        if (this.activeLength >= this.whipLength && !this.grabbed) {
            this.reverting = true;
        }
        if (this.grabbed && (System.currentTimeMillis() > this.time || this.playerHealth > this.player.getHealth())) {
            this.grabbed = false;
            this.reverting = true;
        }
        this.useArm();
        this.dragEntity(this.end);
        this.grapplePlayer(this.end);
    }

    private boolean canPlaceBlock(Block block) {
        if (!(EarthMethods.isTransparentToEarthbending(this.player, block) || WaterMethods.isWater(block) && TempBlock.isTempBlock(block))) {
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterArms", block.getLocation())) {
            return false;
        }
        return true;
    }

    private void useArm() {
        if (this.waterArms.canDisplayActiveArm()) {
            Location l1 = null;
            l1 = this.arm.equals((Object)WaterArms.Arm.Left) ? this.waterArms.getLeftArmEnd().clone() : this.waterArms.getRightArmEnd().clone();
            Vector dir = this.player.getLocation().getDirection();
            int i = 1;
            while (i <= this.activeLength) {
                Location l2 = l1.clone().add(dir.normalize().multiply(i));
                if (!this.canPlaceBlock(l2.getBlock())) {
                    if (!l2.getBlock().getType().equals((Object)Material.BARRIER)) {
                        this.grappled = true;
                    }
                    this.reverting = true;
                    break;
                }
                new com.projectkorra.projectkorra.util.TempBlock(l2.getBlock(), Material.STATIONARY_WATER,(byte) 8);
                WaterArms.revert.put(l2.getBlock(), 0L);
                if (i == this.activeLength) {
                    Location l3 = null;
                    l3 = this.arm.equals((Object)WaterArms.Arm.Left) ? GeneralMethods.getRightSide(l2, 1.0) : GeneralMethods.getLeftSide(l2, 1.0);
                    this.end = l3.clone();
                    if (this.canPlaceBlock(l3.getBlock())) {
                        new com.projectkorra.projectkorra.util.TempBlock(l3.getBlock(), Material.STATIONARY_WATER, (byte) 3);
                        WaterArms.revert.put(l3.getBlock(), 0L);
                        this.performAction(l3);
                    } else {
                        if (!l3.getBlock().getType().equals((Object)Material.BARRIER)) {
                            this.grappled = true;
                        }
                        this.reverting = true;
                    }
                }
                ++i;
            }
        }
    }

    private void performAction(Location location) {
        Location endOfArm = this.waterArms.getLeftArmEnd().clone();
        block0 : switch (WaterArmsWhip.$SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArmsWhip$Whip()[this.ability.ordinal()]) {
            case 1: {
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 2.0)) {
                    if (entity instanceof Player && Commands.invincible.contains(((Player)entity).getName())) continue;
                    Vector vector = endOfArm.toVector().subtract(entity.getLocation().toVector());
                    entity.setVelocity(vector.multiply(this.pullMultiplier));
                }
                break;
            }
            case 2: {
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 2.0)) {
                    if (entity instanceof Player && Commands.invincible.contains(((Player)entity).getName())) continue;
                    Vector vector = entity.getLocation().toVector().subtract(endOfArm.toVector());
                    entity.setVelocity(vector.multiply(0.15));
                    if (!(entity instanceof LivingEntity) || entity.getEntityId() == this.player.getEntityId()) continue;
                    this.hasDamaged = true;
                    GeneralMethods.damageEntity(this.player, entity, this.punchDamage, Element.Water, "WaterArms Punch");
                }
                break;
            }
            case 3: {
                this.grapplePlayer(this.end);
                break;
            }
            case 4: {
                if (this.grabbedEntity != null) break;
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, 2.0)) {
                    if (!(entity instanceof LivingEntity) || entity.getEntityId() == this.player.getEntityId() || grabbedEntities.containsKey((Object)entity)) continue;
                    grabbedEntities.put((LivingEntity)entity, this.id);
                    this.grabbedEntity = (LivingEntity)entity;
                    this.grabbed = true;
                    this.reverting = true;
                    this.waterArms.setActiveArmCooldown(true);
                    break block0;
                }
            }
        }
    }

    private void dragEntity(Location location) {
        if (this.grabbedEntity != null && this.grabbed) {
            if (!this.waterArms.canDisplayActiveArm() || this.grabbedEntity.isDead()) {
                this.grabbed = false;
                grabbedEntities.remove((Object)this.grabbedEntity);
                return;
            }
            Location newlocation = this.grabbedEntity.getLocation();
            double distance = location.distance(newlocation);
            double dx = location.getX() - newlocation.getX();
            double dy = location.getY() - newlocation.getY();
            double dz = location.getZ() - newlocation.getZ();
            Vector vector = new Vector(dx, dy, dz);
            if (distance > 0.5) {
                this.grabbedEntity.setVelocity(vector.normalize().multiply(0.65));
            } else {
                this.grabbedEntity.setVelocity(new Vector(0, 0, 0));
            }
            this.grabbedEntity.setFallDistance(0.0f);
            if (this.grabbedEntity instanceof Creature) {
                ((Creature)this.grabbedEntity).setTarget(null);
            }
        }
    }

    private void grapplePlayer(Location location) {
        if (this.reverting && this.grappled && this.player != null && this.end != null && this.ability.equals((Object)Whip.Grapple)) {
            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterArms", location) && this.grappleRespectRegions) {
                return;
            }
            Vector vector = this.player.getLocation().toVector().subtract(location.toVector());
            this.player.setVelocity(vector.multiply(-0.25));
            this.player.setFallDistance(0.0f);
        }
    }

    public static Integer getId(Player player) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            if (!WaterArmsWhip.instances.get((Object)Integer.valueOf((int)id)).player.equals((Object)player)) continue;
            return id;
        }
        return 0;
    }

    public static void checkValidEntities() {
        for (LivingEntity e : grabbedEntities.keySet()) {
            if (instances.containsKey(grabbedEntities.get((Object)e))) {
                if (WaterArmsWhip.instances.get((Object)WaterArmsWhip.grabbedEntities.get((Object)e)).grabbedEntity != null) continue;
                grabbedEntities.remove((Object)e);
                continue;
            }
            grabbedEntities.remove((Object)e);
        }
    }

    private void remove() {
        if (WaterArms.instances.containsKey((Object)this.player)) {
            if (this.arm.equals((Object)WaterArms.Arm.Left)) {
                this.waterArms.setLeftArmCooldown(false);
            } else {
                this.waterArms.setRightArmCooldown(false);
            }
            if (this.hasDamaged) {
                this.waterArms.setMaxPunches(this.waterArms.getMaxPunches() - 1);
            }
            this.waterArms.setMaxUses(this.waterArms.getMaxUses() - 1);
        }
        instances.remove(this.id);
    }

    public static void progressAll() {
        WaterArmsWhip.checkValidEntities();
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            instances.get(ID).progress();
        }
    }

    public static void removeAll() {
        grabbedEntities.clear();
        instances.clear();
    }

    public Player getPlayer() {
        return this.player;
    }

    public Integer getWhipLength() {
        return this.whipLength;
    }

    public void setArmLength(int armLength) {
        this.whipLength = armLength;
    }

    public Double getPunchDamage() {
        return this.punchDamage;
    }

    public void setPunchDamage(double damage) {
        this.punchDamage = damage;
    }

    public long getHoldTime() {
        return this.holdTime;
    }

    public void setHoldTime(long holdTime) {
        this.holdTime = holdTime;
    }

    public boolean getReverting() {
        return this.reverting;
    }

    public void setReverting(boolean reverting) {
        this.reverting = reverting;
    }

    public boolean getGrappled() {
        return this.grappled;
    }

    public void setGrappled(boolean grappled) {
        this.grappled = grappled;
    }

    public boolean getGrabbed() {
        return this.grabbed;
    }

    public void setGrabbed(boolean grabbed) {
        this.grabbed = grabbed;
    }

    public LivingEntity getHeldEntity() {
        return this.grabbedEntity;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArmsWhip$Whip() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArmsWhip$Whip;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[Whip.values().length];
        try {
            arrn[Whip.Grab.ordinal()] = 4;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[Whip.Grapple.ordinal()] = 3;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[Whip.Pull.ordinal()] = 1;
        }
        catch (NoSuchFieldError v3) {}
        try {
            arrn[Whip.Punch.ordinal()] = 2;
        }
        catch (NoSuchFieldError v4) {}
        $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArmsWhip$Whip = arrn;
        return $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArmsWhip$Whip;
    }

    public static enum Whip {
        Pull,
        Punch,
        Grapple,
        Grab;
        

    }

}

