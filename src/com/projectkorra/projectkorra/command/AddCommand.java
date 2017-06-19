/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.PluginManager
 */
package com.projectkorra.projectkorra.command;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;

public class AddCommand
extends PKCommand {
    public AddCommand() {
        super("add", "/bending add <Element> [Player]", "This command will allow the user to add an element to the targeted <Player>, or themselves if the target is not specified. This command is typically reserved for server administrators.", new String[]{"add", "a"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.correctLength(sender, args.size(), 1, 2)) {
            return;
        }
        if (args.size() == 1) {
            if (!this.hasPermission(sender) || !this.isPlayer(sender)) {
                return;
            }
            this.add(sender, (Player)sender, args.get(0).toLowerCase());
        } else if (args.size() == 2) {
            if (!this.hasPermission(sender, "others")) {
                return;
            }
            Player player = Bukkit.getPlayer((String)args.get(1));
            if (player == null) {
                sender.sendMessage((Object)ChatColor.RED + "That player is not online.");
                return;
            }
            this.add(sender, player, args.get(0).toLowerCase());
        }
    }

    private void add(CommandSender sender, Player target, String element) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(target.getName());
        if (bPlayer == null) {
            GeneralMethods.createBendingPlayer(target.getUniqueId(), target.getName());
            bPlayer = GeneralMethods.getBendingPlayer(target.getName());
        }
        if (bPlayer.isPermaRemoved()) {
            sender.sendMessage((Object)ChatColor.RED + "That player's bending was permanently removed.");
            return;
        }
        if (Arrays.asList(Commands.elementaliases).contains(element.toLowerCase())) {
            element = this.getElement(element.toLowerCase());
            Element type = Element.getType(element);
            bPlayer.addElement(type);
            ChatColor color = GeneralMethods.getElementColor(type);
            if (element.charAt(0) == 'w' || element.charAt(0) == 'f') {
                target.sendMessage((Object)color + "You are also a " + Character.toString(element.charAt(0)).toUpperCase() + element.substring(1) + "bender.");
            } else if (element.charAt(0) == 'e' || element.charAt(0) == 'a') {
                target.sendMessage((Object)color + "You are also an " + Character.toString(element.charAt(0)).toUpperCase() + element.substring(1) + "bender.");
            } else if (element.charAt(0) == 'c' || element.equalsIgnoreCase("chi")) {
                target.sendMessage((Object)color + "You are now a Chiblocker.");
            }
            if (!(sender instanceof Player) || !((Player)sender).equals((Object)target)) {
                if (element.charAt(0) == 'w' || element.charAt(0) == 'f') {
                    sender.sendMessage((Object)ChatColor.DARK_AQUA + target.getName() + (Object)color + " is also a " + Character.toString(element.charAt(0)).toUpperCase() + element.substring(1) + "bender.");
                } else if (element.charAt(0) == 'e' || element.charAt(0) == 'a') {
                    sender.sendMessage((Object)ChatColor.DARK_AQUA + target.getName() + (Object)color + " is also an " + Character.toString(element.charAt(0)).toUpperCase() + element.substring(1) + "bender.");
                } else if (element.charAt(0) == 'c' || element.equalsIgnoreCase("chi")) {
                    sender.sendMessage((Object)ChatColor.DARK_AQUA + target.getName() + (Object)color + " is also a " + Character.toString(element.charAt(0)).toUpperCase() + element.substring(1) + "blocker.");
                }
            }
            GeneralMethods.saveElements(bPlayer);
            Bukkit.getServer().getPluginManager().callEvent((Event)new PlayerChangeElementEvent(sender, target, type, PlayerChangeElementEvent.Result.ADD));
            return;
        }
        sender.sendMessage((Object)ChatColor.RED + "You must specify a valid element.");
    }
}

