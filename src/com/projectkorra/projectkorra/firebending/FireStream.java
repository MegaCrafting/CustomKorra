/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.material.MaterialData
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.firebending;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class FireStream
implements ConfigLoadable {
    public static ConcurrentHashMap<Integer, FireStream> instances = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Player> ignitedblocks = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Long> ignitedtimes = new ConcurrentHashMap();
    public static ConcurrentHashMap<Location, MaterialData> replacedBlocks = new ConcurrentHashMap();
    public static ConcurrentHashMap<LivingEntity, Player> ignitedentities = new ConcurrentHashMap();
    private static long soonesttime = config.get().getLong("Properties.GlobalCooldown");
    private static int firedamage = 3;
    private static int tickdamage = 2;
    private static int idCounter = 0;
    private static double speed = 15.0;
    private static long interval = (long)(1000.0 / speed);
    private static long dissipateAfter = 400;
    private Player player;
    private Location origin;
    private Location location;
    private Vector direction;
    private long time;
    private double range;
    private int id;

    public FireStream(Location location, Vector direction, Player player, int range) {
        this.range = FireMethods.getFirebendingDayAugment(range, player.getWorld());
        this.player = player;
        this.origin = location.clone();
        this.location = this.origin.clone();
        this.direction = direction.clone();
        this.direction.setY(0);
        this.direction = this.direction.clone().normalize();
        this.location = this.location.clone().add(this.direction);
        this.time = System.currentTimeMillis();
        instances.put(idCounter, this);
        this.id = idCounter;
        idCounter = (idCounter + 1) % Integer.MAX_VALUE;
    }

    public static void dissipateAll() {
        if (dissipateAfter != 0) {
            for (Block block : ignitedtimes.keySet()) {
                if (block.getType() != Material.FIRE) {
                    FireStream.remove(block);
                    continue;
                }
                long time = ignitedtimes.get((Object)block);
                if (System.currentTimeMillis() <= time + dissipateAfter) continue;
                block.setType(Material.AIR);
                FireStream.remove(block);
            }
        }
    }

    public static String getDescription() {
        return "This ability no longer exists.";
    }

    public static boolean isIgnitable(Player player, Block block) {
        Material[] overwriteable = new Material[]{Material.SAPLING, Material.LONG_GRASS, Material.DEAD_BUSH, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.FIRE, Material.SNOW, Material.TORCH};
        if (Arrays.asList(overwriteable).contains((Object)block.getType())) {
            return true;
        }
        if (block.getType() != Material.AIR) {
            return false;
        }
        Material[] ignitable = new Material[]{Material.BEDROCK, Material.BOOKSHELF, Material.BRICK, Material.CLAY, Material.CLAY_BRICK, Material.COAL_ORE, Material.COBBLESTONE, Material.DIAMOND_ORE, Material.DIAMOND_BLOCK, Material.DIRT, Material.ENDER_STONE, Material.GLOWING_REDSTONE_ORE, Material.GOLD_BLOCK, Material.GRAVEL, Material.GRASS, Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2, Material.LAPIS_BLOCK, Material.LAPIS_ORE, Material.LOG, Material.MOSSY_COBBLESTONE, Material.MYCEL, Material.NETHER_BRICK, Material.NETHERRACK, Material.OBSIDIAN, Material.REDSTONE_ORE, Material.SAND, Material.SANDSTONE, Material.SMOOTH_BRICK, Material.STONE, Material.SOUL_SAND, Material.WOOD, Material.WOOL, Material.LEAVES, Material.LEAVES_2, Material.MELON_BLOCK, Material.PUMPKIN, Material.JACK_O_LANTERN, Material.NOTE_BLOCK, Material.GLOWSTONE, Material.IRON_BLOCK, Material.DISPENSER, Material.SPONGE, Material.IRON_ORE, Material.GOLD_ORE, Material.COAL_BLOCK, Material.WORKBENCH, Material.HAY_BLOCK, Material.REDSTONE_LAMP_OFF, Material.REDSTONE_LAMP_ON, Material.EMERALD_ORE, Material.EMERALD_BLOCK, Material.REDSTONE_BLOCK, Material.QUARTZ_BLOCK, Material.QUARTZ_ORE, Material.STAINED_CLAY, Material.HARD_CLAY};
        Block belowblock = block.getRelative(BlockFace.DOWN);
        if (Arrays.asList(ignitable).contains((Object)belowblock.getType())) {
            return true;
        }
        return false;
    }

    public static void remove(Block block) {
        if (ignitedblocks.containsKey((Object)block)) {
            ignitedblocks.remove((Object)block);
        }
        if (ignitedtimes.containsKey((Object)block)) {
            ignitedtimes.remove((Object)block);
        }
        if (replacedBlocks.containsKey((Object)block.getLocation())) {
            block.setType(replacedBlocks.get((Object)block.getLocation()).getItemType());
            block.setData(replacedBlocks.get((Object)block.getLocation()).getData());
            replacedBlocks.remove((Object)block.getLocation());
        }
    }

    public static void removeAll() {
        for (Block block : ignitedblocks.keySet()) {
            FireStream.remove(block);
        }
        for (Integer key : instances.keySet()) {
            instances.get(key).remove();
        }
    }

    public static void removeAroundPoint(Location location, double radius) {
        for (FireStream stream : instances.values()) {
            if (!stream.location.getWorld().equals((Object)location.getWorld()) || stream.location.distance(location) > radius) continue;
            stream.remove();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getRange() {
        return this.range;
    }

    private void ignite(Block block) {
        if (block.getType() != Material.AIR) {
            if (FireMethods.canFireGrief()) {
                if (WaterMethods.isPlant(block)) {
                    new com.projectkorra.projectkorra.waterbending.Plantbending(block);
                }
            } else if (block.getType() != Material.FIRE) {
                replacedBlocks.put(block.getLocation(), block.getState().getData());
            }
        }
        block.setType(Material.FIRE);
        ignitedblocks.put(block, this.player);
        ignitedtimes.put(block, System.currentTimeMillis());
    }

    public boolean progress() {
        if (System.currentTimeMillis() - this.time >= interval) {
            this.location = this.location.clone().add(this.direction);
            this.time = System.currentTimeMillis();
            if (this.location.distance(this.origin) > this.range) {
                this.remove();
                return false;
            }
            Block block = this.location.getBlock();
            if (FireStream.isIgnitable(this.player, block)) {
                this.ignite(block);
                return true;
            }
            if (FireStream.isIgnitable(this.player, block.getRelative(BlockFace.DOWN))) {
                this.ignite(block.getRelative(BlockFace.DOWN));
                this.location = block.getRelative(BlockFace.DOWN).getLocation();
                return true;
            }
            if (FireStream.isIgnitable(this.player, block.getRelative(BlockFace.UP))) {
                this.ignite(block.getRelative(BlockFace.UP));
                this.location = block.getRelative(BlockFace.UP).getLocation();
                return true;
            }
            this.remove();
            return false;
        }
        return false;
    }

    public static void progressAll() {
        for (FireStream ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove(this.id);
    }

    @Override
    public void reloadVariables() {
        soonesttime = config.get().getLong("Properties.GlobalCooldown");
    }

    public void setRange(double range) {
        this.range = range;
    }
}

