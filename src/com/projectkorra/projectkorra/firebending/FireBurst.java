/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingManager;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.firebending.FireMethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class FireBurst
implements ConfigLoadable {
    public static final ConcurrentHashMap<Player, FireBurst> instances = new ConcurrentHashMap();
    private static double PARTICLES_PERCENTAGE = 5.0;
    private Player player;
    private long starttime;
    private int damage = config.get().getInt("Abilities.Fire.FireBurst.Damage");
    private long chargetime = config.get().getLong("Abilities.Fire.FireBurst.ChargeTime");
    private long range = config.get().getLong("Abilities.Fire.FireBurst.Range");
    private double deltheta = 10.0;
    private double delphi = 10.0;
    private boolean charged = false;
    private ArrayList<FireBlast> blasts = new ArrayList();

    public FireBurst(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("FireBurst")) {
            return;
        }
        if (instances.containsKey((Object)player)) {
            return;
        }
        this.starttime = System.currentTimeMillis();
        if (FireMethods.isDay(player.getWorld())) {
            this.chargetime = (long)((double)this.chargetime / config.get().getDouble("Properties.Fire.DayFactor"));
        }
        if (AvatarState.isAvatarState(player)) {
            this.chargetime = 0;
        }
        if (BendingManager.events.containsKey((Object)player.getWorld()) && BendingManager.events.get((Object)player.getWorld()).equalsIgnoreCase("SozinsComet")) {
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

    public static String getDescription() {
        return "FireBurst is a very powerful firebending ability. To use, press and hold sneak to charge your burst. Once charged, you can either release sneak to launch a cone-shaped burst of flames in front of you, or click to release the burst in a sphere around you. ";
    }

    private void coneBurst() {
        if (this.charged) {
            Location location = this.player.getEyeLocation();
            List<Block> safeblocks = GeneralMethods.getBlocksAroundPoint(this.player.getLocation(), 2.0);
            Vector vector = location.getDirection();
            double angle = Math.toRadians(30.0);
            double r = 1.0;
            double theta = 0.0;
            while (theta <= 180.0) {
                double dphi = this.delphi / Math.sin(Math.toRadians(theta));
                double phi = 0.0;
                while (phi < 360.0) {
                    double rphi = Math.toRadians(phi);
                    double rtheta = Math.toRadians(theta);
                    double x = r * Math.cos(rphi) * Math.sin(rtheta);
                    double y = r * Math.sin(rphi) * Math.sin(rtheta);
                    double z = r * Math.cos(rtheta);
                    Vector direction = new Vector(x, z, y);
                    if ((double)direction.angle(vector) <= angle) {
                        FireBlast fblast = new FireBlast(location, direction.normalize(), this.player, this.damage, safeblocks);
                        fblast.setRange(this.range);
                    }
                    phi += dphi;
                }
                theta += this.deltheta;
            }
        }
        this.remove();
    }

    public long getChargetime() {
        return this.chargetime;
    }

    public int getDamage() {
        return this.damage;
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getRange() {
        return this.range;
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (FireBurst ability : instances.values()) {
            ability.remove();
        }
    }

    public void handleSmoothParticles() {
        int i = 0;
        while (i < this.blasts.size()) {
            final FireBlast fblast = this.blasts.get(i);
            int toggleTime = (int)((double)i % (100.0 / PARTICLES_PERCENTAGE));
            new BukkitRunnable(){

                public void run() {
                    fblast.setShowParticles(true);
                }
            }.runTaskLater((Plugin)ProjectKorra.plugin, (long)toggleTime);
            ++i;
        }
    }

    public boolean progress() {
        if (!GeneralMethods.canBend(this.player.getName(), "FireBurst")) {
            this.remove();
            return false;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            this.remove();
            return false;
        }
        if (!GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("FireBurst")) {
            this.remove();
            return false;
        }
        if (System.currentTimeMillis() > this.starttime + this.chargetime && !this.charged) {
            this.charged = true;
        }
        if (!this.player.isSneaking()) {
            if (this.charged) {
                this.sphereBurst();
            } else {
                this.remove();
            }
        } else if (this.charged) {
            Location location = this.player.getEyeLocation();
            location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 4, 3);
        }
        return true;
    }

    public static void progressAll() {
        for (FireBurst ability : instances.values()) {
            ability.progress();
        }
    }

    @Override
    public void reloadVariables() {
    }

    public void setChargetime(long chargetime) {
        this.chargetime = chargetime;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setRange(long range) {
        this.range = range;
    }

    private void sphereBurst() {
        if (this.charged) {
            Location location = this.player.getEyeLocation();
            List<Block> safeblocks = GeneralMethods.getBlocksAroundPoint(this.player.getLocation(), 2.0);
            double r = 1.0;
            double theta = 0.0;
            while (theta <= 180.0) {
                double dphi = this.delphi / Math.sin(Math.toRadians(theta));
                double phi = 0.0;
                while (phi < 360.0) {
                    double rphi = Math.toRadians(phi);
                    double rtheta = Math.toRadians(theta);
                    double x = r * Math.cos(rphi) * Math.sin(rtheta);
                    double y = r * Math.sin(rphi) * Math.sin(rtheta);
                    double z = r * Math.cos(rtheta);
                    Vector direction = new Vector(x, z, y);
                    FireBlast fblast = new FireBlast(location, direction.normalize(), this.player, this.damage, safeblocks);
                    fblast.setRange(this.range);
                    fblast.setShowParticles(false);
                    this.blasts.add(fblast);
                    phi += dphi;
                }
                theta += this.deltheta;
            }
        }
        this.remove();
        this.handleSmoothParticles();
    }

}

