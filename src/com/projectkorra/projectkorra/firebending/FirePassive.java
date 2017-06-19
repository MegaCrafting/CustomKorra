/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.firebending;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;

public class FirePassive {
    public static void handlePassive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!GeneralMethods.canBendPassive(player.getName(), Element.Fire) || player.getFireTicks() <= 80) continue;
            player.setFireTicks(80);
        }
    }
}

