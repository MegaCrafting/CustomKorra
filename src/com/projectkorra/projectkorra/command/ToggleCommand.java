/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.command;

import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.command.PKCommand;

public class ToggleCommand
extends PKCommand {
    public ToggleCommand() {
        super("toggle", "/bending toggle <all | (element) <player>>", "This command will toggle a player's own Bending on or off. If toggled off, all abilities should stop working until it is toggled back on. Logging off will automatically toggle your Bending back on. If you run the command /bending toggle all, Bending will be turned off for all players and cannot be turned back on until the command is run again.", new String[]{"toggle", "t"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.correctLength(sender, args.size(), 0, 2)) {
            return;
        }
        if (args.size() == 0) {
            if (!this.hasPermission(sender) || !this.isPlayer(sender)) {
                return;
            }
            if (Commands.isToggledForAll) {
                sender.sendMessage((Object)ChatColor.RED + "Bending is currently toggled off for all players.");
                return;
            }
            BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(sender.getName());
            if (bPlayer == null) {
                GeneralMethods.createBendingPlayer(((Player)sender).getUniqueId(), sender.getName());
                bPlayer = GeneralMethods.getBendingPlayer(sender.getName());
            }
            if (bPlayer.isToggled()) {
                sender.sendMessage((Object)ChatColor.RED + "Your bending has been toggled off. You will not be able to use most abilities until you toggle it back.");
                bPlayer.toggleBending();
            } else {
                sender.sendMessage((Object)ChatColor.GREEN + "You have turned your Bending back on.");
                bPlayer.toggleBending();
            }
        } else if (args.size() == 1 && args.get(0).equalsIgnoreCase("all") && this.hasPermission(sender, "all")) {
            if (Commands.isToggledForAll) {
                Commands.isToggledForAll = false;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage((Object)ChatColor.GREEN + "Bending has been toggled back on for all players.");
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage((Object)ChatColor.GREEN + "Bending has been toggled back on for all players.");
                }
            } else {
                Commands.isToggledForAll = true;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage((Object)ChatColor.RED + "Bending has been toggled off for all players.");
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage((Object)ChatColor.RED + "Bending has been toggled off for all players.");
                }
            }
        } else if (sender instanceof Player && args.size() == 1 && Element.getType(args.get(0)) != null && sender.hasPermission("bending." + args.get(0).toLowerCase())) {
            Element e = Element.getType(args.get(0));
            BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(sender.getName());
            bPlayer.toggleElement(e);
            if (!bPlayer.isElementToggled(e)) {
                if (e == Element.Chi) {
                    sender.sendMessage((Object)GeneralMethods.getElementColor(e) + "You have toggled off your " + args.get(0).toLowerCase() + "blocking");
                } else {
                    sender.sendMessage((Object)GeneralMethods.getElementColor(e) + "You have toggled off your " + args.get(0).toLowerCase() + "bending");
                }
            } else if (e == Element.Chi) {
                sender.sendMessage((Object)GeneralMethods.getElementColor(e) + "You have toggled on your " + args.get(0).toLowerCase() + "blocking");
            } else {
                sender.sendMessage((Object)GeneralMethods.getElementColor(e) + "You have toggled on your " + args.get(0).toLowerCase() + "bending");
            }
        } else if (sender instanceof Player && args.size() == 2 && Element.getType(args.get(0)) != null && sender.hasPermission("bending." + args.get(0).toLowerCase())) {
            Player target = Bukkit.getPlayer((String)args.get(1));
            if (target == null) {
                sender.sendMessage((Object)ChatColor.RED + "Target is not found.");
            }
            Element e = Element.getType(args.get(0));
            BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(target.getName());
            if (bPlayer.isElementToggled(e)) {
                if (e == Element.Chi) {
                    sender.sendMessage((Object)GeneralMethods.getElementColor(e) + "You have toggled off " + (Object)ChatColor.DARK_AQUA + target.getName() + "'s " + args.get(0).toLowerCase() + "blocking");
                    target.sendMessage((Object)GeneralMethods.getElementColor(e) + "Your " + args.get(0).toLowerCase() + "blocking has been toggled off by " + (Object)ChatColor.DARK_AQUA + sender.getName());
                } else {
                    sender.sendMessage((Object)GeneralMethods.getElementColor(e) + "You have toggled off " + (Object)ChatColor.DARK_AQUA + target.getName() + "'s " + args.get(0).toLowerCase() + "bending");
                    target.sendMessage((Object)GeneralMethods.getElementColor(e) + "Your " + args.get(0).toLowerCase() + "bending has been toggled off by " + (Object)ChatColor.DARK_AQUA + sender.getName());
                }
            } else if (e == Element.Chi) {
                sender.sendMessage((Object)GeneralMethods.getElementColor(e) + "You have toggled on " + (Object)ChatColor.DARK_AQUA + target.getName() + "'s " + args.get(0).toLowerCase() + "blocking");
                target.sendMessage((Object)GeneralMethods.getElementColor(e) + "Your " + args.get(0).toLowerCase() + "blocking has been toggled on by " + (Object)ChatColor.DARK_AQUA + sender.getName());
            } else {
                sender.sendMessage((Object)GeneralMethods.getElementColor(e) + "You have toggled on " + (Object)ChatColor.DARK_AQUA + target.getName() + "'s " + args.get(0).toLowerCase() + "bending");
                target.sendMessage((Object)GeneralMethods.getElementColor(e) + "Your " + args.get(0).toLowerCase() + "bending has been toggled on by " + (Object)ChatColor.DARK_AQUA + sender.getName());
            }
            bPlayer.toggleElement(e);
        } else {
            this.help(sender, false);
        }
    }
}

