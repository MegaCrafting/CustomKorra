/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.FireStream;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ArcOfFire
implements ConfigLoadable {
    private static int defaultarc = config.get().getInt("Abilities.Fire.Blaze.ArcOfFire.Arc");
    private static int defaultrange = config.get().getInt("Abilities.Fire.Blaze.ArcOfFire.Range");
    private static int stepsize = 2;

    public ArcOfFire(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("Blaze")) {
            return;
        }
        Location location = player.getLocation();
        int arc = (int)FireMethods.getFirebendingDayAugment(defaultarc, player.getWorld());
        int i = - arc;
        while (i <= arc) {
            double angle = Math.toRadians(i);
            Vector direction = player.getEyeLocation().getDirection().clone();
            double x = direction.getX();
            double z = direction.getZ();
            double vx = x * Math.cos(angle) - z * Math.sin(angle);
            double vz = x * Math.sin(angle) + z * Math.cos(angle);
            direction.setX(vx);
            direction.setZ(vz);
            int range = defaultrange;
            if (AvatarState.isAvatarState(player)) {
                range = AvatarState.getValue(range);
            }
            new com.projectkorra.projectkorra.firebending.FireStream(location, direction, player, range);
            i += stepsize;
        }
        bPlayer.addCooldown("Blaze", GeneralMethods.getGlobalCooldown());
    }

    public static String getDescription() {
        return "To use, simply left-click in any direction. An arc of fire will flow from your location, igniting anything in its path. Additionally, tap sneak to engulf the area around you in roaring flames.";
    }

    @Override
    public void reloadVariables() {
        defaultarc = config.get().getInt("Abilities.Fire.Blaze.ArcOfFire.Arc");
        defaultrange = config.get().getInt("Abilities.Fire.Blaze.ArcOfFire.Range");
    }
}

