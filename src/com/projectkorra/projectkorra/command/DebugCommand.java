/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.projectkorra.projectkorra.command;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.command.PKCommand;

public class DebugCommand
extends PKCommand {
    public DebugCommand() {
        super("debug", "/bending debug", "Outputs information on the current ProjectKorra installation to /plugins/ProjectKorra/debug.txt", new String[]{"debug", "de"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.hasPermission(sender)) {
            return;
        }
        if (args.size() != 0) {
            this.help(sender, false);
            return;
        }
        GeneralMethods.runDebug();
        sender.sendMessage((Object)ChatColor.GREEN + "Debug File Created as debug.txt in the ProjectKorra plugin folder.");
        sender.sendMessage((Object)ChatColor.GREEN + "Put contents on pastie.org and create a bug report  on the ProjectKorra forum if you need to.");
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (!sender.hasPermission("bending.admin." + this.getName())) {
            sender.sendMessage((Object)ChatColor.RED + "You don't have permission to use this command.");
            return false;
        }
        return true;
    }
}

