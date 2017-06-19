/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.chiblocking;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.waterbending.WaterArmsWhip;

public class HighJump {
    private int jumpheight = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.HighJump.Height");
    private long cooldown = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.HighJump.Cooldown");

    public HighJump(Player p) {
        WaterArmsWhip waw;
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(p.getName());
        if (bPlayer.isOnCooldown("HighJump")) {
            return;
        }
        if (WaterArmsWhip.grabbedEntities.containsKey((Object)p) && (waw = WaterArmsWhip.instances.get(WaterArmsWhip.grabbedEntities.get((Object)p))) != null) {
            waw.setGrabbed(false);
        }
        this.jump(p);
        bPlayer.addCooldown("HighJump", this.cooldown);
    }

    private void jump(Player p) {
        if (!GeneralMethods.isSolid(p.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
            return;
        }
        Vector vec = p.getVelocity();
        vec.setY(this.jumpheight);
        p.setVelocity(vec);
    }

    public static String getDescription() {
        return "To use this ability, simply click. You will jump quite high. This ability has a short cooldown.";
    }
}

