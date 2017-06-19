/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.projectkorra.projectkorra.command;

import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.command.PKCommand;

public class InvincibleCommand
extends PKCommand {
    public InvincibleCommand() {
        super("invincible", "/bending invincible", "This command will make you impervious to all Bending damage. Once you use this command, you will stay invincible until you log off or use this command again.", new String[]{"invincible", "inv"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!(this.hasPermission(sender) && this.isPlayer(sender) && this.correctLength(sender, args.size(), 0, 0))) {
            return;
        }
        if (!Commands.invincible.contains(sender.getName())) {
            Commands.invincible.add(sender.getName());
            sender.sendMessage((Object)ChatColor.GREEN + "You are now invincible to all bending damage and effects. Use this command again to disable this.");
        } else {
            Commands.invincible.remove(sender.getName());
            sender.sendMessage((Object)ChatColor.RED + "You are no longer invincible to all bending damage and effects.");
        }
    }
}

