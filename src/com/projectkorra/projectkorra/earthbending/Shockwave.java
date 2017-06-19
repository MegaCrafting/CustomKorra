/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.earthbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.earthbending.Ripple;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Shockwave {
    public static ConcurrentHashMap<Player, Shockwave> instances = new ConcurrentHashMap();
    private static final double angle = Math.toRadians(40.0);
    private static final long defaultchargetime = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.Shockwave.ChargeTime");
    private static final double threshold = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Shockwave.FallThreshold");
    private Player player;
    private long starttime;
    private long chargetime = defaultchargetime;
    private boolean charged = false;

    public Shockwave(Player player) {
        if (instances.containsKey((Object)player)) {
            return;
        }
        this.starttime = System.currentTimeMillis();
        if (AvatarState.isAvatarState(player)) {
            this.chargetime = 0;
        }
        this.player = player;
        instances.put(player, this);
    }

    public static void fallShockwave(Player player) {
        if (!GeneralMethods.canBend(player.getName(), "Shockwave")) {
            return;
        }
        if (GeneralMethods.getBoundAbility(player) == null || !GeneralMethods.getBoundAbility(player).equalsIgnoreCase("Shockwave")) {
            return;
        }
        if (instances.containsKey((Object)player) || (double)player.getFallDistance() < threshold || !EarthMethods.isEarthbendable(player, player.getLocation().add(0.0, -1.0, 0.0).getBlock())) {
            return;
        }
        Shockwave.areaShockwave(player);
    }

    private void progress() {
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            instances.remove((Object)this.player);
            return;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "Shockwave") || !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("Shockwave")) {
            instances.remove((Object)this.player);
            return;
        }
        if (System.currentTimeMillis() > this.starttime + this.chargetime && !this.charged) {
            this.charged = true;
        }
        if (!this.player.isSneaking()) {
            if (this.charged) {
                Shockwave.areaShockwave(this.player);
                instances.remove((Object)this.player);
            } else {
                instances.remove((Object)this.player);
            }
        } else if (this.charged) {
            Location location = this.player.getEyeLocation();
            location.getWorld().playEffect(location, Effect.SMOKE, GeneralMethods.getIntCardinalDirection(this.player.getEyeLocation().getDirection()), 3);
        }
    }

    public static void progressAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).progress();
        }
        Ripple.progressAll();
    }

    private static void areaShockwave(Player player) {
        double dtheta = 360.0 / (6.283185307179586 * Ripple.RADIUS) - 1.0;
        double theta = 0.0;
        while (theta < 360.0) {
            double rtheta = Math.toRadians(theta);
            Vector vector = new Vector(Math.cos(rtheta), 0.0, Math.sin(rtheta));
            new com.projectkorra.projectkorra.earthbending.Ripple(player, vector.normalize());
            theta += dtheta;
        }
    }

    public static void coneShockwave(Player player) {
        if (instances.containsKey((Object)player) && Shockwave.instances.get((Object)player).charged) {
            double dtheta = 360.0 / (6.283185307179586 * Ripple.RADIUS) - 1.0;
            double theta = 0.0;
            while (theta < 360.0) {
                double rtheta = Math.toRadians(theta);
                Vector vector = new Vector(Math.cos(rtheta), 0.0, Math.sin(rtheta));
                if ((double)vector.angle(player.getEyeLocation().getDirection()) < angle) {
                    new com.projectkorra.projectkorra.earthbending.Ripple(player, vector.normalize());
                }
                theta += dtheta;
            }
            instances.remove((Object)player);
        }
    }

    public static String getDescription() {
        return "This is one of the most powerful moves in the earthbender's arsenal. To use, you must first charge it by holding sneak (default: shift). Once charged, you can release sneak to create an enormous shockwave of earth, disturbing all earth around you and expanding radially outwards. Anything caught in the shockwave will be blasted back and dealt damage. If you instead click while charged, the disruption is focused in a cone in front of you. Lastly, if you fall from a great enough height with this ability selected, you will automatically create a shockwave.";
    }

    public static void removeAll() {
        instances.clear();
        Ripple.removeAll();
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getChargetime() {
        return this.chargetime;
    }

    public void setChargetime(long chargetime) {
        this.chargetime = chargetime;
    }
}

