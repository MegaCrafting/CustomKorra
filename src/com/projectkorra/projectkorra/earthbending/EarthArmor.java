/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.util.TempPotionEffect;

public class EarthArmor {
    public static ConcurrentHashMap<Player, EarthArmor> instances = new ConcurrentHashMap();
    private static long interval = 2000;
    private static long cooldown = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.EarthArmor.Cooldown");
    private static long duration = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.EarthArmor.Duration");
    private static int STRENGTH = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.EarthArmor.Strength");
    private static int range = 7;
    private Player player;
    private Block headblock;
    private Block legsblock;
    private Location headblocklocation;
    private Location legsblocklocation;
    private Material headtype;
    private Material legstype;
    private byte headdata;
    private byte legsdata;
    private long time;
    private long starttime;
    private boolean formed = false;
    private boolean complete = false;
    private int strength = STRENGTH;
    public ItemStack[] oldarmor;

    public EarthArmor(Player player) {
        if (instances.containsKey((Object)player)) {
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("EarthArmor")) {
            return;
        }
        this.player = player;
        this.headblock = player.getTargetBlock(EarthMethods.getTransparentEarthbending(), range);
        if (EarthMethods.getEarthbendableBlocksLength(player, this.headblock, new Vector(0, -1, 0), 2) >= 2) {
            this.legsblock = this.headblock.getRelative(BlockFace.DOWN);
            this.headtype = this.headblock.getType();
            this.legstype = this.legsblock.getType();
            this.headdata = this.headblock.getData();
            this.legsdata = this.legsblock.getData();
            this.headblocklocation = this.headblock.getLocation();
            this.legsblocklocation = this.legsblock.getLocation();
            Block oldheadblock = this.headblock;
            Block oldlegsblock = this.legsblock;
            if (!this.moveBlocks()) {
                return;
            }
            if (ProjectKorra.plugin.getConfig().getBoolean("Properties.Earth.RevertEarthbending")) {
                EarthMethods.addTempAirBlock(oldheadblock);
                EarthMethods.addTempAirBlock(oldlegsblock);
            } else {
                GeneralMethods.removeBlock(oldheadblock);
                GeneralMethods.removeBlock(oldlegsblock);
            }
            instances.put(player, this);
        }
    }

    private boolean moveBlocks() {
        if (!this.player.getWorld().equals((Object)this.headblock.getWorld())) {
            this.cancel();
            return false;
        }
        Location headlocation = this.player.getEyeLocation();
        Location legslocation = this.player.getLocation();
        Vector headdirection = headlocation.toVector().subtract(this.headblocklocation.toVector()).normalize().multiply(0.5);
        Vector legsdirection = legslocation.toVector().subtract(this.legsblocklocation.toVector()).normalize().multiply(0.5);
        Block newheadblock = this.headblock;
        Block newlegsblock = this.legsblock;
        if (!headlocation.getBlock().equals((Object)this.headblock)) {
            this.headblocklocation = this.headblocklocation.clone().add(headdirection);
            newheadblock = this.headblocklocation.getBlock();
        }
        if (!legslocation.getBlock().equals((Object)this.legsblock)) {
            this.legsblocklocation = this.legsblocklocation.clone().add(legsdirection);
            newlegsblock = this.legsblocklocation.getBlock();
        }
        if (EarthMethods.isTransparentToEarthbending(this.player, newheadblock) && !newheadblock.isLiquid()) {
            GeneralMethods.breakBlock(newheadblock);
        } else if (!EarthMethods.isEarthbendable(this.player, newheadblock) && !newheadblock.isLiquid() && newheadblock.getType() != Material.AIR) {
            this.cancel();
            return false;
        }
        if (EarthMethods.isTransparentToEarthbending(this.player, newlegsblock) && !newlegsblock.isLiquid()) {
            GeneralMethods.breakBlock(newlegsblock);
        } else if (!EarthMethods.isEarthbendable(this.player, newlegsblock) && !newlegsblock.isLiquid() && newlegsblock.getType() != Material.AIR) {
            this.cancel();
            return false;
        }
        if (this.headblock.getLocation().distance(this.player.getEyeLocation()) > (double)range || this.legsblock.getLocation().distance(this.player.getLocation()) > (double)range) {
            this.cancel();
            return false;
        }
        if (!newheadblock.equals((Object)this.headblock)) {
            new com.projectkorra.projectkorra.util.TempBlock(newheadblock, this.headtype, this.headdata);
            if (TempBlock.isTempBlock(this.headblock)) {
                TempBlock.revertBlock(this.headblock, Material.AIR);
            }
        }
        if (!newlegsblock.equals((Object)this.legsblock)) {
            new com.projectkorra.projectkorra.util.TempBlock(newlegsblock, this.legstype, this.legsdata);
            if (TempBlock.isTempBlock(this.legsblock)) {
                TempBlock.revertBlock(this.legsblock, Material.AIR);
            }
        }
        this.headblock = newheadblock;
        this.legsblock = newlegsblock;
        return true;
    }

