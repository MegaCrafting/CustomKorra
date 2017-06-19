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
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;

public class RemoveCommand
extends PKCommand {
    public RemoveCommand() {
        super("remove", "/bending remove <Player> [Element]", "This command will remove the element of the targeted [Player]. The player will be able to re-pick their element after this command is run on them, assuming their Bending was not permaremoved.", new String[]{"remove", "rm"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.hasPermission(sender) || !this.correctLength(sender, args.size(), 1, 2)) {
            return;
        }
        Player player = Bukkit.getPlayer((String)args.get(0));
        if (player == null) {
            sender.sendMessage((Object)ChatColor.RED + "That player is not online.");
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer == null) {
            GeneralMethods.createBendingPlayer(player.getUniqueId(), player.getName());
            bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        }
        if (args.size() == 2 && Element.getType(args.get(1)) != null) {
            bPlayer.getElements().remove((Object)Element.getType(args.get(1)));
            GeneralMethods.removeUnusableAbilities(player.getName());
            if (Element.getType(args.get(1)) == Element.Chi) {
                sender.sendMessage((Object)ChatColor.GREEN + "You have removed the " + args.get(1).toLowerCase() + "blocking of " + (Object)ChatColor.DARK_AQUA + player.getName());
                player.sendMessage((Object)ChatColor.GREEN + "Your " + args.get(1).toLowerCase() + "blocking has been removed by " + (Object)ChatColor.DARK_AQUA + sender.getName());
            } else {
                sender.sendMessage((Object)ChatColor.GREEN + "You have removed the " + args.get(1).toLowerCase() + "bending of " + (Object)ChatColor.DARK_AQUA + player.getName());
                player.sendMessage((Object)ChatColor.GREEN + "Your " + args.get(1).toLowerCase() + "bending has been removed by " + (Object)ChatColor.DARK_AQUA + sender.getName());
            }
            Bukkit.getServer().getPluginManager().callEvent((Event)new PlayerChangeElementEvent(sender, player, Element.getType(args.get(1)), PlayerChangeElementEvent.Result.REMOVE));
            return;
        }
        bPlayer.getElements().clear();
        GeneralMethods.saveElements(bPlayer);
        GeneralMethods.removeUnusableAbilities(player.getName());
        sender.sendMessage((Object)ChatColor.GREEN + "You have removed the bending of " + (Object)ChatColor.DARK_AQUA + player.getName());
        player.sendMessage((Object)ChatColor.GREEN + "Your bending has been removed by " + (Object)ChatColor.DARK_AQUA + sender.getName());
        Bukkit.getServer().getPluginManager().callEvent((Event)new PlayerChangeElementEvent(sender, player, null, PlayerChangeElementEvent.Result.REMOVE));
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission("bending.admin." + this.getName())) {
            return true;
        }
        sender.sendMessage((Object)ChatColor.RED + "You don't have permission to use this command.");
        return false;
    }
}

