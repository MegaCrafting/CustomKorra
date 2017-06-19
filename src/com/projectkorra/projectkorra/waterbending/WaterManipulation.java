/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
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
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.Combustion;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.FreezeMelt;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterReturn;
import com.projectkorra.projectkorra.waterbending.WaterSpout;
import com.projectkorra.projectkorra.waterbending.WaterWall;
import com.projectkorra.projectkorra.waterbending.Wave;
import com.projectkorra.projectkorra.util.ClickType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WaterManipulation {
    private static FileConfiguration config = ProjectKorra.plugin.getConfig();
    public static ConcurrentHashMap<Integer, WaterManipulation> instances = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap();
    public static ConcurrentHashMap<Player, Integer> prepared = new ConcurrentHashMap();
    static double RANGE = config.getDouble("Abilities.Water.WaterManipulation.Range");
    private static double PUSH_FACTOR = config.getDouble("Abilities.Water.WaterManipulation.Push");
    private static double defaultdamage = config.getDouble("Abilities.Water.WaterManipulation.Damage");
    private static double speed = config.getDouble("Abilities.Water.WaterManipulation.Speed");
    private static long COOLDOWN = config.getLong("Abilities.Water.WaterManipulation.Cooldown");
    private static long interval = (long)(1000.0 / speed);
    private static final double deflectrange = 3.0;
    private static int ID = Integer.MIN_VALUE;
    private static final byte full = 0;
    private static HashSet<Byte> water = new HashSet();
    Player player;
    private long time;
    private double damage = defaultdamage;
    private int displrange;
    private int id;
    private Location location = null;
    private Block sourceblock = null;
    private TempBlock trail;
    private TempBlock trail2;
    private Location firstdestination = null;
    private Location targetdestination = null;
    private Vector firstdirection = null;
    private Vector targetdirection = null;
    private boolean progressing = false;
    private boolean falling = false;
    private boolean settingup = false;
    private final boolean displacing = false;
    private double range = RANGE;
    private double pushfactor = PUSH_FACTOR;
    private long cooldown = COOLDOWN;

    public WaterManipulation(Player player) {
        if (water.isEmpty()) {
            water.add(Byte.valueOf((byte)0));
            water.add(Byte.valueOf((byte)8));
            water.add(Byte.valueOf((byte)9));
        }
        this.player = player;
        if (this.prepare()) {
            this.id = ID;
            instances.put(this.id, this);
            prepared.put(player, this.id);
            if (ID == Integer.MAX_VALUE) {
                ID = Integer.MIN_VALUE;
            }
            ++ID;
            this.time = System.currentTimeMillis();
        }
    }

    public boolean prepare() {
        Block block = BlockSource.getWaterSourceBlock(this.player, this.range, ClickType.SHIFT_DOWN, true, true, WaterMethods.canPlantbend(this.player));
        this.cancelPrevious();
        WaterManipulation.block(this.player);
        if (block != null) {
            this.sourceblock = block;
            this.focusBlock();
            return true;
        }
        return false;
    }

    private void cancelPrevious() {
        if (prepared.containsKey((Object)this.player) && instances.containsKey(prepared.get((Object)this.player))) {
            WaterManipulation old = instances.get(prepared.get((Object)this.player));
            if (!old.progressing) {
                old.cancel();
            }
        }
    }

    public void cancel() {
        this.unfocusBlock();
    }

    private void focusBlock() {
        this.location = this.sourceblock.getLocation();
    }

    private void unfocusBlock() {
        WaterManipulation.remove(this.id);
    }

    public void moveWater() {
        if (this.sourceblock != null) {
            if (this.sourceblock.getWorld().equals((Object)this.player.getWorld())) {
                this.targetdestination = WaterManipulation.getTargetLocation(this.player, this.range);
                if (this.targetdestination.distance(this.location) <= 1.0) {
                    this.progressing = false;
                    this.targetdestination = null;
                    WaterManipulation.remove(this.id);
                } else {
                    this.progressing = true;
                    this.settingup = true;
                    this.firstdestination = this.getToEyeLevel();
                    this.firstdirection = GeneralMethods.getDirection(this.sourceblock.getLocation(), this.firstdestination).normalize();
                    this.targetdestination = GeneralMethods.getPointOnLine(this.firstdestination, this.targetdestination, this.range);
                    this.targetdirection = GeneralMethods.getDirection(this.firstdestination, this.targetdestination).normalize();
                    if (WaterMethods.isPlant(this.sourceblock)) {
                        new com.projectkorra.projectkorra.waterbending.Plantbending(this.sourceblock);
                    }
                    WaterManipulation.addWater(this.sourceblock);
                }
            }
            GeneralMethods.getBendingPlayer(this.player.getName()).addCooldown("WaterManipulation", GeneralMethods.getGlobalCooldown());
        }
    }

    private static Location getTargetLocation(Player player) {
        return WaterManipulation.getTargetLocation(player, RANGE);
    }

    private static Location getTargetLocation(Player player, double range) {
        Entity target = GeneralMethods.getTargetedEntity(player, range, new ArrayList<Entity>());
        Location location = target == null ? GeneralMethods.getTargetedLocation(player, range, EarthMethods.transparentToEarthbending) : ((LivingEntity)target).getEyeLocation();
        return location;
    }

    private Location getToEyeLevel() {
        Location loc = this.sourceblock.getLocation().clone();
        double dy = this.targetdestination.getY() - (double)this.sourceblock.getY();
        if (dy <= 2.0) {
            loc.setY((double)(this.sourceblock.getY() + 2));
        } else {
            loc.setY(this.targetdestination.getY() - 1.0);
        }
        return loc;
    }

    private static void remove(int id) {
        Player player = WaterManipulation.instances.get((Object)Integer.valueOf((int)id)).player;
        if (prepared.containsKey((Object)player) && prepared.get((Object)player) == id) {
            prepared.remove((Object)player);
        }
        instances.remove(id);
    }

    private void redirect(Player player, Location targetlocation) {
        if (this.progressing && !this.settingup) {
            if (this.location.distance(player.getLocation()) <= this.range) {
                this.targetdirection = GeneralMethods.getDirection(this.location, targetlocation).normalize();
            }
            this.targetdestination = targetlocation;
            this.player = player;
        }
    }

    public static void progressAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            if (instances.get(ID) == null) {
                instances.remove(ID);
                continue;
            }
            instances.get(ID).progress();
        }
    }

    private boolean progress() {
        if (this.player.isDead() || !this.player.isOnline() || !GeneralMethods.canBend(this.player.getName(), "WaterManipulation")) {
            this.breakBlock();
            return false;
        }
        if (System.currentTimeMillis() - this.time >= interval) {
            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterManipulation", this.location)) {
                this.breakBlock();
                return false;
            }
            this.time = System.currentTimeMillis();
            if (GeneralMethods.getBoundAbility(this.player) == null) {
                this.breakBlock();
                return false;
            }
            if (!(this.progressing || this.falling || GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("WaterManipulation"))) {
                this.unfocusBlock();
                return false;
            }
            if (this.falling) {
                this.breakBlock();
                new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, this.sourceblock);
                return false;
            }
            if (!this.progressing) {
                this.sourceblock.getWorld().playEffect(this.location, Effect.SMOKE, 4, (int)this.range);
                return false;
            }
            if (this.sourceblock.getLocation().distance(this.firstdestination) < 0.5) {
                this.settingup = false;
            }
            Vector direction = this.settingup ? this.firstdirection : this.targetdirection;
            Block block = this.location.getBlock();
            WaterMethods.removeWaterSpouts(this.location, this.player);
            AirMethods.removeAirSpouts(this.location, this.player);
            if (GeneralMethods.rand.nextInt(4) == 0) {
                WaterMethods.playWaterbendingSound(this.location);
            }
            double radius = FireBlast.AFFECTING_RADIUS;
            Player source = this.player;
            if (this.location != null) {
                if (EarthBlast.annihilateBlasts(this.location, radius, source) || WaterManipulation.annihilateBlasts(this.location, radius, source) || FireBlast.annihilateBlasts(this.location, radius, source)) {
                    this.breakBlock();
                    new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, this.sourceblock);
                    return false;
                }
                Combustion.removeAroundPoint(this.location, radius);
            }
            this.location = this.location.clone().add(direction);
            block = this.location.getBlock();
            if (block.getLocation().equals((Object)this.sourceblock.getLocation())) {
                this.location = this.location.clone().add(direction);
                block = this.location.getBlock();
            }
            if (this.trail2 != null && this.trail2.getBlock().equals((Object)block)) {
                this.trail2.revertBlock();
                this.trail2 = null;
            }
            if (this.trail != null && this.trail.getBlock().equals((Object)block)) {
                this.trail.revertBlock();
                this.trail = null;
                if (this.trail2 != null) {
                    this.trail2.revertBlock();
                    this.trail2 = null;
                }
            }
            if (EarthMethods.isTransparentToEarthbending(this.player, block) && !block.isLiquid()) {
                GeneralMethods.breakBlock(block);
            } else if (block.getType() != Material.AIR && !WaterMethods.isWater(block)) {
                this.breakBlock();
                new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, this.sourceblock);
                return false;
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, FireBlast.AFFECTING_RADIUS)) {
                if (!(entity instanceof LivingEntity) || entity.getEntityId() == this.player.getEntityId()) continue;
                Location location = this.player.getEyeLocation();
                Vector vector = location.getDirection();
                entity.setVelocity(vector.normalize().multiply(this.pushfactor));
                if (AvatarState.isAvatarState(this.player)) {
                    this.damage = AvatarState.getValue(this.damage);
                }
                GeneralMethods.damageEntity(this.player, entity, (int)WaterMethods.waterbendingNightAugment(this.damage, this.player.getWorld()), "WaterManipulation");
                AirMethods.breakBreathbendingHold(entity);
                this.progressing = false;
            }
            if (!this.progressing) {
                this.breakBlock();
                new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, this.sourceblock);
                return false;
            }
            WaterManipulation.addWater(block);
            this.reduceWater(this.sourceblock);
            if (this.trail2 != null) {
                this.trail2.revertBlock();
                this.trail2 = null;
            }
            if (this.trail != null) {
                this.trail2 = this.trail;
                this.trail2.setType(Material.STATIONARY_WATER, (byte)2);
            }
            this.trail = new TempBlock(this.sourceblock, Material.STATIONARY_WATER, (byte)1);
            this.sourceblock = block;
            if (this.location.distance(this.targetdestination) <= 1.0 || this.location.distance(this.firstdestination) > this.range) {
                this.falling = true;
                this.progressing = false;
            }
            return true;
        }
        return false;
    }

    private void breakBlock() {
        this.finalRemoveWater(this.sourceblock);
        WaterManipulation.remove(this.id);
    }

    private void reduceWater(Block block) {
        if (affectedblocks.containsKey((Object)block)) {
            if (!GeneralMethods.isAdjacentToThreeOrMoreSources(block)) {
                block.setType(Material.AIR);
            }
            affectedblocks.remove((Object)block);
        }
    }

    private void removeWater(Block block) {
        if (block != null && affectedblocks.containsKey((Object)block)) {
            if (!GeneralMethods.isAdjacentToThreeOrMoreSources(block)) {
                block.setType(Material.AIR);
            }
            affectedblocks.remove((Object)block);
        }
    }

    private void finalRemoveWater(Block block) {
        if (this.trail != null) {
            this.trail.revertBlock();
            this.trail = null;
        }
        if (this.trail2 != null) {
            this.trail2.revertBlock();
            this.trail = null;
        }
        if (affectedblocks.containsKey((Object)block)) {
            if (!GeneralMethods.isAdjacentToThreeOrMoreSources(block)) {
                block.setType(Material.AIR);
            }
            affectedblocks.remove((Object)block);
        }
    }

    private static void addWater(Block block) {
        if (!affectedblocks.containsKey((Object)block)) {
            affectedblocks.put(block, block);
        }
        if (FreezeMelt.frozenblocks.containsKey((Object)block)) {
            FreezeMelt.frozenblocks.remove((Object)block);
        }
        if (WaterMethods.isWater(block)) {
            ParticleEffect.WATER_BUBBLE.display((float)Math.random(), (float)Math.random(), (float)Math.random(), 0.0f, 5, block.getLocation().clone().add(0.5, 0.5, 0.5), 257.0);
        }
        block.setType(Material.STATIONARY_WATER);
        block.setData((byte)0);
    }

    public static void moveWater(Player player) {
        Location eyeloc;
        Block block;
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("WaterManipulation")) {
            return;
        }
        if (prepared.containsKey((Object)player)) {
            if (instances.containsKey(prepared.get((Object)player))) {
                instances.get(prepared.get((Object)player)).moveWater();
            }
            prepared.remove((Object)player);
        } else if (WaterReturn.hasWaterBottle(player) && EarthMethods.isTransparentToEarthbending(player, block = (eyeloc = player.getEyeLocation()).add(eyeloc.getDirection().normalize()).getBlock()) && EarthMethods.isTransparentToEarthbending(player, eyeloc.getBlock()) && WaterManipulation.getTargetLocation(player).distance(block.getLocation()) > 1.0) {
            block.setType(Material.WATER);
            block.setData((byte)0);
            WaterManipulation watermanip = new WaterManipulation(player);
            watermanip.moveWater();
            if (!watermanip.progressing) {
                block.setType(Material.AIR);
            } else {
                WaterReturn.emptyWaterBottle(player);
            }
        }
        WaterManipulation.redirectTargettedBlasts(player);
    }

    private static void redirectTargettedBlasts(Player player) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            WaterManipulation manip = instances.get(id);
            if (!manip.progressing || !manip.location.getWorld().equals((Object)player.getWorld()) || GeneralMethods.isRegionProtectedFromBuild(player, "WaterManipulation", manip.location)) continue;
            if (manip.player.equals((Object)player)) {
                manip.redirect(player, WaterManipulation.getTargetLocation(player));
            }
            Location location = player.getEyeLocation();
            Vector vector = location.getDirection();
            Location mloc = manip.location;
            if (mloc.distance(location) > manip.range || GeneralMethods.getDistanceFromLine(vector, location, manip.location) >= 3.0 || mloc.distance(location.clone().add(vector)) >= mloc.distance(location.clone().add(vector.clone().multiply(-1)))) continue;
            manip.redirect(player, WaterManipulation.getTargetLocation(player));
        }
    }

    private static void block(Player player) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            WaterManipulation manip = instances.get(id);
            if (manip.player.equals((Object)player) || !manip.location.getWorld().equals((Object)player.getWorld()) || !manip.progressing || GeneralMethods.isRegionProtectedFromBuild(player, "WaterManipulation", manip.location)) continue;
            Location location = player.getEyeLocation();
            Vector vector = location.getDirection();
            Location mloc = manip.location;
            if (mloc.distance(location) > manip.range || GeneralMethods.getDistanceFromLine(vector, location, manip.location) >= 3.0 || mloc.distance(location.clone().add(vector)) >= mloc.distance(location.clone().add(vector.clone().multiply(-1)))) continue;
            manip.breakBlock();
        }
    }

    public static boolean progress(int ID) {
        if (instances.containsKey(ID)) {
            return instances.get(ID).progress();
        }
        return false;
    }

    public static boolean canFlowFromTo(Block from, Block to) {
        if (affectedblocks.containsKey((Object)to) || affectedblocks.containsKey((Object)from)) {
            return false;
        }
        if (WaterSpout.affectedblocks.containsKey((Object)to) || WaterSpout.affectedblocks.containsKey((Object)from)) {
            return false;
        }
        if (WaterWall.affectedblocks.containsKey((Object)to) || WaterWall.affectedblocks.containsKey((Object)from)) {
            return false;
        }
        if (WaterWall.wallblocks.containsKey((Object)to) || WaterWall.wallblocks.containsKey((Object)from)) {
            return false;
        }
        if (Wave.isBlockWave(to) || Wave.isBlockWave(from)) {
            return false;
        }
        if (TempBlock.isTempBlock(to) || TempBlock.isTempBlock(from)) {
            return false;
        }
        if (WaterMethods.isAdjacentToFrozenBlock(to) || WaterMethods.isAdjacentToFrozenBlock(from)) {
            return false;
        }
        return true;
    }

    public static boolean canPhysicsChange(Block block) {
        if (affectedblocks.containsKey((Object)block)) {
            return false;
        }
        if (WaterSpout.affectedblocks.containsKey((Object)block)) {
            return false;
        }
        if (WaterWall.affectedblocks.containsKey((Object)block)) {
            return false;
        }
        if (WaterWall.wallblocks.containsKey((Object)block)) {
            return false;
        }
        if (Wave.isBlockWave(block)) {
            return false;
        }
        if (TempBlock.isTempBlock(block)) {
            return false;
        }
        if (TempBlock.isTouchingTempBlock(block)) {
            return false;
        }
        return true;
    }

    public static void removeAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            instances.get(id).breakBlock();
        }
        prepared.clear();
    }

    public static boolean canBubbleWater(Block block) {
        return WaterManipulation.canPhysicsChange(block);
    }

    public static void removeAroundPoint(Location location, double radius) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            WaterManipulation manip = instances.get(id);
            if (!manip.location.getWorld().equals((Object)location.getWorld()) || manip.location.distance(location) > radius) continue;
            manip.breakBlock();
        }
    }

    public static ArrayList<WaterManipulation> getAroundPoint(Location location, double radius) {
        ArrayList<WaterManipulation> list = new ArrayList<WaterManipulation>();
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            WaterManipulation manip = instances.get(id);
            if (!manip.location.getWorld().equals((Object)location.getWorld()) || manip.location.distance(location) > radius) continue;
            list.add(manip);
        }
        return list;
    }

    public static boolean annihilateBlasts(Location location, double radius, Player source) {
        boolean broke = false;
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            WaterManipulation manip = instances.get(id);
            if (!manip.location.getWorld().equals((Object)location.getWorld()) || source.equals((Object)manip.player) || manip.location.distance(location) > radius) continue;
            manip.breakBlock();
            broke = true;
        }
        return broke;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getPushfactor() {
        return this.pushfactor;
    }

    public void setPushfactor(double pushfactor) {
        this.pushfactor = pushfactor;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
        if (this.player != null) {
            GeneralMethods.getBendingPlayer(this.player.getName()).addCooldown("WaterManipulation", cooldown);
        }
    }
}

