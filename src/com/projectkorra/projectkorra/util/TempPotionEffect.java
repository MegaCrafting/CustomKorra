/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package com.projectkorra.projectkorra.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TempPotionEffect {
    private static ConcurrentHashMap<LivingEntity, TempPotionEffect> instances = new ConcurrentHashMap();
    private static final long tick = 21;
    private int ID = Integer.MIN_VALUE;
    private ConcurrentHashMap<Integer, PotionInfo> infos = new ConcurrentHashMap();
    private LivingEntity entity;

    public TempPotionEffect(LivingEntity entity, PotionEffect effect) {
        this(entity, effect, System.currentTimeMillis());
    }

    public TempPotionEffect(LivingEntity entity, PotionEffect effect, long starttime) {
        this.entity = entity;
        if (instances.containsKey((Object)entity)) {
            TempPotionEffect instance = instances.get((Object)entity);
            instance.infos.put(instance.ID++, new PotionInfo(starttime, effect));
            instances.replace(entity, instance);
        } else {
            this.infos.put(this.ID++, new PotionInfo(starttime, effect));
            instances.put(entity, this);
        }
    }

    public static void progressAll() {
        for (LivingEntity entity : instances.keySet()) {
            instances.get((Object)entity).progress();
        }
    }

    private void addEffect(PotionEffect effect) {
        for (PotionEffect peffect : this.entity.getActivePotionEffects()) {
            if (!peffect.getType().equals((Object)effect.getType())) continue;
            if (peffect.getAmplifier() > effect.getAmplifier()) {
                if (peffect.getDuration() > effect.getDuration()) {
                    return;
                }
                int dt = effect.getDuration() - peffect.getDuration();
                PotionEffect neweffect = new PotionEffect(effect.getType(), dt, effect.getAmplifier());
                new com.projectkorra.projectkorra.util.TempPotionEffect(this.entity, neweffect, System.currentTimeMillis() + (long)peffect.getDuration() * 21);
                return;
            }
            if (peffect.getDuration() > effect.getDuration()) {
                this.entity.removePotionEffect(peffect.getType());
                this.entity.addPotionEffect(effect);
                int dt = peffect.getDuration() - effect.getDuration();
                PotionEffect neweffect = new PotionEffect(peffect.getType(), dt, peffect.getAmplifier());
                new com.projectkorra.projectkorra.util.TempPotionEffect(this.entity, neweffect, System.currentTimeMillis() + (long)effect.getDuration() * 21);
                return;
            }
            this.entity.removePotionEffect(peffect.getType());
            this.entity.addPotionEffect(effect);
            return;
        }
        this.entity.addPotionEffect(effect);
    }

    private void progress() {
        Iterator iterator = this.infos.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            PotionInfo info = this.infos.get(id);
            if (info.getTime() >= System.currentTimeMillis()) continue;
            this.addEffect(info.getEffect());
            this.infos.remove(id);
        }
        if (this.infos.isEmpty() && instances.containsKey((Object)this.entity)) {
            instances.remove((Object)this.entity);
        }
    }

    private class PotionInfo {
        private long starttime;
        private PotionEffect effect;

        public PotionInfo(long starttime, PotionEffect effect) {
            this.starttime = starttime;
            this.effect = effect;
        }

        public long getTime() {
            return this.starttime;
        }

        public PotionEffect getEffect() {
            return this.effect;
        }
    }

}

