/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class WaterWave {
    public static ArrayList<WaterWave> instances = new ArrayList();
    public static ConcurrentHashMap<Block, TempBlock> frozenBlocks = new ConcurrentHashMap();
    public static boolean ICE_ONLY = false;
    public static boolean ENABLED = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Water.WaterSpout.Wave.Enabled");
    public static double RANGE = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.WaterSpout.Wave.Range");
    public static double MAX_SPEED = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.WaterSpout.Wave.Speed");
    public static long CHARGE_TIME = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.WaterSpout.Wave.ChargeTime");
    public static long FLIGHT_TIME = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.WaterSpout.Wave.FlightTime");
    public static long COOLDOWN = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.WaterSpout.Wave.Cooldown");
    public static double WAVE_RADIUS = 1.5;
    public static double ICE_WAVE_DAMAGE = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.WaterCombo.IceWave.Damage");
    private Player player;
    private long time;
    private AbilityType type;
    private Location origin;
    private Location currentLoc;
    private Vector direction;
    private double radius = 3.8;
    private boolean charging = false;
    private boolean iceWave = false;
    private int progressCounter = 0;
    private AnimateState anim;
    private double range = RANGE;
    private double speed = MAX_SPEED;
    private double chargeTime = CHARGE_TIME;
    private double flightTime = FLIGHT_TIME;
    private double waveRadius = WAVE_RADIUS;
    private double damage = ICE_WAVE_DAMAGE;
    private long cooldown = COOLDOWN;
    private ConcurrentHashMap<Block, TempBlock> affectedBlocks = new ConcurrentHashMap();
    private ArrayList<Entity> affectedEntities = new ArrayList();
    private ArrayList<BukkitRunnable> tasks = new ArrayList();

    public WaterWave(Player player, AbilityType type) {
        if (!ENABLED || GeneralMethods.getBendingPlayer(player.getName()).isOnCooldown("WaterWave")) {
            return;
        }
        this.player = player;
        this.time = System.currentTimeMillis();
        this.type = type;
        instances.add(this);
        if (type == AbilityType.CLICK) {
            this.progress();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void progress() {
        ++this.progressCounter;
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (this.origin != null && !this.player.getWorld().equals((Object)this.origin.getWorld())) {
            this.remove();
            return;
        }
        if (this.type != AbilityType.RELEASE) {
            if (!GeneralMethods.canBend(this.player.getName(), "WaterSpout") || !this.player.hasPermission("bending.ability.WaterSpout.Wave")) {
                this.remove();
                return;
            }
            String ability = GeneralMethods.getBoundAbility(this.player);
            if (ability == null || !ability.equalsIgnoreCase("WaterSpout")) {
                this.remove();
                return;
            }
        }
        if (this.type == AbilityType.CLICK) {
            if (this.origin == null) {
                WaterWave.removeType(this.player, AbilityType.CLICK);
                Block block = BlockSource.getWaterSourceBlock(this.player, this.range, ClickType.LEFT_CLICK, true, true, WaterMethods.canPlantbend(this.player));
                if (block == null) {
                    if (!instances.contains(this)) return;
                    this.remove();
                    return;
                }
                instances.add(this);
                Block blockAbove = block.getRelative(BlockFace.UP);
                if (blockAbove.getType() != Material.AIR && !WaterMethods.isWaterbendable(blockAbove, this.player)) {
                    this.remove();
                    return;
                }
                this.origin = block.getLocation();
                if (!WaterMethods.isWaterbendable(block, this.player) || GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterSpout", this.origin)) {
                    this.remove();
                    return;
                }
                if (ICE_ONLY && block.getType() != Material.ICE && block.getType() != Material.SNOW && block.getType() != Material.PACKED_ICE) {
                    this.remove();
                    return;
                }
            }
            if (this.player.getLocation().distance(this.origin) > this.range) {
                this.remove();
                return;
            }
            if (this.player.isSneaking()) {
                new com.projectkorra.projectkorra.waterbending.WaterWave(this.player, AbilityType.SHIFT);
                return;
            }
            WaterMethods.playFocusWaterEffect(this.origin.getBlock());
            return;
        } else if (this.type == AbilityType.SHIFT) {
            if (this.direction == null) {
                this.direction = this.player.getEyeLocation().getDirection();
            }
            if (!this.charging) {
                if (!WaterWave.containsType(this.player, AbilityType.SHIFT)) {
                    WaterWave.removeType(this.player, AbilityType.CLICK);
                    this.remove();
                    return;
                }
                this.charging = true;
                this.anim = AnimateState.RISE;
                if (!WaterWave.getType(this.player, AbilityType.CLICK).isEmpty()) {
                    WaterWave clickSpear = WaterWave.getType(this.player, AbilityType.CLICK).get(0);
                    this.origin = clickSpear.origin.clone();
                    this.currentLoc = this.origin.clone();
                    if (WaterMethods.isPlant(this.origin.getBlock())) {
                        new com.projectkorra.projectkorra.waterbending.Plantbending(this.origin.getBlock());
                    }
                }
            }
            WaterWave.removeType(this.player, AbilityType.CLICK);
            if (!this.player.isSneaking()) {
                if ((double)(System.currentTimeMillis() - this.time) > this.chargeTime) {
                    WaterWave wwave = new WaterWave(this.player, AbilityType.RELEASE);
                    wwave.anim = AnimateState.SHRINK;
                    wwave.direction = this.direction;
                }
                this.remove();
                return;
            }
            double animSpeed = 1.2;
            if (this.anim == AnimateState.RISE && this.currentLoc != null) {
                this.revertBlocks();
                this.currentLoc.add(0.0, animSpeed, 0.0);
                Block block = this.currentLoc.getBlock();
                if (!WaterMethods.isWaterbendable(block, this.player) && block.getType() != Material.AIR || GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterSpout", block.getLocation())) {
                    this.remove();
                    return;
                }
                this.createBlock(block, Material.STATIONARY_WATER);
                if (this.currentLoc.distance(this.origin) <= 2.0) return;
                this.anim = AnimateState.TOWARDPLAYER;
                return;
            } else if (this.anim == AnimateState.TOWARDPLAYER) {
                this.revertBlocks();
                Location eyeLoc = player.getTargetBlock((HashSet<Material>) null, 2).getLocation();
                eyeLoc.setY(this.player.getEyeLocation().getY());
                Vector vec = GeneralMethods.getDirection(this.currentLoc, eyeLoc);
                this.currentLoc.add(vec.normalize().multiply(animSpeed));
                Block block = this.currentLoc.getBlock();
                if (!WaterMethods.isWaterbendable(block, this.player) && block.getType() != Material.AIR || GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterSpout", block.getLocation())) {
                    this.remove();
                    return;
                }
                this.createBlock(block, Material.STATIONARY_WATER);
                if (this.currentLoc.distance(eyeLoc) >= 1.3) return;
                this.anim = AnimateState.CIRCLE;
                Vector tempDir = this.player.getLocation().getDirection();
                tempDir.setY(0);
                this.direction = tempDir.normalize();
                this.revertBlocks();
                return;
            } else {
                if (this.anim != AnimateState.CIRCLE) return;
                this.drawCircle(120.0, 5.0);
            }
            return;
        } else {
            if (this.type != AbilityType.RELEASE) return;
            if (this.anim == AnimateState.SHRINK) {
                this.radius -= 0.2;
                this.drawCircle(360.0, 15.0);
                if (this.radius >= 1.0) return;
                this.revertBlocks();
                this.time = System.currentTimeMillis();
                this.anim = null;
                return;
            } else {
                if ((double)(System.currentTimeMillis() - this.time) > this.flightTime && !AvatarState.isAvatarState(this.player) || this.player.isSneaking()) {
                    this.remove();
                    return;
                }
                this.player.setFallDistance(0.0f);
                double currentSpeed = this.speed - this.speed * (double)(System.currentTimeMillis() - this.time) / this.flightTime;
                double nightSpeed = WaterMethods.waterbendingNightAugment(currentSpeed * 0.9, this.player.getWorld());
                double d = currentSpeed = nightSpeed > currentSpeed ? nightSpeed : currentSpeed;
                if (AvatarState.isAvatarState(this.player)) {
                    currentSpeed = WaterMethods.waterbendingNightAugment(this.speed, this.player.getWorld());
                }
                this.player.setVelocity(this.player.getEyeLocation().getDirection().normalize().multiply(currentSpeed));
                for (Block block : GeneralMethods.getBlocksAroundPoint(this.player.getLocation().add(0.0, -1.0, 0.0), this.waveRadius)) {
                    if (block.getType() != Material.AIR || GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterSpout", block.getLocation())) continue;
                    if (this.iceWave) {
                        this.createBlockDelay(block, Material.ICE, (byte)0, 2);
                        continue;
                    }
                    this.createBlock(block, Material.STATIONARY_WATER, (byte)0);
                }
                this.revertBlocksDelay(20);
                if (!this.iceWave || this.progressCounter % 3 != 0) return;
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.player.getLocation().add(0.0, -1.0, 0.0), this.waveRadius * 1.5)) {
                    if (entity == this.player || !(entity instanceof LivingEntity) || this.affectedEntities.contains((Object)entity)) continue;
                    this.affectedEntities.add(entity);
                    final double aug = WaterMethods.getWaterbendingNightAugment(this.player.getWorld());
                    GeneralMethods.damageEntity(this.player, entity, aug * this.damage, Element.Water, "WaterWave");
                    final Player fplayer = this.player;
                    final Entity fent = entity;
                    new BukkitRunnable(){

                        public void run() {
                            WaterWave.this.createIceSphere(fplayer, fent, aug * 2.5);
                        }
                    }.runTaskLater((Plugin)ProjectKorra.plugin, 6);
                }
            }
        }
    }

    public void drawCircle(double theta, double increment) {
        double rotateSpeed = 45.0;
        this.revertBlocks();
        this.direction = GeneralMethods.rotateXZ(this.direction, rotateSpeed);
        double i = 0.0;
        while (i < theta) {
            Vector dir = GeneralMethods.rotateXZ(this.direction, i - theta / 2.0).normalize().multiply(this.radius);
            dir.setY(0);
            Block block = this.player.getEyeLocation().add(dir).getBlock();
            this.currentLoc = block.getLocation();
            if (block.getType() == Material.AIR && !GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterSpout", block.getLocation())) {
                this.createBlock(block, Material.STATIONARY_WATER,(byte) 8);
            }
            i += increment;
        }
    }

    public void remove() {
        instances.remove(this);
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(this.player.getName());
        if (bPlayer != null) {
            bPlayer.addCooldown("WaterWave", this.cooldown);
        }
        this.revertBlocks();
        for (BukkitRunnable task : this.tasks) {
            task.cancel();
        }
    }

    public void createBlockDelay(final Block block, final Material mat, final byte data, long delay) {
        BukkitRunnable br = new BukkitRunnable(){

            public void run() {
                WaterWave.this.createBlock(block, mat, data);
            }
        };
        br.runTaskLater((Plugin)ProjectKorra.plugin, delay);
        this.tasks.add(br);
    }

    public void createBlock(Block block, Material mat) {
        this.createBlock(block, mat, (byte)0);
    }

    public void createBlock(Block block, Material mat, byte data) {
        this.affectedBlocks.put(block, new TempBlock(block, mat, data));
    }

    public void revertBlocks() {
        Enumeration<Block> keys = this.affectedBlocks.keys();
        while (keys.hasMoreElements()) {
            Block block = keys.nextElement();
            this.affectedBlocks.get((Object)block).revertBlock();
            this.affectedBlocks.remove((Object)block);
        }
    }

    public void revertBlocksDelay(long delay) {
        Enumeration<Block> keys = this.affectedBlocks.keys();
        while (keys.hasMoreElements()) {
            final Block block = keys.nextElement();
            final TempBlock tblock = this.affectedBlocks.get((Object)block);
            this.affectedBlocks.remove((Object)block);
            new BukkitRunnable(){

                public void run() {
                    if (!WaterWave.frozenBlocks.containsKey((Object)block)) {
                        tblock.revertBlock();
                    }
                }
            }.runTaskLater((Plugin)ProjectKorra.plugin, delay);
        }
    }

    public void createIceSphere(Player player, Entity entity, double radius) {
        double x = - radius;
        while (x <= radius) {
            double y = - radius;
            while (y <= radius) {
                double z = - radius;
                while (z <= radius) {
                    Block block = entity.getLocation().getBlock().getLocation().add(x, y, z).getBlock();
                    if (block.getLocation().distance(entity.getLocation().getBlock().getLocation()) <= radius && (block.getType() == Material.AIR || block.getType() == Material.ICE || WaterMethods.isWaterbendable(block, player)) && !frozenBlocks.containsKey((Object)block)) {
                        TempBlock tblock = new TempBlock(block, Material.ICE,(byte) 1);
                        frozenBlocks.put(block, tblock);
                    }
                    z += 0.5;
                }
                y += 0.5;
            }
            x += 0.5;
        }
    }

    public static void progressAll() {
        int i = 0;
        while (i < instances.size()) {
            instances.get(i).progress();
            ++i;
        }
    }

    public static void removeAll() {
        int i = 0;
        while (i < instances.size()) {
            instances.get(i).remove();
            --i;
            ++i;
        }
    }

    public static boolean containsType(Player player, AbilityType type) {
        int i = 0;
        while (i < instances.size()) {
            WaterWave wave = instances.get(i);
            if (wave.player.equals((Object)player) && wave.type.equals((Object)type)) {
                return true;
            }
            ++i;
        }
        return false;
    }

    public static void removeType(Player player, AbilityType type) {
        int i = 0;
        while (i < instances.size()) {
            WaterWave wave = instances.get(i);
            if (wave.player.equals((Object)player) && wave.type.equals((Object)type)) {
                instances.remove(i);
                --i;
            }
            ++i;
        }
    }

    public static ArrayList<WaterWave> getType(Player player, AbilityType type) {
        ArrayList<WaterWave> list = new ArrayList<WaterWave>();
        for (WaterWave spear : instances) {
            if (!spear.player.equals((Object)player) || !spear.type.equals((Object)type)) continue;
            list.add(spear);
        }
        return list;
    }

    public static boolean wasBrokenFor(Player player, Block block) {
        if (WaterWave.containsType(player, AbilityType.CLICK)) {
            WaterWave wwave = WaterWave.getType(player, AbilityType.CLICK).get(0);
            if (wwave.origin == null) {
                return false;
            }
            if (wwave.origin.getBlock().equals((Object)block)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canThaw(Block block) {
        return frozenBlocks.containsKey((Object)block);
    }

    public static void thaw(Block block) {
        if (frozenBlocks.containsKey((Object)block)) {
            frozenBlocks.get((Object)block).revertBlock();
            frozenBlocks.remove((Object)block);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getChargeTime() {
        return this.chargeTime;
    }

    public void setChargeTime(double chargeTime) {
        this.chargeTime = chargeTime;
    }

    public double getFlightTime() {
        return this.flightTime;
    }

    public void setFlightTime(double flightTime) {
        this.flightTime = flightTime;
    }

    public double getWaveRadius() {
        return this.waveRadius;
    }

    public void setWaveRadius(double waveRadius) {
        this.waveRadius = waveRadius;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setIceWave(boolean b) {
        this.iceWave = b;
    }

    public boolean isIceWave() {
        return this.iceWave;
    }

    public static enum AbilityType {
        CLICK,
        SHIFT,
        RELEASE;
        

    }

    public static enum AnimateState {
        RISE,
        TOWARDPLAYER,
        CIRCLE,
        SHRINK;
        

    }

}

