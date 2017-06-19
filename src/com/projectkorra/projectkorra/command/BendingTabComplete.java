/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.command;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.combo.ComboAbilityModule;
import com.projectkorra.projectkorra.ability.combo.ComboModuleManager;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.object.Preset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class BendingTabComplete
implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0 || args[0].equals("")) {
            return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, BendingTabComplete.getCommandsForUser(sender));
        }
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("bind") || args[0].equalsIgnoreCase("b")) {
                if (args.length > 3 || !sender.hasPermission("bending.command.bind") || !(sender instanceof Player)) {
                    return new ArrayList<String>();
                }
                ArrayList<String> abilities = new ArrayList<String>();
                if (args.length == 2) {
                    for (String abil : AbilityModuleManager.abilities) {
                        if (!GeneralMethods.canBind(sender.getName(), abil)) continue;
                        abilities.add(abil);
                    }
                } else {
                    int i = 1;
                    while (i < 10) {
                        abilities.add("" + i);
                        ++i;
                    }
                }
                Collections.sort(abilities);
                return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, abilities);
            }
            if (args[0].equalsIgnoreCase("display") || args[0].equalsIgnoreCase("d")) {
                if (args.length > 2 || !sender.hasPermission("bending.command.display")) {
                    return new ArrayList<String>();
                }
                ArrayList<String> list = new ArrayList<String>();
                list.add("Air");
                list.add("Earth");
                list.add("Fire");
                list.add("Water");
                list.add("Chi");
                list.add("Bloodbending");
                list.add("Combustion");
                list.add("Flight");
                list.add("Healing");
                list.add("Ice");
                list.add("Lava");
                list.add("Lightning");
                list.add("Metal");
                list.add("Plantbending");
                list.add("Sand");
                list.add("SpiritualProjection");
                list.add("AirCombos");
                list.add("EarthCombos");
                list.add("FireCombos");
                list.add("WaterCombos");
                list.add("ChiCombos");
                list.add("Avatar");
                return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, list);
            }
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("choose") || args[0].equalsIgnoreCase("ch")) {
                if (args.length > 3 || !sender.hasPermission("bending.command.add")) {
                    return new ArrayList<String>();
                }
                ArrayList<String> l = new ArrayList<String>();
                if (args.length == 2) {
                    l.add("Air");
                    l.add("Earth");
                    l.add("Fire");
                    l.add("Water");
                    l.add("Chi");
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        l.add(p.getName());
                    }
                }
                return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, l);
            }
            if (args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("cl") || args[0].equalsIgnoreCase("c")) {
                if (args.length > 2 || !sender.hasPermission("bending.command.clear")) {
                    return new ArrayList<String>();
                }
                ArrayList<String> l = new ArrayList<String>();
                int i = 1;
                while (i < 10) {
                    l.add("" + i);
                    ++i;
                }
                return l;
            }
            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
                if (args.length > 2 || !sender.hasPermission("bending.command.help")) {
                    return new ArrayList<String>();
                }
                ArrayList<String> list = new ArrayList<String>();
                Element[] arrelement = Element.values();
                int n = arrelement.length;
                int n2 = 0;
                while (n2 < n) {
                    Element e = arrelement[n2];
                    list.add(e.toString());
                    ++n2;
                }
                ArrayList<String> abils = new ArrayList<String>();
                for (String abil : AbilityModuleManager.abilities) {
                    if (!GeneralMethods.canBend(sender.getName(), abil)) continue;
                    abils.add(abil);
                }
                for (ComboAbilityModule abil2 : ComboModuleManager.combo) {
                    if (!GeneralMethods.canBend(sender.getName(), abil2.getName())) continue;
                    abils.add(abil2.getName());
                }
                Collections.sort(abils);
                list.addAll(abils);
                return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, list);
            }
            if (args[0].equalsIgnoreCase("permaremove") || args[0].equalsIgnoreCase("pr") || args[0].equalsIgnoreCase("premove") || args[0].equalsIgnoreCase("permremove")) {
                if (args.length > 2 || !sender.hasPermission("bending.command.permaremove")) {
                    return new ArrayList<String>();
                }
                ArrayList<String> players = new ArrayList<String>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    players.add(p.getName());
                }
                return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, players);
            }
            if (args[0].equalsIgnoreCase("preset") || args[0].equalsIgnoreCase("presets") || args[0].equalsIgnoreCase("pre") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("p")) {
                if (args.length > 3 || !sender.hasPermission("bending.command.preset") || !(sender instanceof Player)) {
                    return new ArrayList<String>();
                }
                ArrayList<String> l = new ArrayList<String>();
                if (args.length == 2) {
                    l.add("create");
                    l.add("delete");
                    l.add("list");
                    l.add("bind");
                    return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, l);
                }
                if (args.length == 3 && Arrays.asList("delete", "d", "del", "bind", "b").contains(args[1].toLowerCase())) {
                    List<Preset> presets = Preset.presets.get(((Player)sender).getUniqueId());
                    ArrayList<String> presetNames = new ArrayList<String>();
                    if (presets != null && presets.size() != 0) {
                        for (Preset preset : presets) {
                            presetNames.add(preset.getName());
                        }
                    } else {
                        return new ArrayList<String>();
                    }
                    return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, presetNames);
                }
            } else {
                if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rm")) {
                    if (args.length > 3 || !sender.hasPermission("bending.command.remove")) {
                        return new ArrayList<String>();
                    }
                    ArrayList<String> l = new ArrayList<String>();
                    if (args.length == 2) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            l.add(p.getName());
                        }
                    } else {
                        l.add("Air");
                        l.add("Earth");
                        l.add("Fire");
                        l.add("Water");
                        l.add("Chi");
                    }
                    return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, l);
                }
                if (args[0].equalsIgnoreCase("who") || args[0].equalsIgnoreCase("w")) {
                    if (args.length > 2 || !sender.hasPermission("bending.command.remove")) {
                        return new ArrayList<String>();
                    }
                    ArrayList<String> l = new ArrayList<String>();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        l.add(p.getName());
                    }
                    return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, l);
                }
                if (!PKCommand.instances.keySet().contains(args[0].toLowerCase())) {
                    return new ArrayList<String>();
                }
            }
        } else {
            return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, BendingTabComplete.getCommandsForUser(sender));
        }
        return new ArrayList<String>();
    }

    public static List<String> getPossibleCompletionsForGivenArgs(String[] args, List<String> possibilitiesOfCompletion) {
        String argumentToFindCompletionFor = args[args.length - 1];
        ArrayList<String> listOfPossibleCompletions = new ArrayList<String>();
        for (String foundString : possibilitiesOfCompletion) {
            if (!foundString.regionMatches(true, 0, argumentToFindCompletionFor, 0, argumentToFindCompletionFor.length())) continue;
            listOfPossibleCompletions.add(foundString);
        }
        return listOfPossibleCompletions;
    }

    public static List<String> getPossibleCompletionsForGivenArgs(String[] args, String[] possibilitiesOfCompletion) {
        return BendingTabComplete.getPossibleCompletionsForGivenArgs(args, Arrays.asList(possibilitiesOfCompletion));
    }

    public static List<String> getCommandsForUser(CommandSender sender) {
        ArrayList<String> list = new ArrayList<String>();
        for (String cmd : PKCommand.instances.keySet()) {
            if (!sender.hasPermission("bending.command." + cmd.toLowerCase())) continue;
            list.add(cmd);
        }
        Collections.sort(list);
        return list;
    }
}

