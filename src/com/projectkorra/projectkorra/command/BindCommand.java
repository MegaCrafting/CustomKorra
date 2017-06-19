/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.PlayerInventory
 */
package com.projectkorra.projectkorra.command;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.command.PKCommand;

public class BindCommand
extends PKCommand {
    public BindCommand() {
        super("bind", "/bending bind [Ability] <#>", "This command will bind an ability to the slot you specify (if you specify one), or the slot currently selected in your hotbar (If you do not specify a Slot #).", new String[]{"bind", "b"});
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!(this.hasPermission(sender) && this.correctLength(sender, args.size(), 1, 2) && this.isPlayer(sender))) {
            return;
        }
        if (!GeneralMethods.abilityExists(args.get(0))) {
            sender.sendMessage((Object)ChatColor.RED + "That ability doesn't exist.");
            return;
        }
        String ability = GeneralMethods.getAbility(args.get(0));
        if (args.size() == 1) {
            this.bind(sender, ability, ((Player)sender).getInventory().getHeldItemSlot() + 1);
        }
        if (args.size() == 2) {
            this.bind(sender, ability, Integer.parseInt(args.get(1)));
        }
    }

    private void bind(CommandSender sender, String ability, int slot) {
        if (slot < 1 || slot > 9) {
            sender.sendMessage((Object)ChatColor.RED + "Slot must be an integer between 1 and 9.");
            return;
        }
        if (!GeneralMethods.canBind(((Player)sender).getName(), ability)) {
            sender.sendMessage((Object)ChatColor.RED + "You don't have permission to bend this element.");
            return;
        }
        if (!GeneralMethods.getBendingPlayer(sender.getName()).isElementToggled(GeneralMethods.getAbilityElement(ability))) {
            sender.sendMessage((Object)ChatColor.RED + "You have that ability's element toggled off currently.");
        }
        GeneralMethods.bindAbility((Player)sender, GeneralMethods.getAbility(ability), slot);
    }
}

