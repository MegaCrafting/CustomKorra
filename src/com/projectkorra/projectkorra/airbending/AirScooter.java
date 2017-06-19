/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.airbending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class AirScooter
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, AirScooter> instances = new ConcurrentHashMap();
    private static double configSpeed = config.get().getDouble("Abilities.Air.AirScooter.Speed");
    private static final long interval = 100;
    private static final double scooterradius = 1.0;
    private Player player;
    private Block floorblock;
    private long time;
    private double speed;
    private ArrayList<Double> angles = new ArrayList();

    public AirScooter(Player player) {
        if (AirScooter.check(player)) {
            return;
        }
        if (!player.isSprinting() || GeneralMethods.isSolid(player.getEyeLocation().getBlock()) || player.getEyeLocation().getBlock().isLiquid()) {
            return;
        }
        if (GeneralMethods.isSolid(player.getLocation().add(0.0, -0.5, 0.0).getBlock())) {
            return;
        }
        this.player = player;
        new com.projectkorra.projectkorra.util.Flight(player);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setSprinting(false);
        this.time = System.currentTimeMillis();
        int i = 0;
        while (i < 5) {
            this.angles.add(Double.valueOf(60 * i));
            ++i;
        }
        instances.put(player, this);
        this.speed = configSpeed;
        this.progress();
    }

    public static boolean check(Player player) {
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).remove();
            return true;
        }
        return false;
    }

    public static ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (AirScooter scooter : instances.values()) {
            players.add(scooter.getPlayer());
        }
        return players;
    }

    private void getFloor() {
        this.floorblock = null;
        int i = 0;
        while (i <= 7) {
            Block block = this.player.getEyeLocation().getBlock().getRelative(BlockFace.DOWN, i);
            if (GeneralMethods.isSolid(block) || block.isLiquid()) {
                this.floorblock = block;
                return;
            }
            ++i;
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double value) {
        this.speed = value;
    }

    public boolean progress() {
        this.getFloor();
        if (this.floorblock == null) {
            this.remove();
            return false;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "AirScooter")) {
            this.remove();
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirScooter", this.player.getLocation())) {
            this.remove();
            return false;
        }
        Vector velocity = this.player.getEyeLocation().getDirection().clone();
        velocity.setY(0);
        velocity = velocity.clone().normalize().multiply(this.speed);
        if (System.currentTimeMillis() > this.time + 100) {
            this.time = System.currentTimeMillis();
            if (this.player.getVelocity().length() < this.speed * 0.5) {
                this.remove();
                return false;
            }
            this.spinScooter();
        }
        double distance = this.player.getLocation().getY() - (double)this.floorblock.getY();
        double dx = Math.abs(distance - 2.4);
        if (distance > 2.75) {
            velocity.setY(-0.25 * dx * dx);
        } else if (distance < 2.0) {
            velocity.setY(0.25 * dx * dx);
        } else {
            velocity.setY(0);
        }
        Location loc = this.player.getLocation();
        if (WaterMethods.isWater(this.player.getLocation().add(0.0, 2.0, 0.0).getBlock())) {
            return false;
        }
        loc.setY((double)this.floorblock.getY() + 1.5);
        this.player.setSprinting(false);
        this.player.removePotionEffect(PotionEffectType.SPEED);
        this.player.setVelocity(velocity);
        if (GeneralMethods.rand.nextInt(4) == 0) {
            AirMethods.playAirbendingSound(this.player.getLocation());
        }
        return true;
    }

    public static void progressAll() {
        for (AirScooter ability : instances.values()) {
            ability.progress();
        }
    }

    @Override
    public void reloadVariables() {
        this.speed = AirScooter.configSpeed = config.get().getDouble("Abilities.Air.AirScooter.Speed");
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (AirScooter ability : instances.values()) {
            ability.remove();
        }
    }

    private void spinScooter() {
        Location origin = this.player.getLocation().clone();
        origin.add(0.0, -1.0, 0.0);
        int i = 0;
        while (i < 5) {
            double x = Math.cos(Math.toRadians(this.angles.get(i))) * 1.0;
            double y = (double)i / 2.0 * 1.0 - 1.0;
            double z = Math.sin(Math.toRadians(this.angles.get(i))) * 1.0;
            AirMethods.playAirbendingParticles(origin.clone().add(x, y, z), 7);
            ++i;
        }
        i = 0;
        while (i < 5) {
            this.angles.set(i, this.angles.get(i) + 10.0);
            ++i;
        }
    }
}

