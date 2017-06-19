/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.firebending;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.firebending.Extinguish;

public class Enflamed {
    private static ConcurrentHashMap<Entity, Player> instances = new ConcurrentHashMap();
    private static ConcurrentHashMap<Entity, Long> times = new ConcurrentHashMap();
    private static final int damage = 1;
    private static final int max = 90;
    private static final long buffer = 30;

    public Enflamed(Entity entity, Player source) {
        if (entity.getEntityId() == source.getEntityId()) {
            return;
        }
        if (instances.containsKey((Object)entity)) {
            instances.replace(entity, source);
        } else {
            instances.put(entity, source);
        }
    }

    public static boolean isEnflamed(Entity entity) {
        if (instances.containsKey((Object)entity)) {
            if (times.containsKey((Object)entity)) {
                long time = times.get((Object)entity);
                if (System.currentTimeMillis() < time + 30) {
                    return false;
                }
            }
            times.put(entity, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public static void dealFlameDamage(Entity entity) {
        if (instances.containsKey((Object)entity) && entity instanceof LivingEntity) {
            if (entity instanceof Player && !Extinguish.canBurn((Player)entity)) {
                return;
            }
            LivingEntity Lentity = (LivingEntity)entity;
            Player source = instances.get((Object)entity);
            Lentity.damage(1.0, (Entity)source);
            if (entity.getFireTicks() > 90) {
                entity.setFireTicks(90);
            }
        }
    }

    public static void handleFlames() {
        for (Entity entity : instances.keySet()) {
            if (entity.getFireTicks() > 0) continue;
            instances.remove((Object)entity);
        }
    }
}

