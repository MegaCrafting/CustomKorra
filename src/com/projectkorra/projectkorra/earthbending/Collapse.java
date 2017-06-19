/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.CompactColumn;
import com.projectkorra.projectkorra.earthbending.EarthColumn;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;

public class Collapse {
    public static final int range = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.Collapse.Range");
    private static final double defaultradius = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Collapse.Radius");
    private static final int height = EarthColumn.standardheight;
    private ConcurrentHashMap<Block, Block> blocks = new ConcurrentHashMap();
    private ConcurrentHashMap<Block, Integer> baseblocks = new ConcurrentHashMap();
    private double radius = defaultradius;
    private Player player;

    public Collapse(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("Collapse")) {
            return;
        }
        this.player = player;
        Block sblock = BlockSource.getEarthSourceBlock(player, range, ClickType.SHIFT_DOWN);
        Location location = sblock == null ? player.getTargetBlock(EarthMethods.getTransparentEarthbending(), range).getLocation() : sblock.getLocation();
        for (Block block2 : GeneralMethods.getBlocksAroundPoint(location, this.radius)) {
            if (!EarthMethods.isEarthbendable(player, block2) || this.blocks.containsKey((Object)block2) || block2.getY() < location.getBlockY()) continue;
            this.getAffectedBlocks(block2);
        }
        if (!this.baseblocks.isEmpty()) {
            bPlayer.addCooldown("Collapse", GeneralMethods.getGlobalCooldown());
        }
        for (Block block2 : this.baseblocks.keySet()) {
            new com.projectkorra.projectkorra.earthbending.CompactColumn(player, block2.getLocation());
        }
    }


	private void getAffectedBlocks(Block block) {
		Block baseblock = block;
		int tall = 0;
		ArrayList<Block> bendableblocks = new ArrayList<Block>();
		bendableblocks.add(block);
		for (int i = 1; i <= height; i++) {
			Block blocki = block.getRelative(BlockFace.DOWN, i);
			if (EarthMethods.isEarthbendable(player, blocki)) {
				baseblock = blocki;
				bendableblocks.add(blocki);
				tall++;
			} else {
				break;
			}
		}
		baseblocks.put(baseblock, tall);
		for (Block blocki : bendableblocks) {
			blocks.put(blocki, baseblock);
		}

	}
}

