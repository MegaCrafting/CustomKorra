/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.chiblocking;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.Suffocate;
import com.projectkorra.projectkorra.chiblocking.ChiPassive;

public class RapidPunch {
    public static ConcurrentHashMap<Player, RapidPunch> instances = new ConcurrentHashMap();
    public static List<Player> punching = new ArrayList<Player>();
    private int damage = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.RapidPunch.Damage");
    private int punches = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.RapidPunch.Punches");
    private int distance = ProjectKorra.plugin.getConfig().getInt("Abilities.Chi.RapidPunch.Distance");
    private long cooldown = ProjectKorra.plugin.getConfig().getLong("Abilities.Chi.RapidPunch.Cooldown");
    private int numpunches;
    private Entity target;
    private Player player;

    public RapidPunch(Player p) {
        this.player = p;
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(p.getName());
        if (instances.containsKey((Object)p)) {
            return;
        }
        if (bPlayer.isOnCooldown("RapidPunch")) {
            return;
        }
        Entity t = GeneralMethods.getTargetedEntity(p, this.distance, new ArrayList<Entity>());
        if (t == null) {
            return;
        }
        this.target = t;
        this.numpunches = 0;
        instances.put(p, this);
    }

    public static void startPunchAll() {
        for (Player player : instances.keySet()) {
            if (player == null) continue;
            instances.get((Object)player).startPunch(player);
        }
    }

    public void startPunch(Player p) {
        if (this.numpunches >= this.punches) {
            instances.remove((Object)p);
        }
        if (this.target instanceof LivingEntity && this.target != null) {
            LivingEntity lt = (LivingEntity)this.target;
            GeneralMethods.damageEntity(p, this.target, this.damage, "RapidPunch");
            if (this.target instanceof Player) {
                if (ChiPassive.willChiBlock(p, (Player)this.target)) {
                    ChiPassive.blockChi((Player)this.target);
                }
                if (Suffocate.isChannelingSphere((Player)this.target)) {
                    Suffocate.remove((Player)this.target);
                }
            }
            lt.setNoDamageTicks(0);
        }
        GeneralMethods.getBendingPlayer(p.getName()).addCooldown("RapidPunch", this.cooldown);
        this.swing(p);
        ++this.numpunches;
    }

    private void swing(Player p) {
    }

    public static String getDescription() {
        return "This ability allows the chiblocker to punch rapidly in a short period. To use, simply punch. This has a short cooldown.";
    }

    public int getDamage() {
        return this.damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDistance() {
        return this.distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
        if (this.player != null) {
            GeneralMethods.getBendingPlayer(this.player.getName()).addCooldown("RapidPunch", cooldown);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getPunches() {
        return this.punches;
    }

    public void setPunches(int punches) {
        this.punches = punches;
    }

    public int getNumpunches() {
        return this.numpunches;
    }

    public void setNumpunches(int numpunches) {
        this.numpunches = numpunches;
    }
}

