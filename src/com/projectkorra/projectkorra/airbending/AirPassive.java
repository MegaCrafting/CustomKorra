/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package com.projectkorra.projectkorra.airbending;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;

public class AirPassive
implements ConfigLoadable {
    private static ConcurrentHashMap<Player, Float> food = new ConcurrentHashMap();
    private static float factor = (float)config.get().getDouble("Abilities.Air.Passive.Factor");
    private static int speedPower = config.get().getInt("Abilities.Air.Passive.Speed");
    private static int jumpPower = config.get().getInt("Abilities.Air.Passive.Jump");

    public static float getExhaustion(Player player, float level) {
        if (!food.keySet().contains((Object)player)) {
            food.put(player, Float.valueOf(level));
            return level;
        }
        float oldlevel = food.get((Object)player).floatValue();
        if (level < oldlevel) {
            level = 0.0f;
        } else {
            factor = (float)config.get().getDouble("Abilities.Air.Passive.Factor");
            level = (level - oldlevel) * factor + oldlevel;
        }
        food.replace(player, Float.valueOf(level));
        return level;
    }

    public static void handlePassive(Server server) {
        for (World world : server.getWorlds()) {
            for (Player player : world.getPlayers()) {
                if (!player.isOnline()) {
                    return;
                }
           
                if (!GeneralMethods.canBendPassive(player.getName(), Element.Air)) continue;
                player.setExhaustion(AirPassive.getExhaustion(player, player.getExhaustion()));
                if (!player.isSprinting()) continue;
                if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
                    speedPower = config.get().getInt("Abilities.Air.Passive.Speed");
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, speedPower - 1));
                }
                if (player.hasPotionEffect(PotionEffectType.JUMP)) continue;
                jumpPower = config.get().getInt("Abilities.Air.Passive.Jump");
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, jumpPower - 1));
            }
        }
    }

    @Override
    public void reloadVariables() {
        factor = (float)config.get().getDouble("Abilities.Air.Passive.Factor");
        speedPower = config.get().getInt("Abilities.Air.Passive.Speed");
        jumpPower = config.get().getInt("Abilities.Air.Passive.Jump");
    }
}

