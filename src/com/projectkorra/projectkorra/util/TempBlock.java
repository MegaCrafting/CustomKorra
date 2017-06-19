/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 */
package com.projectkorra.projectkorra.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

import com.projectkorra.projectkorra.GeneralMethods;

public class TempBlock {
    public static ConcurrentHashMap<Block, TempBlock> instances = new ConcurrentHashMap();
    private Block block;
    private Material newtype;
    private byte newdata;
    private BlockState state;

    public TempBlock(Block block, Material newtype, byte newdata) {
        this.block = block;
        this.newdata = newdata;
        this.newtype = newtype;
        if (instances.containsKey((Object)block)) {
            TempBlock temp = instances.get((Object)block);
            if (newtype != temp.newtype) {
                temp.block.setType(newtype);
                temp.newtype = newtype;
            }
            if (newdata != temp.newdata) {
                temp.block.setData(newdata);
                temp.newdata = newdata;
            }
            this.state = temp.state;
            instances.replace(block, temp);
        } else {
            this.state = block.getState();
            block.setType(newtype);
            block.setData(newdata);
            instances.put(block, this);
        }
        if (this.state.getType() == Material.FIRE) {
            this.state.setType(Material.AIR);
        }
    }

    public static TempBlock get(Block block) {
        if (TempBlock.isTempBlock(block)) {
            return instances.get((Object)block);
        }
        return null;
    }

    public static boolean isTempBlock(Block block) {
        return instances.containsKey((Object)block);
    }

    public static boolean isTouchingTempBlock(Block block) {
        BlockFace[] faces;
        BlockFace[] arrblockFace = faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
        int n = arrblockFace.length;
        int n2 = 0;
        while (n2 < n) {
            BlockFace face = arrblockFace[n2];
            if (instances.containsKey((Object)block.getRelative(face))) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public static void removeAll() {
        for (Block block : instances.keySet()) {
            TempBlock.revertBlock(block, Material.AIR);
        }
    }

    public static void removeBlock(Block block) {
        instances.remove((Object)block);
    }

    public static void revertBlock(Block block, Material defaulttype) {
        if (instances.containsKey((Object)block)) {
            instances.get((Object)block).revertBlock();
        } else if ((defaulttype == Material.LAVA || defaulttype == Material.STATIONARY_LAVA) && GeneralMethods.isAdjacentToThreeOrMoreSources(block)) {
            block.setType(Material.LAVA);
            block.setData((byte)0);
        } else if ((defaulttype == Material.WATER || defaulttype == Material.STATIONARY_WATER) && GeneralMethods.isAdjacentToThreeOrMoreSources(block)) {
            block.setType(Material.WATER);
            block.setData((byte)0);
        } else {
            block.setType(defaulttype);
        }
    }

    public Block getBlock() {
        return this.block;
    }

    public Location getLocation() {
        return this.block.getLocation();
    }

    public BlockState getState() {
        return this.state;
    }

    public void revertBlock() {
        this.state.update(true);
        instances.remove((Object)this.block);
    }

    public void setState(BlockState newstate) {
        this.state = newstate;
    }

    public void setType(Material material) {
        this.setType(material, this.newdata);
    }

    public void setType(Material material, byte data) {
        this.newtype = material;
        this.newdata = data;
        this.block.setType(material);
        this.block.setData(data);
    }
}