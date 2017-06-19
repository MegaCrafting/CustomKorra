/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.waterbending.IceSpike;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class SpikeField {
    private static long cooldown = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.IceSpike.Cooldown");
    private static int radius = 6;
    public static int numofspikes = radius * 2 * (radius * 2) / 16;
    Random ran = new Random();
    private int damage = 2;
    private Vector thrown = new Vector(0, 1, 0);

    public SpikeField(Player p) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(p.getName());
        if (bPlayer.isOnCooldown("IceSpike")) {
            return;
        }
        int locX = p.getLocation().getBlockX();
        int locY = p.getLocation().getBlockY();
        int locZ = p.getLocation().getBlockZ();
        ArrayList<Block> iceblocks = new ArrayList<Block>();
        int x = - radius - 1;
        while (x <= radius - 1) {
            int z = - radius - 1;
            while (z <= radius - 1) {
                int y = -1;
                while (y <= 1) {
                    Block testblock = p.getWorld().getBlockAt(locX + x, locY + y, locZ + z);
                    if (testblock.getType() == Material.ICE && testblock.getRelative(BlockFace.UP).getType() == Material.AIR && (testblock.getX() != p.getEyeLocation().getBlock().getX() || testblock.getZ() != p.getEyeLocation().getBlock().getZ())) {
                        iceblocks.add(testblock);
                        for (Block iceblockforsound : iceblocks) {
                            WaterMethods.playIcebendingSound(iceblockforsound.getLocation());
                        }
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        List<Entity> entities = GeneralMethods.getEntitiesAroundPoint(p.getLocation(), radius);
        int i = 0;
        while (i < numofspikes) {
            if (iceblocks.isEmpty()) {
                return;
            }
            Entity target = null;
            Block targetblock = null;
            block5 : for (Entity entity : entities) {
                if (!(entity instanceof LivingEntity) || entity.getEntityId() == p.getEntityId()) continue;
                for (Block block : iceblocks) {
                    if (block.getX() != entity.getLocation().getBlockX() || block.getZ() != entity.getLocation().getBlockZ()) continue;
                    target = entity;
                    targetblock = block;
                    continue block5;
                }
            }
            if (target != null) {
                entities.remove((Object)target);
            } else {
                targetblock = (Block)iceblocks.get(this.ran.nextInt(iceblocks.size()));
            }
            if (targetblock.getRelative(BlockFace.UP).getType() != Material.ICE) {
                new com.projectkorra.projectkorra.waterbending.IceSpike(p, targetblock.getLocation(), this.damage, this.thrown, cooldown);
                bPlayer.addCooldown("IceSpike", cooldown);
                iceblocks.remove((Object)targetblock);
            }
            ++i;
        }
    }
}

