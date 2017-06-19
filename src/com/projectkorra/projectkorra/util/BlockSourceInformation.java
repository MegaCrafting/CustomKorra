/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.util;

import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class BlockSourceInformation {
    private Player player;
    private Block block;
    private BlockSource.BlockSourceType sourceType;
    private ClickType clickType;
    private long creationTime;

    public BlockSourceInformation(Player player, Block block, BlockSource.BlockSourceType sourceType, ClickType clickType) {
        this.player = player;
        this.block = block;
        this.sourceType = sourceType;
        this.creationTime = System.currentTimeMillis();
        this.clickType = clickType;
    }

    public Block getBlock() {
        return this.block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockSource.BlockSourceType getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(BlockSource.BlockSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ClickType getClickType() {
        return this.clickType;
    }

    public void setClickType(ClickType clickType) {
        this.clickType = clickType;
    }
}

