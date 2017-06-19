/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.airbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.airbending.AirBlast;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.airbending.AirSpout;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.object.HorizontalVelocityTracker;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.waterbending.WaterSpout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AirSuction
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, AirSuction> instances = new ConcurrentHashMap();
    private static ConcurrentHashMap<Player, Location> origins = new ConcurrentHashMap();
    private static final double maxspeed = AirBlast.maxspeed;
    private static final int maxticks = 10000;
    private static double SPEED = config.get().getDouble("Abilities.Air.AirSuction.Speed");
    private static double RANGE = config.get().getDouble("Abilities.Air.AirSuction.Range");
    private static double RADIUS = config.get().getDouble("Abilities.Air.AirSuction.Radius");
    private static double PUSH_FACTOR = config.get().getDouble("Abilities.Air.AirSuction.Push");
    private static double originselectrange = 10.0;
    private Location location;
    private Location origin;
    private Vector direction;
    private Player player;
    private boolean otherorigin = false;
    private int ticks = 0;
    private double speed = SPEED;
    private double range = RANGE;
    private double affectingradius = RADIUS;
    private double pushfactor = PUSH_FACTOR;
    private double speedfactor;
    private ArrayList<Entity> affectedentities = new ArrayList();

    public AirSuction(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("AirSuction")) {
            return;
        }
        if (player.getEyeLocation().getBlock().isLiquid()) {
            return;
        }
        if (AirSpout.getPlayers().contains((Object)player) || WaterSpout.getPlayers().contains((Object)player)) {
            return;
        }
        this.player = player;
        if (origins.containsKey((Object)player)) {
            this.origin = origins.get((Object)player);
            this.otherorigin = true;
            origins.remove((Object)player);
        } else {
            this.origin = player.getEyeLocation();
        }
        this.location = GeneralMethods.getTargetedLocation(player, this.range, GeneralMethods.nonOpaque);
        this.direction = GeneralMethods.getDirection(this.location, this.origin).normalize();
        Entity entity = GeneralMethods.getTargetedEntity(player, this.range, new ArrayList<Entity>());
        if (entity != null) {
            this.direction = GeneralMethods.getDirection(entity.getLocation(), this.origin).normalize();
            this.location = this.getLocation(this.origin, this.direction.clone().multiply(-1));
        }
        instances.put(player, this);
        bPlayer.addCooldown("AirSuction", GeneralMethods.getGlobalCooldown());
    }

    public static String getDescription() {
        return "To use, simply left-click in a direction. A gust of wind will originate as far as it can in that direction and flow towards you, sucking anything in its path harmlessly with it. Skilled benders can use this technique to pull items from precarious locations. Additionally, tapping sneak will change the origin of your next AirSuction to your targeted location.";
    }

    private static void playOriginEffect(Player player) {
        if (!origins.containsKey((Object)player)) {
            return;
        }
        Location origin = origins.get((Object)player);
        if (!origin.getWorld().equals((Object)player.getWorld())) {
            origins.remove((Object)player);
            return;
        }
        if (GeneralMethods.getBoundAbility(player) == null) {
            origins.remove((Object)player);
            return;
        }
        if (!GeneralMethods.getBoundAbility(player).equalsIgnoreCase("AirSuction") || !GeneralMethods.canBend(player.getName(), "AirSuction")) {
            origins.remove((Object)player);
            return;
        }
        if (origin.distance(player.getEyeLocation()) > originselectrange) {
            origins.remove((Object)player);
            return;
        }
        AirMethods.playAirbendingParticles(origin, 6);
    }

    public static void progressAll() {
        for (AirSuction ability : instances.values()) {
            ability.progress();
        }
        for (Player player : origins.keySet()) {
            AirSuction.playOriginEffect(player);
        }
    }

    public static void setOrigin(Player player) {
        Location location = GeneralMethods.getTargetedLocation(player, originselectrange, GeneralMethods.nonOpaque);
        if (location.getBlock().isLiquid() || GeneralMethods.isSolid(location.getBlock())) {
            return;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "AirSuction", location)) {
            return;
        }
        if (origins.containsKey((Object)player)) {
            origins.replace(player, location);
        } else {
            origins.put(player, location);
        }
    }

    private void advanceLocation() {
        AirMethods.playAirbendingParticles(this.location, 6, 0.275f, 0.275f, 0.275f);
        if (GeneralMethods.rand.nextInt(4) == 0) {
            AirMethods.playAirbendingSound(this.location);
        }
        this.location = this.location.add(this.direction.clone().multiply(this.speedfactor));
    }

    public double getAffectingradius() {
        return this.affectingradius;
    }

    private Location getLocation(Location origin, Vector direction) {
        Location location = origin.clone();
        double i = 1.0;
        while (i <= this.range) {
            location = origin.clone().add(direction.clone().multiply(i));
            if (!EarthMethods.isTransparentToEarthbending(this.player, location.getBlock()) || GeneralMethods.isRegionProtectedFromBuild(this.player, "AirSuction", location)) {
                return origin.clone().add(direction.clone().multiply(i - 1.0));
            }
            i += 1.0;
        }
        return location;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getPushfactor() {
        return this.pushfactor;
    }

    public double getRange() {
        return this.range;
    }

    public double getSpeed() {
        return this.speed;
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirSuction", this.location)) {
            this.remove();
            return false;
        }
        this.speedfactor = this.speed * ((double)ProjectKorra.time_step / 1000.0);
        ++this.ticks;
        if (this.ticks > 10000) {
            this.remove();
            return false;
        }
        if (this.location.distance(this.origin) > this.range || this.location.distance(this.origin) <= 1.0) {
            this.remove();
            return false;
        }
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, this.affectingradius)) {
            Vector push;
            double comp;
            if (entity.getEntityId() == this.player.getEntityId() && !this.otherorigin) continue;
            Vector velocity = entity.getVelocity();
            double max = maxspeed;
            double factor = this.pushfactor;
            if (AvatarState.isAvatarState(this.player)) {
                max = AvatarState.getValue(maxspeed);
                factor = AvatarState.getValue(factor);
            }
            if (Math.abs((push = this.direction.clone()).getY()) > max && entity.getEntityId() != this.player.getEntityId()) {
                if (push.getY() < 0.0) {
                    push.setY(- max);
                } else {
                    push.setY(max);
                }
            }
            if ((comp = velocity.dot(push.clone().normalize())) > (factor *= 1.0 - this.location.distance(this.origin) / (2.0 * this.range))) {
                velocity.multiply(0.5);
                velocity.add(push.clone().normalize().multiply(velocity.clone().dot(push.clone().normalize())));
            } else if (comp + factor * 0.5 > factor) {
                velocity.add(push.clone().multiply(factor - comp));
            } else {
                velocity.add(push.clone().multiply(factor * 0.5));
            }
            if (entity instanceof Player && Commands.invincible.contains(((Player)entity).getName())) continue;
            GeneralMethods.setVelocity(entity, velocity);
            new com.projectkorra.projectkorra.object.HorizontalVelocityTracker(entity, this.player, 200, "AirSuction", Element.Air, null);
            entity.setFallDistance(0.0f);
            if (entity.getEntityId() != this.player.getEntityId() && entity instanceof Player) {
                new com.projectkorra.projectkorra.util.Flight((Player)entity, this.player);
            }
            if (entity.getFireTicks() > 0) {
                entity.getWorld().playEffect(entity.getLocation(), Effect.EXTINGUISH, 0);
            }
            entity.setFireTicks(0);
            AirMethods.breakBreathbendingHold(entity);
        }
        this.advanceLocation();
        return true;
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (AirSuction ability : instances.values()) {
            ability.remove();
        }
    }

    public static boolean removeAirSuctionsAroundPoint(Location location, double radius) {
        boolean removed = false;
        for (AirSuction airSuction : instances.values()) {
            Location airSuctionlocation = airSuction.location;
            if (location.getWorld() != airSuctionlocation.getWorld()) continue;
            if (location.distance(airSuctionlocation) <= radius) {
                airSuction.remove();
            }
            removed = true;
        }
        return removed;
    }

    @Override
    public void reloadVariables() {
        SPEED = config.get().getDouble("Abilities.Air.AirSuction.Speed");
        RANGE = config.get().getDouble("Abilities.Air.AirSuction.Range");
        RADIUS = config.get().getDouble("Abilities.Air.AirSuction.Radius");
        PUSH_FACTOR = config.get().getDouble("Abilities.Air.AirSuction.Push");
        this.speed = SPEED;
        this.range = RANGE;
        this.affectingradius = RADIUS;
        this.pushfactor = PUSH_FACTOR;
    }

    public void setAffectingradius(double affectingradius) {
        this.affectingradius = affectingradius;
    }

    public void setPushfactor(double pushfactor) {
        this.pushfactor = pushfactor;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}

