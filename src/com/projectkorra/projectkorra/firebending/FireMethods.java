/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.World$Environment
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.firebending.Cook;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.firebending.FireBurst;
import com.projectkorra.projectkorra.firebending.FireCombo;
import com.projectkorra.projectkorra.firebending.FireJet;
import com.projectkorra.projectkorra.firebending.FireShield;
import com.projectkorra.projectkorra.firebending.FireStream;
import com.projectkorra.projectkorra.firebending.Fireball;
import com.projectkorra.projectkorra.firebending.Illumination;
import com.projectkorra.projectkorra.firebending.Lightning;
import com.projectkorra.projectkorra.firebending.WallOfFire;
import com.projectkorra.projectkorra.util.Information;
import com.projectkorra.projectkorra.util.ParticleEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class FireMethods {
    static ProjectKorra plugin;
    private static FileConfiguration config;
    public static ConcurrentHashMap<Location, Information> tempFire;

    static {
        config = ProjectKorra.plugin.getConfig();
        tempFire = new ConcurrentHashMap();
    }

    public FireMethods(ProjectKorra plugin) {
        FireMethods.plugin = plugin;
    }

    public static boolean canCombustionbend(Player player) {
        if (player.hasPermission("bending.fire.combustionbending")) {
            return true;
        }
        return false;
    }

    public static boolean canLightningbend(Player player) {
        if (player.hasPermission("bending.fire.lightningbending")) {
            return true;
        }
        return false;
    }

    public static boolean canFireGrief() {
        return config.getBoolean("Properties.Fire.FireGriefing");
    }

    public static void createTempFire(Location loc) {
        if (loc.getBlock().getType() == Material.AIR) {
            loc.getBlock().setType(Material.FIRE);
            return;
        }
        Information info = new Information();
        long time = config.getLong("Properties.Fire.RevertTicks") + (long)(GeneralMethods.rand.nextDouble() * (double)config.getLong("Properties.Fire.RevertTicks"));
        if (tempFire.containsKey((Object)loc)) {
            info = tempFire.get((Object)loc);
        } else {
            info.setBlock(loc.getBlock());
            info.setLocation(loc);
            info.setState(loc.getBlock().getState());
        }
        info.setTime(time + System.currentTimeMillis());
        loc.getBlock().setType(Material.FIRE);
        tempFire.put(loc, info);
    }

    public static double getFirebendingDayAugment(double value, World world) {
        if (FireMethods.isDay(world)) {
            return value * config.getDouble("Properties.Fire.DayFactor");
        }
        return value;
    }

    public static ChatColor getFireColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.Fire"));
    }

    public static ChatColor getFireSubColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.FireSub"));
    }

    public static boolean isCombustionbendingAbility(String ability) {
        return AbilityModuleManager.combustionabilities.contains(ability);
    }

    public static boolean isLightningbendingAbility(String ability) {
        return AbilityModuleManager.lightningabilities.contains(ability);
    }

    public static boolean isDay(World world) {
        long time = world.getTime();
        if (world.getEnvironment() == World.Environment.NETHER || world.getEnvironment() == World.Environment.THE_END) {
            return true;
        }
        if (time >= 23500 || time <= 12500) {
            return true;
        }
        return false;
    }

    public static boolean isFireAbility(String ability) {
        return AbilityModuleManager.firebendingabilities.contains(ability);
    }

    public static void playLightningbendingParticle(Location loc) {
        FireMethods.playLightningbendingParticle(loc, (float)Math.random(), (float)Math.random(), (float)Math.random());
    }

    public static void playLightningbendingParticle(Location loc, float xOffset, float yOffset, float zOffset) {
        loc.setX(loc.getX() + Math.random() * (double)(xOffset / 2.0f - (- xOffset / 2.0f)));
        loc.setY(loc.getY() + Math.random() * (double)(yOffset / 2.0f - (- yOffset / 2.0f)));
        loc.setZ(loc.getZ() + Math.random() * (double)(zOffset / 2.0f - (- zOffset / 2.0f)));
        GeneralMethods.displayColoredParticle(loc, "#01E1FF");
    }

    public static void playFirebendingParticles(Location loc, int amount, float xOffset, float yOffset, float zOffset) {
        ParticleEffect.FLAME.display(loc, xOffset, yOffset, zOffset, 0.0f, amount);
    }

    public static void playFirebendingSound(Location loc) {
        if (plugin.getConfig().getBoolean("Properties.Fire.PlaySound")) {
            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_BURN, 1.0f, 10.0f);
        }
    }

    public static void playCombustionSound(Location loc) {
        if (plugin.getConfig().getBoolean("Properties.Fire.PlaySound")) {
            loc.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_BLAST, 1.0f, -1.0f);
        }
    }

    public static boolean isWithinFireShield(Location loc) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("FireShield");
        return GeneralMethods.blockAbilities(null, list, loc, 0.0);
    }

    public static void removeFire() {
        for (Location loc : tempFire.keySet()) {
            Information info = tempFire.get((Object)loc);
            if (info.getLocation().getBlock().getType() != Material.FIRE && info.getLocation().getBlock().getType() != Material.AIR) {
                FireMethods.revertTempFire(loc);
                continue;
            }
            if (info.getBlock().getType() != Material.AIR && System.currentTimeMillis() <= info.getTime()) continue;
            FireMethods.revertTempFire(loc);
        }
    }

    public static void revertTempFire(Location location) {
        if (!tempFire.containsKey((Object)location)) {
            return;
        }
        Information info = tempFire.get((Object)location);
        if (info.getLocation().getBlock().getType() != Material.FIRE && info.getLocation().getBlock().getType() != Material.AIR) {
            if (info.getState().getType() == Material.RED_ROSE || info.getState().getType() == Material.YELLOW_FLOWER) {
                info.getState().getBlock().getWorld().dropItemNaturally(info.getLocation(), new ItemStack(info.getState().getData().getItemType(), 1, (short)info.getState().getRawData()));
            }
        } else {
            info.getBlock().setType(info.getState().getType());
            info.getBlock().setData(info.getState().getRawData());
        }
        tempFire.remove((Object)location);
    }

    public static void stopBending() {
        FireStream.removeAll();
        Fireball.removeAll();
        WallOfFire.removeAll();
        Lightning.removeAll();
        FireShield.removeAll();
        FireBlast.removeAll();
        FireBurst.removeAll();
        FireJet.removeAll();
        Cook.removeAll();
        Illumination.removeAll();
        FireCombo.removeAll();
        for (Location loc : tempFire.keySet()) {
            FireMethods.revertTempFire(loc);
        }
    }
}

