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

public class PermaremoveCommand
extends PKCommand {
    public PermaremoveCommand() {
        super("permaremove", "/bending permaremove [Player]", "This command will permanently remove the Bending of the targeted <Player>. Once removed, a player may only receive Bending again if this command is run on them again. This command is typically reserved for administrators.", new String[]{"permaremove", "premove", "permremove", "pr"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.hasPermission(sender) || !this.correctLength(sender, args.size(), 0, 1)) {
            return;
        }
        if (args.size() == 1) {
            this.permaremove(sender, args.get(0));
        } else if (args.size() == 0 && this.isPlayer(sender)) {
            this.permaremove(sender, sender.getName());
        }
    }

    private void permaremove(CommandSender sender, String target) {
        Player player = Bukkit.getPlayer((String)target);
        if (player == null) {
            sender.sendMessage((Object)ChatColor.RED + "That player is not online.");
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer == null) {
            GeneralMethods.createBendingPlayer(player.getUniqueId(), player.getName());
            bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        }
        if (bPlayer.isPermaRemoved()) {
            bPlayer.setPermaRemoved(false);
            GeneralMethods.savePermaRemoved(bPlayer);
            player.sendMessage((Object)ChatColor.GREEN + "Your bending has been restored.");
            if (!(sender instanceof Player) || sender.getName().equals(target)) {
                sender.sendMessage((Object)ChatColor.GREEN + "You have restored the bending of: " + (Object)ChatColor.DARK_AQUA + player.getName());
            }
        } else {
            bPlayer.getElements().clear();
            GeneralMethods.saveElements(bPlayer);
            bPlayer.setPermaRemoved(true);
            GeneralMethods.savePermaRemoved(bPlayer);
            GeneralMethods.removeUnusableAbilities(player.getName());
            player.sendMessage((Object)ChatColor.RED + "Your bending has been permanently removed.");
            if (!(sender instanceof Player) || sender.getName().equals(target)) {
                sender.sendMessage((Object)ChatColor.RED + "You have permenantly removed the bending of: " + (Object)ChatColor.DARK_AQUA + player.getName());
            }
            Bukkit.getServer().getPluginManager().callEvent((Event)new PlayerChangeElementEvent(sender, player, null, PlayerChangeElementEvent.Result.PERMAREMOVE));
        }
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (sender.hasPermission("bending.admin.permremove")) {
            sender.sendMessage((Object)ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }
        return false;
    }
}