    private void cancel() {
        if (ProjectKorra.plugin.getConfig().getBoolean("Properties.Earth.RevertEarthbending")) {
            if (TempBlock.isTempBlock(this.headblock)) {
                TempBlock.revertBlock(this.headblock, Material.AIR);
            }
            if (TempBlock.isTempBlock(this.legsblock)) {
                TempBlock.revertBlock(this.legsblock, Material.AIR);
            }
        } else {
            this.headblock.breakNaturally();
            this.legsblock.breakNaturally();
        }
        if (instances.containsKey((Object)this.player)) {
            instances.remove((Object)this.player);
        }
    }

    private boolean inPosition() {
        if (this.headblock.equals((Object)this.player.getEyeLocation().getBlock()) && this.legsblock.equals((Object)this.player.getLocation().getBlock())) {
            return true;
        }
        return false;
    }

    private void formArmor() {
        if (TempBlock.isTempBlock(this.headblock)) {
            TempBlock.revertBlock(this.headblock, Material.AIR);
        }
        if (TempBlock.isTempBlock(this.legsblock)) {
            TempBlock.revertBlock(this.legsblock, Material.AIR);
        }
        this.oldarmor = this.player.getInventory().getArmorContents();
        ItemStack[] armors = new ItemStack[]{new ItemStack(Material.LEATHER_BOOTS, 1), new ItemStack(Material.LEATHER_LEGGINGS, 1), new ItemStack(Material.LEATHER_CHESTPLATE, 1), new ItemStack(Material.LEATHER_HELMET, 1)};
        this.player.getInventory().setArmorContents(armors);
        PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int)duration / 50, this.strength - 1);
        new com.projectkorra.projectkorra.util.TempPotionEffect((LivingEntity)this.player, resistance);
        this.formed = true;
        this.starttime = System.currentTimeMillis();
    }

    public static void moveArmorAll() {
        for (Player player : instances.keySet()) {
            EarthArmor.moveArmor(player);
        }
    }

    public static void moveArmor(Player player) {
        if (!instances.containsKey((Object)player)) {
            return;
        }
        EarthArmor eartharmor = instances.get((Object)player);
        if (player.isDead() || !player.isOnline()) {
            eartharmor.cancel();
            eartharmor.removeEffect();
            return;
        }
        if (eartharmor.formed) {
            if (System.currentTimeMillis() > eartharmor.starttime + duration && !eartharmor.complete) {
                eartharmor.complete = true;
                eartharmor.removeEffect();
                eartharmor.cancel();
                GeneralMethods.getBendingPlayer(player.getName()).addCooldown("EarthArmor", cooldown);
                return;
            }
        } else if (System.currentTimeMillis() > eartharmor.time + interval) {
            if (!eartharmor.moveBlocks()) {
                return;
            }
            if (eartharmor.inPosition()) {
                eartharmor.formArmor();
            }
        }
    }

    private void removeEffect() {
        this.player.getInventory().setArmorContents(this.oldarmor);
    }

    public static void removeEffect(Player player) {
        if (!instances.containsKey((Object)player)) {
            return;
        }
        instances.get((Object)player).removeEffect();
    }

    public static void removeAll() {
        for (Player player : instances.keySet()) {
            EarthArmor eartharmor = instances.get((Object)player);
            eartharmor.cancel();
            eartharmor.removeEffect();
        }
    }

    public static String getDescription() {
        return "This ability encases the earthbender in temporary armor. To use, click on a block that is earthbendable. If there is another block under it that is earthbendable, the block will fly to you and grant you temporary armor and damage reduction. This ability has a long cooldown.";
    }

    public static boolean canRemoveArmor(Player player) {
        if (instances.containsKey((Object)player)) {
            EarthArmor eartharmor = instances.get((Object)player);
            if (System.currentTimeMillis() < eartharmor.starttime + duration) {
                return false;
            }
        }
        return true;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getStrength() {
        return this.strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}

