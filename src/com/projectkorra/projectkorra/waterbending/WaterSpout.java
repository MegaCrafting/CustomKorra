/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffectType
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.chiblocking.Paralyze;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Bloodbending;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterWave;

public class WaterSpout {
    public static ConcurrentHashMap<Player, WaterSpout> instances = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Block> newaffectedblocks = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Block> baseblocks = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Long> revert = new ConcurrentHashMap();
    private static final int HEIGHT = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.WaterSpout.Height");
    private static final boolean PARTICLES = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Water.WaterSpout.Particles");
    private static final boolean BLOCKS = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Water.WaterSpout.BlockSpiral");
    private static final byte full = 0;
    private Player player;
    private Block base;
    private TempBlock baseblock;
    private int defaultheight = HEIGHT;
    private long time = 0;
    private long interval = 50;
    private int angle = 0;
    private double rotation;
    private boolean canBendOnPackedIce = false;

    public WaterSpout(Player player) {
        Material mat;
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).remove();
            return;
        }
        this.player = player;
        this.canBendOnPackedIce = ProjectKorra.plugin.getConfig().getBoolean("Properties.Water.CanBendPackedIce");
        WaterWave wwave = new WaterWave(player, WaterWave.AbilityType.CLICK);
        if (WaterWave.instances.contains(wwave)) {
            return;
        }
        Block topBlock = GeneralMethods.getTopBlock(player.getLocation(), 0, -50);
        if (topBlock == null) {
            topBlock = player.getLocation().getBlock();
        }
        if ((mat = topBlock.getType()) != Material.WATER && mat != Material.STATIONARY_WATER && mat != Material.ICE && mat != Material.PACKED_ICE && mat != Material.SNOW && mat != Material.SNOW_BLOCK) {
            return;
        }
        if (mat == Material.PACKED_ICE && !this.canBendOnPackedIce) {
            return;
        }
        new com.projectkorra.projectkorra.util.Flight(player);
        player.setAllowFlight(true);
        instances.put(player, this);
        WaterSpout.spout(player);
    }

    private void remove() {
        WaterSpout.revertBaseBlock(this.player);
        instances.remove((Object)this.player);
    }

    private static void progressRevert(boolean ignoreTime) {
        for (Block block : revert.keySet()) {
            long time = revert.get((Object)block);
            if (System.currentTimeMillis() <= time && !ignoreTime) continue;
            if (TempBlock.isTempBlock(block)) {
                TempBlock.revertBlock(block, Material.AIR);
            }
            revert.remove((Object)block);
        }
    }

    public static void handleSpouts(Server server) {
        newaffectedblocks.clear();
        WaterSpout.progressRevert(false);
        for (Player player : instances.keySet()) {
            if (!player.isOnline() || player.isDead()) {
                instances.get((Object)player).remove();
                continue;
            }
            if (GeneralMethods.canBend(player.getName(), "WaterSpout")) {
                WaterSpout.spout(player);
                continue;
            }
            instances.get((Object)player).remove();
        }
        for (Block block : affectedblocks.keySet()) {
            if (newaffectedblocks.containsKey((Object)block)) continue;
            WaterSpout.remove(block);
        }
    }

    private static void remove(Block block) {
        affectedblocks.remove((Object)block);
        TempBlock.revertBlock(block, Material.AIR);
    }

    public static void spout(Player player) {
        WaterSpout spout = instances.get((Object)player);
        if (Bloodbending.isBloodbended((Entity)player) || Paralyze.isParalyzed((Entity)player)) {
            instances.get((Object)player).remove();
        } else {
            player.setFallDistance(0.0f);
            player.setSprinting(false);
            if (GeneralMethods.rand.nextInt(4) == 0) {
                WaterMethods.playWaterbendingSound(player.getLocation());
            }
            player.removePotionEffect(PotionEffectType.SPEED);
            Location location = player.getLocation().clone().add(0.0, 0.2, 0.0);
            Block block = location.clone().getBlock();
            int height = WaterSpout.spoutableWaterHeight(location, player);
            if (height != -1) {
                location = spout.base.getLocation();
                int i = 1;
                while (i <= height) {
                    block = location.clone().add(0.0, (double)i, 0.0).getBlock();
                    if (!TempBlock.isTempBlock(block)) {
                        new com.projectkorra.projectkorra.util.TempBlock(block, Material.STATIONARY_WATER, (byte)8);
                    }
                    if (!affectedblocks.containsKey((Object)block)) {
                        affectedblocks.put(block, block);
                    }
                    instances.get((Object)player).rotateParticles(block);
                    newaffectedblocks.put(block, block);
                    ++i;
                }
                instances.get((Object)player).displayWaterSpiral(location.clone().add(0.5, 0.0, 0.5));
                if (player.getLocation().getBlockY() > block.getY()) {
                    player.setFlying(false);
                } else {
                    new com.projectkorra.projectkorra.util.Flight(player);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                }
            } else {
                instances.get((Object)player).remove();
            }
        }
    }

    public void rotateParticles(Block block) {
        if (!PARTICLES) {
            return;
        }
        if (System.currentTimeMillis() >= this.time + this.interval) {
            this.time = System.currentTimeMillis();
            Location location = block.getLocation();
            Location playerloc = this.player.getLocation();
            location = new Location(location.getWorld(), playerloc.getX(), location.getY(), playerloc.getZ());
            double dy = playerloc.getY() - (double)block.getY();
            if (dy > (double)HEIGHT) {
                dy = HEIGHT;
            }
            float[] directions = new float[]{-0.5f, 0.325f, 0.25f, 0.125f, 0.0f, 0.125f, 0.25f, 0.325f, 0.5f};
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
                ParticleEffect.WATER_SPLASH.display(effectloc2, directions[index], directions[index], directions[index], 5.0f, HEIGHT + 5);
                ++i;
            }
        }
    }

    private static int spoutableWaterHeight(Location location, Player player) {
        WaterSpout spout = instances.get((Object)player);
        int height = spout.defaultheight;
        if (WaterMethods.isNight(player.getWorld())) {
            height = (int)WaterMethods.waterbendingNightAugment(height, player.getWorld());
        }
        int maxheight = (int)((double)spout.defaultheight * ProjectKorra.plugin.getConfig().getDouble("Properties.Water.NightFactor")) + 5;
        int i = 0;
        while (i < maxheight) {
            Block blocki = location.clone().add(0.0, (double)(- i), 0.0).getBlock();
            if (GeneralMethods.isRegionProtectedFromBuild(player, "WaterSpout", blocki.getLocation())) {
                return -1;
            }
            if (!affectedblocks.contains((Object)blocki)) {
                if (blocki.getType() == Material.WATER || blocki.getType() == Material.STATIONARY_WATER) {
                    if (!TempBlock.isTempBlock(blocki)) {
                        WaterSpout.revertBaseBlock(player);
                    }
                    spout.base = blocki;
                    if (i > height) {
                        return height;
                    }
                    return i;
                }
                if (blocki.getType() == Material.ICE || blocki.getType() == Material.SNOW || blocki.getType() == Material.SNOW_BLOCK || blocki.getType() == Material.PACKED_ICE && spout.canBendOnPackedIce) {
                    if (!TempBlock.isTempBlock(blocki)) {
                        WaterSpout.revertBaseBlock(player);
                        WaterSpout.instances.get((Object)player).baseblock = new TempBlock(blocki, Material.STATIONARY_WATER, (byte)8);
                    }
                    spout.base = blocki;
                    if (i > height) {
                        return height;
                    }
                    return i;
                }
                if (!(blocki.getType() == Material.AIR || WaterMethods.isPlant(blocki) && WaterMethods.canPlantbend(player))) {
                    WaterSpout.revertBaseBlock(player);
                    return -1;
                }
            }
            ++i;
        }
        WaterSpout.revertBaseBlock(player);
        return -1;
    }

    private void displayWaterSpiral(Location location) {
        if (!BLOCKS) {
            return;
        }
        double maxHeight = this.player.getLocation().getY() - location.getY() - 0.5;
        double height = 0.0;
        this.rotation += 0.4;
        int i = 0;
        while (height < maxHeight) {
            double angle = (double)(i += 20) * 3.141592653589793 / 180.0;
            double x = 1.0 * Math.cos(angle + this.rotation);
            double z = 1.0 * Math.sin(angle + this.rotation);
            Location loc = location.clone().getBlock().getLocation().add(0.5, 0.5, 0.5);
            loc.add(x, height += 0.4, z);
            Block block = loc.getBlock();
            if (!block.getType().equals((Object)Material.AIR) && GeneralMethods.isSolid(block)) continue;
            revert.put(block, 0L);
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.STATIONARY_WATER, (byte)1);
        }
    }

    public static void revertBaseBlock(Player player) {
        if (instances.containsKey((Object)player) && WaterSpout.instances.get((Object)player).baseblock != null) {
            WaterSpout.instances.get((Object)player).baseblock.revertBlock();
            WaterSpout.instances.get((Object)player).baseblock = null;
        }
    }

    public static void removeAll() {
        WaterSpout.progressRevert(true);
        revert.clear();
        for (Player player : instances.keySet()) {
            instances.get((Object)player).remove();
        }
        for (Block block : affectedblocks.keySet()) {
            TempBlock.revertBlock(block, Material.AIR);
            affectedblocks.remove((Object)block);
        }
    }

    public static ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (Player player : instances.keySet()) {
            players.add(player);
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
            if (distance > radius || dy <= 0.0 || dy >= (double)WaterSpout.instances.get((Object)player).defaultheight) continue;
            removed = true;
            instances.get((Object)player).remove();
        }
        return removed;
    }

    public static String getDescription() {
        return "To use this ability, click while over or in water. You will spout water up from beneath you to experience controlled levitation. This ability is a toggle, so you can activate it then use other abilities and it will remain on. If you try to spout over an area with no water, snow or ice, the spout will dissipate and you will fall. Click again with this ability selected to deactivate it.";
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getDefaultheight() {
        return this.defaultheight;
    }

    public void setDefaultheight(int defaultheight) {
        this.defaultheight = defaultheight;
    }
}

