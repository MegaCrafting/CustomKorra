/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.firebending;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.Melt;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class HeatMelt
implements ConfigLoadable {
    private static int range = config.get().getInt("Abilities.Fire.HeatControl.Melt.Range");
    private static int radius = config.get().getInt("Abilities.Fire.HeatControl.Melt.Radius");

    public HeatMelt(Player player) {
        Location location = GeneralMethods.getTargetedLocation(player, (int)FireMethods.getFirebendingDayAugment(range, player.getWorld()));
        for (Block block : GeneralMethods.getBlocksAroundPoint(location, (int)FireMethods.getFirebendingDayAugment(radius, player.getWorld()))) {
            if (WaterMethods.isMeltable(block)) {
                Melt.melt(player, block);
                continue;
            }
            if (!HeatMelt.isHeatable(block)) continue;
            HeatMelt.heat(block);
        }
    }

    private static void heat(Block block) {
        if (block.getType() == Material.OBSIDIAN) {
            block.setType(Material.LAVA);
            block.setData((byte)0);
        }
    }

    private static boolean isHeatable(Block block) {
        return false;
    }

    @Override
    public void reloadVariables() {
        config.get().getInt("Abilities.Fire.HeatControl.Melt.Range");
        radius = config.get().getInt("Abilities.Fire.HeatControl.Melt.Radius");
    }
}

