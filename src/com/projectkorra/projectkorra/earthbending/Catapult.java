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
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.earthbending.EarthMethods;

public class Catapult
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, Catapult> instances = new ConcurrentHashMap();
    private static int LENGTH = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.Catapult.Length");
    private static double SPEED = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Catapult.Speed");
    private static double PUSH = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Catapult.Push");
    private int length = LENGTH;
    private double speed = SPEED;
    private double push = PUSH;
    private Player player;
    private Location origin;
    private Location location;
    private Vector direction;
    private int distance;
    private boolean catapult = false;
    private boolean moving = false;
    private boolean flying = false;

    public Catapult(Player player) {
        Entity target;
        BendingPlayer bplayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bplayer.isOnCooldown("Catapult")) {
            return;
        }
        this.player = player;
        this.origin = player.getEyeLocation().clone();
        this.direction = this.origin.getDirection().clone().normalize();
        Vector neg = this.direction.clone().multiply(-1);
        this.distance = 0;
        if (player.isSneaking() && (target = GeneralMethods.getTargetedEntity(player, 10.0, new ArrayList<Entity>())) instanceof LivingEntity) {
            Location under = target.getLocation();
            Material type = under.getBlock().getType();
            if (type == Material.AIR) {
                under = under.getBlock().getRelative(BlockFace.DOWN).getLocation();
                type = under.getBlock().getType();
            }
            boolean onEarth = true;
            int i = 0;
            while (i < 5) {
                if (!EarthMethods.isEarthbendable(under.getBlock().getType())) {
                    onEarth = false;
                }
                under.add(0.0, (double)(- i), 0.0);
                ++i;
            }
            player.sendMessage("onEarth = " + onEarth);
            if (onEarth) {
                Vector dir = under.add(target.getLocation()).toVector();
                target.setVelocity(target.getVelocity().setY(5));
            }
        }
        int i = 0;
        while (i <= this.length) {
            this.location = this.origin.clone().add(neg.clone().multiply((double)i));
            Block block = this.location.getBlock();
            if (EarthMethods.isEarthbendable(player, block)) {
                this.distance = EarthMethods.getEarthbendableBlocksLength(player, block, neg, this.length - i);
                break;
            }
            if (!EarthMethods.isTransparentToEarthbending(player, block)) break;
            ++i;
        }
        if (this.distance != 0) {
            if ((double)this.distance >= this.location.distance(this.origin)) {
                this.catapult = true;
            }
            if (player.isSneaking()) {
                this.distance /= 2;
            }
            this.moving = true;
            instances.put(player, this);
            bplayer.addCooldown("Catapult", GeneralMethods.getGlobalCooldown());
        }
    }

    public Catapult(Player player, Catapult source) {
        this.player = player;
        this.flying = true;
        this.moving = false;
        this.location = source.location.clone();
        this.direction = source.direction.clone();
        this.distance = source.distance;
        instances.put(player, this);
        EarthMethods.playEarthbendingSound(player.getLocation());
        this.fly();
    }

    public static String getDescription() {
        return "To use, left-click while looking in the direction you want to be launched. A pillar of earth will jut up from under you and launch you in that direction - if and only if there is enough earth behind where you're looking to launch you. Skillful use of this ability takes much time and work, and it does result in the death of certain gung-ho earthbenders. If you plan to use this ability, be sure you've read about your passive ability you innately have as an earthbender.";
    }

    public static ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (Catapult cata : instances.values()) {
            players.add(cata.getPlayer());
        }
        return players;
    }

    private void fly() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (!this.player.getWorld().equals((Object)this.location.getWorld())) {
            this.remove();
            return;
        }
        if (this.player.getLocation().distance(this.location) < 3.0) {
            if (!this.moving) {
                this.flying = false;
            }
            return;
        }
        for (Block block : GeneralMethods.getBlocksAroundPoint(this.player.getLocation(), 1.5)) {
            if (!GeneralMethods.isSolid(block) && !block.isLiquid()) continue;
            this.flying = false;
            return;
        }
        Vector vector = this.direction.clone().multiply(this.push * 2.0 * (double)this.distance / (double)this.length);
        vector.setY(this.player.getVelocity().getY());
        this.player.setVelocity(vector);
    }

    public int getLength() {
        return this.length;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getPush() {
        return this.push;
    }

    public double getSpeed() {
        return this.speed;
    }

    private boolean moveEarth() {
        this.location = this.location.clone().add(this.direction);
        if (this.catapult) {
            if (this.location.distance(this.origin) < 0.5) {
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.origin, 2.0)) {
                    if (entity instanceof Player) {
                        Player target = (Player)entity;
                        new com.projectkorra.projectkorra.earthbending.Catapult(target, this);
                    }
                    entity.setVelocity(this.direction.clone().multiply(this.push * (double)this.distance / (double)this.length));
                }
                return false;
            }
        } else if (this.location.distance(this.origin) <= (double)(this.length - this.distance)) {
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.0)) {
                entity.setVelocity(this.direction.clone().multiply(this.push * (double)this.distance / (double)this.length));
            }
            return false;
        }
        EarthMethods.moveEarth(this.player, this.location.clone().subtract(this.direction), this.direction, this.distance, false);
        return true;
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (this.moving && !this.moveEarth()) {
            this.moving = false;
        }
        if (this.flying) {
            this.fly();
        }
        if (!this.flying && !this.moving) {
            this.remove();
        }
        return true;
    }

    public static void progressAll() {
        for (Catapult ability : instances.values()) {
            ability.progress();
        }
    }

    public static void removeAll() {
        for (Catapult ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        LENGTH = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.Catapult.Length");
        SPEED = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Catapult.Speed");
        PUSH = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Catapult.Push");
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setPush(double push) {
        this.push = push;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

