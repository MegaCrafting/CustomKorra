/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.command;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.command.PKCommand;

public class ClearCommand
extends PKCommand {
    public ClearCommand() {
        super("clear", "/bending clear [Slot]", "This command will clear the bound ability from the slot you specify (if you specify one). If you choose not to specify a slot, all of your abilities will be cleared.", new String[]{"clear", "cl", "c"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!(this.hasPermission(sender) && this.correctLength(sender, args.size(), 0, 1) && this.isPlayer(sender))) {
            return;
        }
        if (MultiAbilityManager.hasMultiAbilityBound((Player)sender)) {
            sender.sendMessage((Object)ChatColor.RED + "You can't edit your binds right now!");
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(sender.getName());
        if (bPlayer == null) {
            GeneralMethods.createBendingPlayer(((Player)sender).getUniqueId(), sender.getName());
            bPlayer = GeneralMethods.getBendingPlayer(sender.getName());
        }
        if (args.size() == 0) {
            bPlayer.getAbilities().clear();
            int i = 1;
            while (i <= 9) {
                GeneralMethods.saveAbility(bPlayer, i, null);
                ++i;
            }
            sender.sendMessage("Your bound abilities have been cleared.");
        } else if (args.size() == 1) {
            try {
                int slot = Integer.parseInt(args.get(0));
                if (slot < 1 || slot > 9) {
                    sender.sendMessage((Object)ChatColor.RED + "The slot must be an integer between 1 and 9.");
                }
                if (bPlayer.getAbilities().get(slot) != null) {
                    bPlayer.getAbilities().remove(slot);
                    GeneralMethods.saveAbility(bPlayer, slot, null);
                    sender.sendMessage("You have cleared slot #" + slot);
                } else {
                    sender.sendMessage("That slot was already empty.");
                }
            }
            catch (NumberFormatException e) {
                sender.sendMessage((Object)ChatColor.RED + "The slot must be an integer between 1 and 9.");
            }
        }
    }
}

