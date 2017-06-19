/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingManager;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Bloodbending;
import com.projectkorra.projectkorra.waterbending.FreezeMelt;
import com.projectkorra.projectkorra.waterbending.IceSpike;
import com.projectkorra.projectkorra.waterbending.IceSpike2;
import com.projectkorra.projectkorra.waterbending.OctopusForm;
import com.projectkorra.projectkorra.waterbending.PlantArmor;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.WaterArms;
import com.projectkorra.projectkorra.waterbending.WaterCombo;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterReturn;
import com.projectkorra.projectkorra.waterbending.WaterSpout;
import com.projectkorra.projectkorra.waterbending.WaterWall;
import com.projectkorra.projectkorra.waterbending.WaterWave;
import com.projectkorra.projectkorra.waterbending.Wave;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WaterMethods {
    static ProjectKorra plugin;
    private static FileConfiguration config;
    private static Integer[] plantIds;

    static {
        config = ProjectKorra.plugin.getConfig();
        plantIds = new Integer[]{6, 18, 31, 37, 38, 39, 40, 59, 81, 83, 86, 99, 100, 103, 104, 105, 106, 111, 161, 175};
    }

    public WaterMethods(ProjectKorra plugin) {
        WaterMethods.plugin = plugin;
    }

    public static boolean canBeBloodbent(Player player) {
        if (AvatarState.isAvatarState(player) && ChiMethods.isChiBlocked(player.getName())) {
            return true;
        }
        if (GeneralMethods.canBend(player.getName(), "Bloodbending") && !GeneralMethods.getBendingPlayer(player.getName()).isToggled()) {
            return false;
        }
        return true;
    }

    public static boolean canBloodbend(Player player) {
        if (player.hasPermission("bending.water.bloodbending")) {
            return true;
        }
        return false;
    }

    public static boolean canBloodbendAtAnytime(Player player) {
        if (WaterMethods.canBloodbend(player) && player.hasPermission("bending.water.bloodbending.anytime")) {
            return true;
        }
        return false;
    }

    public static boolean canIcebend(Player player) {
        if (player.hasPermission("bending.water.icebending")) {
            return true;
        }
        return false;
    }

    public static boolean canWaterHeal(Player player) {
        if (player.hasPermission("bending.water.healing")) {
            return true;
        }
        return false;
    }

    public static boolean canPlantbend(Player player) {
        return player.hasPermission("bending.water.plantbending");
    }

    public static double getWaterbendingNightAugment(World world) {
        if (WaterMethods.isNight(world) && BendingManager.events.get((Object)world).equalsIgnoreCase("FullMoon")) {
            return config.getDouble("Properties.Water.FullMoonFactor");
        }
        if (WaterMethods.isNight(world)) {
            return config.getDouble("Properties.Water.NightFactor");
        }
        return 1.0;
    }

    public static ChatColor getWaterColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.Water"));
    }

    public static ChatColor getWaterSubColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.WaterSub"));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Block getWaterSourceBlock(Player player, double range, boolean plantbending) {
        Location location = player.getEyeLocation();
        Vector vector = location.getDirection().clone().normalize();
        double i = 0.0;
        while (i <= range) {
            Block block = location.clone().add(vector.clone().multiply(i)).getBlock();
            if (!GeneralMethods.isRegionProtectedFromBuild(player, "WaterManipulation", location) && WaterMethods.isWaterbendable(block, player) && (!WaterMethods.isPlant(block) || plantbending)) {
                if (!TempBlock.isTempBlock(block)) return block;
                TempBlock tb = TempBlock.get(block);
                byte full = 0;
                if (tb.getState().getRawData() == full) return block;
                if (tb.getState().getType() == Material.WATER && tb.getState().getType() == Material.STATIONARY_WATER) {
                    return block;
                }
            }
            i += 1.0;
        }
        return null;
    }

    public static Block getIceSourceBlock(Player player, double range) {
        Location location = player.getEyeLocation();
        Vector vector = location.getDirection().clone().normalize();
        double i = 0.0;
        while (i <= range) {
            Block block = location.clone().add(vector.clone().multiply(i)).getBlock();
            if (!GeneralMethods.isRegionProtectedFromBuild(player, "IceBlast", location) && WaterMethods.isIcebendable(block) && !TempBlock.isTempBlock(block)) {
                return block;
            }
            i += 1.0;
        }
        return null;
    }

    public static Block getPlantSourceBlock(Player player, double range, boolean onlyLeaves) {
        Location location = player.getEyeLocation();
        Vector vector = location.getDirection().clone().normalize();
        double i = 0.0;
        while (i <= range) {
            Block block = location.clone().add(vector.clone().multiply(i)).getBlock();
            if (!GeneralMethods.isRegionProtectedFromBuild(player, "PlantDisc", location) && WaterMethods.isPlantbendable(block, onlyLeaves) && !TempBlock.isTempBlock(block)) {
                return block;
            }
            i += 1.0;
        }
        return null;
    }

    public static boolean isAdjacentToFrozenBlock(Block block) {
        BlockFace[] faces = new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH};
        boolean adjacent = false;
        BlockFace[] arrblockFace = faces;
        int n = arrblockFace.length;
        int n2 = 0;
        while (n2 < n) {
            BlockFace face = arrblockFace[n2];
            if (FreezeMelt.frozenblocks.containsKey((Object)block.getRelative(face))) {
                adjacent = true;
            }
            ++n2;
        }
        return adjacent;
    }

    public static boolean isHealingAbility(String ability) {
        return AbilityModuleManager.healingabilities.contains(ability);
    }

    public static boolean isIcebendingAbility(String ability) {
        return AbilityModuleManager.iceabilities.contains(ability);
    }

    public static boolean isPlantbendingAbility(String ability) {
        return AbilityModuleManager.plantabilities.contains(ability);
    }

    public static boolean isBloodbendingAbility(String ability) {
        return AbilityModuleManager.bloodabilities.contains(ability);
    }

    public static boolean isFullMoon(World world) {
        long days = world.getFullTime() / 24000;
        long phase = days % 8;
        if (phase == 0) {
            return true;
        }
        return false;
    }

    public static boolean isMeltable(Block block) {
        if (block.getType() == Material.ICE || block.getType() == Material.SNOW) {
            return true;
        }
        return false;
    }

    public static boolean isNight(World world) {
        if (world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.THE_END) {
            return false;
        }
        long time = world.getTime();
        if (time >= 12950 && time <= 23050) {
            return true;
        }
        return false;
    }

    public static boolean isPlant(Block block) {
        if (block == null) {
            return false;
        }
        if (Arrays.asList(plantIds).contains(block.getTypeId())) {
            return true;
        }
        return false;
    }

    public static boolean isWater(Block block) {
        if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
            return true;
        }
        return false;
    }

    public static boolean isWaterAbility(String ability) {
        return AbilityModuleManager.waterbendingabilities.contains(ability);
    }

    public static boolean isWaterbendable(Block block, Player player) {
        byte full = 0;
        if (TempBlock.isTempBlock(block)) {
            return false;
        }
        if ((block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) && block.getData() == full) {
            return true;
        }
        if (block.getType() == Material.ICE || block.getType() == Material.SNOW) {
            return true;
        }
        if (block.getType() == Material.PACKED_ICE && plugin.getConfig().getBoolean("Properties.Water.CanBendPackedIce")) {
            return true;
        }
        if (WaterMethods.canPlantbend(player) && WaterMethods.isPlant(block)) {
            return true;
        }
        return false;
    }

    public static boolean isIcebendable(Block block) {
        if (block.getType() == Material.ICE) {
            return true;
        }
        if (block.getType() == Material.PACKED_ICE && plugin.getConfig().getBoolean("Properties.Water.CanBendPackedIce")) {
            return true;
        }
        return false;
    }

    public static boolean isPlantbendable(Block block, boolean leavesOnly) {
        if (block.getType() == Material.LEAVES) {
            return true;
        }
        if (block.getType() == Material.LEAVES_2) {
            return true;
        }
        if (WaterMethods.isPlant(block) && !leavesOnly) {
            return true;
        }
        return false;
    }

    public static boolean isPlantbendable(Block block) {
        return WaterMethods.isPlantbendable(block, false);
    }

    public static void playFocusWaterEffect(Block block) {
        block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 4, 20);
    }

    public static void removeWaterSpouts(Location loc, double radius, Player source) {
        WaterSpout.removeSpouts(loc, radius, source);
    }

    public static void removeWaterSpouts(Location loc, Player source) {
        WaterMethods.removeWaterSpouts(loc, 1.5, source);
    }

    public static double waterbendingNightAugment(double value, World world) {
        if (WaterMethods.isNight(world)) {
            if (WaterMethods.isFullMoon(world)) {
                return plugin.getConfig().getDouble("Properties.Water.FullMoonFactor") * value;
            }
            return plugin.getConfig().getDouble("Properties.Water.NightFactor") * value;
        }
        return value;
    }

    public static boolean isNegativeEffect(PotionEffectType effect) {
        if (effect.equals((Object)PotionEffectType.POISON)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.BLINDNESS)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.CONFUSION)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.HARM)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.HUNGER)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.SLOW)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.SLOW_DIGGING)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.WEAKNESS)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.WITHER)) {
            return true;
        }
        return false;
    }

    public static boolean isPositiveEffect(PotionEffectType effect) {
        if (effect.equals((Object)PotionEffectType.ABSORPTION)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.DAMAGE_RESISTANCE)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.FAST_DIGGING)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.FIRE_RESISTANCE)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.HEAL)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.HEALTH_BOOST)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.INCREASE_DAMAGE)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.JUMP)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.NIGHT_VISION)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.REGENERATION)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.SATURATION)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.SPEED)) {
            return true;
        }
        if (effect.equals((Object)PotionEffectType.WATER_BREATHING)) {
            return true;
        }
        return false;
    }

    public static boolean isNeutralEffect(PotionEffectType effect) {
        if (effect.equals((Object)PotionEffectType.INVISIBILITY)) {
            return true;
        }
        return false;
    }

    public static void playWaterbendingSound(Location loc) {
        if (plugin.getConfig().getBoolean("Properties.Water.PlaySound")) {
            loc.getWorld().playSound(loc, Sound.BLOCK_WATER_AMBIENT, 1.0f, 10.0f);
        }
    }

    public static void playIcebendingSound(Location loc) {
        if (plugin.getConfig().getBoolean("Properties.Water.PlaySound")) {
            loc.getWorld().playSound(loc, Sound.ITEM_FLINTANDSTEEL_USE, 2.0f, 10.0f);
        }
    }

    public static void playPlantbendingSound(Location loc) {
        if (plugin.getConfig().getBoolean("Properties.Water.PlaySound")) {
            loc.getWorld().playSound(loc, Sound.BLOCK_GRASS_STEP, 1.0f, 10.0f);
        }
    }

    public static void stopBending() {
        FreezeMelt.removeAll();
        IceSpike.removeAll();
        IceSpike2.removeAll();
        WaterManipulation.removeAll();
        WaterSpout.removeAll();
        WaterWall.removeAll();
        Wave.removeAll();
        Plantbending.regrowAll();
        OctopusForm.removeAll();
        Bloodbending.instances.clear();
        WaterWave.removeAll();
        WaterCombo.removeAll();
        WaterReturn.removeAll();
        WaterArms.removeAll();
        PlantArmor.removeAll();
    }
}

