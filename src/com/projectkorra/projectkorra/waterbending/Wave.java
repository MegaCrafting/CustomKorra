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
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.FreezeMelt;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterReturn;
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

public class Wave {
    public static ConcurrentHashMap<Integer, Wave> instances = new ConcurrentHashMap();
    private static final double defaultmaxradius = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Surge.Wave.Radius");
    private static final double defaultfactor = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Surge.Wave.HorizontalPush");
    private static final double defaultupfactor = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Surge.Wave.VerticalPush");
    private static final double MAX_FREEZE_RADIUS = 7.0;
    private static final long interval = 30;
    private static final byte full = 0;
    static double defaultrange = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Surge.Wave.Range");
    Player player;
    private Location location = null;
    private Block sourceblock = null;
    private Location targetdestination = null;
    private Vector targetdirection = null;
    private ConcurrentHashMap<Block, Block> wave = new ConcurrentHashMap();
    private ConcurrentHashMap<Block, Block> frozenblocks = new ConcurrentHashMap();
    private long time;
    private double radius = 1.0;
    private double maxradius = defaultmaxradius;
    private double factor = defaultfactor;
    private double upfactor = defaultupfactor;
    private double maxfreezeradius = 7.0;
    private boolean freeze = false;
    private boolean activatefreeze = false;
    private Location frozenlocation;
    double range = defaultrange;
    boolean progressing = false;
    boolean canhitself = true;

    public Wave(Player player) {
        this.player = player;
        if (instances.containsKey(player.getEntityId()) && Wave.instances.get((Object)Integer.valueOf((int)player.getEntityId())).progressing && !Wave.instances.get((Object)Integer.valueOf((int)player.getEntityId())).freeze) {
            Wave.instances.get((Object)Integer.valueOf((int)player.getEntityId())).freeze = true;
            return;
        }
        if (AvatarState.isAvatarState(player)) {
            this.maxradius = AvatarState.getValue(this.maxradius);
        }
        this.maxradius = WaterMethods.waterbendingNightAugment(this.maxradius, player.getWorld());
        if (this.prepare()) {
            if (instances.containsKey(player.getEntityId())) {
                instances.get(player.getEntityId()).cancel();
            }
            instances.put(player.getEntityId(), this);
            this.time = System.currentTimeMillis();
        }
    }

    public boolean prepare() {
        this.cancelPrevious();
        Block block = BlockSource.getWaterSourceBlock(this.player, this.range, ClickType.SHIFT_DOWN, true, true, WaterMethods.canPlantbend(this.player));
        if (block != null) {
            this.sourceblock = block;
            this.focusBlock();
            return true;
        }
        return false;
    }

    private void cancelPrevious() {
        if (instances.containsKey(this.player.getEntityId())) {
            Wave old = instances.get(this.player.getEntityId());
            if (old.progressing) {
                old.breakBlock();
                old.thaw();
                old.returnWater(old.location);
            } else {
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
        instances.remove(this.player.getEntityId());
    }

    public void moveWater() {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(this.player.getName());
        if (bPlayer.isOnCooldown("Surge")) {
            return;
        }
        bPlayer.addCooldown("Surge", GeneralMethods.getGlobalCooldown());
        if (this.sourceblock != null) {
            Entity target;
            if (!this.sourceblock.getWorld().equals((Object)this.player.getWorld())) {
                return;
            }
            this.range = WaterMethods.waterbendingNightAugment(this.range, this.player.getWorld());
            if (AvatarState.isAvatarState(this.player)) {
                this.factor = AvatarState.getValue(this.factor);
            }
            this.targetdestination = (target = GeneralMethods.getTargetedEntity(this.player, this.range, new ArrayList<Entity>())) == null ? this.player.getTargetBlock(EarthMethods.getTransparentEarthbending(), (int)this.range).getLocation() : ((LivingEntity)target).getEyeLocation();
            if (this.targetdestination.distance(this.location) <= 1.0) {
                this.progressing = false;
                this.targetdestination = null;
            } else {
                this.progressing = true;
                this.targetdirection = this.getDirection(this.sourceblock.getLocation(), this.targetdestination).normalize();
                this.targetdestination = this.location.clone().add(this.targetdirection.clone().multiply(this.range));
                if (WaterMethods.isPlant(this.sourceblock)) {
                    new com.projectkorra.projectkorra.waterbending.Plantbending(this.sourceblock);
                }
                if (!GeneralMethods.isAdjacentToThreeOrMoreSources(this.sourceblock)) {
                    this.sourceblock.setType(Material.AIR);
                }
                this.addWater(this.sourceblock);
            }
        }
    }

    private Vector getDirection(Location location, Location destination) {
        double x1 = destination.getX();
        double y1 = destination.getY();
        double z1 = destination.getZ();
        double x0 = location.getX();
        double y0 = location.getY();
        double z0 = location.getZ();
        return new Vector(x1 - x0, y1 - y0, z1 - z0);
    }

    public static void progressAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            instances.get(ID).progress();
        }
    }

