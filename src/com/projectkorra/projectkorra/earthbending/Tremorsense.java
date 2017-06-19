/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;

public class Tremorsense {
    private static FileConfiguration config = ProjectKorra.plugin.getConfig();
    public static ConcurrentHashMap<Player, Tremorsense> instances = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Player> blocks = new ConcurrentHashMap();
    private static final int maxdepth = config.getInt("Abilities.Earth.Tremorsense.MaxDepth");
    private static final int radius = config.getInt("Abilities.Earth.Tremorsense.Radius");
    private static final byte lightthreshold = (byte)config.getInt("Abilities.Earth.Tremorsense.LightThreshold");
    private static long cooldown = config.getLong("Abilities.Earth.Tremorsense.Cooldown");
    private Player player;
    private Block block;

    public Tremorsense(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("Tremorsense")) {
            return;
        }
        if (EarthMethods.isEarthbendable(player, player.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
            this.player = player;
            bPlayer.addCooldown("Tremorsense", cooldown);
            this.activate();
        }
    }

    public Tremorsense(Player player, boolean value) {
        this.player = player;
        this.set();
    }

    private void activate() {
        Block block = this.player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        int i = - radius;
        while (i <= radius) {
            int j = - radius;
            while (j <= radius) {
                boolean earth = false;
                boolean foundair = false;
                Block smokeblock = null;
                int k = 0;
                while (k <= maxdepth) {
                    Block blocki = block.getRelative(BlockFace.EAST, i).getRelative(BlockFace.NORTH, j).getRelative(BlockFace.DOWN, k);
                    if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "RaiseEarth", blocki.getLocation())) {
                        if (EarthMethods.isEarthbendable(this.player, blocki) && !earth) {
                            earth = true;
                            smokeblock = blocki;
                        } else {
                            if (!EarthMethods.isEarthbendable(this.player, blocki) && earth) {
                                foundair = true;
                                break;
                            }
                            if (!EarthMethods.isEarthbendable(this.player, blocki) && !earth && blocki.getType() != Material.AIR) break;
                        }
                    }
                    ++k;
                }
                if (foundair) {
                    smokeblock.getWorld().playEffect(smokeblock.getRelative(BlockFace.UP).getLocation(), Effect.SMOKE, 4, radius);
                }
                ++j;
            }
            ++i;
        }
    }

    private void set() {
        Block standblock = this.player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        BendingPlayer bp = GeneralMethods.getBendingPlayer(this.player.getName());
        if (!bp.isTremorSensing()) {
            if (this.block != null) {
                this.revert();
            }
            return;
        }
        if (EarthMethods.isEarthbendable(this.player, standblock) && this.block == null) {
            this.block = standblock;
            this.player.sendBlockChange(this.block.getLocation(), 89, (byte) 1);
            instances.put(this.player, this);
        } else if (EarthMethods.isEarthbendable(this.player, standblock) && !this.block.equals((Object)standblock)) {
            this.revert();
            this.block = standblock;
            this.player.sendBlockChange(this.block.getLocation(), 89, (byte) 1);
            instances.put(this.player, this);
        } else {
            if (this.block == null) {
                return;
            }
            if (!this.player.getWorld().equals((Object)this.block.getWorld())) {
                this.revert();
            } else if (!EarthMethods.isEarthbendable(this.player, standblock)) {
                this.revert();
            }
        }
    }

    private void revert() {
        if (this.block != null) {
            this.player.sendBlockChange(this.block.getLocation(), this.block.getTypeId(), this.block.getData());
            instances.remove((Object)this.player);
        }
    }

    public static void manage(Server server) {
        for (Player player : server.getOnlinePlayers()) {
            if (instances.containsKey((Object)player) && (!GeneralMethods.canBend(player.getName(), "Tremorsense") || player.getLocation().getBlock().getLightLevel() > lightthreshold)) {
                instances.get((Object)player).revert();
                continue;
            }
            if (instances.containsKey((Object)player)) {
                instances.get((Object)player).set();
                continue;
            }
            if (!GeneralMethods.canBend(player.getName(), "Tremorsense") || player.getLocation().getBlock().getLightLevel() >= lightthreshold) continue;
            new com.projectkorra.projectkorra.earthbending.Tremorsense(player, false);
        }
    }

    public static void removeAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).revert();
        }
    }

    public static String getDescription() {
        return "This is a pure utility ability for earthbenders. If you have this ability bound to any slot whatsoever, then you are able to 'see' using the earth. If you are in an area of low-light and are standing on top of an earthbendable block, this ability will automatically turn that block into glowstone, visible *only by you*. If you lose contact with a bendable block, the light will go out, as you have lost contact with the earth and cannot 'see' until you can touch earth again. Additionally, if you click with this ability selected, smoke will appear above nearby earth with pockets of air beneath them.";
    }
}

