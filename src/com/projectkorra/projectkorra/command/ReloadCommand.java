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

public class ReloadCommand
extends PKCommand {
    public ReloadCommand() {
        super("reload", "/bending reload", "This command will reload the Bending config file.", new String[]{"reload", "r"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.hasPermission(sender) || !this.correctLength(sender, args.size(), 0, 0)) {
            return;
        }
        GeneralMethods.reloadPlugin();
        sender.sendMessage((Object)ChatColor.AQUA + "Bending config reloaded.");
    }
}

