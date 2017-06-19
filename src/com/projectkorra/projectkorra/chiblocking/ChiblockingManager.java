/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.chiblocking;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.chiblocking.AcrobatStance;
import com.projectkorra.projectkorra.chiblocking.ChiPassive;
import com.projectkorra.projectkorra.chiblocking.Paralyze;
import com.projectkorra.projectkorra.chiblocking.Smokescreen;
import com.projectkorra.projectkorra.chiblocking.WarriorStance;

public class ChiblockingManager
implements Runnable {
    public ProjectKorra plugin;

    public ChiblockingManager(ProjectKorra plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ChiPassive.handlePassive();
        WarriorStance.progressAll();
        AcrobatStance.progressAll();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Smokescreen.removeFromHashMap((Entity)player);
            if (!Paralyze.isParalyzed((Entity)player)) continue;
            player.setFallDistance(0.0f);
        }
    }
}

