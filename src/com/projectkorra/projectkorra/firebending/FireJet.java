/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.FireStream;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FireJet
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, FireJet> instances = new ConcurrentHashMap();
    private static double defaultfactor = config.get().getDouble("Abilities.Fire.FireJet.Speed");
    private static long defaultduration = config.get().getLong("Abilities.Fire.FireJet.Duration");
    private static boolean isToggle = config.get().getBoolean("Abilities.Fire.FireJet.IsAvatarStateToggle");
    private Player player;
    private long time;
    private long duration = defaultduration;
    private double factor = defaultfactor;

    public FireJet(Player player) {
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).remove();
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("FireJet")) {
            return;
        }
        this.factor = FireMethods.getFirebendingDayAugment(defaultfactor, player.getWorld());
        Block block = player.getLocation().getBlock();
        if (FireStream.isIgnitable(player, block) || block.getType() == Material.AIR || AvatarState.isAvatarState(player)) {
            player.setVelocity(player.getEyeLocation().getDirection().clone().normalize().multiply(this.factor));
            if (FireMethods.canFireGrief()) {
                FireMethods.createTempFire(block.getLocation());
            } else {
                block.setType(Material.FIRE);
            }
            this.player = player;
            new com.projectkorra.projectkorra.util.Flight(player);
            player.setAllowFlight(true);
            this.time = System.currentTimeMillis();
            instances.put(player, this);
            bPlayer.addCooldown("FireJet", config.get().getLong("Abilities.Fire.FireJet.Cooldown"));
        }
    }

    public static boolean checkTemporaryImmunity(Player player) {
        if (instances.containsKey((Object)player)) {
            return true;
        }
        return false;
    }

    public static ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (FireJet jet : instances.values()) {
            players.add(jet.getPlayer());
        }
        return players;
    }

    public long getDuration() {
        return this.duration;
    }

    public double getFactor() {
        return this.factor;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (!(!WaterMethods.isWater(this.player.getLocation().getBlock()) && System.currentTimeMillis() <= this.time + this.duration || AvatarState.isAvatarState(this.player) && isToggle)) {
            this.remove();
        } else {
            if (GeneralMethods.rand.nextInt(2) == 0) {
                FireMethods.playFirebendingSound(this.player.getLocation());
            }
            ParticleEffect.FLAME.display(this.player.getLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 20);
            ParticleEffect.SMOKE_NORMAL.display(this.player.getLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 20);
            double timefactor = AvatarState.isAvatarState(this.player) && isToggle ? 1.0 : 1.0 - (double)(System.currentTimeMillis() - this.time) / (2.0 * (double)this.duration);
            Vector velocity = this.player.getEyeLocation().getDirection().clone().normalize().multiply(this.factor * timefactor);
            this.player.setVelocity(velocity);
            this.player.setFallDistance(0.0f);
        }
        return true;
    }

    public static void progressAll() {
        for (FireJet ability : instances.values()) {
            ability.progress();
        }
    }

    @Override
    public void reloadVariables() {
        defaultfactor = config.get().getDouble("Abilities.Fire.FireJet.Speed");
        defaultduration = config.get().getLong("Abilities.Fire.FireJet.Duration");
        isToggle = config.get().getBoolean("Abilities.Fire.FireJet.IsAvatarStateToggle");
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (FireJet ability : instances.values()) {
            ability.remove();
        }
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }
}

