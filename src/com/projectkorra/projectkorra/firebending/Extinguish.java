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
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.firebending;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.airbending.AirBlast;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireJet;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.HeatMelt;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class Extinguish
implements ConfigLoadable {
    private static double defaultrange = config.get().getDouble("Abilities.Fire.HeatControl.Extinguish.Range");
    private static double defaultradius = config.get().getDouble("Abilities.Fire.HeatControl.Extinguish.Radius");
    private static byte full = AirBlast.full;

    public Extinguish(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("HeatControl")) {
            return;
        }
        double range = FireMethods.getFirebendingDayAugment(defaultrange, player.getWorld());
        if (WaterMethods.isMeltable(player.getTargetBlock((HashSet<Material>) null, (int) range))) {
            new com.projectkorra.projectkorra.firebending.HeatMelt(player);
            return;
        }
        double radius = FireMethods.getFirebendingDayAugment(defaultradius, player.getWorld());
        for (Block block : GeneralMethods.getBlocksAroundPoint(player.getTargetBlock((HashSet<Material>) null, (int) range)
        		.getLocation(), radius)) {
            Material mat = block.getType();
            if (mat != Material.FIRE || GeneralMethods.isRegionProtectedFromBuild(player, "Blaze", block.getLocation()) || block.getType() != Material.FIRE) continue;
            block.setType(Material.AIR);
            block.getWorld().playEffect(block.getLocation(), Effect.EXTINGUISH, 0);
        }
        bPlayer.addCooldown("HeatControl", GeneralMethods.getGlobalCooldown());
    }

    public static boolean canBurn(Player player) {
        if (GeneralMethods.getBoundAbility(player) != null && (GeneralMethods.getBoundAbility(player).equalsIgnoreCase("HeatControl") || FireJet.checkTemporaryImmunity(player))) {
            player.setFireTicks(-1);
            return false;
        }
        if (player.getFireTicks() > 80 && GeneralMethods.canBendPassive(player.getName(), Element.Fire)) {
            player.setFireTicks(80);
        }
        return true;
    }

    public static String getDescription() {
        return "While this ability is selected, the firebender becomes impervious to fire damage and cannot be ignited. If the user left-clicks with this ability, the targeted area will be extinguished, although it will leave any creature burning engulfed in flames. This ability can also cool lava. If this ability is used while targetting ice or snow, it will instead melt blocks in that area. Finally, sneaking with this ability will cook any food in your hand.";
    }

    @Override
    public void reloadVariables() {
        defaultrange = config.get().getDouble("Abilities.Fire.HeatControl.Extinguish.Range");
        defaultradius = config.get().getDouble("Abilities.Fire.HeatControl.Extinguish.Radius");
    }
}

