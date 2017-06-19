/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.airbending;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class AirBubble
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, AirBubble> instances = new ConcurrentHashMap();
    private static double DEFAULT_AIR_RADIUS = config.get().getDouble("Abilities.Air.AirBubble.Radius");
    private static double DEFAULT_WATER_RADIUS = config.get().getDouble("Abilities.Water.WaterBubble.Radius");
    private Player player;
    private double radius;
    private double defaultAirRadius = DEFAULT_AIR_RADIUS;
    private double defaultWaterRadius = DEFAULT_WATER_RADIUS;
    private ConcurrentHashMap<Block, BlockState> waterorigins;

    public AirBubble(Player player) {
        this.player = player;
        this.waterorigins = new ConcurrentHashMap();
        instances.put(player, this);
    }

    public static boolean canFlowTo(Block block) {
        for (AirBubble airBubble : instances.values()) {
            if (!airBubble.blockInBubble(block)) continue;
            return false;
        }
        return true;
    }

    public static String getDescription() {
        return "To use, the bender must merely have the ability selected. All water around the user in a small bubble will vanish, replacing itself once the user either gets too far away or selects a different ability.";
    }

    public static void handleBubbles(Server server) {
        for (Player player : server.getOnlinePlayers()) {
            if (GeneralMethods.getBoundAbility(player) == null || !GeneralMethods.getBoundAbility(player).equalsIgnoreCase("AirBubble") && !GeneralMethods.getBoundAbility(player).equalsIgnoreCase("WaterBubble") || instances.containsKey((Object)player) || !player.isSneaking()) continue;
            new com.projectkorra.projectkorra.airbending.AirBubble(player);
        }
        AirBubble.progressAll();
    }

    public boolean blockInBubble(Block block) {
        if (block.getWorld() != this.player.getWorld()) {
            return false;
        }
        if (block.getLocation().distance(this.player.getLocation()) <= this.radius) {
            return true;
        }
        return false;
    }

    public double getDefaultAirRadius() {
        return this.defaultAirRadius;
    }

    public double getDefaultWaterRadius() {
        return this.defaultWaterRadius;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getRadius() {
        return this.radius;
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (!this.player.isSneaking()) {
            this.remove();
            return false;
        }
        if (GeneralMethods.getBoundAbility(this.player) != null) {
            if (GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("AirBubble") && GeneralMethods.canBend(this.player.getName(), "AirBubble")) {
                this.pushWater();
                return false;
            }
            if (GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("WaterBubble") && GeneralMethods.canBend(this.player.getName(), "WaterBubble")) {
                this.pushWater();
                return false;
            }
        }
        this.remove();
        return true;
    }

    private void pushWater() {
        this.radius = GeneralMethods.isBender(this.player.getName(), Element.Air) ? this.defaultAirRadius : this.defaultWaterRadius;
        if (GeneralMethods.isBender(this.player.getName(), Element.Water) && WaterMethods.isNight(this.player.getWorld())) {
            this.radius = WaterMethods.waterbendingNightAugment(this.defaultWaterRadius, this.player.getWorld());
        }
        if (this.defaultAirRadius > this.radius && GeneralMethods.isBender(this.player.getName(), Element.Air)) {
            this.radius = this.defaultAirRadius;
        }
        Location location = this.player.getLocation();
        for (Block block2 : this.waterorigins.keySet()) {
            if (block2.getWorld() != location.getWorld()) {
                if (block2.getType() == Material.AIR || WaterMethods.isWater(block2)) {
                    this.waterorigins.get((Object)block2).update(true);
                }
                this.waterorigins.remove((Object)block2);
                continue;
            }
            if (block2.getLocation().distance(location) <= this.radius) continue;
            if (block2.getType() == Material.AIR || WaterMethods.isWater(block2)) {
                this.waterorigins.get((Object)block2).update(true);
            }
            this.waterorigins.remove((Object)block2);
        }
        for (Block block2 : GeneralMethods.getBlocksAroundPoint(location, this.radius)) {
            if (this.waterorigins.containsKey((Object)block2) || !WaterMethods.isWater(block2) || GeneralMethods.isRegionProtectedFromBuild(this.player, "AirBubble", block2.getLocation()) || block2.getType() != Material.STATIONARY_WATER && block2.getType() != Material.WATER || !WaterManipulation.canBubbleWater(block2)) continue;
            this.waterorigins.put(block2, block2.getState());
            block2.setType(Material.AIR);
        }
    }

    public static void progressAll() {
        for (AirBubble ability : instances.values()) {
            ability.progress();
        }
    }

    @Override
    public void reloadVariables() {
        DEFAULT_AIR_RADIUS = config.get().getDouble("Abilities.Air.AirBubble.Radius");
        DEFAULT_WATER_RADIUS = config.get().getDouble("Abilities.Water.WaterBubble.Radius");
        this.defaultAirRadius = DEFAULT_AIR_RADIUS;
        this.defaultWaterRadius = DEFAULT_WATER_RADIUS;
    }

    public void remove() {
        for (Block block : this.waterorigins.keySet()) {
            if (block.getType() != Material.AIR && !block.isLiquid()) continue;
            this.waterorigins.get((Object)block).update(true);
        }
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (AirBubble ability : instances.values()) {
            ability.remove();
        }
    }

    public void setDefaultAirRadius(double defaultAirRadius) {
        this.defaultAirRadius = defaultAirRadius;
    }

    public void setDefaultWaterRadius(double defaultWaterRadius) {
        this.defaultWaterRadius = defaultWaterRadius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}

