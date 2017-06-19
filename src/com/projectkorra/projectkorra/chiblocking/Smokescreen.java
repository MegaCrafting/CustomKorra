/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.Snowball
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package com.projectkorra.projectkorra.chiblocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.command.Commands;

public class Smokescreen {
    public static HashMap<String, Long> cooldowns = new HashMap();
    public static List<Integer> snowballs = new ArrayList<Integer>();
    public static HashMap<String, Long> blinded = new HashMap();
    private long cooldown = ProjectKorra.plugin.getConfig().getLong("Abilities.Chi.Smokescreen.Cooldown");
    public static int duration = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.Smokescreen.Duration");
    public static double radius = ProjectKorra.plugin.getConfig().getDouble("Abilities.Chi.Smokescreen.Radius");

    public Smokescreen(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("Smokescreen")) {
            return;
        }
        snowballs.add(((Snowball)player.launchProjectile(Snowball.class)).getEntityId());
        bPlayer.addCooldown("Smokescreen", this.cooldown);
    }

    public static void playEffect(Location loc) {
        int z = -2;
        int x = -2;
        int y = 0;
        int i = 0;
        while (i < 125) {
        	Location newLoc = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
            
            int direction = 0;
            while (direction < 8) {
                loc.getWorld().playEffect(newLoc, Effect.SMOKE, direction);
                ++direction;
            }
            if (z == 2) {
                z = -2;
            }
            if (x == 2) {
                x = -2;
                ++z;
            }
            ++x;
            ++i;
        }
    }

    public static void applyBlindness(Entity entity) {
        if (entity instanceof Player) {
            if (Commands.invincible.contains(((Player)entity).getName())) {
                return;
            }
            Player p = (Player)entity;
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration * 20, 2));
            blinded.put(p.getName(), System.currentTimeMillis());
        }
    }

    public static void removeFromHashMap(Entity entity) {
        Player p;
        if (entity instanceof Player && blinded.containsKey((p = (Player)entity).getName()) && blinded.get(p.getName()) + (long)duration >= System.currentTimeMillis()) {
            blinded.remove(p.getName());
        }
    }
}

