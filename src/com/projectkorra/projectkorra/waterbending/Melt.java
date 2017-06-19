/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.FreezeMelt;
import com.projectkorra.projectkorra.waterbending.Torrent;
import com.projectkorra.projectkorra.waterbending.WaterCombo;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterWave;
import com.projectkorra.projectkorra.waterbending.Wave;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Melt {
    private static final int seaLevel = ProjectKorra.plugin.getConfig().getInt("Properties.SeaLevel");
    private static final int defaultrange = FreezeMelt.defaultrange;
    private static final int defaultradius = FreezeMelt.defaultradius;
    private static final int defaultevaporateradius = 3;
    private static final byte full = 0;

    public Melt(Player player) {
        if (!WaterMethods.canIcebend(player)) {
            return;
        }
        int range = (int)WaterMethods.waterbendingNightAugment(defaultrange, player.getWorld());
        int radius = (int)WaterMethods.waterbendingNightAugment(defaultradius, player.getWorld());
        if (AvatarState.isAvatarState(player)) {
            range = AvatarState.getValue(range);
            radius = AvatarState.getValue(radius);
        }
        boolean evaporate = false;
        Location location = GeneralMethods.getTargetedLocation(player, range);
        if (WaterMethods.isWater(player.getTargetBlock((HashSet<Material>) null, range)) && !(player.getEyeLocation().getBlockY() <= 62)) {
            evaporate = true;
            radius = (int)WaterMethods.waterbendingNightAugment(3.0, player.getWorld());
        }
        for (Block block : GeneralMethods.getBlocksAroundPoint(location, radius)) {
            if (evaporate) {
                if (block.getY() <= seaLevel) continue;
                Melt.evaporate(player, block);
                continue;
            }
            Melt.melt(player, block);
        }
    }

    public static void melt(Player player, Block block) {
        if (GeneralMethods.isRegionProtectedFromBuild(player, "PhaseChange", block.getLocation())) {
            return;
        }
        if (!Wave.canThaw(block)) {
            Wave.thaw(block);
            return;
        }
        if (!Torrent.canThaw(block)) {
            Torrent.thaw(block);
            return;
        }
        WaterWave.thaw(block);
        WaterCombo.thaw(block);
        if (WaterMethods.isMeltable(block) && !TempBlock.isTempBlock(block) && WaterManipulation.canPhysicsChange(block)) {
            if (block.getType() == Material.SNOW) {
                block.setType(Material.AIR);
                return;
            }
            if (FreezeMelt.frozenblocks.containsKey((Object)block)) {
                FreezeMelt.thaw(block);
            } else {
                block.setType(Material.WATER);
                block.setData((byte)0);
            }
        }
    }

    public static void evaporate(Player player, Block block) {
        if (GeneralMethods.isRegionProtectedFromBuild(player, "PhaseChange", block.getLocation())) {
            return;
        }
        if (WaterMethods.isWater(block) && !TempBlock.isTempBlock(block) && WaterManipulation.canPhysicsChange(block)) {
            block.setType(Material.AIR);
            block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 1);
        }
    }
}

