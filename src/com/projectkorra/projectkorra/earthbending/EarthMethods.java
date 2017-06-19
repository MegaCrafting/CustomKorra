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
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.FallingBlock
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.earthbending;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.Catapult;
import com.projectkorra.projectkorra.earthbending.CompactColumn;
import com.projectkorra.projectkorra.earthbending.EarthArmor;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.earthbending.EarthColumn;
import com.projectkorra.projectkorra.earthbending.EarthPassive;
import com.projectkorra.projectkorra.earthbending.EarthSmash;
import com.projectkorra.projectkorra.earthbending.EarthTunnel;
import com.projectkorra.projectkorra.earthbending.LavaFlow;
import com.projectkorra.projectkorra.earthbending.Shockwave;
import com.projectkorra.projectkorra.earthbending.Tremorsense;
import com.projectkorra.projectkorra.util.Information;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class EarthMethods {
    static ProjectKorra plugin;
    private static FileConfiguration config;
    public static ConcurrentHashMap<Block, Information> movedearth;
    public static ConcurrentHashMap<Integer, Information> tempair;
    public static HashSet<Block> tempNoEarthbending;
    public static Integer[] transparentToEarthbending;
    private static final ItemStack pickaxe;
    public static ArrayList<Block> tempnophysics;

    static {
        config = ProjectKorra.plugin.getConfig();
        movedearth = new ConcurrentHashMap();
        tempair = new ConcurrentHashMap();
        tempNoEarthbending = new HashSet();
        transparentToEarthbending = new Integer[]{0, 6, 8, 9, 10, 11, 30, 31, 32, 37, 38, 39, 40, 50, 51, 59, 78, 83, 106, 175};
        pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        tempnophysics = new ArrayList();
    }

    public EarthMethods(ProjectKorra plugin) {
        EarthMethods.plugin = plugin;
    }

    public static void addTempAirBlock(Block block) {
        if (movedearth.containsKey((Object)block)) {
            Information info = movedearth.get((Object)block);
            block.setType(Material.AIR);
            info.setTime(System.currentTimeMillis());
            movedearth.remove((Object)block);
            tempair.put(info.getID(), info);
        } else {
            Information info = new Information();
            info.setBlock(block);
            info.setState(block.getState());
            info.setTime(System.currentTimeMillis());
            block.setType(Material.AIR);
            tempair.put(info.getID(), info);
        }
    }

    public static boolean canSandbend(Player player) {
        if (player.hasPermission("bending.earth.sandbending")) {
            return true;
        }
        return false;
    }

    public static boolean canMetalbend(Player player) {
        if (player.hasPermission("bending.earth.metalbending")) {
            return true;
        }
        return false;
    }

    public static boolean canLavabend(Player player) {
        return player.hasPermission("bending.earth.lavabending");
    }


	public static void displaySandParticle(Location loc, float xOffset, float yOffset, float zOffset, float amount, float speed, boolean red) {
		if (amount <= 0)
			return;

		for (int x = 0; x < amount; x++) {
			if (!red) {
				ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.SAND, (byte) 0), new Vector(((Math.random() - 0.5) * xOffset), ((Math.random() - 0.5) * yOffset), ((Math.random() - 0.5) * zOffset)), speed, loc, 257.0D);
				ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.SANDSTONE, (byte) 0), new Vector(((Math.random() - 0.5) * xOffset), ((Math.random() - 0.5) * yOffset), ((Math.random() - 0.5) * zOffset)), speed, loc, 257.0D);
			} else if (red) {
				ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.SAND, (byte) 1), new Vector(((Math.random() - 0.5) * xOffset), ((Math.random() - 0.5) * yOffset), ((Math.random() - 0.5) * zOffset)), speed, loc, 257.0D);
				ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.RED_SANDSTONE, (byte) 0), new Vector(((Math.random() - 0.5) * xOffset), ((Math.random() - 0.5) * yOffset), ((Math.random() - 0.5) * zOffset)), speed, loc, 257.0D);
			}

		}
}
    public static ChatColor getEarthColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.Earth"));
    }

    public static ChatColor getEarthSubColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.EarthSub"));
    }

    public static int getEarthbendableBlocksLength(Player player, Block block, Vector direction, int maxlength) {
        Location location = block.getLocation();
        direction = direction.normalize();
        int i = 0;
        while (i <= maxlength) {
            double j = i;
            if (!EarthMethods.isEarthbendable(player, location.clone().add(direction.clone().multiply(j)).getBlock())) {
                return i;
            }
            ++i;
        }
        return maxlength;
    }

    public static Block getEarthSourceBlock(Player player, double range) {
        Block testblock = player.getTargetBlock(EarthMethods.getTransparentEarthbending(), (int)range);
        if (EarthMethods.isEarthbendable(player, testblock)) {
            return testblock;
        }
        Location location = player.getEyeLocation();
        Vector vector = location.getDirection().clone().normalize();
        double i = 0.0;
        while (i <= range) {
            Block block = location.clone().add(vector.clone().multiply(i)).getBlock();
            if (!GeneralMethods.isRegionProtectedFromBuild(player, "RaiseEarth", location) && EarthMethods.isEarthbendable(player, block)) {
                return block;
            }
            i += 1.0;
        }
        return null;
    }

    public static Block getNearbyEarthBlock(Location loc, double radius, int maxVertical) {
        if (loc == null) {
            return null;
        }
        int rotation = 30;
        int i = 0;
        while ((double)i < radius) {
            Vector tracer = new Vector(i, 0, 0);
            int deg = 0;
            while (deg < 360) {
                Location searchLoc = loc.clone().add(tracer);
                Block block = GeneralMethods.getTopBlock(searchLoc, maxVertical);
                if (block != null && EarthMethods.isEarthbendable(block.getType())) {
                    return block;
                }
                tracer = GeneralMethods.rotateXZ(tracer, rotation);
                deg += rotation;
            }
            ++i;
        }
        return null;
    }

    @Deprecated
    public static ChatColor getMetalbendingColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.Metalbending"));
    }

    public static HashSet<Byte> getTransparentEarthbending() {
        HashSet<Byte> set = new HashSet<Byte>();
        Integer[] arrinteger = transparentToEarthbending;
        int n = arrinteger.length;
        int n2 = 0;
        while (n2 < n) {
            int i = arrinteger[n2];
            set.add(Byte.valueOf((byte)i));
            ++n2;
        }
        return set;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Block getLavaSourceBlock(Player player, double range) {
        Location location = player.getEyeLocation();
        Vector vector = location.getDirection().clone().normalize();
        double i = 0.0;
        while (i <= range) {
            Block block = location.clone().add(vector.clone().multiply(i)).getBlock();
            if (!GeneralMethods.isRegionProtectedFromBuild(player, "LavaSurge", location) && EarthMethods.isLavabendable(block, player)) {
                if (!TempBlock.isTempBlock(block)) return block;
                TempBlock tb = TempBlock.get(block);
                byte full = 0;
                if (tb.getState().getRawData() == full) return block;
                if (tb.getState().getType() == Material.LAVA && tb.getState().getType() == Material.STATIONARY_LAVA) {
                    return block;
                }
            }
            i += 1.0;
        }
        return null;
    }

    public static boolean isLavabendingAbility(String ability) {
        return AbilityModuleManager.lavaabilities.contains(ability);
    }

    public static boolean isMetalbendingAbility(String ability) {
        return AbilityModuleManager.metalabilities.contains(ability);
    }

    public static boolean isSandbendingAbility(String ability) {
        return AbilityModuleManager.sandabilities.contains(ability);
    }

    public static boolean isEarthAbility(String ability) {
        return AbilityModuleManager.earthbendingabilities.contains(ability);
    }

    public static boolean isEarthbendable(Player player, Block block) {
        return EarthMethods.isEarthbendable(player, "RaiseEarth", block);
    }

    public static boolean isMetal(Block block) {
        Material material = block.getType();
        return config.getStringList("Properties.Earth.MetalBlocks").contains(material.toString());
    }

    public static double getMetalAugment(double value) {
        return value * config.getDouble("Properties.Earth.MetalPowerFactor");
    }

    public static boolean isEarthbendable(Material mat) {
        for (String s : config.getStringList("Properties.Earth.EarthbendableBlocks")) {
            if (mat != Material.getMaterial((String)s)) continue;
            return true;
        }
        return false;
    }

    public static boolean isEarthbendable(Player player, String ability, Block block) {
        Material material = block.getType();
        boolean valid = false;
        for (String s : config.getStringList("Properties.Earth.EarthbendableBlocks")) {
            if (material != Material.getMaterial((String)s)) continue;
            valid = true;
            break;
        }
        if (EarthMethods.isMetal(block) && EarthMethods.canMetalbend(player)) {
            valid = true;
        }
        if (!valid) {
            return false;
        }
        if (tempNoEarthbending.contains((Object)block)) {
            return false;
        }
        if (!GeneralMethods.isRegionProtectedFromBuild(player, ability, block.getLocation())) {
            return true;
        }
        return false;
    }

    public static boolean isMetalBlock(Block block) {
        if (block.getType() == Material.GOLD_BLOCK || block.getType() == Material.IRON_BLOCK || block.getType() == Material.IRON_ORE || block.getType() == Material.GOLD_ORE || block.getType() == Material.QUARTZ_BLOCK || block.getType() == Material.QUARTZ_ORE) {
            return true;
        }
        return false;
    }

    public static boolean isTransparentToEarthbending(Player player, Block block) {
        return EarthMethods.isTransparentToEarthbending(player, "RaiseEarth", block);
    }

    public static boolean isTransparentToEarthbending(Player player, String ability, Block block) {
        if (!Arrays.asList(transparentToEarthbending).contains(block.getTypeId())) {
            return false;
        }
        if (!GeneralMethods.isRegionProtectedFromBuild(player, ability, block.getLocation())) {
            return true;
        }
        return false;
    }

    public static boolean isLava(Block block) {
        if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
            return true;
        }
        return false;
    }

    public static boolean isLavabendable(Block block, Player player) {
        TempBlock tblock;
        byte full = 0;
        if (TempBlock.isTempBlock(block) && ((tblock = TempBlock.instances.get((Object)block)) == null || !LavaFlow.TEMP_LAVA_BLOCKS.contains(tblock))) {
            return false;
        }
        if ((block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) && block.getData() == full) {
            return true;
        }
        return false;
    }

    public static void moveEarth(Player player, Block block, Vector direction, int chainlength) {
        EarthMethods.moveEarth(player, block, direction, chainlength, true);
    }

    public static boolean moveEarth(Player player, Block block, Vector direction, int chainlength, boolean throwplayer) {
        if (EarthMethods.isEarthbendable(player, block) && !GeneralMethods.isRegionProtectedFromBuild(player, "RaiseEarth", block.getLocation())) {
            boolean up = false;
            boolean down = false;
            Vector norm = direction.clone().normalize();
            if (norm.dot(new Vector(0, 1, 0)) == 1.0) {
                up = true;
            } else if (norm.dot(new Vector(0, -1, 0)) == 1.0) {
                down = true;
            }
            Vector negnorm = norm.clone().multiply(-1);
            Location location = block.getLocation();
            ArrayList<Block> blocks = new ArrayList<Block>();
            double j = -2.0;
            while (j <= (double)chainlength) {
            	Block checkblock = location.clone().add(negnorm.clone().multiply(j)).getBlock();
                if (!tempnophysics.contains(checkblock)) {
                    blocks.add((Block)checkblock);
                    tempnophysics.add((Block)checkblock);
                }
                j += 1.0;
            }
            Block affectedblock = location.clone().add(norm).getBlock();
            if (EarthPassive.isPassiveSand(block)) {
                EarthPassive.revertSand(block);
            }
            if (affectedblock == null) {
                return false;
            }
            if (EarthMethods.isTransparentToEarthbending(player, affectedblock)) {
                if (throwplayer) {
                    for (Entity entity : GeneralMethods.getEntitiesAroundPoint(affectedblock.getLocation(), 1.75)) {
                        if (entity instanceof LivingEntity) {
                            LivingEntity lentity = (LivingEntity)entity;
                            if (lentity.getEyeLocation().getBlockX() != affectedblock.getX() || lentity.getEyeLocation().getBlockZ() != affectedblock.getZ() || entity instanceof FallingBlock) continue;
                            entity.setVelocity(norm.clone().multiply(0.75));
                            continue;
                        }
                        if (entity.getLocation().getBlockX() != affectedblock.getX() || entity.getLocation().getBlockZ() != affectedblock.getZ() || entity instanceof FallingBlock) continue;
                        entity.setVelocity(norm.clone().multiply(0.75));
                    }
                }
                if (up) {
                    Block topblock = affectedblock.getRelative(BlockFace.UP);
                    if (topblock.getType() != Material.AIR) {
                        GeneralMethods.breakBlock(affectedblock);
                    } else if (!affectedblock.isLiquid() && affectedblock.getType() != Material.AIR) {
                        EarthMethods.moveEarthBlock(affectedblock, topblock);
                    }
                } else {
                    GeneralMethods.breakBlock(affectedblock);
                }
                EarthMethods.moveEarthBlock(block, affectedblock);
                EarthMethods.playEarthbendingSound(block.getLocation());
                double i = 1.0;
                while (i < (double)chainlength) {
                    affectedblock = location.clone().add(negnorm.getX() * i, negnorm.getY() * i, negnorm.getZ() * i).getBlock();
                    if (!EarthMethods.isEarthbendable(player, affectedblock)) {
                        if (!down || !EarthMethods.isTransparentToEarthbending(player, affectedblock) || affectedblock.isLiquid() || affectedblock.getType() == Material.AIR) break;
                        EarthMethods.moveEarthBlock(affectedblock, block);
                        break;
                    }
                    if (EarthPassive.isPassiveSand(affectedblock)) {
                        EarthPassive.revertSand(affectedblock);
                    }
                    if (block == null) {
                        for (Block checkblock : blocks) {
                            tempnophysics.remove((Object)checkblock);
                        }
                        return false;
                    }
                    EarthMethods.moveEarthBlock(affectedblock, block);
                    block = affectedblock;
                    i += 1.0;
                }
                int i2 = chainlength;
                affectedblock = location.clone().add(negnorm.getX() * (double)i2, negnorm.getY() * (double)i2, negnorm.getZ() * (double)i2).getBlock();
                if (!EarthMethods.isEarthbendable(player, affectedblock) && down && EarthMethods.isTransparentToEarthbending(player, affectedblock) && !affectedblock.isLiquid()) {
                    EarthMethods.moveEarthBlock(affectedblock, block);
                }
            } else {
                for (Block checkblock : blocks) {
                    tempnophysics.remove((Object)checkblock);
                }
                return false;
            }
            for (Block checkblock : blocks) {
                tempnophysics.remove((Object)checkblock);
            }
            return true;
        }
        return false;
    }

    public static void moveEarth(Player player, Location location, Vector direction, int chainlength) {
        EarthMethods.moveEarth(player, location, direction, chainlength, true);
    }

    public static void moveEarth(Player player, Location location, Vector direction, int chainlength, boolean throwplayer) {
        Block block = location.getBlock();
        EarthMethods.moveEarth(player, block, direction, chainlength, throwplayer);
    }

    public static void moveEarthBlock(Block source, Block target) {
        Information info;
        byte full = 0;
        if (movedearth.containsKey((Object)source)) {
            info = movedearth.get((Object)source);
            info.setTime(System.currentTimeMillis());
            movedearth.remove((Object)source);
            movedearth.put(target, info);
        } else {
            info = new Information();
            info.setBlock(source);
            info.setTime(System.currentTimeMillis());
            info.setState(source.getState());
            movedearth.put(target, info);
        }
        if (GeneralMethods.isAdjacentToThreeOrMoreSources(source)) {
            source.setType(Material.WATER);
            source.setData(full);
        } else {
            source.setType(Material.AIR);
        }
        if (info.getState().getType() == Material.SAND) {
            if (info.getState().getRawData() == 1) {
                target.setType(Material.RED_SANDSTONE);
            } else {
                target.setType(Material.SANDSTONE);
            }
        } else {
            target.setType(info.getState().getType());
            target.setData(info.getState().getRawData());
        }
    }

    public static void playSandBendingSound(Location loc) {
        if (plugin.getConfig().getBoolean("Properties.Earth.PlaySound")) {
            loc.getWorld().playSound(loc, Sound.BLOCK_SAND_HIT, 1.5f, 5.0f);
        }
    }

    public static void removeAllEarthbendedBlocks() {
        for (Block block : movedearth.keySet()) {
            EarthMethods.revertBlock(block);
        }
        for (Integer i : tempair.keySet()) {
            EarthMethods.revertAirBlock(i, true);
        }
    }

    public static void removeRevertIndex(Block block) {
        if (movedearth.containsKey((Object)block)) {
            Information info = movedearth.get((Object)block);
            if (block.getType() == Material.SANDSTONE && info.getType() == Material.SAND) {
                block.setType(Material.SAND);
            }
            if (EarthColumn.blockInAllAffectedBlocks(block)) {
                EarthColumn.revertBlock(block);
            }
            movedearth.remove((Object)block);
        }
    }

    public static void revertAirBlock(int i) {
        EarthMethods.revertAirBlock(i, false);
    }

    public static void revertAirBlock(int i, boolean force) {
        if (!tempair.containsKey(i)) {
            return;
        }
        Information info = tempair.get(i);
        Block block = info.getState().getBlock();
        if (block.getType() != Material.AIR && !block.isLiquid()) {
            if (force || !movedearth.containsKey((Object)block)) {
                GeneralMethods.dropItems(block, GeneralMethods.getDrops(block, info.getState().getType(), info.getState().getRawData(), pickaxe));
                tempair.remove(i);
            } else {
                info.setTime(info.getTime() + 10000);
            }
            return;
        }
        info.getState().update(true);
        tempair.remove(i);
    }

    public static boolean revertBlock(Block block) {
        byte full = 0;
        if (!ProjectKorra.plugin.getConfig().getBoolean("Properties.Earth.RevertEarthbending")) {
            movedearth.remove((Object)block);
            return false;
        }
        if (movedearth.containsKey((Object)block)) {
            Information info = movedearth.get((Object)block);
            Block sourceblock = info.getState().getBlock();
            if (info.getState().getType() == Material.AIR) {
                movedearth.remove((Object)block);
                return true;
            }
            if (block.equals((Object)sourceblock)) {
                info.getState().update(true);
                if (EarthColumn.blockInAllAffectedBlocks(sourceblock)) {
                    EarthColumn.revertBlock(sourceblock);
                }
                if (EarthColumn.blockInAllAffectedBlocks(block)) {
                    EarthColumn.revertBlock(block);
                }
                movedearth.remove((Object)block);
                return true;
            }
            if (movedearth.containsKey((Object)sourceblock)) {
                EarthMethods.addTempAirBlock(block);
                movedearth.remove((Object)block);
                return true;
            }
            if (sourceblock.getType() == Material.AIR || sourceblock.isLiquid()) {
                info.getState().update(true);
            } else {
                GeneralMethods.dropItems(block, GeneralMethods.getDrops(block, info.getState().getType(), info.getState().getRawData(), pickaxe));
            }
            if (GeneralMethods.isAdjacentToThreeOrMoreSources(block)) {
                block.setType(Material.WATER);
                block.setData(full);
            } else {
                block.setType(Material.AIR);
            }
            if (EarthColumn.blockInAllAffectedBlocks(sourceblock)) {
                EarthColumn.revertBlock(sourceblock);
            }
            if (EarthColumn.blockInAllAffectedBlocks(block)) {
                EarthColumn.revertBlock(block);
            }
            movedearth.remove((Object)block);
        }
        return true;
    }

    public static void playEarthbendingSound(Location loc) {
        if (plugin.getConfig().getBoolean("Properties.Earth.PlaySound")) {
            loc.getWorld().playEffect(loc, Effect.GHAST_SHOOT, 0, 10);
        }
    }

    public static void playMetalbendingSound(Location loc) {
        if (plugin.getConfig().getBoolean("Properties.Earth.PlaySound")) {
            loc.getWorld().playSound(loc, Sound.ENTITY_IRONGOLEM_HURT, 1.0f, 10.0f);
        }
    }

    public static void stopBending() {
        Catapult.removeAll();
        CompactColumn.removeAll();
        EarthBlast.removeAll();
        EarthColumn.removeAll();
        EarthPassive.removeAll();
        EarthArmor.removeAll();
        EarthTunnel.instances.clear();
        Shockwave.removeAll();
        Tremorsense.removeAll();
        LavaFlow.removeAll();
        EarthSmash.removeAll();
        if (ProjectKorra.plugin.getConfig().getBoolean("Properties.Earth.RevertEarthbending")) {
            EarthMethods.removeAllEarthbendedBlocks();
        }
        EarthPassive.removeAll();
    }
}

