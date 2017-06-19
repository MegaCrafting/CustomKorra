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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.chiblocking.ChiPassive;
import com.projectkorra.projectkorra.chiblocking.Paralyze;
import com.projectkorra.projectkorra.chiblocking.WarriorStance;
import com.projectkorra.projectkorra.earthbending.MetalClips;
import com.projectkorra.projectkorra.waterbending.Bloodbending;

public class AcrobatStance {
    public static double CHI_BLOCK_BOOST = ProjectKorra.plugin.getConfig().getDouble("Abilities.Chi.AcrobatStance.ChiBlockBoost");
    public static double PARA_DODGE_BOOST = ProjectKorra.plugin.getConfig().getDouble("Abilities.Chi.AcrobatStance.ParalyzeChanceDecrease");
    public static ConcurrentHashMap<Player, AcrobatStance> instances = new ConcurrentHashMap();
    private Player player;
    public double chiBlockBost = CHI_BLOCK_BOOST;
    public double paralyzeDodgeBoost = PARA_DODGE_BOOST;
    public int speed = ChiPassive.speedPower + 1;
    public int jump = ChiPassive.jumpPower + 1;

    public AcrobatStance(Player player) {
        this.player = player;
        if (instances.containsKey((Object)player)) {
            instances.remove((Object)player);
            return;
        }
        if (WarriorStance.isInWarriorStance(player)) {
            WarriorStance.remove(player);
        }
        instances.put(player, this);
    }

    public void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "AcrobatStance")) {
            this.remove();
            return;
        }
        if (MetalClips.isControlled(this.player) || Paralyze.isParalyzed((Entity)this.player) || Bloodbending.isBloodbended((Entity)this.player)) {
            this.remove();
            return;
        }
        if (!this.player.hasPotionEffect(PotionEffectType.SPEED)) {
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, this.speed));
        }
        if (!this.player.hasPotionEffect(PotionEffectType.JUMP)) {
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, this.jump));
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

    public static void remove(Player player) {
        instances.remove((Object)player);
    }

    public static boolean isInAcrobatStance(Player player) {
        return instances.containsKey((Object)player);
    }

    public double getChiBlockBost() {
        return this.chiBlockBost;
    }

    public void setChiBlockBost(double chiBlockBost) {
        this.chiBlockBost = chiBlockBost;
    }

    public double getParalyzeDodgeBoost() {
        return this.paralyzeDodgeBoost;
    }

    public void setParalyzeDodgeBoost(double paralyzeDodgeBoost) {
        this.paralyzeDodgeBoost = paralyzeDodgeBoost;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getJump() {
        return this.jump;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }
}

