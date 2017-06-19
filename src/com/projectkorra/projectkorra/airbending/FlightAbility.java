/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.airbending;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.util.Flight;

public class FlightAbility
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, FlightAbility> instances = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, Integer> hits = new ConcurrentHashMap();
    private static ConcurrentHashMap<String, Boolean> hovering = new ConcurrentHashMap();
    private Player player;
    private Flight flight;

    public FlightAbility(Player player) {
        if (!AirMethods.canFly(player, true, false)) {
            return;
        }
        if (this.flight == null) {
            this.flight = new Flight(player);
        }
        player.setAllowFlight(true);
        player.setVelocity(player.getEyeLocation().getDirection().normalize());
        this.player = player;
        instances.put(player, this);
    }

    public static void addHit(Player player) {
        if (FlightAbility.contains(player)) {
            if (hits.containsKey(player.getName())) {
                if (hits.get(player.getName()) >= 4) {
                    hits.remove(player.getName());
                    FlightAbility.remove(player);
                }
            } else {
                hits.put(player.getName(), 1);
            }
        }
    }

    public static boolean contains(Player player) {
        return instances.containsKey((Object)player);
    }

    public static boolean isHovering(Player player) {
        return hovering.containsKey(player.getName());
    }

    public static void remove(Player player) {
        if (FlightAbility.contains(player)) {
            instances.get((Object)player).remove();
        }
    }

    public static void removeAll() {
        for (FlightAbility ability : instances.values()) {
            ability.remove();
        }
        hits.clear();
        hovering.clear();
    }

    public static void setHovering(Player player, boolean bool) {
        String playername = player.getName();
        if (bool) {
            if (!hovering.containsKey(playername)) {
                hovering.put(playername, true);
                player.setVelocity(new Vector(0, 0, 0));
            }
        } else if (hovering.containsKey(playername)) {
            hovering.remove(playername);
        }
    }

    public boolean progress() {
        if (!AirMethods.canFly(this.player, false, FlightAbility.isHovering(this.player))) {
            FlightAbility.remove(this.player);
            return false;
        }
        if (this.flight == null) {
            this.flight = new Flight(this.player);
        }
        if (FlightAbility.isHovering(this.player)) {
            Vector vec = this.player.getVelocity().clone();
            vec.setY(0);
            this.player.setVelocity(vec);
        } else {
            this.player.setVelocity(this.player.getEyeLocation().getDirection().normalize());
        }
        return true;
    }

    public static void progressAll() {
        for (FlightAbility ability : instances.values()) {
            ability.progress();
        }
    }

    @Override
    public void reloadVariables() {
    }

    public void remove() {
        String name = this.player.getName();
        instances.remove((Object)this.player);
        hits.remove(name);
        hovering.remove(name);
        if (this.flight != null) {
            this.flight.revert();
        }
    }
}

