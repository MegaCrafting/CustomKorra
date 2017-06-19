/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.Flight;

public class SandSpout {
    public static ConcurrentHashMap<Player, SandSpout> instances = new ConcurrentHashMap();
    private static final double HEIGHT = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.SandSpout.Height");
    private static final int BTIME = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.SandSpout.BlindnessTime");
    private static final int SPOUTDAMAGE = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.SandSpout.SpoutDamage");
    private static final boolean SPIRAL = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Earth.SandSpout.Spiral");
    private static final long interval = 100;
    private Player player;
    private long time;
    private int angle = 0;
    private double height = HEIGHT;
    private int bTime = BTIME;
    private double spoutDamage = SPOUTDAMAGE;
    private double y = 0.0;

    public SandSpout(Player player) {
        Material mat;
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).remove();
            return;
        }
        this.player = player;
        this.time = System.currentTimeMillis();
        Block topBlock = GeneralMethods.getTopBlock(player.getLocation(), 0, -50);
        if (topBlock == null) {
            topBlock = player.getLocation().getBlock();
        }
        if ((mat = topBlock.getType()) != Material.SAND && mat != Material.SANDSTONE && mat != Material.RED_SANDSTONE) {
            return;
        }
        new com.projectkorra.projectkorra.util.Flight(player);
        instances.put(player, this);
        this.spout();
    }

    public static void spoutAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).spout();
        }
    }

    public static ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        players.addAll(instances.keySet());
        return players;
    }

    private void spout() {
        Block block;
        if (!GeneralMethods.canBend(this.player.getName(), "SandSpout") || this.player.getEyeLocation().getBlock().isLiquid() || GeneralMethods.isSolid(this.player.getEyeLocation().getBlock()) || this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        this.player.setFallDistance(0.0f);
        this.player.setSprinting(false);
        if (GeneralMethods.rand.nextInt(2) == 0) {
            EarthMethods.playSandBendingSound(this.player.getLocation());
        }
        if ((block = this.getGround()) != null && (block.getType() == Material.SAND || block.getType() == Material.SANDSTONE || block.getType() == Material.RED_SANDSTONE)) {
            double dy = this.player.getLocation().getY() - (double)block.getY();
            if (dy > this.height) {
                this.removeFlight();
            } else {
                this.allowFlight();
            }
            this.rotateSandColumn(block);
        } else {
            this.remove();
        }
    }

    private void allowFlight() {
        this.player.setAllowFlight(true);
        this.player.setFlying(true);
        this.player.setFlySpeed(0.05f);
    }

    private void removeFlight() {
        this.player.setAllowFlight(false);
        this.player.setFlying(false);
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

    private void rotateSandColumn(Block block) {
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
                if (SPIRAL) {
                    this.displayHelix(block.getLocation(), this.player.getLocation(), block);
                }
                if (block != null && (block.getType() == Material.SAND && block.getData() == 0 || block.getType() == Material.SANDSTONE)) {
                    EarthMethods.displaySandParticle(effectloc2, 1.0f, 3.0f, 1.0f, 20.0f, 0.2f, false);
                } else if (block != null && (block.getType() == Material.SAND && block.getData() == 1 || block.getType() == Material.RED_SANDSTONE)) {
                    EarthMethods.displaySandParticle(effectloc2, 1.0f, 3.0f, 1.0f, 20.0f, 0.2f, true);
                }
                Collection<Player> players = GeneralMethods.getPlayersAroundPoint(effectloc2, 1.5);
                if (!players.isEmpty()) {
                    for (Player sPlayer : players) {
                        if (sPlayer.equals((Object)this.player)) continue;
                        sPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, this.bTime * 20, 1));
                        GeneralMethods.damageEntity(this.player, (Entity)sPlayer, this.spoutDamage, "SandSpout");
                    }
                }
                ++i;
            }
        }
    }

    private void displayHelix(Location location, Location player, Block block) {
        this.y += 0.1;
        if (this.y >= player.getY() - location.getY()) {
            this.y = 0.0;
        }
        int points = 0;
        while (points <= 5) {
            double x = Math.cos(this.y);
            double z = Math.sin(this.y);
            double nx = x * -1.0;
            double nz = z * -1.0;
            Location newloc = new Location(player.getWorld(), location.getX() + x, location.getY() + this.y, location.getZ() + z);
            Location secondloc = new Location(player.getWorld(), location.getX() + nx, location.getY() + this.y, location.getZ() + nz);
            if (block != null && (block.getType() == Material.SAND && block.getData() == 0 || block.getType() == Material.SANDSTONE)) {
                EarthMethods.displaySandParticle(newloc.add(0.5, 0.5, 0.5), 0.1f, 0.1f, 0.1f, 2.0f, 1.0f, false);
                EarthMethods.displaySandParticle(secondloc.add(0.5, 0.5, 0.5), 0.1f, 0.1f, 0.1f, 2.0f, 1.0f, false);
            } else if (block != null && (block.getType() == Material.SAND && block.getData() == 1 || block.getType() == Material.RED_SANDSTONE)) {
                EarthMethods.displaySandParticle(newloc.add(0.5, 0.5, 0.5), 0.1f, 0.1f, 0.1f, 2.0f, 1.0f, true);
                EarthMethods.displaySandParticle(secondloc.add(0.5, 0.5, 0.5), 0.1f, 0.1f, 0.1f, 2.0f, 1.0f, true);
            }
            ++points;
        }
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

    private void remove() {
        this.removeFlight();
        this.player.setFlySpeed(0.1f);
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).remove();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}

