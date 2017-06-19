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
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireCombo;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterSourceGrabber;
import com.projectkorra.projectkorra.waterbending.WaterWave;
import com.projectkorra.projectkorra.util.ClickType;

import java.util.ArrayList;
import java.util.Enumeration;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class WaterCombo {
    private static boolean enabled = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Water.WaterCombo.Enabled");
    public static long ICE_WAVE_COOLDOWN = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.WaterCombo.IceWave.Cooldown");
    public static double ICE_PILLAR_HEIGHT = 8.0;
    public static double ICE_PILLAR_RADIUS = 1.5;
    public static double ICE_PILLAR_DAMAGE = 4.0;
    public static double ICE_PILLAR_RANGE = 10.0;
    public static long ICE_PILLAR_COOLDOWN = 500;
    public static double ICE_BULLET_RADIUS = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.WaterCombo.IceBullet.Radius");
    public static double ICE_BULLET_DAMAGE = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.WaterCombo.IceBullet.Damage");
    public static double ICE_BULLET_RANGE = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.WaterCombo.IceBullet.Range");
    public static double ICE_BULLET_ANIM_SPEED = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.WaterCombo.IceBullet.AnimationSpeed");
    public static int ICE_BULLET_MAX_SHOTS = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.WaterCombo.IceBullet.MaxShots");
    public static long ICE_BULLET_COOLDOWN = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.WaterCombo.IceBullet.Cooldown");
    public static long ICE_BULLET_SHOOT_TIME = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.WaterCombo.IceBullet.ShootTime");
    public static ArrayList<WaterCombo> instances = new ArrayList();
    public static ConcurrentHashMap<Block, TempBlock> frozenBlocks = new ConcurrentHashMap();
    private Player player;
    private BendingPlayer bplayer;
    private String ability;
    private long time;
    private Location origin;
    private Location currentLoc;
    private Location destination;
    private Vector direction;
    private AbilityState state;
    private int progressCounter = 0;
    private int leftClicks = 0;
    private int rightClicks = 0;
    private double damage = 0.0;
    private double speed = 0.0;
    private double range = 0.0;
    private double knockback = 0.0;
    private double radius = 0.0;
    private double shootTime = 0.0;
    private double maxShots = 0.0;
    private double shots = 0.0;
    private long cooldown = 0;
    private WaterSourceGrabber waterGrabber;
    private ArrayList<Entity> affectedEntities = new ArrayList();
    private ArrayList<BukkitRunnable> tasks = new ArrayList();
    private ConcurrentHashMap<Block, TempBlock> affectedBlocks = new ConcurrentHashMap();

    public WaterCombo(Player player, String ability) {
        if (!enabled) {
            return;
        }
        if (!GeneralMethods.getBendingPlayer(player.getName()).hasElement(Element.Water)) {
            return;
        }
        if (Commands.isToggledForAll) {
            return;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "WaterManipulation", player.getLocation())) {
            return;
        }
        if (!GeneralMethods.getBendingPlayer(player.getName()).isToggled()) {
            return;
        }
        this.time = System.currentTimeMillis();
        this.player = player;
        this.ability = ability;
        this.bplayer = GeneralMethods.getBendingPlayer(player.getName());
        if (!GeneralMethods.canBend(player.getName(), ability)) {
            return;
        }
        if (ability.equalsIgnoreCase("IceWave")) {
            this.cooldown = ICE_WAVE_COOLDOWN;
        } else if (ability.equalsIgnoreCase("IcePillar")) {
            this.damage = ICE_PILLAR_DAMAGE;
            this.range = ICE_PILLAR_RANGE;
            this.radius = ICE_PILLAR_RADIUS;
            this.cooldown = ICE_WAVE_COOLDOWN;
        } else if (ability.equalsIgnoreCase("IceBullet")) {
            this.damage = ICE_BULLET_DAMAGE;
            this.range = ICE_BULLET_RANGE;
            this.radius = ICE_BULLET_RADIUS;
            this.cooldown = ICE_BULLET_COOLDOWN;
            this.shootTime = ICE_BULLET_SHOOT_TIME;
            this.maxShots = ICE_BULLET_MAX_SHOTS;
            this.speed = 1.0;
        }
        double aug = WaterMethods.getWaterbendingNightAugment(player.getWorld());
        if (aug > 1.0) {
            aug = 1.0 + (aug - 1.0) / 3.0;
        }
        this.damage *= aug;
        this.range *= aug;
        this.shootTime *= aug;
        this.maxShots *= aug;
        this.radius *= aug;
        if (AvatarState.isAvatarState(player)) {
            this.cooldown = 0;
            this.damage = AvatarState.getValue(this.damage);
            this.range = AvatarState.getValue(this.range);
            this.shootTime = AvatarState.getValue(this.shootTime);
            this.maxShots = AvatarState.getValue(this.maxShots);
            this.knockback *= 1.3;
        }
        if (ability.equalsIgnoreCase("IceBulletLeftClick") || ability.equalsIgnoreCase("IceBulletRightClick")) {
            ArrayList<WaterCombo> bullets = WaterCombo.getWaterCombo(player, "IceBullet");
            if (bullets.size() == 0) {
                return;
            }
            for (WaterCombo bullet : bullets) {
                if (ability.equalsIgnoreCase("IceBulletLeftClick")) {
                    if (bullet.leftClicks > bullet.rightClicks) continue;
                    ++bullet.leftClicks;
                    continue;
                }
                if (bullet.leftClicks < bullet.rightClicks) continue;
                ++bullet.rightClicks;
            }
        }
        instances.add(this);
    }

    public void progress() {
        ++this.progressCounter;
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (this.ability.equalsIgnoreCase("IceWave")) {
            if (this.origin == null && WaterWave.containsType(this.player, WaterWave.AbilityType.RELEASE)) {
                if (this.bplayer.isOnCooldown("IceWave") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.bplayer.addCooldown("IceWave", this.cooldown);
                this.origin = this.player.getLocation();
                WaterWave wave = WaterWave.getType(this.player, WaterWave.AbilityType.RELEASE).get(0);
                wave.setIceWave(true);
            } else if (!WaterWave.containsType(this.player, WaterWave.AbilityType.RELEASE)) {
                this.remove();
                return;
            }
        } else if (this.ability.equalsIgnoreCase("IcePillar")) {
            if (this.progressCounter > 0) {
                this.remove();
                return;
            }
            if (this.origin == null) {
                if (this.bplayer.isOnCooldown("IcePillar") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.origin = this.player.getLocation();
                Entity ent = GeneralMethods.getTargetedEntity(this.player, this.range, new ArrayList<Entity>());
                if (ent == null || !(ent instanceof LivingEntity)) {
                    this.remove();
                    return;
                }
                Location startingLoc = GeneralMethods.getTopBlock(ent.getLocation().add(0.0, -1.0, 0.0), (int)this.range).getLocation();
                if (startingLoc == null) {
                    this.remove();
                    return;
                }
                startingLoc.setX(ent.getLocation().getX());
                startingLoc.setZ(ent.getLocation().getZ());
                int badBlocks = 0;
                double x = - this.radius;
                while (x <= this.radius) {
                    double z = - this.radius;
                    while (z <= this.radius) {
                        Block block;
                        Location tmpLoc = startingLoc.clone().add(x, 0.0, z);
                        if (tmpLoc.distance(startingLoc) <= this.radius && !WaterMethods.isWaterbendable(block = GeneralMethods.getTopBlock(tmpLoc, (int)this.range, (int)this.range), this.player)) {
                            ++badBlocks;
                        }
                        z += 1.0;
                    }
                    x += 1.0;
                }
                if (badBlocks > 5) {
                    this.remove();
                    return;
                }
                this.origin = startingLoc;
                this.currentLoc = this.origin.clone();
                this.state = AbilityState.ICE_PILLAR_RISING;
                this.bplayer.addCooldown("IcePillar", this.cooldown);
            } else if (this.state == AbilityState.ICE_PILLAR_RISING) {
                if (Math.abs(this.currentLoc.distance(this.origin)) > ICE_PILLAR_HEIGHT) {
                    this.remove();
                    return;
                }
                double x = - this.radius;
                while (x <= this.radius) {
                    double z = - this.radius;
                    while (z <= this.radius) {
                        Block block = this.currentLoc.clone().add(x, 0.0, z).getBlock();
                        if ((!WaterMethods.isWaterbendable(block, this.player) && block.getType() != Material.AIR || block.getLocation().distance(this.currentLoc) <= this.radius) && !GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterManipulation", block.getLocation())) {
                            TempBlock tblock = new TempBlock(block, Material.ICE, (byte)0);
                            frozenBlocks.put(block, tblock);
                        }
                        z += 1.0;
                    }
                    x += 1.0;
                }
                this.currentLoc.add(0.0, 1.0, 0.0);
            }
        } else if (this.ability.equalsIgnoreCase("IceBullet")) {
            if (this.shots > this.maxShots || !this.player.isSneaking()) {
                this.remove();
                return;
            }
            if (this.origin == null) {
                if (this.bplayer.isOnCooldown("IceBullet") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                Block waterBlock = BlockSource.getWaterSourceBlock(this.player, this.range, ClickType.LEFT_CLICK, true, true, WaterMethods.canPlantbend(this.player));
                if (waterBlock == null) {
                    this.remove();
                    return;
                }
                this.time = 0;
                this.origin = waterBlock.getLocation();
                this.currentLoc = this.origin.clone();
                this.state = AbilityState.ICE_BULLET_FORMING;
                this.bplayer.addCooldown("IceBullet", this.cooldown);
                this.direction = new Vector(1, 0, 1);
                this.waterGrabber = new WaterSourceGrabber(this.player, this.origin.clone());
            } else {
                if (this.waterGrabber.getState() == WaterSourceGrabber.AnimationState.FAILED) {
                    this.remove();
                    return;
                }
                if (this.waterGrabber.getState() == WaterSourceGrabber.AnimationState.FINISHED) {
                    if (this.time == 0) {
                        this.time = System.currentTimeMillis();
                    }
                    long timeDiff = System.currentTimeMillis() - this.time;
                    double animSpeed = ICE_BULLET_ANIM_SPEED;
                    if (this.state == AbilityState.ICE_BULLET_FORMING) {
                        if ((double)timeDiff < 1000.0 * animSpeed) {
                            double steps = this.radius * ((double)(timeDiff + 100) / (1000.0 * animSpeed));
                            this.revertBlocks();
                            double i = 0.0;
                            while (i < steps) {
                                this.drawWaterCircle(this.player.getEyeLocation().clone().add(0.0, i, 0.0), 360.0, 5.0, this.radius - i);
                                this.drawWaterCircle(this.player.getEyeLocation().clone().add(0.0, - i, 0.0), 360.0, 5.0, this.radius - i);
                                i += 1.0;
                            }
                        } else if ((double)timeDiff < 2500.0 * animSpeed) {
                            this.revertBlocks();
                            double i = 0.0;
                            while (i < this.radius) {
                                this.drawWaterCircle(this.player.getEyeLocation().clone().add(0.0, i, 0.0), 360.0, 5.0, this.radius - i, Material.ICE, (byte)0);
                                this.drawWaterCircle(this.player.getEyeLocation().clone().add(0.0, - i, 0.0), 360.0, 5.0, this.radius - i, Material.ICE, (byte)0);
                                i += 1.0;
                            }
                        }
                        if ((double)timeDiff < this.shootTime) {
                            if (this.shots < (double)(this.rightClicks + this.leftClicks)) {
                                this.shots += 1.0;
                                Vector vec = this.player.getEyeLocation().getDirection().normalize();
                                Location loc = this.player.getEyeLocation().add(vec.clone().multiply(this.radius + 1.3));
                                FireCombo.FireComboStream fs = new FireCombo.FireComboStream(null, vec, loc, this.range, this.speed, "IceBullet");
                                fs.setDensity(10);
                                fs.setSpread(0.1f);
                                fs.setUseNewParticles(true);
                                fs.setParticleEffect(ParticleEffect.SNOW_SHOVEL);
                                fs.setCollides(false);
                                fs.runTaskTimer((Plugin)ProjectKorra.plugin, 0, 1);
                                this.tasks.add(fs);
                            }
                            this.manageShots();
                        } else {
                            this.remove();
                        }
                    }
                } else {
                    this.waterGrabber.progress();
                }
            }
        }
    }

    public void manageShots() {
        int i = 0;
        while (i < this.tasks.size()) {
            if (((FireCombo.FireComboStream)this.tasks.get(i)).isCancelled()) {
                this.tasks.remove(i);
                --i;
            }
            ++i;
        }
        i = 0;
        while (i < this.tasks.size()) {
            FireCombo.FireComboStream fstream = (FireCombo.FireComboStream)this.tasks.get(i);
            Location loc = fstream.getLocation();
            if (!EarthMethods.isTransparentToEarthbending(this.player, loc.clone().add(0.0, 0.2, 0.0).getBlock())) {
                fstream.remove();
                return;
            }
            if (i % 2 == 0) {
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc, 1.5)) {
                    if (GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterManipulation", entity.getLocation())) {
                        this.remove();
                        return;
                    }
                    if (entity.equals((Object)this.player)) continue;
                    if (this.knockback != 0.0) {
                        Vector force = fstream.getDirection();
                        entity.setVelocity(force.multiply(this.knockback));
                    }
                    if (this.damage == 0.0 || !(entity instanceof LivingEntity)) continue;
                    if (fstream.getAbility().equalsIgnoreCase("IceBullet")) {
                        GeneralMethods.damageEntity(this.player, entity, this.damage, SubElement.Icebending, "IceBullets");
                        continue;
                    }
                    GeneralMethods.damageEntity(this.player, entity, this.damage, Element.Water, "WaterCombo");
                }
                if (GeneralMethods.blockAbilities(this.player, FireCombo.abilitiesToBlock, loc, 1.0)) {
                    fstream.remove();
                }
            }
            ++i;
        }
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

    public void drawWaterCircle(Location loc, double theta, double increment, double radius) {
        this.drawWaterCircle(loc, theta, increment, radius, Material.STATIONARY_WATER, (byte)0);
    }

    public void drawWaterCircle(Location loc, double theta, double increment, double radius, Material mat, byte data) {
        double rotateSpeed = theta;
        this.direction = GeneralMethods.rotateXZ(this.direction, rotateSpeed);
        double i = 0.0;
        while (i < theta) {
            Vector dir = GeneralMethods.rotateXZ(this.direction, i - theta / 2.0).normalize().multiply(radius);
            dir.setY(0);
            Block block = loc.clone().add(dir).getBlock();
            this.currentLoc = block.getLocation();
            if (block.getType() == Material.AIR && !GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterManipulation", block.getLocation())) {
                this.createBlock(block, mat, data);
            }
            i += increment;
        }
    }

    public void remove() {
        instances.remove(this);
        for (BukkitRunnable task : this.tasks) {
            task.cancel();
        }
        this.revertBlocks();
        if (this.waterGrabber != null) {
            this.waterGrabber.remove();
        }
    }

    public static void progressAll() {
        int i = instances.size() - 1;
        while (i >= 0) {
            instances.get(i).progress();
            --i;
        }
    }

    public static void removeAll() {
        int i = instances.size() - 1;
        while (i >= 0) {
            instances.get(i).remove();
            --i;
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public static ArrayList<WaterCombo> getWaterCombo(Player player) {
        ArrayList<WaterCombo> list = new ArrayList<WaterCombo>();
        for (WaterCombo combo : instances) {
            if (combo.player == null || combo.player != player) continue;
            list.add(combo);
        }
        return list;
    }

    public static ArrayList<WaterCombo> getWaterCombo(Player player, String ability) {
        ArrayList<WaterCombo> list = new ArrayList<WaterCombo>();
        for (WaterCombo combo : instances) {
            if (combo.player == null || combo.player != player || ability == null || !combo.ability.equalsIgnoreCase(ability)) continue;
            list.add(combo);
        }
        return list;
    }

    public static boolean removeAroundPoint(Player player, String ability, Location loc, double radius) {
        boolean removed = false;
        int i = 0;
        while (i < instances.size()) {
            WaterCombo combo = instances.get(i);
            if (!combo.getPlayer().equals((Object)player) && ability.equalsIgnoreCase("Twister") && combo.ability.equalsIgnoreCase("Twister") && combo.currentLoc != null && Math.abs(combo.currentLoc.distance(loc)) <= radius) {
                instances.remove(combo);
                removed = true;
            }
            ++i;
        }
        return removed;
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

    public static enum AbilityState {
        ICE_PILLAR_RISING,
        ICE_BULLET_FORMING;
        

       
    }

}

