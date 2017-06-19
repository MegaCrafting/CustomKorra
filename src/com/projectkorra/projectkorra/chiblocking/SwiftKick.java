/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.chiblocking;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.chiblocking.ChiPassive;

public class SwiftKick {
    public static int damage = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.SwiftKick.Damage");
    public static int blockChance = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.ChiCombo.ChiBlockChance");

    public SwiftKick(Player player) {
        if (!this.isEligible(player)) {
            return;
        }
        Entity e = GeneralMethods.getTargetedEntity(player, 4.0, new ArrayList<Entity>());
        if (e == null) {
            return;
        }
        GeneralMethods.damageEntity(player, e, damage, "SwiftKick");
        if (e instanceof Player && ChiPassive.willChiBlock(player, (Player)e)) {
            ChiPassive.blockChi((Player)e);
        }
        GeneralMethods.getBendingPlayer(player.getName()).addCooldown("SwiftKick", 4000);
    }

    public boolean isEligible(Player player) {
        if (!GeneralMethods.canBend(player.getName(), "SwiftKick")) {
            return false;
        }
        if (GeneralMethods.getBoundAbility(player) == null) {
            return false;
        }
        if (!GeneralMethods.getBoundAbility(player).equalsIgnoreCase("SwiftKick")) {
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "SwiftKick", player.getLocation())) {
            return false;
        }
        if (GeneralMethods.getBendingPlayer(player.getName()).isOnCooldown("SwiftKick")) {
            return false;
        }
        if (player.isOnGround()) {
            return false;
        }
        return true;
    }
}

