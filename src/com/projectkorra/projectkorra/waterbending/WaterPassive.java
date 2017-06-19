/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthArmor;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterSpout;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WaterPassive {
    private static double swimFactor = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Passive.SwimSpeedFactor");

    public static boolean applyNoFall(Player player) {
        Block block = player.getLocation().getBlock();
        Block fallblock = block.getRelative(BlockFace.DOWN);
        if (TempBlock.isTempBlock(fallblock) && fallblock.getType().equals((Object)Material.ICE)) {
            return true;
        }
        if (WaterMethods.isWaterbendable(block, player) && !WaterMethods.isPlant(block)) {
            return true;
        }
        if (fallblock.getType() == Material.AIR) {
            return true;
        }
        if (WaterMethods.isWaterbendable(fallblock, player) && !WaterMethods.isPlant(fallblock) || fallblock.getType() == Material.SNOW_BLOCK) {
            return true;
        }
        return false;
    }

    public static void handlePassive() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            String ability = GeneralMethods.getBoundAbility(player);
            if (!GeneralMethods.canBendPassive(player.getName(), Element.Water) || WaterSpout.instances.containsKey((Object)player) || EarthArmor.instances.containsKey((Object)player) || ability != null && AbilityModuleManager.shiftabilities.contains(ability) || !player.isSneaking() || !WaterMethods.isWater(player.getLocation().getBlock())) continue;
            player.setVelocity(player.getEyeLocation().getDirection().clone().normalize().multiply(swimFactor));
        }
    }
}

