/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.command;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.ability.combo.ComboManager;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisplayCommand
extends PKCommand {
    public DisplayCommand() {
        super("display", "/bending display <Element>", "This command will show you all of the elements you have bound if you do not specify an element. If you do specify an element (Air, Water, Earth, Fire, or Chi), it will show you all of the available abilities of that element installed on the server.", new String[]{"display", "dis", "d"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.hasPermission(sender) || !this.correctLength(sender, args.size(), 0, 1)) {
            return;
        }
        if (args.size() == 1) {
            String element = args.get(0).toLowerCase();
            if (Arrays.asList(Commands.comboaliases).contains(element)) {
                Element e = Element.getType(element = this.getElement(element));
                ArrayList<String> combos = ComboManager.getCombosForElement(e);
                if (combos.isEmpty()) {
                    sender.sendMessage((Object)GeneralMethods.getElementColor(e) + "There are no " + element + " combos avaliable.");
                    return;
                }
                for (String combomove : combos) {
                    if (!sender.hasPermission("bending.ability." + combomove)) continue;
                    ChatColor color = GeneralMethods.getComboColor(combomove);
                    sender.sendMessage((Object)color + combomove);
                }
                return;
            }
            if (Arrays.asList(Commands.elementaliases).contains(element)) {
                element = this.getElement(element);
                this.displayElement(sender, element);
            } else if (Arrays.asList(Commands.subelementaliases).contains(element)) {
                this.displaySubElement(sender, element);
            } else if (Arrays.asList(Commands.avataraliases).contains(element)) {
                this.displayAvatar(sender);
            } else {
                ChatColor w = ChatColor.WHITE;
                sender.sendMessage((Object)ChatColor.RED + "Not a valid argument." + (Object)ChatColor.WHITE + "\nElements: " + (Object)AirMethods.getAirColor() + "Air" + (Object)ChatColor.WHITE + " | " + (Object)WaterMethods.getWaterColor() + "Water" + (Object)ChatColor.WHITE + " | " + (Object)EarthMethods.getEarthColor() + "Earth" + (Object)ChatColor.WHITE + " | " + (Object)FireMethods.getFireColor() + "Fire" + (Object)ChatColor.WHITE + " | " + (Object)ChiMethods.getChiColor() + "Chi");
                sender.sendMessage((Object)w + "SubElements: " + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Air) + " Flight" + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Earth) + " Lavabending" + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Earth) + " Metalbending" + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Earth) + " Sandbending" + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Fire) + " Combustion" + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Fire) + " Lightning" + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Water) + " Bloodbending" + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Water) + " Healing" + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Water) + " Icebending" + (Object)w + "\n-" + (Object)GeneralMethods.getSubBendingColor(Element.Water) + " Plantbending");
            }
        }
        if (args.size() == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage((Object)ChatColor.RED + "This command is only usable by players.");
                return;
            }
            this.displayBinds(sender);
        }
    }

    private void displayAvatar(CommandSender sender) {
        ArrayList<String> abilities = new ArrayList<String>();
        for (String ability2 : AbilityModuleManager.abilities) {
            if (AbilityModuleManager.airbendingabilities.contains(ability2) || AbilityModuleManager.earthbendingabilities.contains(ability2) || AbilityModuleManager.firebendingabilities.contains(ability2) || AbilityModuleManager.waterbendingabilities.contains(ability2) || AbilityModuleManager.chiabilities.contains(ability2)) continue;
            abilities.add(ability2);
        }
        if (abilities.isEmpty()) {
            sender.sendMessage((Object)ChatColor.YELLOW + "There are no " + (Object)GeneralMethods.getAvatarColor() + "avatar" + (Object)ChatColor.YELLOW + " abilities on this server!");
            return;
        }
        for (String ability2 : abilities) {
            if (sender instanceof Player) {
                if (!GeneralMethods.canView((Player)sender, ability2)) continue;
                sender.sendMessage((Object)GeneralMethods.getAvatarColor() + ability2);
                continue;
            }
            sender.sendMessage((Object)GeneralMethods.getAvatarColor() + ability2);
        }
    }

    private void displayElement(CommandSender sender, String element) {
        List<String> abilities = ProjectKorra.plugin.abManager.getAbilities(element);
        if (abilities == null) {
            sender.sendMessage((Object)ChatColor.RED + "You must select a valid element.");
            return;
        }
        if (abilities.isEmpty()) {
            sender.sendMessage((Object)ChatColor.YELLOW + "There are no " + (Object)GeneralMethods.getElementColor(Element.valueOf(element)) + element + (Object)ChatColor.YELLOW + " abilities enabled on the server.");
        }
        for (String ability : abilities) {
            if (GeneralMethods.isSubAbility(ability) || sender instanceof Player && !GeneralMethods.canView((Player)sender, ability)) continue;
            sender.sendMessage((Object)GeneralMethods.getElementColor(Element.getType(element)) + ability);
        }
        if (element.equalsIgnoreCase("earth")) {
            if (sender.hasPermission("bending.earth.lavabending")) {
                sender.sendMessage((Object)ChatColor.DARK_GREEN + "Lavabending abilities: " + (Object)ChatColor.GREEN + "/bending display Lavabending");
            }
            if (sender.hasPermission("bending.earth.metalbending")) {
                sender.sendMessage((Object)ChatColor.DARK_GREEN + "Metalbending abilities: " + (Object)ChatColor.GREEN + "/bending display Metalbending");
            }
            if (sender.hasPermission("bending.earth.sandbending")) {
                sender.sendMessage((Object)ChatColor.DARK_GREEN + "Sandbending abilities: " + (Object)ChatColor.GREEN + "/bending display Sandbending");
            }
        }
        if (element.equalsIgnoreCase("air")) {
            sender.sendMessage((Object)ChatColor.DARK_GRAY + "Combos: " + (Object)ChatColor.GRAY + "/bending display AirCombos");
            if (sender.hasPermission("bending.air.flight")) {
                sender.sendMessage((Object)ChatColor.DARK_GRAY + "Flight abilities: " + (Object)ChatColor.GRAY + "/bending display Flight");
            }
        }
        if (element.equalsIgnoreCase("fire")) {
            sender.sendMessage((Object)ChatColor.DARK_RED + "Combos: " + (Object)ChatColor.RED + "/bending display FireCombos");
            if (sender.hasPermission("bending.fire.lightningbending")) {
                sender.sendMessage((Object)ChatColor.DARK_RED + "Lightning abilities: " + (Object)ChatColor.RED + "/bending display Lightning");
            }
            if (sender.hasPermission("bending.fire.combustionbending")) {
                sender.sendMessage((Object)ChatColor.DARK_RED + "Combustion abilities: " + (Object)ChatColor.RED + "/bending display Combustion");
            }
        }
        if (element.equalsIgnoreCase("water")) {
            sender.sendMessage((Object)ChatColor.DARK_AQUA + "Combos: " + (Object)ChatColor.AQUA + "/bending display WaterCombos");
            if (sender.hasPermission("bending.water.bloodbending")) {
                sender.sendMessage((Object)ChatColor.DARK_AQUA + "Bloodbending abilities: " + (Object)ChatColor.AQUA + "/bending display Bloodbending");
            }
            if (sender.hasPermission("bending.water.healing")) {
                sender.sendMessage((Object)ChatColor.DARK_AQUA + "Healing abilities: " + (Object)ChatColor.AQUA + "/bending display Healing");
            }
            if (sender.hasPermission("bending.water.icebending")) {
                sender.sendMessage((Object)ChatColor.DARK_AQUA + "Icebending abilities: " + (Object)ChatColor.AQUA + "/bending display Icebending");
            }
            if (sender.hasPermission("bending.water.plantbending")) {
                sender.sendMessage((Object)ChatColor.DARK_AQUA + "Plantbending abilities: " + (Object)ChatColor.AQUA + "/bending display Plantbending");
            }
        }
        if (element.equalsIgnoreCase("chi")) {
            sender.sendMessage((Object)ChatColor.GOLD + "Combos: " + (Object)ChatColor.YELLOW + "/bending display ChiCombos");
        }
    }

    private void displaySubElement(CommandSender sender, String element) {
        List<String> abilities = ProjectKorra.plugin.abManager.getAbilities(element);
        if (abilities.isEmpty() && element != null) {
            Element e = SubElement.getType(element.toLowerCase()).getMainElement();
            ChatColor color = GeneralMethods.getSubBendingColor(e);
            sender.sendMessage((Object)ChatColor.YELLOW + "There are no " + (Object)color + element + (Object)ChatColor.YELLOW + " abilities installed!");
            return;
        }
        for (String ability : abilities) {
            if (sender instanceof Player && !GeneralMethods.canView((Player)sender, ability)) continue;
            sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.getType(this.getElement(element))) + ability);
        }
    }

    private void displayBinds(CommandSender sender) {
        HashMap<Integer, String> abilities;
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(sender.getName());
        if (bPlayer == null) {
            GeneralMethods.createBendingPlayer(((Player)sender).getUniqueId(), sender.getName());
            bPlayer = GeneralMethods.getBendingPlayer(sender.getName());
        }
        if ((abilities = bPlayer.getAbilities()).isEmpty()) {
            sender.sendMessage((Object)ChatColor.RED + "You don't have any bound abilities.");
            sender.sendMessage("If you would like to see a list of available abilities, please use the /bending display [Element] command. Use /bending help for more information.");
            return;
        }
        int i = 1;
        while (i <= 9) {
            String ability = abilities.get(i);
            if (ability != null && !ability.equalsIgnoreCase("null")) {
                sender.sendMessage(String.valueOf(i) + " - " + (Object)GeneralMethods.getAbilityColor(ability) + ability);
            }
            ++i;
        }
    }
}

