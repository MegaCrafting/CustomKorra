/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.LeatherArmorMeta
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class PlantArmor {
    public static ConcurrentHashMap<Player, PlantArmor> instances = new ConcurrentHashMap();
    private static long cooldown = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.PlantArmor.Cooldown");
    private static long DURATION = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.PlantArmor.Duration");
    private static int RESISTANCE = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.PlantArmor.Resistance");
    private static int RANGE = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.PlantArmor.Range");
    private Player player;
    private Block block;
    private Location location;
    private Plantbending plantbending;
    private long starttime;
    private boolean formed = false;
    private int resistance = RESISTANCE;
    public ItemStack[] oldarmor;
    public boolean hadEffect;
    private double range = RANGE;
    private long duration = DURATION;
    private Material blocktype;

    public PlantArmor(Player player) {
        if (instances.containsKey((Object)player)) {
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("PlantArmor")) {
            return;
        }
        this.player = player;
        this.range = WaterMethods.getWaterbendingNightAugment(player.getWorld()) * this.range;
        Double d = WaterMethods.getWaterbendingNightAugment(player.getWorld()) * (double)this.duration;
        this.duration = d.longValue();
        this.block = WaterMethods.getPlantSourceBlock(player, this.range, true);
        if (this.block == null) {
            return;
        }
        this.location = this.block.getLocation();
        this.hadEffect = player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        if (!this.canUse()) {
            return;
        }
        this.plantbending = new Plantbending(this.block);
        this.blocktype = this.block.getType();
        this.block.setType(Material.AIR);
        instances.put(player, this);
    }

    private boolean canUse() {
        if (!this.player.getWorld().equals((Object)this.block.getWorld())) {
            this.cancel();
            return false;
        }
        if (this.location.distance(this.player.getEyeLocation()) > this.range) {
            this.cancel();
            return false;
        }
        if (!WaterMethods.canPlantbend(this.player)) {
            this.cancel();
            return false;
        }
        return true;
    }

    private void playEffect() {
        if (!this.formed) {
            if (GeneralMethods.rand.nextInt(4) == 0) {
                WaterMethods.playPlantbendingSound(this.location);
            }
            GeneralMethods.displayColoredParticle(this.location, "009933");
            Vector v = this.player.getEyeLocation().toVector().subtract(this.location.toVector());
            this.location = this.location.add(v.normalize());
        }
    }

    private void cancel() {
        if (this.plantbending != null) {
            this.plantbending.revert();
        }
        if (instances.containsKey((Object)this.player)) {
            instances.remove((Object)this.player);
        }
    }

    private boolean inPosition() {
        if (this.location.distance(this.player.getEyeLocation()) <= 1.5) {
            return true;
        }
        return false;
    }

    private void formArmor() {
        this.oldarmor = this.player.getInventory().getArmorContents();
        ItemStack helmet = new ItemStack(this.blocktype);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta im = (LeatherArmorMeta)chestplate.getItemMeta();
        im.setColor(Color.GREEN);
        chestplate.setItemMeta((ItemMeta)im);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        leggings.setItemMeta((ItemMeta)im);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        boots.setItemMeta((ItemMeta)im);
        this.player.getInventory().setHelmet(helmet);
        this.player.getInventory().setChestplate(chestplate);
        this.player.getInventory().setLeggings(leggings);
        this.player.getInventory().setBoots(boots);
        if (!this.hadEffect) {
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, this.resistance - 1));
        }
        this.formed = true;
        this.starttime = System.currentTimeMillis();
    }

    public static void progressAll() {
        for (Player player : instances.keySet()) {
            PlantArmor.progress(player);
        }
    }

    public static void progress(Player player) {
        if (!instances.containsKey((Object)player)) {
            return;
        }
        PlantArmor plantarmor = instances.get((Object)player);
        if (player.isDead() || !player.isOnline()) {
            plantarmor.removeEffect();
            plantarmor.cancel();
            return;
        }
        if (plantarmor.formed) {
            if (System.currentTimeMillis() > plantarmor.starttime + plantarmor.duration) {
                plantarmor.removeEffect();
                plantarmor.cancel();
                GeneralMethods.getBendingPlayer(player.getName()).addCooldown("PlantArmor", cooldown);
                return;
            }
        } else {
            if (!plantarmor.canUse()) {
                return;
            }
            plantarmor.playEffect();
            if (plantarmor.inPosition()) {
                plantarmor.formArmor();
            }
        }
    }

    private void removeEffect() {
        this.player.getInventory().setArmorContents(this.oldarmor);
        if (!this.hadEffect) {
            this.player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        }
    }

    public static void removeEffect(Player player) {
        if (!instances.containsKey((Object)player)) {
            return;
        }
        instances.get((Object)player).removeEffect();
    }

    public static void removeAll() {
        for (Player player : instances.keySet()) {
            PlantArmor plantarmor = instances.get((Object)player);
            plantarmor.removeEffect();
            plantarmor.cancel();
        }
    }

    public static boolean canRemoveArmor(Player player) {
        if (instances.containsKey((Object)player)) {
            PlantArmor plantarmor = instances.get((Object)player);
            if (System.currentTimeMillis() < plantarmor.starttime + plantarmor.duration) {
                return false;
            }
        }
        return true;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getResistance() {
        return this.resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = resistance;
        if (!this.hadEffect) {
            this.player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            this.player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, resistance - 1));
        }
    }
}

