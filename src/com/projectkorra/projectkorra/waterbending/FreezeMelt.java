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
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.OctopusForm;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class FreezeMelt {
    public static ConcurrentHashMap<Block, Byte> frozenblocks = new ConcurrentHashMap();
    public static final int defaultrange = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.PhaseChange.Range");
    public static final int defaultradius = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.PhaseChange.Radius");
    public static final int OVERLOADING_LIMIT = 200;
    public static boolean overloading = false;
    public static int overloadCounter = 0;

    public FreezeMelt(Player player) {
        if (!WaterMethods.canIcebend(player)) {
            return;
        }
        int range = (int)WaterMethods.waterbendingNightAugment(defaultrange, player.getWorld());
        int radius = (int)WaterMethods.waterbendingNightAugment(defaultradius, player.getWorld());
        if (AvatarState.isAvatarState(player)) {
            range = AvatarState.getValue(range);
        }
        Location location = GeneralMethods.getTargetedLocation(player, range);
        for (Block block : GeneralMethods.getBlocksAroundPoint(location, radius)) {
            if (!FreezeMelt.isFreezable(player, block)) continue;
            FreezeMelt.freeze(player, block);
        }
    }

    private static boolean isFreezable(Player player, Block block) {
        if (GeneralMethods.isRegionProtectedFromBuild(player, "PhaseChange", block.getLocation())) {
            return false;
        }
        if ((block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) && WaterManipulation.canPhysicsChange(block) && !TempBlock.isTempBlock(block)) {
            return true;
        }
        return false;
    }

    static void freeze(Player player, Block block) {
        if (GeneralMethods.isRegionProtectedFromBuild(player, "PhaseChange", block.getLocation())) {
            return;
        }
        if (TempBlock.isTempBlock(block)) {
            return;
        }
        byte data = block.getData();
        block.setType(Material.ICE);
        if (frozenblocks.size() % 50 == 0) {
            WaterMethods.playIcebendingSound(block.getLocation());
        }
        frozenblocks.put(block, Byte.valueOf(data));
    }

    public static void thaw(Block block) {
        if (frozenblocks.containsKey((Object)block)) {
            byte data = frozenblocks.get((Object)block).byteValue();
            frozenblocks.remove((Object)block);
            block.setType(Material.WATER);
            block.setData(data);
        }
    }

    public static void handleFrozenBlocks() {
        int size = frozenblocks.keySet().size();
        ++overloadCounter;
        if ((overloadCounter %= 10) == 0) {
            boolean bl = FreezeMelt.overloading = size > 200;
        }
        if (overloading && overloadCounter != 0) {
            return;
        }
        if (overloading) {
            int i = 0;
            Iterator iterator = frozenblocks.keySet().iterator();
            while (iterator.hasNext()) {
                Block block;
                Block fblock = block = (Block)iterator.next();
                new BukkitRunnable(){

                    public void run() {
                        if (FreezeMelt.canThaw(fblock)) {
                            FreezeMelt.thaw(fblock);
                        }
                    }
                }.runTaskLater((Plugin)ProjectKorra.plugin, (long)(i % 10));
                ++i;
            }
        } else {
            for (Block block : frozenblocks.keySet()) {
                if (!FreezeMelt.canThaw(block)) continue;
                FreezeMelt.thaw(block);
            }
        }
    }

    public static boolean canThaw(Block block) {
        if (frozenblocks.containsKey((Object)block)) {
            for (Player player : block.getWorld().getPlayers()) {
                if (GeneralMethods.getBoundAbility(player) == null) {
                    return true;
                }
                if (GeneralMethods.getBoundAbility(player).equalsIgnoreCase("OctopusForm") && block.getLocation().distance(player.getLocation()) <= OctopusForm.RADIUS + 2.0) {
                    return false;
                }
                if (!GeneralMethods.canBend(player.getName(), "PhaseChange")) continue;
                double range = WaterMethods.waterbendingNightAugment(defaultrange, player.getWorld());
                if (AvatarState.isAvatarState(player)) {
                    range = AvatarState.getValue(range);
                }
                if (block.getLocation().distance(player.getLocation()) > range) continue;
                return false;
            }
        }
        if (!WaterManipulation.canPhysicsChange(block)) {
            return false;
        }
        return true;
    }

    private static void thawAll() {
        for (Block block : frozenblocks.keySet()) {
            if (block.getType() != Material.ICE) continue;
            byte data = frozenblocks.get((Object)block).byteValue();
            block.setType(Material.WATER);
            block.setData(data);
            frozenblocks.remove((Object)block);
        }
    }

    public static void removeAll() {
        FreezeMelt.thawAll();
    }

    public static String getDescription() {
        return "To use, simply left-click. Any water you are looking at within range will instantly freeze over into solid ice. Provided you stay within range of the ice and do not unbind PhaseChange, that ice will not thaw. If, however, you do either of those the ice will instantly thaw. If you sneak (default: shift), anything around where you are looking at will instantly melt. Since this is a more favorable state for these things, they will never re-freeze unless they would otherwise by nature or some other bending ability. Additionally, if you tap sneak while targetting water with PhaseChange, it will evaporate water around that block that is above sea level. ";
    }

}

