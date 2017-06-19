/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Torrent;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterReturn;

public class TorrentBurst {
    public static ConcurrentHashMap<Integer, TorrentBurst> instances = new ConcurrentHashMap();
    private static int ID = Integer.MIN_VALUE;
    private static double defaultmaxradius = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Torrent.Wave.Radius");
    private static double dr = 0.5;
    private static double defaultfactor = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Torrent.Wave.Knockback");
    private static double MAX_HEIGHT = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.Torrent.Wave.Height");
    private static long interval = Torrent.interval;
    private int id;
    private long time;
    private double radius = dr;
    private double maxradius = defaultmaxradius;
    private double factor = defaultfactor;
    private double maxheight = MAX_HEIGHT;
    private Location origin;
    private Player player;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Double>> heights = new ConcurrentHashMap();
    private ArrayList<TempBlock> blocks = new ArrayList();
    private ArrayList<Entity> affectedentities = new ArrayList();

    public TorrentBurst(Player player) {
        this(player, player.getEyeLocation(), dr);
    }

    public TorrentBurst(Player player, Location location) {
        this(player, location, dr);
    }

    public TorrentBurst(Player player, double radius) {
        this(player, player.getEyeLocation(), radius);
    }

    public TorrentBurst(Player player, Location location, double radius) {
        this.player = player;
        World world = player.getWorld();
        this.origin = location.clone();
        this.time = System.currentTimeMillis();
        this.id = ID++;
        this.factor = WaterMethods.waterbendingNightAugment(this.factor, world);
        this.maxradius = WaterMethods.waterbendingNightAugment(this.maxradius, world);
        this.radius = radius;
        if (ID >= Integer.MAX_VALUE) {
            ID = Integer.MIN_VALUE;
        }
        this.initializeHeightsMap();
        instances.put(this.id, this);
    }

    private void initializeHeightsMap() {
        int i = -1;
        while ((double)i <= this.maxheight) {
            ConcurrentHashMap<Integer, Double> angles = new ConcurrentHashMap<Integer, Double>();
            double dtheta = Math.toDegrees(1.0 / (this.maxradius + 2.0));
            int j = 0;
            double theta = 0.0;
            while (theta < 360.0) {
                angles.put(j, theta);
                ++j;
                theta += dtheta;
            }
            this.heights.put(i, angles);
            ++i;
        }
    }

    private void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "Torrent")) {
            this.remove();
            return;
        }
        if (System.currentTimeMillis() > this.time + interval) {
            if (this.radius < this.maxradius) {
                this.radius += dr;
            } else {
                this.remove();
                this.returnWater();
                return;
            }
            this.formBurst();
            this.time = System.currentTimeMillis();
        }
    }

    private void formBurst() {
        for (TempBlock tempBlock : this.blocks) {
            tempBlock.revertBlock();
        }
        this.blocks.clear();
        this.affectedentities.clear();
        ArrayList<Entity> indexlist = new ArrayList<Entity>();
        indexlist.addAll(GeneralMethods.getEntitiesAroundPoint(this.origin, this.radius + 2.0));
        ArrayList<Block> torrentblocks = new ArrayList<Block>();
        if (indexlist.contains((Object)this.player)) {
            indexlist.remove((Object)this.player);
        }
        Iterator iterator = this.heights.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            ConcurrentHashMap<Integer, Double> angles = this.heights.get(id);
            Iterator iterator2 = angles.keySet().iterator();
            while (iterator2.hasNext()) {
                int index = (Integer)iterator2.next();
                double angle = angles.get(index);
                double theta = Math.toRadians(angle);
                double dx = Math.cos(theta) * this.radius;
                double dy = id;
                double dz = Math.sin(theta) * this.radius;
                Location location = this.origin.clone().add(dx, dy, dz);
                Block block = location.getBlock();
                if (torrentblocks.contains((Object)block)) continue;
                if (!EarthMethods.isTransparentToEarthbending(this.player, block)) {
                    angles.remove(index);
                    continue;
                }
                TempBlock tempBlock2 = new TempBlock(block, Material.STATIONARY_WATER,(byte) 8);
                this.blocks.add(tempBlock2);
                torrentblocks.add(block);
                for (Entity entity : indexlist) {
                    if (this.affectedentities.contains((Object)entity) || entity.getLocation().distance(location) > 2.0) continue;
                    this.affectedentities.add(entity);
                    this.affect(entity);
                }
                for (Block sound : torrentblocks) {
                    if (GeneralMethods.rand.nextInt(50) != 0) continue;
                    WaterMethods.playWaterbendingSound(sound.getLocation());
                }
            }
            if (!angles.isEmpty()) continue;
            this.heights.remove(id);
        }
        if (this.heights.isEmpty()) {
            this.remove();
        }
    }

    private void affect(Entity entity) {
        Vector direction = GeneralMethods.getDirection(this.origin, entity.getLocation());
        direction.setY(0);
        direction.normalize();
        entity.setVelocity(entity.getVelocity().clone().add(direction.multiply(this.factor)));
    }

    private void remove() {
        for (TempBlock block : this.blocks) {
            block.revertBlock();
        }
        instances.remove(this.id);
    }

    private void returnWater() {
        Location location = new Location(this.origin.getWorld(), this.origin.getX() + this.radius, this.origin.getY(), this.origin.getZ());
        if (!location.getWorld().equals((Object)this.player.getWorld())) {
            return;
        }
        if (location.distance(this.player.getLocation()) > this.maxradius + 5.0) {
            return;
        }
        new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, location.getBlock());
    }

    public static void progressAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            instances.get(id).progress();
        }
    }

    public static void removeAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            instances.get(id).remove();
        }
    }

    public double getMaxradius() {
        return this.maxradius;
    }

    public void setMaxradius(double maxradius) {
        this.maxradius = maxradius;
    }

    public double getFactor() {
        return this.factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double getMaxheight() {
        return this.maxheight;
    }

    public void setMaxheight(double maxheight) {
        this.maxheight = maxheight;
    }

    public Player getPlayer() {
        return this.player;
    }
}

