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
package com.projectkorra.projectkorra.airbending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.util.Flight;

public class Tornado
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, Tornado> instances = new ConcurrentHashMap();
    private static double MAX_HEIGHT = config.get().getDouble("Abilities.Air.Tornado.Height");
    private static double PLAYER_PUSH_FACTOR = config.get().getDouble("Abilities.Air.Tornado.PlayerPushFactor");
    private static double MAX_RADIUS = config.get().getDouble("Abilities.Air.Tornado.Radius");
    private static double RANGE = config.get().getDouble("Abilities.Air.Tornado.Range");
    private static double NPC_PUSH_FACTOR = config.get().getDouble("Abilities.Air.Tornado.MobPushFactor");
    private static int numberOfStreams = (int)(0.3 * MAX_HEIGHT);
    private static double speedfactor = 1.0;
    private ConcurrentHashMap<Integer, Integer> angles = new ConcurrentHashMap();
    private Location origin;
    private Player player;
    private double maxheight = MAX_HEIGHT;
    private double PCpushfactor = PLAYER_PUSH_FACTOR;
    private double maxradius = MAX_RADIUS;
    private double range = RANGE;
    private double NPCpushfactor = NPC_PUSH_FACTOR;
    private double height = 2.0;
    private double radius = this.height / this.maxheight * this.maxradius;

    public Tornado(Player player) {
        this.player = player;
        origin = player.getTargetBlock((HashSet<Material>) null, (int) range).getLocation();
        this.origin.setY(this.origin.getY() - 0.1 * this.height);
        int angle = 0;
        int i = 0;
        while ((double)i <= this.maxheight) {
            this.angles.put(i, angle);
            if ((angle += 90) == 360) {
                angle = 0;
            }
            i += (int)this.maxheight / numberOfStreams;
        }
        new com.projectkorra.projectkorra.util.Flight(player);
        player.setAllowFlight(true);
        instances.put(player, this);
    }

    public static ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (Tornado tornado : instances.values()) {
            players.add(tornado.getPlayer());
        }
        return players;
    }

    public double getMaxheight() {
        return this.maxheight;
    }

    public double getMaxradius() {
        return this.maxradius;
    }

    public double getNPCpushfactor() {
        return this.NPCpushfactor;
    }

    public double getPCpushfactor() {
        return this.PCpushfactor;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getRange() {
        return this.range;
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "Tornado") || this.player.getEyeLocation().getBlock().isLiquid()) {
            this.remove();
            return false;
        }
        String abil = GeneralMethods.getBoundAbility(this.player);
        if (abil == null) {
            this.remove();
            return false;
        }
        if (!abil.equalsIgnoreCase("Tornado") || !this.player.isSneaking()) {
            this.remove();
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirBlast", this.origin)) {
            this.remove();
            return false;
        }
        this.rotateTornado();
        return true;
    }

    public static void progressAll() {
        for (Tornado ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (Tornado ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        MAX_HEIGHT = config.get().getDouble("Abilities.Air.Tornado.Height");
        PLAYER_PUSH_FACTOR = config.get().getDouble("Abilities.Air.Tornado.PlayerPushFactor");
        MAX_RADIUS = config.get().getDouble("Abilities.Air.Tornado.Radius");
        RANGE = config.get().getDouble("Abilities.Air.Tornado.Range");
        NPC_PUSH_FACTOR = config.get().getDouble("Abilities.Air.Tornado.MobPushFactor");
        numberOfStreams = (int)(0.3 * MAX_HEIGHT);
        this.maxheight = MAX_HEIGHT;
        this.PCpushfactor = PLAYER_PUSH_FACTOR;
        this.maxradius = MAX_RADIUS;
        this.range = RANGE;
        this.NPCpushfactor = NPC_PUSH_FACTOR;
        this.radius = this.height / this.maxheight * this.maxradius;
    }

    private void rotateTornado() {
    	this.origin = player.getTargetBlock((HashSet<Material>) null, (int) range).getLocation();
        double timefactor = this.height / this.maxheight;
        this.radius = timefactor * this.maxradius;
        if (this.origin.getBlock().getType() != Material.AIR) {
            this.origin.setY(this.origin.getY() - 0.1 * this.height);
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.origin, this.height)) {
                double y;
                if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirBlast", entity.getLocation()) || (y = entity.getLocation().getY()) <= this.origin.getY() || y >= this.origin.getY() + this.height) continue;
                double factor = (y - this.origin.getY()) / this.height;
                Location testloc = new Location(this.origin.getWorld(), this.origin.getX(), y, this.origin.getZ());
                if (testloc.distance(entity.getLocation()) >= this.radius * factor) continue;
                double angle = 100.0;
                double vy = 0.7 * this.NPCpushfactor;
                angle = Math.toRadians(angle);
                double x = entity.getLocation().getX() - this.origin.getX();
                double z = entity.getLocation().getZ() - this.origin.getZ();
                double mag = Math.sqrt(x * x + z * z);
                double vx = (x * Math.cos(angle) - z * Math.sin(angle)) / mag;
                double vz = (x * Math.sin(angle) + z * Math.cos(angle)) / mag;
                if (entity instanceof Player) {
                    vy = 0.05 * this.PCpushfactor;
                }
                if (entity.getEntityId() == this.player.getEntityId()) {
                    double oy;
                    Vector direction = this.player.getEyeLocation().getDirection().clone().normalize();
                    vx = direction.getX();
                    vz = direction.getZ();
                    Location playerloc = this.player.getLocation();
                    double py = playerloc.getY();
                    double dy = py - (oy = this.origin.getY());
                    vy = dy >= this.height * 0.95 ? 0.0 : (dy >= this.height * 0.85 ? 6.0 * (0.95 - dy / this.height) : 0.6);
                }
                if (entity instanceof Player && Commands.invincible.contains(((Player)entity).getName())) continue;
                Vector velocity = entity.getVelocity();
                velocity.setX(vx);
                velocity.setZ(vz);
                velocity.setY(vy);
                velocity.multiply(timefactor);
                GeneralMethods.setVelocity(entity, velocity);
                entity.setFallDistance(0.0f);
                AirMethods.breakBreathbendingHold(entity);
                if (!(entity instanceof Player)) continue;
                new com.projectkorra.projectkorra.util.Flight((Player)entity);
            }
            Iterator iterator = this.angles.keySet().iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                double angle = this.angles.get(i).intValue();
                angle = Math.toRadians(angle);
                double y = this.origin.getY() + timefactor * (double)i;
                double factor = (double)i / this.height;
                double x = this.origin.getX() + timefactor * factor * this.radius * Math.cos(angle);
                double z = this.origin.getZ() + timefactor * factor * this.radius * Math.sin(angle);
                Location effect = new Location(this.origin.getWorld(), x, y, z);
                if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "AirBlast", effect)) {
                    AirMethods.playAirbendingParticles(effect, 4);
                    if (GeneralMethods.rand.nextInt(20) == 0) {
                        AirMethods.playAirbendingSound(effect);
                    }
                }
                this.angles.put(i, this.angles.get(i) + 25 * (int)speedfactor);
            }
        }
        if (this.height < this.maxheight) {
            this.height += 1.0;
        }
        if (this.height > this.maxheight) {
            this.height = this.maxheight;
        }
    }

    public void setMaxheight(double maxheight) {
        this.maxheight = maxheight;
    }

    public void setMaxradius(double maxradius) {
        this.maxradius = maxradius;
    }

    public void setNPCpushfactor(double nPCpushfactor) {
        this.NPCpushfactor = nPCpushfactor;
    }

    public void setPCpushfactor(double pCpushfactor) {
        this.PCpushfactor = pCpushfactor;
    }

    public void setRange(double range) {
        this.range = range;
    }
}

