/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
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

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.airbending.AirBlast;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.airbending.AirSuction;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.Combustion;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.firebending.FireStream;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AirShield
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, AirShield> instances = new ConcurrentHashMap();
    private static double MAX_RADIUS = config.get().getDouble("Abilities.Air.AirShield.Radius");
    private static boolean isToggle = config.get().getBoolean("Abilities.Air.AirShield.IsAvatarStateToggle");
    private static int numberOfStreams = (int)(0.75 * MAX_RADIUS);
    private double maxradius = MAX_RADIUS;
    private double radius = MAX_RADIUS;
    private double speedfactor;
    private Player player;
    private HashMap<Integer, Integer> angles = new HashMap();

    public AirShield(Player player) {
        if (AvatarState.isAvatarState(player) && instances.containsKey((Object)player) && isToggle) {
            instances.get((Object)player).remove();
            return;
        }
        this.player = player;
        int angle = 0;
        int di = (int)(this.maxradius * 2.0 / (double)numberOfStreams);
        int i = - (int)this.maxradius + di;
        while (i < (int)this.maxradius) {
            this.angles.put(i, angle);
            if ((angle += 90) == 360) {
                angle = 0;
            }
            i += di;
        }
        instances.put(player, this);
    }

    public static String getDescription() {
        return "Air Shield is one of the most powerful defensive techniques in existence. To use, simply sneak (default: shift). This will create a whirlwind of air around the user, with a small pocket of safe space in the center. This wind will deflect all projectiles and will prevent any creature from entering it for as long as its maintained. ";
    }

    public static boolean isWithinShield(Location loc) {
        for (AirShield ashield : instances.values()) {
            if (ashield.player.getLocation().getWorld() != loc.getWorld()) {
                return false;
            }
            if (ashield.player.getLocation().distance(loc) > ashield.radius) continue;
            return true;
        }
        return false;
    }

    public double getMaxradius() {
        return this.maxradius;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirShield", this.player.getLocation())) {
            this.remove();
            return false;
        }
        this.speedfactor = 1.0;
        if (!GeneralMethods.canBend(this.player.getName(), "AirShield") || this.player.getEyeLocation().getBlock().isLiquid()) {
            this.remove();
            return false;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            this.remove();
            return false;
        }
        if (isToggle) {
            if (!(GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("AirShield") && this.player.isSneaking() || AvatarState.isAvatarState(this.player))) {
                this.remove();
                return false;
            }
        } else if (!GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("AirShield") || !this.player.isSneaking()) {
            this.remove();
            return false;
        }
        this.rotateShield();
        return true;
    }

    public static void progressAll() {
        for (AirShield ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (AirShield ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        MAX_RADIUS = config.get().getDouble("Abilities.Air.AirShield.Radius");
        isToggle = config.get().getBoolean("Abilities.Air.AirShield.IsAvatarStateToggle");
        numberOfStreams = (int)(0.75 * MAX_RADIUS);
        this.maxradius = MAX_RADIUS;
    }

    private void rotateShield() {
        Location origin = this.player.getLocation();
        FireBlast.removeFireBlastsAroundPoint(origin, this.radius);
        Combustion.removeAroundPoint(origin, this.radius);
        FireStream.removeAroundPoint(origin, this.radius);
        AirBlast.removeAirBlastsAroundPoint(origin, this.radius);
        AirSuction.removeAirSuctionsAroundPoint(origin, this.radius);
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(origin, this.radius)) {
            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirShield", entity.getLocation()) || origin.distance(entity.getLocation()) <= 2.0) continue;
            double angle = 50.0;
            angle = Math.toRadians(angle);
            double x = entity.getLocation().getX() - origin.getX();
            double z = entity.getLocation().getZ() - origin.getZ();
            double mag = Math.sqrt(x * x + z * z);
            double vx = (x * Math.cos(angle) - z * Math.sin(angle)) / mag;
            double vz = (x * Math.sin(angle) + z * Math.cos(angle)) / mag;
            Vector velocity = entity.getVelocity();
            if (AvatarState.isAvatarState(this.player)) {
                velocity.setX(AvatarState.getValue(vx));
                velocity.setZ(AvatarState.getValue(vz));
            } else {
                velocity.setX(vx);
                velocity.setZ(vz);
            }
            if (entity instanceof Player && Commands.invincible.contains(((Player)entity).getName())) continue;
            velocity.multiply(this.radius / this.maxradius);
            GeneralMethods.setVelocity(entity, velocity);
            entity.setFallDistance(0.0f);
        }
        for (Block testblock : GeneralMethods.getBlocksAroundPoint(this.player.getLocation(), this.radius)) {
            if (testblock.getType() != Material.FIRE) continue;
            testblock.setType(Material.AIR);
            testblock.getWorld().playEffect(testblock.getLocation(), Effect.EXTINGUISH, 0);
        }
        Set<Integer> keys = this.angles.keySet();
        Iterator<Integer> x = keys.iterator();
        while (x.hasNext()) {
            int i = x.next();
            double angle = this.angles.get(i).intValue();
            angle = Math.toRadians(angle);
            double factor = this.radius / this.maxradius;
            double y = origin.getY() + factor * (double)i;
            double f = Math.sqrt(1.0 - factor * factor * ((double)i / this.radius) * ((double)i / this.radius));
            double x2 = origin.getX() + this.radius * Math.cos(angle) * f;
            double z = origin.getZ() + this.radius * Math.sin(angle) * f;
            Location effect = new Location(origin.getWorld(), x2, y, z);
            if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "AirShield", effect)) {
                AirMethods.playAirbendingParticles(effect, 5);
                if (GeneralMethods.rand.nextInt(4) == 0) {
                    AirMethods.playAirbendingSound(effect);
                }
            }
            this.angles.put(i, this.angles.get(i) + (int)(10.0 * this.speedfactor));
        }
        if (this.radius < this.maxradius) {
            this.radius += 0.3;
        }
        if (this.radius > this.maxradius) {
            this.radius = this.maxradius;
        }
    }

    public void setMaxradius(double maxradius) {
        this.maxradius = maxradius;
    }
}

