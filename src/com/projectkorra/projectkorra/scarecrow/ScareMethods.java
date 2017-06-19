/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.file.FileConfiguration
 */
package com.projectkorra.projectkorra.scarecrow;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.ProjectKorra;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ScareMethods {
    static ProjectKorra plugin;
    private static FileConfiguration config;

    static {
        config = ProjectKorra.plugin.getConfig();
    }

    public static ChatColor getScareColor() {
        return ChatColor.DARK_GRAY;
    }

    public static boolean isScareAbility(String ability) {
        return AbilityModuleManager.ScarecrowAbilities.contains(ability);
    }

    public static boolean isSnowmanAbility(String abil) {
        return false;
    }

    public static void stopBending() {
    }

    public ScareMethods(ProjectKorra plugin) {
        ScareMethods.plugin = plugin;
    }
}

