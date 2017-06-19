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
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.ability.StockAbility;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.firebending.Combustion;
import com.projectkorra.projectkorra.firebending.Enflamed;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.FireStream;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

public class FireShield
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, FireShield> instances = new ConcurrentHashMap();
    private static long interval = 100;
    private static long DURATION = config.get().getLong("Abilities.Fire.FireShield.Duration");
    private static double RADIUS = config.get().getDouble("Abilities.Fire.FireShield.Radius");
    private static double DISC_RADIUS = config.get().getDouble("Abilities.Fire.FireShield.DiscRadius");
    private static double fireticks = config.get().getDouble("Abilities.Fire.FireShield.FireTicks");
    private static boolean ignite = true;
    private Player player;
    private long time;
    private long starttime;
    private boolean shield = false;
    private long duration = DURATION;
    private double radius = RADIUS;
    private double discradius = DISC_RADIUS;

    public FireShield(Player player) {
        this(player, false);
    }

    public FireShield(Player player, boolean shield) {
        if (instances.containsKey((Object)player)) {
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("FireShield")) {
            return;
        }
        this.player = player;
        this.shield = shield;
        if (!player.getEyeLocation().getBlock().isLiquid()) {
            this.starttime = this.time = System.currentTimeMillis();
            instances.put(player, this);
            if (!shield) {
                bPlayer.addCooldown("FireShield", GeneralMethods.getGlobalCooldown());
            }
        }
    }

    public static String getDescription() {
        return "FireShield is a basic defensive ability. Clicking with this ability selected will create a small disc of fire in front of you, which will block most attacks and bending. Alternatively, pressing and holding sneak creates a very small shield of fire, blocking most attacks. Creatures that contact this fire are ignited.";
    }

    public static boolean isWithinShield(Location loc) {
        for (FireShield fshield : instances.values()) {
            Location playerLoc = fshield.player.getLocation();
            if (fshield.shield) {
                if (!playerLoc.getWorld().equals((Object)loc.getWorld())) {
                    return false;
                }
                if (playerLoc.distance(loc) > fshield.radius) continue;
                return true;
            }
            Location tempLoc = playerLoc.clone().add(playerLoc.multiply(fshield.discradius));
            if (!tempLoc.getWorld().equals((Object)loc.getWorld())) {
                return false;
            }
            if (tempLoc.distance(loc) > fshield.discradius) continue;
            return true;
        }
        return false;
    }

    public static void shield(Player player) {
        new com.projectkorra.projectkorra.firebending.FireShield(player, true);
    }

    public double getDiscradius() {
        return this.discradius;
    }

    public long getDuration() {
        return this.duration;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getRadius() {
        return this.radius;
    }

    public StockAbility getStockAbility() {
        return StockAbility.FireShield;
    }

    public boolean isShield() {
        return this.shield;
    }

    public boolean progress() {
        if (!this.player.isSneaking() && this.shield || !GeneralMethods.canBend(this.player.getName(), "FireShield")) {
            this.remove();
            return false;
        }
        if (!this.player.isOnline() || this.player.isDead()) {
            this.remove();
            return false;
        }
        if (System.currentTimeMillis() > this.starttime + this.duration && !this.shield) {
            this.remove();
            return false;
        }
        if (System.currentTimeMillis() > this.time + interval) {
            this.time = System.currentTimeMillis();
            if (this.shield) {
                ArrayList<Block> blocks = new ArrayList<Block>();
                Location location = this.player.getEyeLocation().clone();
                double theta = 0.0;
                while (theta < 180.0) {
                    double phi = 0.0;
                    while (phi < 360.0) {
                        double rphi = Math.toRadians(phi);
                        double rtheta = Math.toRadians(theta);
                        Block block = location.clone().add(this.radius * Math.cos(rphi) * Math.sin(rtheta), this.radius * Math.cos(rtheta), this.radius * Math.sin(rphi) * Math.sin(rtheta)).getBlock();
                        if (!(blocks.contains((Object)block) || GeneralMethods.isSolid(block) || block.isLiquid())) {
                            blocks.add(block);
                        }
                        phi += 20.0;
                    }
                    theta += 20.0;
                }
                for (Block block : blocks) {
                    if (GeneralMethods.isRegionProtectedFromBuild(this.player, "FireShield", block.getLocation())) continue;
                    if (GeneralMethods.rand.nextInt(3) == 0) {
                        ParticleEffect.SMOKE_NORMAL.display(block.getLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 1);
                    }
                    ParticleEffect.FLAME.display(block.getLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 1);
                    if (GeneralMethods.rand.nextInt(7) != 0) continue;
                    FireMethods.playFirebendingSound(block.getLocation());
                }
                for (Block testblock : GeneralMethods.getBlocksAroundPoint(this.player.getLocation(), this.radius)) {
                    if (testblock.getType() != Material.FIRE) continue;
                    testblock.setType(Material.AIR);
                    testblock.getWorld().playEffect(testblock.getLocation(), Effect.EXTINGUISH, 0);
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, this.radius)) {
                    if (GeneralMethods.isRegionProtectedFromBuild(this.player, "FireShield", entity.getLocation()) || this.player.getEntityId() == entity.getEntityId() || !ignite) continue;
                    entity.setFireTicks(120);
                    new com.projectkorra.projectkorra.firebending.Enflamed(entity, this.player);
                }
                FireBlast.removeFireBlastsAroundPoint(location, this.radius + 1.0);
                FireStream.removeAroundPoint(location, this.radius + 1.0);
                Combustion.removeAroundPoint(location, this.radius + 1.0);
            } else {
                ArrayList<Block> blocks = new ArrayList<Block>();
                Location location = this.player.getEyeLocation().clone();
                Vector direction = location.getDirection();
                if (GeneralMethods.isRegionProtectedFromBuild(this.player, "FireShield", location = location.clone().add(direction.multiply(this.radius)))) {
                    this.remove();
                    return false;
                }
                double theta = 0.0;
                while (theta < 360.0) {
                    Vector vector = GeneralMethods.getOrthogonalVector(direction, theta, this.discradius);
                    Block block = location.clone().add(vector).getBlock();
                    if (!(blocks.contains((Object)block) || GeneralMethods.isSolid(block) || block.isLiquid())) {
                        blocks.add(block);
                    }
                    theta += 20.0;
                }
                for (Block block : blocks) {
                    if (GeneralMethods.isRegionProtectedFromBuild(this.player, "FireShield", block.getLocation())) continue;
                    if (GeneralMethods.rand.nextInt(1) == 0) {
                        ParticleEffect.SMOKE_NORMAL.display(block.getLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 1);
                    }
                    ParticleEffect.FLAME.display(block.getLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 3);
                    if (GeneralMethods.rand.nextInt(4) != 0) continue;
                    FireMethods.playFirebendingSound(block.getLocation());
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, this.discradius)) {
                    if (GeneralMethods.isRegionProtectedFromBuild(this.player, "FireShield", entity.getLocation()) || this.player.getEntityId() == entity.getEntityId() || !ignite) continue;
                    entity.setFireTicks((int)(fireticks * 20.0));
                    if (entity instanceof LivingEntity) continue;
                    entity.remove();
                }
                FireBlast.removeFireBlastsAroundPoint(location, this.discradius);
                WaterManipulation.removeAroundPoint(location, this.discradius);
                EarthBlast.removeAroundPoint(location, this.discradius);
                FireStream.removeAroundPoint(location, this.discradius);
                Combustion.removeAroundPoint(location, this.discradius);
                for (Entity entity2 : GeneralMethods.getEntitiesAroundPoint(location, this.discradius)) {
                    if (!(entity2 instanceof Projectile)) continue;
                    entity2.remove();
                }
            }
        }
        return true;
    }

    public static void progressAll() {
        for (FireShield ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (FireShield ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        DURATION = config.get().getLong("Abilities.Fire.FireShield.Duration");
        RADIUS = config.get().getDouble("Abilities.Fire.FireShield.Radius");
        DISC_RADIUS = config.get().getDouble("Abilities.Fire.FireShield.DiscRadius");
        fireticks = config.get().getDouble("Abilities.Fire.FireShield.FireTicks");
        this.duration = DURATION;
        this.radius = RADIUS;
        this.discradius = DISC_RADIUS;
    }

    public void setDiscradius(double discradius) {
        this.discradius = discradius;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setShield(boolean shield) {
        this.shield = shield;
    }
}

