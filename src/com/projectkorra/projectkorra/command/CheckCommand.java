/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.projectkorra.projectkorra.command;

import java.util.List;
import org.bukkit.command.CommandSender;

import com.projectkorra.projectkorra.command.PKCommand;

public class CheckCommand
extends PKCommand {
    public CheckCommand() {
        super("check", "/bending check", "Checks if ProjectKorra is up to date.", new String[]{"check", "chk"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.hasPermission(sender)) {
            return;
        }
        if (args.size() > 0) {
            this.help(sender, false);
            return;
        }
    }
}

