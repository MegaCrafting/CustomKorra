/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthColumn;
import com.projectkorra.projectkorra.earthbending.EarthMethods;

public class EarthGrab {
    private static double range = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.EarthGrab.Range");

    public EarthGrab(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("EarthGrab")) {
            return;
        }
        Location origin = player.getEyeLocation();
        Vector direction = origin.getDirection();
        double lowestdistance = range + 1.0;
        Entity closestentity = null;
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(origin, range)) {
            double distance;
            if (GeneralMethods.getDistanceFromLine(direction, origin, entity.getLocation()) > 3.0 || !(entity instanceof LivingEntity) || entity.getEntityId() == player.getEntityId() || (distance = origin.distance(entity.getLocation())) >= lowestdistance) continue;
            closestentity = entity;
            lowestdistance = distance;
        }
        if (closestentity != null) {
            ArrayList<Block> blocks = new ArrayList<Block>();
            Location location = closestentity.getLocation();
            Location loc1 = location.clone();
            Location loc2 = location.clone();
            double factor = 3.0;
            double factor2 = 4.0;
            int height1 = 3;
            int height2 = 2;
            double angle = 0.0;
            while (angle <= 360.0) {
                Location testloc = loc1.clone().add(factor * Math.cos(Math.toRadians(angle)), 1.0, factor * Math.sin(Math.toRadians(angle)));
                Location testloc2 = loc2.clone().add(factor2 * Math.cos(Math.toRadians(angle)), 1.0, factor2 * Math.sin(Math.toRadians(angle)));
                int y = 0;
                while (y < EarthColumn.standardheight - height1) {
                    if (EarthMethods.isEarthbendable(player, (testloc = testloc.clone().add(0.0, -1.0, 0.0)).getBlock())) {
                        if (!blocks.contains((Object)testloc.getBlock())) {
                            new com.projectkorra.projectkorra.earthbending.EarthColumn(player, testloc, height1 + y - 1);
                        }
                        blocks.add(testloc.getBlock());
                        break;
                    }
                    ++y;
                }
                y = 0;
                while (y < EarthColumn.standardheight - height2) {
                    if (EarthMethods.isEarthbendable(player, (testloc2 = testloc2.clone().add(0.0, -1.0, 0.0)).getBlock())) {
                        if (!blocks.contains((Object)testloc2.getBlock())) {
                            new com.projectkorra.projectkorra.earthbending.EarthColumn(player, testloc2, height2 + y - 1);
                        }
                        blocks.add(testloc2.getBlock());
                        break;
                    }
                    ++y;
                }
                angle += 20.0;
            }
            if (!blocks.isEmpty()) {
                bPlayer.addCooldown("EarthGrab", GeneralMethods.getGlobalCooldown());
            }
        }
    }

    public static void EarthGrabSelf(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("EarthGrab")) {
            return;
        }
        Player closestentity = player;
        if (closestentity != null) {
            ArrayList<Block> blocks = new ArrayList<Block>();
            Location location = closestentity.getLocation();
            Location loc1 = location.clone();
            Location loc2 = location.clone();
            double factor = 3.0;
            double factor2 = 4.0;
            int height1 = 3;
            int height2 = 2;
            double angle = 0.0;
            while (angle <= 360.0) {
                Location testloc = loc1.clone().add(factor * Math.cos(Math.toRadians(angle)), 1.0, factor * Math.sin(Math.toRadians(angle)));
                Location testloc2 = loc2.clone().add(factor2 * Math.cos(Math.toRadians(angle)), 1.0, factor2 * Math.sin(Math.toRadians(angle)));
                int y = 0;
                while (y < EarthColumn.standardheight - height1) {
                    if (EarthMethods.isEarthbendable(player, (testloc = testloc.clone().add(0.0, -1.0, 0.0)).getBlock())) {
                        if (!blocks.contains((Object)testloc.getBlock())) {
                            new com.projectkorra.projectkorra.earthbending.EarthColumn(player, testloc, height1 + y - 1);
                        }
                        blocks.add(testloc.getBlock());
                        break;
                    }
                    ++y;
                }
                y = 0;
                while (y < EarthColumn.standardheight - height2) {
                    if (EarthMethods.isEarthbendable(player, (testloc2 = testloc2.clone().add(0.0, -1.0, 0.0)).getBlock())) {
                        if (!blocks.contains((Object)testloc2.getBlock())) {
                            new com.projectkorra.projectkorra.earthbending.EarthColumn(player, testloc2, height2 + y - 1);
                        }
                        blocks.add(testloc2.getBlock());
                        break;
                    }
                    ++y;
                }
                angle += 20.0;
            }
            if (!blocks.isEmpty()) {
                bPlayer.addCooldown("EarthGrab", GeneralMethods.getGlobalCooldown());
            }
        }
    }
}

