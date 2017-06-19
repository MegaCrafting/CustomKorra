/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.chiblocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;

public class ChiCombo {
    private static boolean enabled = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Chi.ChiCombo.Enabled");
    public static long IMMOBILIZE_DURATION = ProjectKorra.plugin.getConfig().getLong("Abilities.Chi.ChiCombo.Immobilize.ParalyzeDuration");
    public static long IMMOBILIZE_COOLDOWN = ProjectKorra.plugin.getConfig().getLong("Abilities.Chi.ChiCombo.Immobilize.Cooldown");
    public static List<ChiCombo> instances = new ArrayList<ChiCombo>();
    public static Map<Entity, Long> paralyzedEntities = new HashMap<Entity, Long>();
    private Entity target;

    public ChiCombo(Player player, String ability) {
        if (!enabled) {
            return;
        }
        if (ability.equalsIgnoreCase("Immobilize")) {
            if (!GeneralMethods.canBend(player.getName(), "Immobilize") || GeneralMethods.getBendingPlayer(player.getName()).isOnCooldown("Immobilize")) {
                return;
            }
            this.target = GeneralMethods.getTargetedEntity(player, 5.0, new ArrayList<Entity>());
            ChiCombo.paralyze(this.target, IMMOBILIZE_DURATION);
            instances.add(this);
            GeneralMethods.getBendingPlayer(player.getName()).addCooldown("Immobilize", IMMOBILIZE_COOLDOWN);
        }
    }

    private static void paralyze(Entity target, Long duration) {
        paralyzedEntities.put(target, System.currentTimeMillis() + duration);
    }

    public static boolean isParalyzed(Player player) {
        return ChiCombo.isParalyzed((Entity)player);
    }

    public static boolean isParalyzed(Entity entity) {
        return paralyzedEntities.containsKey((Object)entity);
    }

    public static void handleParalysis() {
        for (Entity e : paralyzedEntities.keySet()) {
            if (paralyzedEntities.get((Object)e) > System.currentTimeMillis()) continue;
            paralyzedEntities.remove((Object)e);
            for (ChiCombo c : instances) {
                if (!c.target.equals((Object)e)) continue;
                instances.remove(c);
            }
        }
    }
}

