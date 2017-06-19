/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package com.projectkorra.projectkorra.chiblocking;

import com.projectkorra.projectkorra.ability.StockAbility;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.chiblocking.AcrobatStance;
import com.projectkorra.projectkorra.chiblocking.Paralyze;
import com.projectkorra.projectkorra.waterbending.Bloodbending;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WarriorStance {
    public int strength = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.WarriorStance.Strength") - 1;
    public int resistance = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.WarriorStance.Resistance");
    private Player player;
    public static ConcurrentHashMap<Player, WarriorStance> instances = new ConcurrentHashMap();

    public WarriorStance(Player player) {
        this.player = player;
        if (instances.containsKey((Object)player)) {
            instances.remove((Object)player);
            return;
        }
        if (AcrobatStance.isInAcrobatStance(player)) {
            AcrobatStance.remove(player);
        }
        instances.put(player, this);
    }

    private void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (!GeneralMethods.canBend(this.player.getName(), StockAbility.WarriorStance.toString())) {
            this.remove();
            return;
        }
        if (Paralyze.isParalyzed((Entity)this.player) || Bloodbending.isBloodbended((Entity)this.player)) {
            this.remove();
            return;
        }
        if (!this.player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, this.resistance));
        }
        if (!this.player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, this.strength));
        }
    }

    public static void progressAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).progress();
        }
    }

    private void remove() {
        instances.remove((Object)this.player);
    }

    public static boolean isInWarriorStance(Player player) {
        if (instances.containsKey((Object)player)) {
            return true;
        }
        return false;
    }

    public static void remove(Player player) {
        instances.remove((Object)player);
    }

    public int getStrength() {
        return this.strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getResistance() {
        return this.resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    public Player getPlayer() {
        return this.player;
    }
}

