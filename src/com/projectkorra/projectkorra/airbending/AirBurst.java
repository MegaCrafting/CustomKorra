/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.airbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirBlast;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class AirBurst
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, AirBurst> instances = new ConcurrentHashMap();
    private static double PARTICLES_PERCENTAGE = 50.0;
    private static double threshold = config.get().getDouble("Abilities.Air.AirBurst.FallThreshold");
    private static double pushfactor = config.get().getDouble("Abilities.Air.AirBurst.PushFactor");
    private static double damage = config.get().getDouble("Abilities.Air.AirBurst.Damage");
    private static double deltheta = 10.0;
    private static double delphi = 10.0;
    private Player player;
    private long starttime;
    private long chargetime = config.get().getLong("Abilities.Air.AirBurst.ChargeTime");
    private boolean charged = false;
    private ArrayList<AirBlast> blasts = new ArrayList();
    private ArrayList<Entity> affectedentities = new ArrayList();

    public AirBurst() {
    }

    public AirBurst(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("AirBurst")) {
            return;
        }
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

    public static void coneBurst(Player player) {
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).coneBurst();
        }
    }

    public static void fallBurst(Player player) {
        if (!GeneralMethods.canBend(player.getName(), "AirBurst")) {
            return;
        }
        if ((double)player.getFallDistance() < threshold) {
            return;
        }
        if (GeneralMethods.getBoundAbility(player) == null) {
            return;
        }
        if (instances.containsKey((Object)player)) {
            return;
        }
        if (!GeneralMethods.getBoundAbility(player).equalsIgnoreCase("AirBurst")) {
            return;
        }
        Location location = player.getLocation();
        double r = 1.0;
        double theta = 75.0;
        while (theta < 105.0) {
            double dphi = delphi / Math.sin(Math.toRadians(theta));
            double phi = 0.0;
            while (phi < 360.0) {
                double rphi = Math.toRadians(phi);
                double rtheta = Math.toRadians(theta);
                double x = r * Math.cos(rphi) * Math.sin(rtheta);
                double y = r * Math.sin(rphi) * Math.sin(rtheta);
                double z = r * Math.cos(rtheta);
                Vector direction = new Vector(x, z, y);
                AirBlast blast = new AirBlast(location, direction.normalize(), player, pushfactor, new AirBurst());
                blast.setDamage(damage);
                phi += dphi;
            }
            theta += deltheta;
        }
    }

    void addAffectedEntity(Entity entity) {
        this.affectedentities.add(entity);
    }

    private void coneBurst() {
        if (this.charged) {
            Location location = this.player.getEyeLocation();
            Vector vector = location.getDirection();
            double angle = Math.toRadians(30.0);
            double r = 1.0;
            double theta = 0.0;
            while (theta <= 180.0) {
                double dphi = delphi / Math.sin(Math.toRadians(theta));
                double phi = 0.0;
                while (phi < 360.0) {
                    double rphi = Math.toRadians(phi);
                    double rtheta = Math.toRadians(theta);
                    double x = r * Math.cos(rphi) * Math.sin(rtheta);
                    double y = r * Math.sin(rphi) * Math.sin(rtheta);
                    double z = r * Math.cos(rtheta);
                    Vector direction = new Vector(x, z, y);
                    if ((double)direction.angle(vector) <= angle) {
                        AirBlast blast = new AirBlast(location, direction.normalize(), this.player, pushfactor, this);
                        blast.setDamage(damage);
                    }
                    phi += dphi;
                }
                theta += deltheta;
            }
        }
        this.remove();
    }

    public void handleSmoothParticles() {
        int i = 0;
        while (i < this.blasts.size()) {
            final AirBlast blast = this.blasts.get(i);
            int toggleTime = 0;
            if (i % 4 != 0) {
                toggleTime = (int)((double)i % (100.0 / PARTICLES_PERCENTAGE)) + 3;
            }
            new BukkitRunnable(){

                public void run() {
                    blast.setShowParticles(true);
                }
            }.runTaskLater((Plugin)ProjectKorra.plugin, (long)toggleTime);
            ++i;
        }
    }

    boolean isAffectedEntity(Entity entity) {
        return this.affectedentities.contains((Object)entity);
    }

    /*
     * Enabled aggressive block sorting
     */
    public boolean progress() {
        if (!GeneralMethods.canBend(this.player.getName(), "AirBurst")) {
            this.remove();
            return false;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            this.remove();
            return false;
        }
        if (!GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("AirBurst")) {
            this.remove();
            return false;
        }
        if (System.currentTimeMillis() > this.starttime + this.chargetime && !this.charged) {
            this.charged = true;
        }
        if (this.player.isSneaking()) {
            if (!this.charged) return true;
            Location location = this.player.getEyeLocation();
            AirMethods.playAirbendingParticles(location, 10);
            return true;
        }
        if (this.charged) {
            this.sphereBurst();
            return true;
        }
        this.remove();
        return false;
    }

    public static void progressAll() {
        for (AirBurst ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (AirBurst ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        threshold = config.get().getDouble("Abilities.Air.AirBurst.FallThreshold");
        pushfactor = config.get().getDouble("Abilities.Air.AirBurst.PushFactor");
        damage = config.get().getDouble("Abilities.Air.AirBurst.Damage");
        this.chargetime = config.get().getLong("Abilities.Air.AirBurst.ChargeTime");
    }

    private void sphereBurst() {
        if (this.charged) {
            Location location = this.player.getEyeLocation();
            double r = 1.0;
            double theta = 0.0;
            while (theta <= 180.0) {
                double dphi = delphi / Math.sin(Math.toRadians(theta));
                double phi = 0.0;
                while (phi < 360.0) {
                    double rphi = Math.toRadians(phi);
                    double rtheta = Math.toRadians(theta);
                    double x = r * Math.cos(rphi) * Math.sin(rtheta);
                    double y = r * Math.sin(rphi) * Math.sin(rtheta);
                    double z = r * Math.cos(rtheta);
                    Vector direction = new Vector(x, z, y);
                    AirBlast blast = new AirBlast(location, direction.normalize(), this.player, pushfactor, this);
                    blast.setDamage(damage);
                    blast.setShowParticles(false);
                    this.blasts.add(blast);
                    phi += dphi;
                }
                theta += deltheta;
            }
        }
        this.remove();
        this.handleSmoothParticles();
    }

}

