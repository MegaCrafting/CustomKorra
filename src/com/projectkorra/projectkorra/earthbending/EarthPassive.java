/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.material.MaterialData
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.earthbending.LavaWall;
import com.projectkorra.projectkorra.earthbending.LavaWave;
import com.projectkorra.projectkorra.util.TempBlock;

public class EarthPassive {
    public static ConcurrentHashMap<Block, Long> sandblocks = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, MaterialData> sandidentities = new ConcurrentHashMap();
    private static final long duration = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.Passive.Duration");
    private static int sandspeed = ProjectKorra.plugin.getConfig().getInt("Properties.Earth.Passive.SandRunPower");

    public static boolean softenLanding(Player player) {
        Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (EarthMethods.canMetalbend(player) && EarthMethods.isMetalBlock(block)) {
            return true;
        }
        if (EarthMethods.isEarthbendable(player, block) || EarthMethods.isTransparentToEarthbending(player, block)) {
            if (!EarthMethods.isTransparentToEarthbending(player, block)) {
                MaterialData type = block.getState().getData();
                if (GeneralMethods.isSolid(block.getRelative(BlockFace.DOWN))) {
                    if (type.getItemType() == Material.RED_SANDSTONE) {
                        byte data = 1;
                        block.setType(Material.SAND);
                        block.setData(data);
                    } else {
                        block.setType(Material.SAND);
                    }
                    if (!sandblocks.containsKey((Object)block)) {
                        sandidentities.put(block, type);
                        sandblocks.put(block, System.currentTimeMillis());
                    }
                }
            }
            for (Block affectedBlock : GeneralMethods.getBlocksAroundPoint(block.getLocation(), 2.0)) {
                if (!EarthMethods.isEarthbendable(player, affectedBlock) || !GeneralMethods.isSolid(affectedBlock.getRelative(BlockFace.DOWN))) continue;
                MaterialData type = affectedBlock.getState().getData();
                if (type.getItemType() == Material.RED_SANDSTONE) {
                    byte data = 1;
                    affectedBlock.setType(Material.SAND);
                    affectedBlock.setData(data);
                } else {
                    affectedBlock.setType(Material.SAND);
                }
                if (sandblocks.containsKey((Object)affectedBlock)) continue;
                sandidentities.putIfAbsent(affectedBlock, type);
                sandblocks.put(affectedBlock, System.currentTimeMillis());
            }
            return true;
        }
        if (EarthMethods.isEarthbendable(player, block) || EarthMethods.isTransparentToEarthbending(player, block)) {
            return true;
        }
        return false;
    }

    public static boolean isPassiveSand(Block block) {
        return sandblocks.containsKey((Object)block);
    }

    public static void revertSand(Block block) {
        MaterialData materialdata = sandidentities.get((Object)block);
        sandidentities.remove((Object)block);
        sandblocks.remove((Object)block);
        if (block.getType() == Material.SAND) {
            block.setType(materialdata.getItemType());
            block.setData(materialdata.getData());
        }
    }

    public static void sandSpeed() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p == null || GeneralMethods.getBendingPlayer(p.getName()) == null || !EarthMethods.canSandbend(p) || !GeneralMethods.getBendingPlayer(p.getName()).hasElement(Element.Earth) || GeneralMethods.canBendPassive(p.getName(), Element.Air) || GeneralMethods.canBendPassive(p.getName(), Element.Chi) || p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.SAND && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.SANDSTONE && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.RED_SANDSTONE) continue;
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, sandspeed - 1));
        }
    }

    public static void handleMetalPassives() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (GeneralMethods.canBendPassive(player.getName(), Element.Earth) && EarthMethods.canMetalbend(player)) {
				if (player.isSneaking() && !GeneralMethods.getBendingPlayer(player.getName()).isOnCooldown("MetalPassive")) {
					Block block = player.getTargetBlock((HashSet<Material>) null, 5);
					if (block == null)
						continue;
					if (block.getType() == Material.IRON_DOOR_BLOCK && !GeneralMethods.isRegionProtectedFromBuild(player, null, block.getLocation())) {
						if (block.getData() >= 8) {
							block = block.getRelative(BlockFace.DOWN);
						}

						if (block.getData() < 4) {
							block.setData((byte) (block.getData() + 4));
							block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 10, 1);
						} else {
							block.setData((byte) (block.getData() - 4));
							block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 10, 1);
						}

						GeneralMethods.getBendingPlayer(player.getName()).addCooldown("MetalPassive", 200);

						//						Door door = (Door) block.getState().getData();
						//						if (door.isTopHalf()) {
						//							block = block.getRelative(BlockFace.DOWN);
						//							if (door.isOpen()) {
						//								door.setOpen(false);
						//							} else {
						//								door.setOpen(true);
						//							}
						//						}
					}
				}
			}
		}
}
    public static void revertSands() {
        for (Block block : sandblocks.keySet()) {
            if (System.currentTimeMillis() < sandblocks.get((Object)block) + duration) continue;
            EarthPassive.revertSand(block);
        }
    }

    public static void revertAllSand() {
        for (Block block : sandblocks.keySet()) {
            EarthPassive.revertSand(block);
        }
    }

    public static void removeAll() {
        EarthPassive.revertAllSand();
    }

    public static boolean canPhysicsChange(Block block) {
        if (LavaWall.affectedblocks.containsKey((Object)block)) {
            return false;
        }
        if (LavaWall.wallblocks.containsKey((Object)block)) {
            return false;
        }
        if (LavaWave.isBlockWave(block)) {
            return false;
        }
        if (TempBlock.isTempBlock(block)) {
            return false;
        }
        if (TempBlock.isTouchingTempBlock(block)) {
            return false;
        }
        return true;
    }

    public static boolean canFlowFromTo(Block from, Block to) {
        if (LavaWall.affectedblocks.containsKey((Object)to) || LavaWall.affectedblocks.containsKey((Object)from)) {
            return false;
        }
        if (LavaWall.wallblocks.containsKey((Object)to) || LavaWall.wallblocks.containsKey((Object)from)) {
            return false;
        }
        if (LavaWave.isBlockWave(to) || LavaWave.isBlockWave(from)) {
            return false;
        }
        if (TempBlock.isTempBlock(to) || TempBlock.isTempBlock(from)) {
            return false;
        }
        return true;
    }
}

