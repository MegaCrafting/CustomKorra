/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.IceSpike2;
import com.projectkorra.projectkorra.waterbending.OctopusForm;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterWall;
import com.projectkorra.projectkorra.waterbending.Wave;

public class WaterReturn {
    private static ConcurrentHashMap<Player, WaterReturn> instances = new ConcurrentHashMap();
    private static long interval = 50;
    private static double range = 30.0;
    private static final byte full = 0;
    private Player player;
    private Location location;
    private TempBlock block;
    private long time;

    public WaterReturn(Player player, Block block) {
        if (instances.containsKey((Object)player)) {
            return;
        }
        this.player = player;
        this.location = block.getLocation();
        if (GeneralMethods.canBend(player.getName(), "WaterManipulation") && !GeneralMethods.isRegionProtectedFromBuild(player, "WaterManipulation", this.location) && GeneralMethods.canBend(player.getName(), "WaterManipulation") && EarthMethods.isTransparentToEarthbending(player, block) && !block.isLiquid() && this.hasEmptyWaterBottle()) {
            this.block = new TempBlock(block, Material.WATER, (byte)0);
        }
        instances.put(player, this);
    }

    private void progress() {
        if (!this.hasEmptyWaterBottle()) {
            this.remove();
            return;
        }
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (!this.player.getWorld().equals((Object)this.location.getWorld())) {
            this.remove();
            return;
        }
        if (System.currentTimeMillis() < this.time + interval) {
            return;
        }
        this.time = System.currentTimeMillis();
        Vector direction = GeneralMethods.getDirection(this.location, this.player.getEyeLocation()).normalize();
        this.location = this.location.clone().add(direction);
        if (this.location == null || this.block == null) {
            this.remove();
            return;
        }
        if (this.location.getBlock().equals((Object)this.block.getLocation().getBlock())) {
            return;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "WaterManipulation", this.location)) {
            this.remove();
            return;
        }
        if (this.location.distance(this.player.getEyeLocation()) > WaterMethods.waterbendingNightAugment(range, this.player.getWorld())) {
            this.remove();
            return;
        }
        if (this.location.distance(this.player.getEyeLocation()) <= 1.5) {
            this.fillBottle();
            return;
        }
        Block newblock = this.location.getBlock();
        if (!EarthMethods.isTransparentToEarthbending(this.player, newblock) || newblock.isLiquid()) {
            this.remove();
            return;
        }
        this.block.revertBlock();
        this.block = new TempBlock(newblock, Material.WATER,(byte) 0);
    }

    private void remove() {
        if (this.block != null) {
            this.block.revertBlock();
            this.block = null;
        }
        instances.remove((Object)this.player);
    }

    private boolean hasEmptyWaterBottle() {
        PlayerInventory inventory = this.player.getInventory();
        if (inventory.contains(Material.GLASS_BOTTLE)) {
            return true;
        }
        return false;
    }

    private void fillBottle() {
        PlayerInventory inventory = this.player.getInventory();
        if (inventory.contains(Material.GLASS_BOTTLE)) {
            int index = inventory.first(Material.GLASS_BOTTLE);
            ItemStack item = inventory.getItem(index);
            if (item.getAmount() == 1) {
                inventory.setItem(index, new ItemStack(Material.POTION));
            } else {
                item.setAmount(item.getAmount() - 1);
                inventory.setItem(index, item);
                HashMap leftover = inventory.addItem(new ItemStack[]{new ItemStack(Material.POTION)});
                Iterator iterator = leftover.keySet().iterator();
                while (iterator.hasNext()) {
                    int left = (Integer)iterator.next();
                    this.player.getWorld().dropItemNaturally(this.player.getLocation(), (ItemStack)leftover.get(left));
                }
            }
        }
        this.remove();
    }

    private static boolean isBending(Player player) {
        int id;
        Iterator iterator = WaterManipulation.instances.keySet().iterator();
        while (iterator.hasNext()) {
            id = (Integer)iterator.next();
            if (!WaterManipulation.instances.get((Object)Integer.valueOf((int)id)).player.equals((Object)player)) continue;
            return true;
        }
        if (OctopusForm.instances.containsKey((Object)player)) {
            return true;
        }
        iterator = Wave.instances.keySet().iterator();
        while (iterator.hasNext()) {
            id = (Integer)iterator.next();
            if (!Wave.instances.get((Object)Integer.valueOf((int)id)).player.equals((Object)player)) continue;
            return true;
        }
        iterator = WaterWall.instances.keySet().iterator();
        while (iterator.hasNext()) {
            id = (Integer)iterator.next();
            if (!WaterWall.instances.get((Object)Integer.valueOf((int)id)).player.equals((Object)player)) continue;
            return true;
        }
        if (IceSpike2.isBending(player)) {
            return true;
        }
        return false;
    }

    public static boolean hasWaterBottle(Player player) {
        if (instances.containsKey((Object)player)) {
            return false;
        }
        if (WaterReturn.isBending(player)) {
            return false;
        }
        PlayerInventory inventory = player.getInventory();
        return inventory.contains(new ItemStack(Material.POTION), 1);
    }

    public static void emptyWaterBottle(Player player) {
        PlayerInventory inventory = player.getInventory();
        int index = inventory.first(new ItemStack(Material.POTION));
        if (index != -1) {
            ItemStack item = inventory.getItem(index);
            if (item.getAmount() == 1) {
                inventory.setItem(index, new ItemStack(Material.GLASS_BOTTLE));
            } else {
                item.setAmount(item.getAmount() - 1);
                inventory.setItem(index, item);
                HashMap leftover = inventory.addItem(new ItemStack[]{new ItemStack(Material.GLASS_BOTTLE)});
                Iterator iterator = leftover.keySet().iterator();
                while (iterator.hasNext()) {
                    int left = (Integer)iterator.next();
                    player.getWorld().dropItemNaturally(player.getLocation(), (ItemStack)leftover.get(left));
                }
            }
        }
    }

    public static void progressAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).progress();
        }
    }

    public static void removeAll() {
        for (Player player : instances.keySet()) {
            WaterReturn wr = instances.get((Object)player);
            if (wr.block == null) continue;
            wr.block.revertBlock();
        }
        instances.clear();
    }
}

