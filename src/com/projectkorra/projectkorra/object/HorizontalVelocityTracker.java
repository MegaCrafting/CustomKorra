/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.object;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.event.HorizontalVelocityChangeEvent;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class HorizontalVelocityTracker {
    public static ConcurrentHashMap<Entity, HorizontalVelocityTracker> instances = new ConcurrentHashMap();
    public boolean hasBeenDamaged = false;
    private long delay;
    private long fireTime;
    private Entity entity;
    private Player instigator;
    private Vector lastVelocity;
    private Vector thisVelocity;
    private Location launchLocation;
    private Location impactLocation;
    private String abil;
    private Element e;
    private SubElement sub;
    public static String[] abils = new String[]{"AirBlast", "AirBurst", "AirSuction", "Bloodbending"};

    public HorizontalVelocityTracker(Entity e, Player instigator, long delay, String ability, Element element, SubElement se) {
        HorizontalVelocityTracker.remove(e);
        this.entity = e;
        this.instigator = instigator;
        this.fireTime = System.currentTimeMillis();
        this.delay = delay;
        this.thisVelocity = e.getVelocity().clone();
        this.launchLocation = e.getLocation().clone();
        this.impactLocation = this.launchLocation.clone();
        this.delay = delay;
        this.abil = ability;
        this.e = element;
        this.sub = se;
        this.update();
        instances.put(this.entity, this);
    }

    public void update() {
        if (System.currentTimeMillis() < this.fireTime + this.delay) {
            return;
        }
        this.lastVelocity = this.thisVelocity.clone();
        this.thisVelocity = this.entity.getVelocity().clone();
        Vector diff = this.thisVelocity.subtract(this.lastVelocity);
        List<Block> blocks = GeneralMethods.getBlocksAroundPoint(this.entity.getLocation(), 1.5);
        for (Block b2 : blocks) {
            if (!WaterMethods.isWater(b2)) continue;
            this.remove();
            return;
        }
        if (this.thisVelocity.length() < this.lastVelocity.length() && (diff.getX() > 1.0 || diff.getX() < -1.0 || diff.getZ() > 1.0 || diff.getZ() < -1.0)) {
            this.impactLocation = this.entity.getLocation();
            for (Block b2 : blocks) {
                if (!GeneralMethods.isSolid(b2) || !this.entity.getLocation().getBlock().getRelative(BlockFace.EAST, 1).equals((Object)b2) && !this.entity.getLocation().getBlock().getRelative(BlockFace.NORTH, 1).equals((Object)b2) && !this.entity.getLocation().getBlock().getRelative(BlockFace.WEST, 1).equals((Object)b2) && !this.entity.getLocation().getBlock().getRelative(BlockFace.SOUTH, 1).equals((Object)b2) || EarthMethods.isTransparentToEarthbending(this.instigator, b2)) continue;
                this.hasBeenDamaged = true;
                ProjectKorra.plugin.getServer().getPluginManager().callEvent((Event)new HorizontalVelocityChangeEvent(this.entity, this.instigator, this.lastVelocity, this.thisVelocity, diff, this.launchLocation, this.impactLocation, this.abil, this.e, this.sub));
                this.remove();
                return;
            }
        }
    }

    public static void updateAll() {
        for (Entity e : instances.keySet()) {
            instances.get((Object)e).update();
        }
    }

    public void remove() {
        instances.remove((Object)this.entity);
    }

    public static void remove(Entity e) {
        if (instances.containsKey((Object)e)) {
            instances.remove((Object)e);
        }
    }

    public static boolean hasBeenDamagedByHorizontalVelocity(Entity e) {
        if (instances.containsKey((Object)e)) {
            return HorizontalVelocityTracker.instances.get((Object)e).hasBeenDamaged;
        }
        return false;
    }
}

