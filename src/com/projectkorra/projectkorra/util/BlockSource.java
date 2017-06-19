/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.util;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.BlockSourceInformation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.util.ClickType;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class BlockSource {
    private static HashMap<Player, HashMap<BlockSourceType, HashMap<ClickType, BlockSourceInformation>>> playerSources = new HashMap();
    private static FileConfiguration config = ConfigManager.defaultConfig.get();
    private static double MAX_RANGE = config.getDouble("Abilities.Water.WaterManipulation.Range");

    public static void update(Player player, ClickType clickType) {
        String boundAbil = GeneralMethods.getBoundAbility(player);
        if (boundAbil == null) {
            return;
        }
        if (WaterMethods.isWaterAbility(boundAbil)) {
            Block waterBlock = WaterMethods.getWaterSourceBlock(player, MAX_RANGE, true);
            if (waterBlock != null) {
                BlockSource.putSource(player, waterBlock, BlockSourceType.WATER, clickType);
                if (WaterMethods.isPlant(waterBlock)) {
                    BlockSource.putSource(player, waterBlock, BlockSourceType.PLANT, clickType);
                }
                if (WaterMethods.isIcebendable(waterBlock)) {
                    BlockSource.putSource(player, waterBlock, BlockSourceType.ICE, clickType);
                }
            }
        } else if (EarthMethods.isEarthAbility(boundAbil)) {
            double lavaDist;
            Block earthBlock = EarthMethods.getEarthSourceBlock(player, MAX_RANGE);
            if (earthBlock != null) {
                BlockSource.putSource(player, earthBlock, BlockSourceType.EARTH, clickType);
                if (EarthMethods.isMetal(earthBlock)) {
                    BlockSource.putSource(player, earthBlock, BlockSourceType.METAL, clickType);
                }
            }
            Block lavaBlock = EarthMethods.getLavaSourceBlock(player, MAX_RANGE);
            double earthDist = earthBlock != null ? earthBlock.getLocation().distanceSquared(player.getLocation()) : Double.MAX_VALUE;
            double d = lavaDist = lavaBlock != null ? lavaBlock.getLocation().distanceSquared(player.getLocation()) : Double.MAX_VALUE;
            if (lavaBlock != null && lavaDist <= earthDist) {
                BlockSource.putSource(player, null, BlockSourceType.EARTH, clickType);
                BlockSource.putSource(player, lavaBlock, BlockSourceType.LAVA, clickType);
            }
        }
    }

    private static void putSource(Player player, Block block, BlockSourceType sourceType, ClickType clickType) {
        if (!playerSources.containsKey((Object)player)) {
            playerSources.put(player, new HashMap());
        }
        if (!playerSources.get((Object)player).containsKey((Object)sourceType)) {
            playerSources.get((Object)player).put(sourceType, new HashMap());
        }
        BlockSourceInformation info = new BlockSourceInformation(player, block, sourceType, clickType);
        playerSources.get((Object)player).get((Object)sourceType).put(clickType, info);
    }

    public static BlockSourceInformation getBlockSourceInformation(Player player, BlockSourceType sourceType, ClickType clickType) {
        if (!playerSources.containsKey((Object)player)) {
            return null;
        }
        if (!playerSources.get((Object)player).containsKey((Object)sourceType)) {
            return null;
        }
        if (!playerSources.get((Object)player).get((Object)sourceType).containsKey((Object)clickType)) {
            return null;
        }
        return playerSources.get((Object)player).get((Object)sourceType).get((Object)clickType);
    }

    public static BlockSourceInformation getValidBlockSourceInformation(Player player, double range, BlockSourceType sourceType, ClickType clickType) {
        BlockSourceInformation blockInfo = BlockSource.getBlockSourceInformation(player, sourceType, clickType);
        return BlockSource.isStillAValidSource(blockInfo, range, clickType) ? blockInfo : null;
    }

    public static Block getSourceBlock(Player player, double range, BlockSourceType sourceType, ClickType clickType) {
        BlockSourceInformation info = BlockSource.getValidBlockSourceInformation(player, range, sourceType, clickType);
        return info != null ? info.getBlock() : null;
    }

    public static Block getWaterSourceBlock(Player player, double range) {
        return BlockSource.getWaterSourceBlock(player, range, ClickType.LEFT_CLICK);
    }

    public static Block getWaterSourceBlock(Player player, double range, ClickType clickType) {
        return BlockSource.getWaterSourceBlock(player, range, clickType, true, true, true);
    }

    public static Block getWaterSourceBlock(Player player, double range, boolean allowWater, boolean allowIce, boolean allowPlant) {
        return BlockSource.getWaterSourceBlock(player, range, ClickType.LEFT_CLICK, allowWater, allowIce, allowPlant);
    }

    public static Block getWaterSourceBlock(Player player, double range, ClickType clickType, boolean allowWater, boolean allowIce, boolean allowPlant) {
        return BlockSource.getWaterSourceBlock(player, range, clickType, allowWater, allowIce, allowPlant, true);
    }

    public static Block getWaterSourceBlock(Player player, double range, ClickType clickType, boolean allowWater, boolean allowIce, boolean allowPlant, boolean allowWaterBottles) {
        Block sourceBlock = null;
        if (allowWaterBottles && ((sourceBlock = WaterMethods.getWaterSourceBlock(player, range, allowPlant)) == null || sourceBlock.getLocation().distance(player.getEyeLocation()) > 3.0)) {
            sourceBlock = null;
        }
        if (allowWater && sourceBlock == null) {
            sourceBlock = BlockSource.getSourceBlock(player, range, BlockSourceType.WATER, clickType);
        }
        if (allowIce && sourceBlock == null) {
            sourceBlock = BlockSource.getSourceBlock(player, range, BlockSourceType.ICE, clickType);
        }
        if (allowPlant && sourceBlock == null) {
            sourceBlock = BlockSource.getSourceBlock(player, range, BlockSourceType.PLANT, clickType);
        }
        return sourceBlock;
    }

    public static Block getEarthSourceBlock(Player player, double range, ClickType clickType) {
        return BlockSource.getEarthSourceBlock(player, range, clickType, true);
    }

    public static Block getEarthSourceBlock(Player player, double range, ClickType clickType, boolean allowNearbySubstitute) {
        Block sourceBlock = BlockSource.getSourceBlock(player, range, BlockSourceType.EARTH, clickType);
        if (sourceBlock == null && allowNearbySubstitute) {
            BlockSourceInformation blockInfo = BlockSource.getBlockSourceInformation(player, BlockSourceType.EARTH, clickType);
            if (blockInfo == null) {
                return null;
            }
            Block tempBlock = blockInfo.getBlock();
            if (tempBlock == null) {
                return null;
            }
            Location loc = tempBlock.getLocation();
            sourceBlock = EarthMethods.getNearbyEarthBlock(loc, 3.0, 3);
            if (sourceBlock == null || !sourceBlock.getLocation().getWorld().equals((Object)player.getWorld()) || Math.abs(sourceBlock.getLocation().distance(player.getEyeLocation())) > range || !EarthMethods.isEarthbendable(player, sourceBlock)) {
                return null;
            }
        }
        return sourceBlock;
    }

    public static Block getLavaSourceBlock(Player player, double range, ClickType clickType) {
        return BlockSource.getSourceBlock(player, range, BlockSourceType.LAVA, clickType);
    }

    public static Block getEarthOrLavaSourceBlock(Player player, double range, ClickType clickType) {
        Block earthBlock = BlockSource.getEarthSourceBlock(player, range, clickType);
        BlockSourceInformation lavaBlockInfo = BlockSource.getValidBlockSourceInformation(player, range, BlockSourceType.LAVA, clickType);
        if (earthBlock != null) {
            return earthBlock;
        }
        if (lavaBlockInfo != null) {
            return lavaBlockInfo.getBlock();
        }
        return null;
    }

    private static boolean isStillAValidSource(BlockSourceInformation info, double range, ClickType clickType) {
        if (info == null || info.getBlock() == null) {
            return false;
        }
        if (info.getClickType() != clickType) {
            return false;
        }
        if (!info.getPlayer().getWorld().equals((Object)info.getBlock().getWorld())) {
            return false;
        }
        if (Math.abs(info.getPlayer().getLocation().distance(info.getBlock().getLocation())) > range) {
            return false;
        }
        if (info.getSourceType() == BlockSourceType.WATER && !WaterMethods.isWaterbendable(info.getBlock(), info.getPlayer())) {
            return false;
        }
        if (info.getSourceType() == BlockSourceType.ICE && !WaterMethods.isIcebendable(info.getBlock())) {
            return false;
        }
        if (!(info.getSourceType() != BlockSourceType.PLANT || WaterMethods.isPlant(info.getBlock()) && WaterMethods.isWaterbendable(info.getBlock(), info.getPlayer()))) {
            return false;
        }
        if (info.getSourceType() == BlockSourceType.EARTH && !EarthMethods.isEarthbendable(info.getPlayer(), info.getBlock())) {
            return false;
        }
        if (!(info.getSourceType() != BlockSourceType.METAL || EarthMethods.isMetal(info.getBlock()) && EarthMethods.isEarthbendable(info.getPlayer(), info.getBlock()))) {
            return false;
        }
        if (!(info.getSourceType() != BlockSourceType.LAVA || EarthMethods.isLava(info.getBlock()) && EarthMethods.isLavabendable(info.getBlock(), info.getPlayer()))) {
            return false;
        }
        return true;
    }

    public static enum BlockSourceType {
        WATER,
        ICE,
        PLANT,
        EARTH,
        METAL,
        LAVA;
        

        
    }

}

