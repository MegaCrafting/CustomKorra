/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public class Information {
    private static int ID = Integer.MIN_VALUE;
    private String string;
    private int id = ID++;
    private int integer;
    private long time;
    private double value;
    private byte data;
    private Block block;
    private BlockState state;
    private Location location;
    private Material type;
    private Player player;

    public Information() {
        if (ID >= Integer.MAX_VALUE) {
            ID = Integer.MIN_VALUE;
        }
    }

    public Block getBlock() {
        return this.block;
    }

    public byte getData() {
        return this.data;
    }

    public double getDouble() {
        return this.value;
    }

    public int getID() {
        return this.id;
    }

    public int getInteger() {
        return this.integer;
    }

    public Location getLocation() {
        return this.location;
    }

    public Player getPlayer() {
        return this.player;
    }

    public BlockState getState() {
        return this.state;
    }

    public String getString() {
        return this.string;
    }

    public long getTime() {
        return this.time;
    }

    public Material getType() {
        return this.type;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public void setDouble(double value) {
        this.value = value;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setType(Material type) {
        this.type = type;
    }
}

