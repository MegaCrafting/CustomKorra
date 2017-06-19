/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.file.FileConfiguration
 */
package com.projectkorra.projectkorra.sunshine;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.ProjectKorra;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class SunshineMethods {
    static ProjectKorra plugin;
    private static FileConfiguration config;

    static {
        config = ProjectKorra.plugin.getConfig();
    }

    public static ChatColor getSunshineColor() {
        return ChatColor.YELLOW;
    }

    public static boolean isSunAbility(String ability) {
        return AbilityModuleManager.SunshineAbilities.contains(ability);
    }

    public static boolean isSunshineAbility(String abil) {
        return false;
    }

    public static void stopBending() {
    }

    public SunshineMethods(ProjectKorra plugin) {
        SunshineMethods.plugin = plugin;
    }
}

