/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.chiblocking.ChiCombo;
import com.projectkorra.projectkorra.chiblocking.RapidPunch;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.object.HorizontalVelocityTracker;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.util.RevertChecker;
import com.projectkorra.projectkorra.util.TempPotionEffect;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class BendingManager
implements Runnable,
ConfigLoadable {
    private static BendingManager instance;
    public static HashMap<World, String> events;
    private static String sunriseMessage;
    private static String sunsetMessage;
    private static String moonriseMessage;
    private static String fullMoonriseMessage;
    private static String moonsetMessage;
    long time;
    long interval;
    private final HashMap<World, Boolean> times = new HashMap();

    static {
        events = new HashMap();
        sunriseMessage = config.get().getString("Properties.Fire.DayMessage");
        sunsetMessage = config.get().getString("Properties.Fire.NightMessage");
        moonriseMessage = config.get().getString("Properties.Water.NightMessage");
        fullMoonriseMessage = config.get().getString("Properties.Water.FullMoonMessage");
        moonsetMessage = config.get().getString("Properties.Water.DayMessage");
    }

    public BendingManager() {
        instance = this;
        this.time = System.currentTimeMillis();
    }

    public static BendingManager getInstance() {
        return instance;
    }

    public void handleCooldowns() {
        for (UUID uuid : BendingPlayer.getPlayers().keySet()) {
            BendingPlayer bPlayer = BendingPlayer.getPlayers().get(uuid);
            for (String abil : bPlayer.getCooldowns().keySet()) {
                if (System.currentTimeMillis() < bPlayer.getCooldown(abil)) continue;
                bPlayer.removeCooldown(abil);
            }
        }
    }

    public void handleDayNight() {
        for (World world2 : Bukkit.getServer().getWorlds()) {
            if (events.containsKey((Object)world2)) continue;
            events.put(world2, "");
        }
        for (World world2 : Bukkit.getServer().getWorlds()) {
            if (!this.times.containsKey((Object)world2)) {
                if (FireMethods.isDay(world2)) {
                    this.times.put(world2, true);
                    continue;
                }
                this.times.put(world2, false);
                continue;
            }
            if (this.times.get((Object)world2).booleanValue() && !FireMethods.isDay(world2)) {
                this.times.put(world2, false);
                if (WaterMethods.isFullMoon(world2)) {
                    events.put(world2, "FullMoon");
                } else {
                    events.put(world2, "");
                }
                for (Player player : world2.getPlayers()) {
                    if (!player.hasPermission("bending.message.nightmessage")) {
                        return;
                    }
                    if (GeneralMethods.isBender(player.getName(), Element.Water)) {
                        if (WaterMethods.isFullMoon(world2)) {
                            player.sendMessage((Object)WaterMethods.getWaterColor() + fullMoonriseMessage);
                        } else {
                            player.sendMessage((Object)WaterMethods.getWaterColor() + moonriseMessage);
                        }
                    }
                    if (!GeneralMethods.isBender(player.getName(), Element.Fire)) continue;
                    if (!player.hasPermission("bending.message.daymessage")) {
                        return;
                    }
                    player.sendMessage((Object)FireMethods.getFireColor() + sunsetMessage);
                }
            }
            if (this.times.get((Object)world2).booleanValue() || !FireMethods.isDay(world2)) continue;
            this.times.put(world2, true);
            events.put(world2, "");
            for (Player player : world2.getPlayers()) {
                if (GeneralMethods.isBender(player.getName(), Element.Water) && player.hasPermission("bending.message.nightmessage")) {
                    player.sendMessage((Object)WaterMethods.getWaterColor() + moonsetMessage);
                }
                if (!GeneralMethods.isBender(player.getName(), Element.Fire) || !player.hasPermission("bending.message.daymessage")) continue;
                player.sendMessage((Object)FireMethods.getFireColor() + sunriseMessage);
            }
        }
    }

    @Override
    public void run() {
        try {
            this.interval = System.currentTimeMillis() - this.time;
            this.time = System.currentTimeMillis();
            ProjectKorra.time_step = this.interval;
            AvatarState.manageAvatarStates();
            TempPotionEffect.progressAll();
            this.handleDayNight();
            Flight.handle();
            RapidPunch.startPunchAll();
            RevertChecker.revertAirBlocks();
            ChiCombo.handleParalysis();
            HorizontalVelocityTracker.updateAll();
            this.handleCooldowns();
        }
        catch (Exception e) {
            GeneralMethods.stopBending();
            e.printStackTrace();
        }
    }

    @Override
    public void reloadVariables() {
        sunriseMessage = config.get().getString("Properties.Fire.DayMessage");
        sunsetMessage = config.get().getString("Properties.Fire.NightMessage");
        moonriseMessage = config.get().getString("Properties.Water.NightMessage");
        fullMoonriseMessage = config.get().getString("Properties.Water.FullMoonMessage");
        moonsetMessage = config.get().getString("Properties.Water.DayMessage");
    }
}

