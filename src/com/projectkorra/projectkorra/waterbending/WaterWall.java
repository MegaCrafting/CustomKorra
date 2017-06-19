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
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterReturn;
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
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WaterWall {
    public static ConcurrentHashMap<Integer, WaterWall> instances = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Player> wallblocks = new ConcurrentHashMap();
    private static double RANGE = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Surge.Wall.Range");
    private static final double defaultradius = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Surge.Wall.Radius");
    private static final long interval = 30;
    private static final byte full = 0;
    Player player;
    private Location location = null;
    private Block sourceblock = null;
    private Location firstdestination = null;
    private Location targetdestination = null;
    private Vector firstdirection = null;
    private Vector targetdirection = null;
    private boolean progressing = false;
    private boolean settingup = false;
    private boolean forming = false;
    private boolean frozen = false;
    private long time;
    private double radius = defaultradius;
    private double range = RANGE;

    public WaterWall(Player player) {
        Location eyeloc;
        Block block;
        this.player = player;
        if (Wave.instances.containsKey(player.getEntityId())) {
            Wave wave = Wave.instances.get(player.getEntityId());
            if (!wave.progressing) {
                Wave.launch(player);
                return;
            }
        }
        if (AvatarState.isAvatarState(player)) {
            this.radius = AvatarState.getValue(this.radius);
        }
        if (instances.containsKey(player.getEntityId())) {
            if (WaterWall.instances.get((Object)Integer.valueOf((int)player.getEntityId())).progressing) {
                WaterWall.freezeThaw(player);
            } else if (this.prepare()) {
                if (instances.containsKey(player.getEntityId())) {
                    instances.get(player.getEntityId()).cancel();
                }
                instances.put(player.getEntityId(), this);
                this.time = System.currentTimeMillis();
            }
        } else if (this.prepare()) {
            if (instances.containsKey(player.getEntityId())) {
                instances.get(player.getEntityId()).cancel();
            }
            instances.put(player.getEntityId(), this);
            this.time = System.currentTimeMillis();
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("Surge")) {
            return;
        }
        if (!instances.containsKey(player.getEntityId()) && WaterReturn.hasWaterBottle(player) && EarthMethods.isTransparentToEarthbending(player, block = (eyeloc = player.getEyeLocation()).add(eyeloc.getDirection().normalize()).getBlock()) && EarthMethods.isTransparentToEarthbending(player, eyeloc.getBlock())) {
            block.setType(Material.WATER);
            block.setData((byte)0);
            Wave wave = new Wave(player);
            wave.canhitself = false;
            wave.moveWater();
            if (!wave.progressing) {
                block.setType(Material.AIR);
                wave.cancel();
            } else {
                WaterReturn.emptyWaterBottle(player);
            }
        }
    }

    private static void freezeThaw(Player player) {
        instances.get(player.getEntityId()).freezeThaw();
    }

    private void freezeThaw() {
        if (!WaterMethods.canIcebend(this.player)) {
            return;
        }
        if (this.frozen) {
            this.thaw();
        } else {
            this.freeze();
        }
    }

    private void freeze() {
        this.frozen = true;
        for (Block block : wallblocks.keySet()) {
            if (wallblocks.get((Object)block) != this.player) continue;
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.ICE,(byte) 0);
            WaterMethods.playIcebendingSound(block.getLocation());
        }
    }

    private void thaw() {
        this.frozen = false;
        for (Block block : wallblocks.keySet()) {
            if (wallblocks.get((Object)block) != this.player) continue;
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.STATIONARY_WATER,(byte) 8);
        }
    }

    public boolean prepare() {
        this.cancelPrevious();
        Block block = BlockSource.getWaterSourceBlock(this.player, this.range, ClickType.LEFT_CLICK, true, true, WaterMethods.canPlantbend(this.player));
        if (block != null) {
            this.sourceblock = block;
            this.focusBlock();
            return true;
        }
        return false;
    }

    private void cancelPrevious() {
        if (instances.containsKey(this.player.getEntityId())) {
            WaterWall old = instances.get(this.player.getEntityId());
            if (old.progressing) {
                old.removeWater(old.sourceblock);
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
        if (this.sourceblock != null) {
            this.targetdestination = this.player.getTargetBlock(EarthMethods.getTransparentEarthbending(), (int)this.range).getLocation();
            if (this.targetdestination.distance(this.location) <= 1.0) {
                this.progressing = false;
                this.targetdestination = null;
            } else {
                this.progressing = true;
                this.settingup = true;
                this.firstdestination = this.getToEyeLevel();
                this.firstdirection = this.getDirection(this.sourceblock.getLocation(), this.firstdestination);
                this.targetdirection = this.getDirection(this.firstdestination, this.targetdestination);
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

    private Location getToEyeLevel() {
        Location loc = this.sourceblock.getLocation().clone();
        loc.setY(this.targetdestination.getY());
        return loc;
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
        if (this.player.isDead() || !this.player.isOnline()) {
            this.breakBlock();
            return false;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "Surge")) {
            this.breakBlock();
            this.returnWater(this.location);
            this.unfocusBlock();
            return false;
        }
        if (System.currentTimeMillis() - this.time >= 30) {
            this.time = System.currentTimeMillis();
            if (GeneralMethods.getBoundAbility(this.player) == null) {
                this.unfocusBlock();
                this.breakBlock();
                this.returnWater(this.location);
                return false;
            }
            if (!this.progressing && !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("Surge")) {
                this.unfocusBlock();
                return false;
            }
            if (!(!this.progressing || this.player.isSneaking() && GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("Surge"))) {
                this.breakBlock();
                this.returnWater(this.location);
                return false;
            }
            if (!this.progressing) {
                this.sourceblock.getWorld().playEffect(this.location, Effect.SMOKE, 4, (int)this.range);
                return false;
            }
            if (this.forming) {
                if (GeneralMethods.rand.nextInt(7) == 0) {
                    WaterMethods.playWaterbendingSound(this.location);
                }
                ArrayList<Block> blocks = new ArrayList<Block>();
                Location loc = GeneralMethods.getTargetedLocation(this.player, (int)this.range, 8, 9, 79);
                this.location = loc.clone();
                Vector dir = this.player.getEyeLocation().getDirection();
                double i = 0.0;
                while (i <= WaterMethods.waterbendingNightAugment(this.radius, this.player.getWorld())) {
                    double angle = 0.0;
                    while (angle < 360.0) {
                        Vector vec = GeneralMethods.getOrthogonalVector(dir.clone(), angle, i);
                        Block block = loc.clone().add(vec).getBlock();
                        if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "Surge", block.getLocation())) {
                            if (wallblocks.containsKey((Object)block)) {
                                blocks.add(block);
                            } else if (!blocks.contains((Object)block) && (block.getType() == Material.AIR || block.getType() == Material.FIRE || WaterMethods.isWaterbendable(block, this.player))) {
                                wallblocks.put(block, this.player);
                                this.addWallBlock(block);
                                blocks.add(block);
                                FireBlast.removeFireBlastsAroundPoint(block.getLocation(), 2.0);
                            }
                        }
                        angle += 10.0;
                    }
                    i += 0.5;
                }
                for (Block blocki : wallblocks.keySet()) {
                    if (wallblocks.get((Object)blocki) != this.player || blocks.contains((Object)blocki)) continue;
                    WaterWall.finalRemoveWater(blocki);
                }
                return true;
            }
            if (this.sourceblock.getLocation().distance(this.firstdestination) < 0.5 && this.settingup) {
                this.settingup = false;
            }
            Vector direction = this.settingup ? this.firstdirection : this.targetdirection;
            this.location = this.location.clone().add(direction);
            Block block = this.location.getBlock();
            if (block.getLocation().equals((Object)this.sourceblock.getLocation())) {
                this.location = this.location.clone().add(direction);
                block = this.location.getBlock();
            }
            if (block.getType() != Material.AIR) {
                this.breakBlock();
                this.returnWater(this.location.subtract(direction));
                return false;
            }
            if (!this.progressing) {
                this.breakBlock();
                return false;
            }
            this.addWater(block);
            this.removeWater(this.sourceblock);
            this.sourceblock = block;
            if (this.location.distance(this.targetdestination) < 1.0) {
                this.removeWater(this.sourceblock);
                this.forming = true;
            }
            return true;
        }
        return false;
    }

    private void addWallBlock(Block block) {
        if (this.frozen) {
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.ICE,(byte) 0);
        } else {
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.STATIONARY_WATER, (byte)8);
        }
    }

    private void breakBlock() {
        WaterWall.finalRemoveWater(this.sourceblock);
        for (Block block : wallblocks.keySet()) {
            if (wallblocks.get((Object)block) != this.player) continue;
            WaterWall.finalRemoveWater(block);
        }
        instances.remove(this.player.getEntityId());
    }

    private void removeWater(Block block) {
        if (block != null && affectedblocks.containsKey((Object)block)) {
            if (!GeneralMethods.isAdjacentToThreeOrMoreSources(block)) {
                TempBlock.revertBlock(block, Material.AIR);
            }
            affectedblocks.remove((Object)block);
        }
    }

    private static void finalRemoveWater(Block block) {
        if (affectedblocks.containsKey((Object)block)) {
            TempBlock.revertBlock(block, Material.AIR);
            affectedblocks.remove((Object)block);
        }
        if (wallblocks.containsKey((Object)block)) {
            TempBlock.revertBlock(block, Material.AIR);
            wallblocks.remove((Object)block);
        }
    }

    private void addWater(Block block) {
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "Surge", block.getLocation())) {
            return;
        }
        if (!TempBlock.isTempBlock(block)) {
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.STATIONARY_WATER,(byte) 8);
            affectedblocks.put(block, block);
        }
    }

    public static void moveWater(Player player) {
        if (instances.containsKey(player.getEntityId())) {
            instances.get(player.getEntityId()).moveWater();
        }
    }

    public static boolean progress(int ID) {
        return instances.get(ID).progress();
    }

    public static void form(Player player) {
        if (!instances.containsKey(player.getEntityId())) {
            if (!Wave.instances.containsKey(player.getEntityId()) && BlockSource.getWaterSourceBlock(player, (int)Wave.defaultrange, ClickType.LEFT_CLICK, true, true, WaterMethods.canPlantbend(player)) == null && WaterReturn.hasWaterBottle(player)) {
                BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
                if (bPlayer.isOnCooldown("Surge")) {
                    return;
                }
                Location eyeloc = player.getEyeLocation();
                Block block = eyeloc.add(eyeloc.getDirection().normalize()).getBlock();
                if (EarthMethods.isTransparentToEarthbending(player, block) && EarthMethods.isTransparentToEarthbending(player, eyeloc.getBlock())) {
                    block.setType(Material.WATER);
                    block.setData((byte)0);
                    WaterWall wall = new WaterWall(player);
                    wall.moveWater();
                    if (!wall.progressing) {
                        block.setType(Material.AIR);
                        wall.cancel();
                    } else {
                        WaterReturn.emptyWaterBottle(player);
                    }
                    return;
                }
            }
            new com.projectkorra.projectkorra.waterbending.Wave(player);
            return;
        }
        if (WaterMethods.isWaterbendable(player.getTargetBlock((HashSet<Material>) null, (int) Wave.defaultrange), player)) {
            new com.projectkorra.projectkorra.waterbending.Wave(player);
            return;
        }
        WaterWall.moveWater(player);
    }

    public static void removeAll() {
        for (Block block2 : affectedblocks.keySet()) {
            TempBlock.revertBlock(block2, Material.AIR);
            affectedblocks.remove((Object)block2);
            wallblocks.remove((Object)block2);
        }
        for (Block block2 : wallblocks.keySet()) {
            TempBlock.revertBlock(block2, Material.AIR);
            affectedblocks.remove((Object)block2);
            wallblocks.remove((Object)block2);
        }
    }

    public static boolean canThaw(Block block) {
        if (wallblocks.keySet().contains((Object)block)) {
            return false;
        }
        return true;
    }

    public static void thaw(Block block) {
        WaterWall.finalRemoveWater(block);
    }

    public static boolean wasBrokenFor(Player player, Block block) {
        if (instances.containsKey(player.getEntityId())) {
            WaterWall wall = instances.get(player.getEntityId());
            if (wall.sourceblock == null) {
                return false;
            }
            if (wall.sourceblock.equals((Object)block)) {
                return true;
            }
        }
        return false;
    }

    private void returnWater(Location location) {
        if (location != null) {
            new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, location.getBlock());
        }
    }

    public static String getDescription() {
        return "This ability has two distinct features. If you sneak to select a source block, you can then click in a direction and a large wave will be launched in that direction. If you sneak again while the wave is en route, the wave will freeze the next target it hits. If, instead, you click to select a source block, you can hold sneak to form a wall of water at your cursor location. Click to shift between a water wall and an ice wall. Release sneak to dissipate it.";
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
}

