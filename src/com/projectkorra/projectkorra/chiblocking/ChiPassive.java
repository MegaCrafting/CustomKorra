/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitScheduler
 */
package com.projectkorra.projectkorra.chiblocking;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.Suffocate;
import com.projectkorra.projectkorra.chiblocking.AcrobatStance;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.chiblocking.QuickStrike;
import com.projectkorra.projectkorra.chiblocking.SwiftKick;

public class ChiPassive {
    private static FileConfiguration config = ProjectKorra.plugin.getConfig();
    public static double FallReductionFactor = config.getDouble("Abilities.Chi.Passive.FallReductionFactor");
    public static int jumpPower = config.getInt("Abilities.Chi.Passive.Jump");
    public static int speedPower = config.getInt("Abilities.Chi.Passive.Speed");
    public static double chance = config.getDouble("Abilities.Chi.Passive.BlockChi.Chance");
    public static int duration = config.getInt("Abilities.Chi.Passive.BlockChi.Duration");
    static long ticks = duration / 1000 * 20;

    public static boolean willChiBlock(Player attacker, Player player) {
        if (AcrobatStance.isInAcrobatStance(attacker)) {
            chance += AcrobatStance.CHI_BLOCK_BOOST;
        }
        if (GeneralMethods.getBoundAbility(player) == "QuickStrike") {
            chance += (double)QuickStrike.blockChance;
        }
        if (GeneralMethods.getBoundAbility(player) == "SwiftKick") {
            chance += (double)SwiftKick.blockChance;
        }
        if (Math.random() > chance / 100.0) {
            return false;
        }
        if (ChiMethods.isChiBlocked(player.getName())) {
            return false;
        }
        return true;
    }

    public static void blockChi(Player player) {
        BendingPlayer bPlayer;
        if (Suffocate.isChannelingSphere(player)) {
            Suffocate.remove(player);
        }
        if ((bPlayer = GeneralMethods.getBendingPlayer(player.getName())) == null) {
            return;
        }
        bPlayer.blockChi();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)ProjectKorra.plugin, new Runnable(){

            @Override
            public void run() {
                bPlayer.unblockChi();
            }
        }, ticks);
    }

    public static void handlePassive() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!GeneralMethods.canBendPassive(player.getName(), Element.Chi) || GeneralMethods.canBendPassive(player.getName(), Element.Air) || !player.isSprinting()) continue;
            if (!player.hasPotionEffect(PotionEffectType.JUMP) && !AcrobatStance.isInAcrobatStance(player)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, jumpPower - 1));
            }
            if (player.hasPotionEffect(PotionEffectType.SPEED) || AcrobatStance.isInAcrobatStance(player)) continue;
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, speedPower - 1));
        }
    }

}

