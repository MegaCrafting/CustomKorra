/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.GameMode
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.airbending.AirScooter;
import com.projectkorra.projectkorra.airbending.AirSpout;
import com.projectkorra.projectkorra.airbending.Tornado;
import com.projectkorra.projectkorra.earthbending.Catapult;
import com.projectkorra.projectkorra.earthbending.SandSpout;
import com.projectkorra.projectkorra.firebending.FireJet;
import com.projectkorra.projectkorra.waterbending.Bloodbending;
import com.projectkorra.projectkorra.waterbending.WaterSpout;

public class Flight {
    private static ConcurrentHashMap<Player, Flight> instances = new ConcurrentHashMap();
    private static long duration = 5000;
    private Player player;
    private Player source;
    private boolean couldFly = false;
    private boolean wasFlying = false;
    private long time;

    public Flight(Player player) {
        this(player, null);
    }

    public Flight(Player player, Player source) {
        if (instances.containsKey((Object)player)) {
            Flight flight = instances.get((Object)player);
            flight.refresh(source);
            instances.replace(player, flight);
            return;
        }
        this.couldFly = player.getAllowFlight();
        this.wasFlying = player.isFlying();
        this.player = player;
        this.source = source;
        this.time = System.currentTimeMillis();
        instances.put(player, this);
    }

    public boolean equals(Object object) {
        if (!(object instanceof Flight)) {
            return false;
        }
        Flight flight = (Flight)object;
        if (flight.player == this.player && flight.source == this.source && flight.couldFly == this.couldFly && flight.wasFlying == this.wasFlying) {
            return true;
        }
        return false;
    }

    public static Player getLaunchedBy(Player player) {
        if (instances.containsKey((Object)player)) {
            return Flight.instances.get((Object)player).source;
        }
        return null;
    }

    public static void handle() {
        ArrayList<Player> players = new ArrayList<Player>();
        ArrayList<Player> newflyingplayers = new ArrayList<Player>();
        ArrayList airscooterplayers = new ArrayList();
        ArrayList waterspoutplayers = new ArrayList();
        ArrayList airspoutplayers = new ArrayList();
        ArrayList sandspoutplayers = new ArrayList();
        players.addAll(Tornado.getPlayers());
        players.addAll(FireJet.getPlayers());
        players.addAll(Catapult.getPlayers());
        airscooterplayers = AirScooter.getPlayers();
        waterspoutplayers = WaterSpout.getPlayers();
        airspoutplayers = AirSpout.getPlayers();
        sandspoutplayers = SandSpout.getPlayers();
        for (Player player : instances.keySet()) {
            Flight flight = instances.get((Object)player);
            if (System.currentTimeMillis() <= flight.time + duration) {
                if (airscooterplayers.contains((Object)player) || waterspoutplayers.contains((Object)player) || airspoutplayers.contains((Object)player) || sandspoutplayers.contains((Object)player)) continue;
                if (Bloodbending.isBloodbended((Entity)player)) {
                    player.setAllowFlight(true);
                    player.setFlying(false);
                    continue;
                }
                if (players.contains((Object)player)) {
                    flight.refresh(null);
                    player.setAllowFlight(true);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        player.setFlying(false);
                    }
                    newflyingplayers.add(player);
                    continue;
                }
                if (flight.source == null) {
                    flight.revert();
                    flight.remove();
                    continue;
                }
                if (System.currentTimeMillis() < flight.time + duration) continue;
                flight.revert();
                flight.remove();
                continue;
            }
            flight.revert();
            flight.remove();
        }
    }

    public static void removeAll() {
        for (Player player : instances.keySet()) {
            Flight flight = instances.get((Object)player);
            if (flight == null) {
                instances.remove((Object)player);
                continue;
            }
            flight.revert();
            flight.remove();
        }
    }

    private void refresh(Player source) {
        this.source = source;
        this.time = System.currentTimeMillis();
        instances.replace(this.player, this);
    }

    public void remove() {
        if (this.player == null) {
            for (Player player : instances.keySet()) {
                if (!instances.get((Object)player).equals(this)) continue;
                instances.remove((Object)player);
            }
            return;
        }
        instances.remove((Object)this.player);
    }

    public void revert() {
        if (this.player == null) {
            return;
        }
        this.player.setAllowFlight(this.couldFly);
        this.player.setFlying(this.wasFlying);
    }
}

