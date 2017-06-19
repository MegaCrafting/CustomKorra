/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.file.FileConfiguration
 */
package com.projectkorra.projectkorra.snowman;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.ProjectKorra;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class SnowMethods {
    static ProjectKorra plugin;
    private static FileConfiguration config;

    static {
        config = ProjectKorra.plugin.getConfig();
    }

    public static ChatColor getSnowColor() {
        return ChatColor.DARK_AQUA;
    }

    public static boolean isSnowAbility(String ability) {
        return AbilityModuleManager.SnowmanAbilities.contains(ability);
    }

    public static boolean isSnowmanAbility(String abil) {
        return false;
    }

    public static void stopBending() {
    }

    public SnowMethods(ProjectKorra plugin) {
        SnowMethods.plugin = plugin;
    }
}