    private boolean progress() {
        if (this.player.isDead() || !this.player.isOnline() || !GeneralMethods.canBend(this.player.getName(), "Surge")) {
            this.breakBlock();
            this.thaw();
            return false;
        }
        if (System.currentTimeMillis() - this.time >= 30) {
            this.time = System.currentTimeMillis();
            if (GeneralMethods.getBoundAbility(this.player) == null) {
                this.unfocusBlock();
                this.thaw();
                this.breakBlock();
                this.returnWater(this.location);
                return false;
            }
            if (!this.progressing && !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("Surge")) {
                this.unfocusBlock();
                return false;
            }
            if (!this.progressing) {
                this.sourceblock.getWorld().playEffect(this.location, Effect.SMOKE, 4, (int)this.range);
                return false;
            }
            if (this.location.getWorld() != this.player.getWorld()) {
                this.thaw();
                this.breakBlock();
                return false;
            }
            if (this.activatefreeze) {
                if (this.location.distance(this.player.getLocation()) > this.range) {
                    this.progressing = false;
                    this.thaw();
                    this.breakBlock();
                    return false;
                }
                if (GeneralMethods.getBoundAbility(this.player) == null) {
                    this.progressing = false;
                    this.thaw();
                    this.breakBlock();
                    this.returnWater(this.location);
                    return false;
                }
                if (!GeneralMethods.canBend(this.player.getName(), "Surge")) {
                    this.progressing = false;
                    this.thaw();
                    this.breakBlock();
                    this.returnWater(this.location);
                    return false;
                }
            } else {
                Vector direction = this.targetdirection;
                this.location = this.location.clone().add(direction);
                Block blockl = this.location.getBlock();
                ArrayList<Block> blocks = new ArrayList<Block>();
                if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "Surge", this.location) && (blockl.getType() == Material.AIR || blockl.getType() == Material.FIRE || WaterMethods.isPlant(blockl) || WaterMethods.isWater(blockl) || WaterMethods.isWaterbendable(blockl, this.player)) && blockl.getType() != Material.LEAVES) {
                    double i = 0.0;
                    while (i <= this.radius) {
                        double angle = 0.0;
                        while (angle < 360.0) {
                            Vector vec = GeneralMethods.getOrthogonalVector(this.targetdirection, angle, i);
                            Block block = this.location.clone().add(vec).getBlock();
                            if (!blocks.contains((Object)block) && (block.getType() == Material.AIR || block.getType() == Material.FIRE) || WaterMethods.isWaterbendable(block, this.player)) {
                                blocks.add(block);
                                FireBlast.removeFireBlastsAroundPoint(block.getLocation(), 2.0);
                            }
                            if (GeneralMethods.rand.nextInt(15) == 0) {
                                WaterMethods.playWaterbendingSound(this.location);
                            }
                            angle += 10.0;
                        }
                        i += 0.5;
                    }
                }
                for (Block block : this.wave.keySet()) {
                    if (blocks.contains((Object)block)) continue;
                    this.finalRemoveWater(block);
                }
                for (Block block2 : blocks) {
                    if (this.wave.containsKey((Object)block2)) continue;
                    this.addWater(block2);
                }
                if (this.wave.isEmpty()) {
                    this.breakBlock();
                    this.returnWater(this.location.subtract(direction));
                    this.progressing = false;
                    return false;
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.0 * this.radius)) {
                    boolean knockback = false;
                    for (Block block3 : this.wave.keySet()) {
                        if (entity.getLocation().distance(block3.getLocation()) > 2.0) continue;
                        if (entity instanceof LivingEntity && this.freeze && entity.getEntityId() != this.player.getEntityId()) {
                            this.activatefreeze = true;
                            this.frozenlocation = entity.getLocation();
                            this.freeze();
                            break;
                        }
                        if (entity.getEntityId() == this.player.getEntityId() && !this.canhitself) continue;
                        knockback = true;
                    }
                    if (!knockback) continue;
                    Vector dir = direction.clone();
                    dir.setY(dir.getY() * this.upfactor);
                    GeneralMethods.setVelocity(entity, entity.getVelocity().clone().add(dir.clone().multiply(WaterMethods.waterbendingNightAugment(this.factor, this.player.getWorld()))));
                    entity.setFallDistance(0.0f);
                    if (entity.getFireTicks() > 0) {
                        entity.getWorld().playEffect(entity.getLocation(), Effect.EXTINGUISH, 0);
                    }
                    entity.setFireTicks(0);
                    AirMethods.breakBreathbendingHold(entity);
                }
                if (!this.progressing) {
                    this.breakBlock();
                    return false;
                }
                if (this.location.distance(this.targetdestination) < 1.0) {
                    this.progressing = false;
                    this.breakBlock();
                    this.returnWater(this.location);
                    return false;
                }
                if (this.radius < this.maxradius) {
                    this.radius += 0.5;
                }
                return true;
            }
        }
        return false;
    }

    private void breakBlock() {
        for (Block block : this.wave.keySet()) {
            this.finalRemoveWater(block);
        }
        instances.remove(this.player.getEntityId());
    }

    private void finalRemoveWater(Block block) {
        if (this.wave.containsKey((Object)block)) {
            TempBlock.revertBlock(block, Material.AIR);
            this.wave.remove((Object)block);
        }
    }

    private void addWater(Block block) {
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "Surge", block.getLocation())) {
            return;
        }
        if (!TempBlock.isTempBlock(block)) {
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.STATIONARY_WATER, (byte)8);
            this.wave.put(block, block);
        }
    }

    private void clearWave() {
        for (Block block : this.wave.keySet()) {
            TempBlock.revertBlock(block, Material.AIR);
        }
        this.wave.clear();
    }

    public static void moveWater(Player player) {
        if (instances.containsKey(player.getEntityId())) {
            instances.get(player.getEntityId()).moveWater();
        }
    }

    public static boolean progress(int ID) {
        return instances.get(ID).progress();
    }

    public static boolean isBlockWave(Block block) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            if (!Wave.instances.get((Object)Integer.valueOf((int)ID)).wave.containsKey((Object)block)) continue;
            return true;
        }
        return false;
    }

    public static void launch(Player player) {
        Wave.moveWater(player);
    }

    public static void removeAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            for (Block block2 : Wave.instances.get((Object)Integer.valueOf((int)id)).wave.keySet()) {
                block2.setType(Material.AIR);
                Wave.instances.get((Object)Integer.valueOf((int)id)).wave.remove((Object)block2);
            }
            for (Block block2 : Wave.instances.get((Object)Integer.valueOf((int)id)).frozenblocks.keySet()) {
                block2.setType(Material.AIR);
                Wave.instances.get((Object)Integer.valueOf((int)id)).frozenblocks.remove((Object)block2);
            }
        }
    }

    private void freeze() {
        this.clearWave();
        if (!WaterMethods.canIcebend(this.player)) {
            return;
        }
        double freezeradius = this.radius;
        if (freezeradius > this.maxfreezeradius) {
            freezeradius = this.maxfreezeradius;
        }
        for (Block block : GeneralMethods.getBlocksAroundPoint(this.frozenlocation, freezeradius)) {
            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "Surge", block.getLocation()) || GeneralMethods.isRegionProtectedFromBuild(this.player, "PhaseChange", block.getLocation()) || TempBlock.isTempBlock(block)) continue;
            if (block.getType() == Material.AIR || block.getType() == Material.SNOW) {
                new com.projectkorra.projectkorra.util.TempBlock(block, Material.ICE, (byte)0);
                this.frozenblocks.put(block, block);
            }
            if (WaterMethods.isWater(block)) {
                FreezeMelt.freeze(this.player, block);
            }
            if (WaterMethods.isPlant(block) && block.getType() != Material.LEAVES) {
                block.breakNaturally();
                new com.projectkorra.projectkorra.util.TempBlock(block, Material.ICE, (byte)0);
                this.frozenblocks.put(block, block);
            }
            for (Block sound : this.frozenblocks.keySet()) {
                if (GeneralMethods.rand.nextInt(4) != 0) continue;
                WaterMethods.playWaterbendingSound(sound.getLocation());
            }
        }
    }

    private void thaw() {
        for (Block block : this.frozenblocks.keySet()) {
            TempBlock.revertBlock(block, Material.AIR);
            this.frozenblocks.remove((Object)block);
        }
    }

    public static void thaw(Block block) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            if (!Wave.instances.get((Object)Integer.valueOf((int)id)).frozenblocks.containsKey((Object)block)) continue;
            TempBlock.revertBlock(block, Material.AIR);
            Wave.instances.get((Object)Integer.valueOf((int)id)).frozenblocks.remove((Object)block);
        }
    }

    public static boolean canThaw(Block block) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            if (!Wave.instances.get((Object)Integer.valueOf((int)id)).frozenblocks.containsKey((Object)block)) continue;
            return false;
        }
        return true;
    }

    void returnWater(Location location) {
        if (location != null) {
            new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, location.getBlock());
        }
    }

    public static String getDescription() {
        return "To use, place your cursor over a waterbendable object (water, ice, plants if you have plantbending) and tap sneak (default: shift). Smoke will appear where you've selected, indicating the origin of your ability. After you have selected an origin, simply left-click in any direction and you will see your water spout off in that direction and form a large wave, knocking back all within its path. If you look towards a creature when you use this ability, it will target that creature. Additionally, tapping sneak while the wave is en route will cause that wave to encase the first target it hits in ice.";
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getMaxradius() {
        return this.maxradius;
    }

    public void setMaxradius(double maxradius) {
        this.maxradius = maxradius;
    }

    public double getFactor() {
        return this.factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double getUpfactor() {
        return this.upfactor;
    }

    public void setUpfactor(double upfactor) {
        this.upfactor = upfactor;
    }

    public double getMaxfreezeradius() {
        return this.maxfreezeradius;
    }

    public void setMaxfreezeradius(double maxfreezeradius) {
        this.maxfreezeradius = maxfreezeradius;
    }
}

