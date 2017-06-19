/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 */
package com.projectkorra.projectkorra.command;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.combo.ComboManager;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand
extends PKCommand {
    public HelpCommand() {
        super("help", "/bending help", "This command provides information on how to use other commands in ProjectKorra.", new String[]{"help", "h"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.hasPermission(sender) || !this.correctLength(sender, args.size(), 0, 1)) {
            return;
        }
        if (args.size() == 0) {
            for (PKCommand command : instances.values()) {
                sender.sendMessage((Object)ChatColor.YELLOW + command.getProperUse());
            }
            return;
        }
        String arg = args.get(0);
        if (instances.keySet().contains(arg.toLowerCase())) {
            ((PKCommand)instances.get(arg)).help(sender, true);
        } else if (Arrays.asList(Commands.comboaliases).contains(arg)) {
            sender.sendMessage((Object)ChatColor.GOLD + "Proper Usage: " + (Object)ChatColor.RED + "/bending display " + arg + (Object)ChatColor.GOLD + " or " + (Object)ChatColor.RED + "/bending help <Combo Name>");
        } else if (GeneralMethods.abilityExists(arg)) {
            String ability = GeneralMethods.getAbility(arg);
            ChatColor color = GeneralMethods.getAbilityColor(ability);
            sender.sendMessage((Object)color + ability + " - ");
            sender.sendMessage((Object)color + AbilityModuleManager.descriptions.get(GeneralMethods.getAbility(ability)));
        } else if (Arrays.asList(Commands.airaliases).contains(args.get(0))) {
            sender.sendMessage((Object)AirMethods.getAirColor() + "Air is the element of freedom. Airbenders are natural pacifists and " + "great explorers. There is nothing stopping them from scaling the tallest of mountains and walls easily. They specialize in redirection, " + "from blasting things away with gusts of winds, to forming a shield around them to prevent damage. Easy to get across flat terrains, " + "such as oceans, there is practically no terrain off limits to Airbenders. They lack much raw damage output, but make up for it with " + "with their ridiculous amounts of utility and speed.");
            sender.sendMessage((Object)ChatColor.YELLOW + "Airbenders can chain their abilities into combos, type " + (Object)AirMethods.getAirColor() + "/b help AirCombos" + (Object)ChatColor.YELLOW + " for more information.");
            sender.sendMessage((Object)ChatColor.YELLOW + "Learn More: " + (Object)ChatColor.DARK_AQUA + "http://tinyurl.com/qffg9m3");
        } else if (Arrays.asList(Commands.wateraliases).contains(args.get(0))) {
            sender.sendMessage((Object)WaterMethods.getWaterColor() + "Water is the element of change. Waterbending focuses on using your " + "opponents own force against them. Using redirection and various dodging tactics, you can be made " + "practically untouchable by an opponent. Waterbending provides agility, along with strong offensive " + "skills while in or near water.");
            sender.sendMessage((Object)ChatColor.YELLOW + "Waterbenders can chain their abilities into combos, type " + (Object)WaterMethods.getWaterColor() + "/b help WaterCombos" + (Object)ChatColor.YELLOW + " for more information.");
            sender.sendMessage((Object)ChatColor.YELLOW + "Learn More: " + (Object)ChatColor.DARK_AQUA + "http://tinyurl.com/lod3plv");
        } else if (Arrays.asList(Commands.earthaliases).contains(args.get(0))) {
            sender.sendMessage((Object)EarthMethods.getEarthColor() + "Earth is the element of substance. Earthbenders share many of the " + "same fundamental techniques as Waterbenders, but their domain is quite different and more readily " + "accessible. Earthbenders dominate the ground and subterranean, having abilities to pull columns " + "of rock straight up from the earth or drill their way through the mountain. They can also launch " + "themselves through the air using pillars of rock, and will not hurt themselves assuming they land " + "on something they can bend. The more skilled Earthbenders can even bend metal.");
            sender.sendMessage((Object)ChatColor.YELLOW + "Learn More: " + (Object)ChatColor.DARK_AQUA + "http://tinyurl.com/qaudl42");
        } else if (Arrays.asList(Commands.firealiases).contains(args.get(0))) {
            sender.sendMessage((Object)FireMethods.getFireColor() + "Fire is the element of power. Firebenders focus on destruction and " + "incineration. Their abilities are pretty straight forward: set things on fire. They do have a bit " + "of utility however, being able to make themselves un-ignitable, extinguish large areas, cook food " + "in their hands, extinguish large areas, small bursts of flight, and then comes the abilities to shoot " + "fire from your hands.");
            sender.sendMessage((Object)ChatColor.YELLOW + "Firebenders can chain their abilities into combos, type " + (Object)FireMethods.getFireColor() + "/b help FireCombos" + (Object)ChatColor.YELLOW + " for more information.");
            sender.sendMessage((Object)ChatColor.YELLOW + "Learn More: " + (Object)ChatColor.DARK_AQUA + "http://tinyurl.com/k4fkjhb");
        } else if (Arrays.asList(Commands.chialiases).contains(args.get(0))) {
            sender.sendMessage((Object)ChiMethods.getChiColor() + "Chiblockers focus on bare handed combat, utilizing their agility and " + "speed to stop any bender right in their path. Although they lack the ability to bend any of the " + "other elements, they are great in combat, and a serious threat to any bender. Chiblocking was " + "first shown to be used by Ty Lee in Avatar: The Last Airbender, then later by members of the " + "Equalists in The Legend of Korra.");
            sender.sendMessage((Object)ChatColor.YELLOW + "Chiblockers can chain their abilities into combos, type " + (Object)ChiMethods.getChiColor() + "/b help ChiCombos" + (Object)ChatColor.YELLOW + " for more information.");
            sender.sendMessage((Object)ChatColor.YELLOW + "Learn More: " + (Object)ChatColor.DARK_AQUA + "http://tinyurl.com/mkp9n6y");
        } else {
            for (String combo : ComboManager.descriptions.keySet()) {
                if (!combo.equalsIgnoreCase(arg)) continue;
                ChatColor color = GeneralMethods.getComboColor(combo);
                sender.sendMessage((Object)color + combo + " (Combo) - ");
                sender.sendMessage((Object)color + ComboManager.descriptions.get(combo));
                sender.sendMessage((Object)ChatColor.GOLD + "Usage: " + ComboManager.instructions.get(combo));
                return;
            }
            sender.sendMessage((Object)ChatColor.RED + "That isn't a valid help topic. Use /bending help for more information.");
        }
    }
}

