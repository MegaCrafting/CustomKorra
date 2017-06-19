/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
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
import com.projectkorra.projectkorra.firebending.FireStream;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class RingOfFire
implements ConfigLoadable {
    static int defaultrange = config.get().getInt("Abilities.Fire.Blaze.RingOfFire.Range");

    public RingOfFire(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("Blaze")) {
            return;
        }
        Location location = player.getLocation();
        double degrees = 0.0;
        while (degrees < 360.0) {
            double angle = Math.toRadians(degrees);
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
            degrees += 10.0;
        }
        bPlayer.addCooldown("Blaze", GeneralMethods.getGlobalCooldown());
    }

    public static String getDescription() {
        return "To use, simply left-click. A circle of fire will emanate from you, engulfing everything around you. Use with extreme caution.";
    }

    @Override
    public void reloadVariables() {
        defaultrange = config.get().getInt("Abilities.Fire.Blaze.RingOfFire.Range");
    }
}

