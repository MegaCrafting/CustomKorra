/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Creature
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.chiblocking;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.Suffocate;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.firebending.Enflamed;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Paralyze {
    private static ConcurrentHashMap<Entity, Long> entities = new ConcurrentHashMap();
    private static ConcurrentHashMap<Entity, Long> cooldowns = new ConcurrentHashMap();
    private static final long cooldown = ProjectKorra.plugin.getConfig().getLong("Abilities.Chi.Paralyze.Cooldown");
    private static final long duration = ProjectKorra.plugin.getConfig().getLong("Abilities.Chi.Paralyze.Duration");

    public Paralyze(Player sourceplayer, Entity targetentity) {
        if (GeneralMethods.getBoundAbility(sourceplayer) == null) {
            return;
        }
        if (GeneralMethods.isBender(sourceplayer.getName(), Element.Chi) && GeneralMethods.getBoundAbility(sourceplayer).equalsIgnoreCase("Paralyze") && GeneralMethods.canBend(sourceplayer.getName(), "Paralyze")) {
            LivingEntity target;
            if (targetentity instanceof LivingEntity && (target = (LivingEntity)targetentity).getFireTicks() > 0) {
                for (String skill : GeneralMethods.getBendingPlayer(sourceplayer.getName()).getCooldowns().keySet()) {
                    if (AbilityModuleManager.harmlessabilities.contains(skill) || AbilityModuleManager.healingabilities.contains(skill) || !AbilityModuleManager.igniteabilities.contains(skill) && !AbilityModuleManager.explodeabilities.contains(skill) && !AbilityModuleManager.lavaabilities.contains(skill) && !Enflamed.isEnflamed((Entity)target)) continue;
                    return;
                }
            }
            if (cooldowns.containsKey((Object)targetentity)) {
                if (System.currentTimeMillis() < cooldowns.get((Object)targetentity) + cooldown) {
                    return;
                }
                cooldowns.remove((Object)targetentity);
            }
            if (targetentity instanceof Player && Commands.invincible.contains(((Player)targetentity).getName())) {
                return;
            }
            Paralyze.paralyze(targetentity);
            cooldowns.put(targetentity, System.currentTimeMillis());
        }
    }

    private static void paralyze(Entity entity) {
        entities.put(entity, System.currentTimeMillis());
        if (entity instanceof Creature) {
            ((Creature)entity).setTarget(null);
        }
        if (entity instanceof Player && Suffocate.isChannelingSphere((Player)entity)) {
            Suffocate.remove((Player)entity);
        }
    }

    public static boolean isParalyzed(Entity entity) {
        if (entity instanceof Player && AvatarState.isAvatarState((Player)entity)) {
            return false;
        }
        if (entities.containsKey((Object)entity)) {
            if (System.currentTimeMillis() < entities.get((Object)entity) + duration) {
                return true;
            }
            entities.remove((Object)entity);
        }
        return false;
    }
}

