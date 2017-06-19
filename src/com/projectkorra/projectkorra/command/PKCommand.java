/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.command;

import com.projectkorra.projectkorra.command.SubCommand;
import com.projectkorra.projectkorra.command.Commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PKCommand
implements SubCommand {
    private final String name;
    private final String properUse;
    private final String description;
    private final String[] aliases;
    public static Map<String, PKCommand> instances = new HashMap<String, PKCommand>();

    public PKCommand(String name, String properUse, String description, String[] aliases) {
        this.name = name;
        this.properUse = properUse;
        this.description = description;
        this.aliases = aliases;
        instances.put(name, this);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getProperUse() {
        return this.properUse;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String[] getAliases() {
        return this.aliases;
    }

    @Override
    public void help(CommandSender sender, boolean description) {
        sender.sendMessage((Object)ChatColor.GOLD + "Proper Usage: " + (Object)ChatColor.DARK_AQUA + this.properUse);
        if (description) {
            sender.sendMessage((Object)ChatColor.YELLOW + this.description);
        }
    }

    protected boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission("bending.command." + this.name)) {
            return true;
        }
        sender.sendMessage((Object)ChatColor.RED + "You don't have permission to do that.");
        return false;
    }

    protected boolean hasPermission(CommandSender sender, String extra) {
        if (sender.hasPermission("bending.command." + this.name + "." + extra)) {
            return true;
        }
        sender.sendMessage((Object)ChatColor.RED + "You don't have permission to do that.");
        return false;
    }

    protected boolean correctLength(CommandSender sender, int size, int min, int max) {
        if (size < min || size > max) {
            this.help(sender, false);
            return false;
        }
        return true;
    }

    protected boolean isPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        }
        sender.sendMessage((Object)ChatColor.RED + "You must be a player to use that command.");
        return false;
    }

    public String getElement(String element) {
        if (Arrays.asList(Commands.firealiases).contains(element) || Arrays.asList(Commands.firecomboaliases).contains(element) || Arrays.asList(Commands.combustionaliases).contains(element) || Arrays.asList(Commands.lightningaliases).contains(element)) {
            return "fire";
        }
        if (Arrays.asList(Commands.earthaliases).contains(element) || Arrays.asList(Commands.earthcomboaliases).contains(element) || Arrays.asList(Commands.metalbendingaliases).contains(element) || Arrays.asList(Commands.sandbendingaliases).contains(element) || Arrays.asList(Commands.lavabendingaliases).contains(element)) {
            return "earth";
        }
        if (Arrays.asList(Commands.airaliases).contains(element) || Arrays.asList(Commands.aircomboaliases).contains(element) || Arrays.asList(Commands.spiritualprojectionaliases).contains(element) || Arrays.asList(Commands.flightaliases).contains(element)) {
            return "air";
        }
        if (Arrays.asList(Commands.wateraliases).contains(element) || Arrays.asList(Commands.watercomboaliases).contains(element) || Arrays.asList(Commands.healingaliases).contains(element) || Arrays.asList(Commands.bloodaliases).contains(element) || Arrays.asList(Commands.icealiases).contains(element) || Arrays.asList(Commands.plantaliases).contains(element)) {
            return "water";
        }
        if (Arrays.asList(Commands.chialiases).contains(element) || Arrays.asList(Commands.chicomboaliases).contains(element)) {
            return "chi";
        }
        return null;
    }
}

