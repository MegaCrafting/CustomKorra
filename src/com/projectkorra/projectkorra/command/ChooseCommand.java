/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Server
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
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;

public class ChooseCommand
extends PKCommand {
    public ChooseCommand() {
        super("choose", "/bending choose <Element> [Player]", "This command will allow the user to choose a player either for himself or <Player> if specified. This command can only be used once per player unless they have permission to rechoose their element.", new String[]{"choose", "ch"});
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
            BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(sender.getName());
            if (bPlayer == null) {
                GeneralMethods.createBendingPlayer(((Player)sender).getUniqueId(), sender.getName());
                bPlayer = GeneralMethods.getBendingPlayer(sender.getName());
            }
            if (bPlayer.isPermaRemoved()) {
                sender.sendMessage((Object)ChatColor.RED + "Your bending was permanently removed.");
                return;
            }
            if (!bPlayer.getElements().isEmpty() && !sender.hasPermission("bending.command.rechoose")) {
                sender.sendMessage((Object)ChatColor.RED + "You don't have permission to do that.");
                return;
            }
            String element = args.get(0).toLowerCase();
            if (Arrays.asList(Commands.elementaliases).contains(element)) {
                if (!this.hasPermission(sender, element)) {
                    return;
                }
                this.add(sender, (Player)sender, element);
                return;
            }
            sender.sendMessage((Object)ChatColor.RED + "That is not a valid element.");
            return;
        }
        if (args.size() == 2) {
            if (!sender.hasPermission("bending.admin.choose")) {
                sender.sendMessage((Object)ChatColor.RED + "You don't have permission to do that.");
                return;
            }
            Player target = ProjectKorra.plugin.getServer().getPlayer(args.get(1));
            if (target == null || !target.isOnline()) {
                sender.sendMessage((Object)ChatColor.RED + "That player is not online.");
                return;
            }
            String element = args.get(0).toLowerCase();
            if (Arrays.asList(Commands.elementaliases).contains(element)) {
                this.add(sender, target, element);
                return;
            }
            sender.sendMessage((Object)ChatColor.RED + "That is not a valid element.");
        }
    }

    private void add(CommandSender sender, Player target, String element) {
        element = this.getElement(element);
        Element e = Element.getType(element);
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(target.getName());
        bPlayer.setElement(e);
        ChatColor color = GeneralMethods.getElementColor(e);
        if (element.charAt(0) == 'w' || element.charAt(0) == 'f') {
            target.sendMessage((Object)color + "You are now a " + Character.toString(element.charAt(0)).toUpperCase() + element.substring(1) + "bender.");
        } else if (element.charAt(0) == 'e' || element.charAt(0) == 'a') {
            target.sendMessage((Object)color + "You are now an " + Character.toString(element.charAt(0)).toUpperCase() + element.substring(1) + "bender.");
        } else if (element.equalsIgnoreCase("chi")) {
            target.sendMessage((Object)color + "You are now a Chiblocker.");
        }
        if (!(sender instanceof Player) || !((Player)sender).equals((Object)target)) {
            if (element.charAt(0) == 'w' || element.charAt(0) == 'f') {
                sender.sendMessage((Object)ChatColor.DARK_AQUA + target.getName() + (Object)color + " is now a " + Character.toString(element.charAt(0)).toUpperCase() + element.substring(1) + "bender.");
            } else if (element.charAt(0) == 'e' || element.charAt(0) == 'a') {
                sender.sendMessage((Object)ChatColor.DARK_AQUA + target.getName() + (Object)color + " is now an " + Character.toString(element.charAt(0)).toUpperCase() + element.substring(1) + "bender.");
            } else if (element.equalsIgnoreCase("chi")) {
                target.sendMessage((Object)color + "You are now a Chiblocker.");
            }
        }
        GeneralMethods.removeUnusableAbilities(target.getName());
        GeneralMethods.saveElements(bPlayer);
        Bukkit.getServer().getPluginManager().callEvent((Event)new PlayerChangeElementEvent(sender, target, e, PlayerChangeElementEvent.Result.CHOOSE));
    }
}

