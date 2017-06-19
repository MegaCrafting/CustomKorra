/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class WaterSourceGrabber {
    private Player player;
    private AnimationState state;
    private Location origin;
    private Location currentLoc;
    private double animSpeed;
    private Material mat;
    private Byte data;
    private ConcurrentHashMap<Block, TempBlock> affectedBlocks = new ConcurrentHashMap();

    public WaterSourceGrabber(Player player, Location origin, double animSpeed) {
        this.player = player;
        this.origin = origin;
        this.animSpeed = animSpeed;
        this.mat = Material.STATIONARY_WATER;
        this.data = Byte.valueOf((byte)0);
        this.currentLoc = origin.clone();
        this.state = AnimationState.RISING;
    }

    public WaterSourceGrabber(Player player, Location origin) {
        this(player, origin, 1.0);
    }

    public void progress() {
        if (this.state == AnimationState.FAILED || this.state == AnimationState.FINISHED) {
            return;
        }
        if (this.state == AnimationState.RISING) {
            this.revertBlocks();
            double locDiff = this.player.getEyeLocation().getY() - this.currentLoc.getY();
            this.currentLoc.add(0.0, this.animSpeed * Math.signum(locDiff), 0.0);
            Block block = this.currentLoc.getBlock();
            if (!WaterMethods.isWaterbendable(block, this.player) && block.getType() != Material.AIR || GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterSpout", block.getLocation())) {
                this.remove();
                return;
            }
            this.createBlock(block, this.mat, this.data.byteValue());
            if (Math.abs(locDiff) < 1.0) {
                this.state = AnimationState.TOWARD;
            }
        } else {
            this.revertBlocks();
            Location eyeLoc = player.getTargetBlock((HashSet<Material>) null, 2).getLocation();
            eyeLoc.setY(this.player.getEyeLocation().getY());
            Vector vec = GeneralMethods.getDirection(this.currentLoc, eyeLoc);
            this.currentLoc.add(vec.normalize().multiply(this.animSpeed));
            Block block = this.currentLoc.getBlock();
            if (!WaterMethods.isWaterbendable(block, this.player) && block.getType() != Material.AIR || GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterManipulation", block.getLocation())) {
                this.remove();
                return;
            }
            this.createBlock(block, this.mat, this.data.byteValue());
            if (this.currentLoc.distance(eyeLoc) < 1.1) {
                this.state = AnimationState.FINISHED;
                this.revertBlocks();
            }
        }
    }

    public AnimationState getState() {
        return this.state;
    }

    public void remove() {
        this.state = AnimationState.FAILED;
    }

    public void revertBlocks() {
        Enumeration<Block> keys = this.affectedBlocks.keys();
        while (keys.hasMoreElements()) {
            Block block = keys.nextElement();
            this.affectedBlocks.get((Object)block).revertBlock();
            this.affectedBlocks.remove((Object)block);
        }
    }

    public void createBlock(Block block, Material mat) {
        this.createBlock(block, mat, (byte) 0);
    }

    public void createBlock(Block block, Material mat, byte data) {
        this.affectedBlocks.put(block, new TempBlock(block, mat, data));
    }

    public Material getMat() {
        return this.mat;
    }

    public void setMat(Material mat) {
        this.mat = mat;
    }

    public Byte getData() {
        return this.data;
    }

    public void setData(Byte data) {
        this.data = data;
    }

    public void setState(AnimationState state) {
        this.state = state;
    }

    public double getAnimSpeed() {
        return this.animSpeed;
    }

    public void setAnimSpeed(double animSpeed) {
        this.animSpeed = animSpeed;
    }

    public static enum AnimationState {
        RISING,
        TOWARD,
        FINISHED,
        FAILED;
        

     
    }

}

