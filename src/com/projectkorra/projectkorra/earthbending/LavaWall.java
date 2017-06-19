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
package com.projectkorra.projectkorra.earthbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.earthbending.LavaWave;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.TempBlock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

public class LavaWall {
    public static ConcurrentHashMap<Integer, LavaWall> instances = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Player> wallblocks = new ConcurrentHashMap();
    private static double range = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Surge.Wall.Range");
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
    private long time;
    private double radius = defaultradius;

    public LavaWall(Player player) {
        BendingPlayer bPlayer;
        this.player = player;
        if (LavaWave.instances.containsKey(player.getEntityId())) {
            LavaWave wave = LavaWave.instances.get(player.getEntityId());
            if (!wave.progressing) {
                LavaWave.launch(player);
                return;
            }
        }
        if (AvatarState.isAvatarState(player)) {
            this.radius = AvatarState.getValue(this.radius);
        }
        if ((bPlayer = GeneralMethods.getBendingPlayer(player.getName())).isOnCooldown("LavaSurge")) {
            return;
        }
    }

    public boolean prepare() {
        this.cancelPrevious();
        Block block = BlockSource.getSourceBlock(this.player, range, BlockSource.BlockSourceType.LAVA, ClickType.LEFT_CLICK);
        if (block != null) {
            this.sourceblock = block;
            this.focusBlock();
            return true;
        }
        return false;
    }

    private void cancelPrevious() {
        if (instances.containsKey(this.player.getEntityId())) {
            LavaWall old = instances.get(this.player.getEntityId());
            if (old.progressing) {
                old.removeLava(old.sourceblock);
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

    public void moveLava() {
        if (this.sourceblock != null) {
            this.targetdestination = this.player.getTargetBlock(EarthMethods.getTransparentEarthbending(), (int)range).getLocation();
            if (this.targetdestination.distance(this.location) <= 1.0) {
                this.progressing = false;
                this.targetdestination = null;
            } else {
                this.progressing = true;
                this.settingup = true;
                this.firstdestination = this.getToEyeLevel();
                this.firstdirection = this.getDirection(this.sourceblock.getLocation(), this.firstdestination);
                this.targetdirection = this.getDirection(this.firstdestination, this.targetdestination);
                if (!GeneralMethods.isAdjacentToThreeOrMoreSources(this.sourceblock)) {
                    this.sourceblock.setType(Material.AIR);
                }
                this.addLava(this.sourceblock);
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
        if (!GeneralMethods.canBend(this.player.getName(), "LavaSurge")) {
            if (!this.forming) {
                this.breakBlock();
            }
            this.unfocusBlock();
            return false;
        }
        if (System.currentTimeMillis() - this.time >= 30) {
            this.time = System.currentTimeMillis();
            if (GeneralMethods.getBoundAbility(this.player) == null) {
                this.unfocusBlock();
                return false;
            }
            if (!this.progressing && !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("LavaSurge")) {
                this.unfocusBlock();
                return false;
            }
            if (!(!this.progressing || this.player.isSneaking() && GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("LavaSurge"))) {
                this.breakBlock();
                return false;
            }
            if (!this.progressing) {
                this.sourceblock.getWorld().playEffect(this.location, Effect.SMOKE, 4, (int)range);
                return false;
            }
            if (this.forming) {
                ArrayList<Block> blocks = new ArrayList<Block>();
                Location loc = GeneralMethods.getTargetedLocation(this.player, (int)range, 8, 9, 79);
                this.location = loc.clone();
                Vector dir = this.player.getEyeLocation().getDirection();
                double i = 0.0;
                while (i <= this.radius) {
                    double angle = 0.0;
                    while (angle < 360.0) {
                        Vector vec = GeneralMethods.getOrthogonalVector(dir.clone(), angle, i);
                        Block block = loc.clone().add(vec).getBlock();
                        if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "LavaSurge", block.getLocation())) {
                            if (wallblocks.containsKey((Object)block)) {
                                blocks.add(block);
                            } else if (!blocks.contains((Object)block) && (block.getType() == Material.AIR || block.getType() == Material.FIRE || EarthMethods.isLavabendable(block, this.player))) {
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
                    LavaWall.finalRemoveLava(blocki);
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
                return false;
            }
            if (!this.progressing) {
                this.breakBlock();
                return false;
            }
            this.addLava(block);
            this.removeLava(this.sourceblock);
            this.sourceblock = block;
            if (this.location.distance(this.targetdestination) < 1.0) {
                this.removeLava(this.sourceblock);
                this.forming = true;
            }
            return true;
        }
        return false;
    }

    private void addWallBlock(Block block) {
        new com.projectkorra.projectkorra.util.TempBlock(block, Material.STATIONARY_LAVA, (byte) 8);
    }

    private void breakBlock() {
        LavaWall.finalRemoveLava(this.sourceblock);
        for (Block block : wallblocks.keySet()) {
            if (wallblocks.get((Object)block) != this.player) continue;
            LavaWall.finalRemoveLava(block);
        }
        instances.remove(this.player.getEntityId());
    }

    private void removeLava(Block block) {
        if (block != null && affectedblocks.containsKey((Object)block)) {
            if (!GeneralMethods.isAdjacentToThreeOrMoreSources(block)) {
                TempBlock.revertBlock(block, Material.AIR);
            }
            affectedblocks.remove((Object)block);
        }
    }

    private static void finalRemoveLava(Block block) {
        if (affectedblocks.containsKey((Object)block)) {
            TempBlock.revertBlock(block, Material.AIR);
            affectedblocks.remove((Object)block);
        }
        if (wallblocks.containsKey((Object)block)) {
            TempBlock.revertBlock(block, Material.AIR);
            wallblocks.remove((Object)block);
        }
    }

    private void addLava(Block block) {
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "LavaSurge", block.getLocation())) {
            return;
        }
        if (!TempBlock.isTempBlock(block)) {
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.STATIONARY_LAVA, (byte) 8);
            affectedblocks.put(block, block);
        }
    }

    public static void moveLava(Player player) {
        if (instances.containsKey(player.getEntityId())) {
            instances.get(player.getEntityId()).moveLava();
        }
    }

    public static void form(Player player) {
        if (!instances.containsKey(player.getEntityId())) {
            new com.projectkorra.projectkorra.earthbending.LavaWave(player);
            return;
        }
        if (EarthMethods.isLavabendable(player.getTargetBlock((HashSet<Byte>) null, (int) LavaWave.defaultrange), player)) {
            new com.projectkorra.projectkorra.earthbending.LavaWave(player);
            return;
        }
        LavaWall.moveLava(player);
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

    public static boolean wasBrokenFor(Player player, Block block) {
        if (instances.containsKey(player.getEntityId())) {
            LavaWall wall = instances.get(player.getEntityId());
            if (wall.sourceblock == null) {
                return false;
            }
            if (wall.sourceblock.equals((Object)block)) {
                return true;
            }
        }
        return false;
    }
}

