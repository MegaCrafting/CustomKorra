/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.chiblocking;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.chiblocking.AcrobatStance;
import com.projectkorra.projectkorra.chiblocking.RapidPunch;
import com.projectkorra.projectkorra.chiblocking.WarriorStance;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChiMethods {
    static ProjectKorra plugin;
    private static FileConfiguration config;

    static {
        config = ProjectKorra.plugin.getConfig();
    }

    public ChiMethods(ProjectKorra plugin) {
        ChiMethods.plugin = plugin;
    }

    public static ChatColor getChiColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.Chi"));
    }

    public static boolean isChiAbility(String ability) {
        return AbilityModuleManager.chiabilities.contains(ability);
    }

    public static boolean isChiBlocked(String player) {
        if (GeneralMethods.getBendingPlayer(player) != null) {
            return GeneralMethods.getBendingPlayer(player).isChiBlocked();
        }
        return false;
    }

    public static void stopBending() {
        RapidPunch.instances.clear();
        WarriorStance.instances.clear();
        AcrobatStance.instances.clear();
    }
}

