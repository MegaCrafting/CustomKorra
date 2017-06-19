/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.object.Preset;

public class PresetCommand
extends PKCommand {
    private static final String[] createaliases = new String[]{"create", "c", "save"};
    private static final String[] deletealiases = new String[]{"delete", "d", "del"};
    private static final String[] listaliases = new String[]{"list", "l"};
    private static final String[] bindaliases = new String[]{"bind", "b"};

    public PresetCommand() {
        super("preset", "/bending preset create|bind|list|delete [name]", "This command manages Presets, which are saved bindings. Use /bending preset list to view your existing presets, use /bending [create|delete] [name] to manage your presets, and use /bending bind [name] to bind an existing preset.", new String[]{"preset", "presets", "pre", "set", "p"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.isPlayer(sender) || !this.correctLength(sender, args.size(), 1, 2)) {
            return;
        }
        if (MultiAbilityManager.hasMultiAbilityBound((Player)sender)) {
            sender.sendMessage((Object)ChatColor.RED + "You can't edit your binds right now!");
            return;
        }
        Player player = (Player)sender;
        if (args.size() == 1) {
            if (Arrays.asList(listaliases).contains(args.get(0)) && this.hasPermission(sender, "list")) {
                List<Preset> presets = Preset.presets.get(player.getUniqueId());
                ArrayList<String> presetNames = new ArrayList<String>();
                if (presets == null || presets.isEmpty()) {
                    sender.sendMessage((Object)ChatColor.RED + "You don't have any presets.");
                    return;
                }
                for (Preset preset : presets) {
                    presetNames.add(preset.getName());
                }
                sender.sendMessage((Object)ChatColor.GREEN + "Your Presets: " + (Object)ChatColor.DARK_AQUA + presetNames.toString());
                return;
            }
            this.help(sender, false);
            return;
        }
        String name = args.get(1);
        if (Arrays.asList(deletealiases).contains(args.get(0)) && this.hasPermission(sender, "delete")) {
            if (!Preset.presetExists(player, name)) {
                sender.sendMessage((Object)ChatColor.RED + "You don't have a preset with that name.");
                return;
            }
            Preset preset = Preset.getPreset(player, name);
            preset.delete();
            sender.sendMessage((Object)ChatColor.GREEN + "You have deleted your preset named: " + (Object)ChatColor.YELLOW + name);
            return;
        }
        if (Arrays.asList(bindaliases).contains(args.get(0)) && this.hasPermission(sender, "bind")) {
            if (!Preset.presetExists(player, name)) {
                sender.sendMessage((Object)ChatColor.RED + "You don't have a preset with that name.");
                return;
            }
            boolean boundAll = Preset.bindPreset(player, name);
            sender.sendMessage((Object)ChatColor.GREEN + "Your bound slots have been set to match the " + (Object)ChatColor.YELLOW + name + (Object)ChatColor.GREEN + " preset.");
            if (!boundAll) {
                sender.sendMessage((Object)ChatColor.RED + "Some abilities were not bound because you cannot bend the required element.");
            }
            return;
        }
        if (Arrays.asList(createaliases).contains(args.get(0)) && this.hasPermission(sender, "create")) {
            int limit = GeneralMethods.getMaxPresets(player);
            if (Preset.presets.get((Object)player) != null && Preset.presets.get((Object)player).size() >= limit) {
                sender.sendMessage((Object)ChatColor.RED + "You have reached your max number of Presets.");
                return;
            }
            if (Preset.presetExists(player, name)) {
                sender.sendMessage((Object)ChatColor.RED + "A preset with that name already exists.");
                return;
            }
            BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
            if (bPlayer == null) {
                return;
            }
            HashMap<Integer, String> abilities = bPlayer.getAbilities();
            Preset preset = new Preset(player.getUniqueId(), name, abilities);
            preset.save();
            sender.sendMessage((Object)ChatColor.GREEN + "Created preset with the name: " + (Object)ChatColor.YELLOW + name);
        } else {
            this.help(sender, false);
        }
    }
}

