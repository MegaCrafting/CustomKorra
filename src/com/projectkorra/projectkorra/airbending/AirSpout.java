/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.airbending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.util.Flight;

public class AirSpout
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, AirSpout> instances = new ConcurrentHashMap();
    private static double HEIGHT = config.get().getDouble("Abilities.Air.AirSpout.Height");
    private static final long interval = 100;
    private Player player;
    private long time;
    private int angle = 0;
    private double height = HEIGHT;

    public AirSpout(Player player) {
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).remove();
            return;
        }
        this.player = player;
        this.time = System.currentTimeMillis();
        new com.projectkorra.projectkorra.util.Flight(player);
        instances.put(player, this);
        this.progress();
    }

    public static ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (AirSpout spout : instances.values()) {
            players.add(spout.getPlayer());
        }
        return players;
    }

    public static boolean removeSpouts(Location loc0, double radius, Player sourceplayer) {
        boolean removed = false;
        for (Player player : instances.keySet()) {
            if (player.equals((Object)sourceplayer)) continue;
            Location loc1 = player.getLocation().getBlock().getLocation();
            loc0 = loc0.getBlock().getLocation();
            double dx = loc1.getX() - loc0.getX();
            double dy = loc1.getY() - loc0.getY();
            double dz = loc1.getZ() - loc0.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance > radius || dy <= 0.0 || dy >= HEIGHT) continue;
            instances.get((Object)player).remove();
            removed = true;
        }
        return removed;
    }

    private void allowFlight() {
        this.player.setAllowFlight(true);
        this.player.setFlying(true);
    }

    private Block getGround() {
        Block standingblock = this.player.getLocation().getBlock();
        int i = 0;
        while ((double)i <= this.height + 5.0) {
            Block block = standingblock.getRelative(BlockFace.DOWN, i);
            if (GeneralMethods.isSolid(block) || block.isLiquid()) {
                return block;
            }
            ++i;
        }
        return null;
    }

    public double getHeight() {
        return this.height;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean progress() {
        Block block;
        if (!GeneralMethods.canBend(this.player.getName(), "AirSpout") || this.player.getEyeLocation().getBlock().isLiquid() || GeneralMethods.isSolid(this.player.getEyeLocation().getBlock()) || this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        this.player.setFallDistance(0.0f);
        this.player.setSprinting(false);
        if (GeneralMethods.rand.nextInt(4) == 0) {
            AirMethods.playAirbendingSound(this.player.getLocation());
        }
        if ((block = this.getGround()) != null) {
            double dy = this.player.getLocation().getY() - (double)block.getY();
            if (dy > this.height) {
                this.removeFlight();
            } else {
                this.allowFlight();
            }
            this.rotateAirColumn(block);
        } else {
            this.remove();
        }
        return true;
    }

    public static void progressAll() {
        for (AirSpout ability : instances.values()) {
            ability.progress();
        }
    }

    @Override
    public void reloadVariables() {
        this.height = AirSpout.HEIGHT = config.get().getDouble("Abilities.Air.AirSpout.Height");
    }

    public void remove() {
        this.removeFlight();
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (AirSpout ability : instances.values()) {
            ability.remove();
        }
    }

    private void removeFlight() {
        this.player.setAllowFlight(false);
        this.player.setFlying(false);
    }

    private void rotateAirColumn(Block block) {
        if (this.player.getWorld() != block.getWorld()) {
            return;
        }
        if (System.currentTimeMillis() >= this.time + 100) {
            this.time = System.currentTimeMillis();
            Location location = block.getLocation();
            Location playerloc = this.player.getLocation();
            location = new Location(location.getWorld(), playerloc.getX(), location.getY(), playerloc.getZ());
            double dy = playerloc.getY() - (double)block.getY();
            if (dy > this.height) {
                dy = this.height;
            }
            Integer[] directions = new Integer[]{0, 1, 2, 3, 5, 6, 7, 8};
            int index = this.angle++;
            if (this.angle >= directions.length) {
                this.angle = 0;
            }
            int i = 1;
            while ((double)i <= dy) {
                if (++index >= directions.length) {
                    index = 0;
                }
                Location effectloc2 = new Location(location.getWorld(), location.getX(), (double)(block.getY() + i), location.getZ());
                AirMethods.playAirbendingParticles(effectloc2, 3, 0.4f, 0.4f, 0.4f);
                ++i;
            }
        }
    }

    public void setHeight(double height) {
        this.height = height;
    }
}

