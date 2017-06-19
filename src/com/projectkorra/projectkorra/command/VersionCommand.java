/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.PluginDescriptionFile
 */
package com.projectkorra.projectkorra.command;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.command.PKCommand;

public class VersionCommand
extends PKCommand {
    public VersionCommand() {
        super("version", "/bending version", "Displays the installed version of ProjectKorra.", new String[]{"version", "v"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.hasPermission(sender) || !this.correctLength(sender, args.size(), 0, 0)) {
            return;
        }
        sender.sendMessage((Object)ChatColor.GREEN + "Core Version: " + (Object)ChatColor.RED + ProjectKorra.plugin.getDescription().getVersion());
        if (GeneralMethods.hasRPG()) {
            sender.sendMessage((Object)ChatColor.GREEN + "RPG Version: " + (Object)ChatColor.RED + GeneralMethods.getRPG().getDescription().getVersion());
        }
        if (GeneralMethods.hasItems()) {
            sender.sendMessage((Object)ChatColor.GREEN + "Items Version: " + (Object)ChatColor.RED + GeneralMethods.getItems().getDescription().getVersion());
        }
        sender.sendMessage((Object)ChatColor.GREEN + "Founded by: " + (Object)ChatColor.RED + "MistPhizzle");
        sender.sendMessage((Object)ChatColor.GREEN + "Learn More: " + (Object)ChatColor.RED + "http://projectkorra.com");
    }
}

