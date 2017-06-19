/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.Collapse;
import com.projectkorra.projectkorra.earthbending.EarthColumn;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;

public class CompactColumn {
    public static ConcurrentHashMap<Integer, CompactColumn> instances = new ConcurrentHashMap();
    private static ConcurrentHashMap<Block, Block> alreadydoneblocks = new ConcurrentHashMap();
    private static int ID = Integer.MIN_VALUE;
    private static int height = EarthColumn.standardheight;
    private static double range = Collapse.range;
    private static double speed = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Collapse.Speed");
    private static final Vector direction = new Vector(0, -1, 0);
    private static long interval = (long)(1000.0 / speed);
    private Location origin;
    private Location location;
    private Block block;
    private Player player;
    private int distance;
    private int id;
    private long time;
    private ConcurrentHashMap<Block, Block> affectedblocks = new ConcurrentHashMap();

    public CompactColumn(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("Collapse")) {
            return;
        }
        this.block = BlockSource.getEarthSourceBlock(player, range, ClickType.LEFT_CLICK);
        if (this.block == null) {
            return;
        }
        this.origin = this.block.getLocation();
        this.location = this.origin.clone();
        this.player = player;
        this.distance = EarthMethods.getEarthbendableBlocksLength(player, this.block, direction.clone().multiply(-1), height);
        this.loadAffectedBlocks();
        if (this.distance != 0 && this.canInstantiate()) {
            this.id = ID;
            instances.put(this.id, this);
            bPlayer.addCooldown("Collapse", GeneralMethods.getGlobalCooldown());
            if (ID >= Integer.MAX_VALUE) {
                ID = Integer.MIN_VALUE;
            }
            ++ID;
            this.time = System.currentTimeMillis() - interval;
        }
    }

    public CompactColumn(Player player, Location origin) {
        this.origin = origin;
        this.player = player;
        this.block = origin.getBlock();
        this.location = origin.clone();
        this.distance = EarthMethods.getEarthbendableBlocksLength(player, this.block, direction.clone().multiply(-1), height);
        this.loadAffectedBlocks();
        if (this.distance != 0 && this.canInstantiate()) {
            this.id = ID;
            instances.put(this.id, this);
            if (ID >= Integer.MAX_VALUE) {
                ID = Integer.MIN_VALUE;
            }
            ++ID;
            this.time = System.currentTimeMillis() - interval;
        }
    }

    private void loadAffectedBlocks() {
        this.affectedblocks.clear();
        int i = 0;
        while (i <= this.distance) {
            Block thisblock = this.block.getWorld().getBlockAt(this.location.clone().add(direction.clone().multiply(- i)));
            this.affectedblocks.put(thisblock, thisblock);
            if (EarthColumn.blockInAllAffectedBlocks(thisblock)) {
                EarthColumn.revertBlock(thisblock);
            }
            ++i;
        }
    }

    private boolean blockInAffectedBlocks(Block block) {
        if (this.affectedblocks.containsKey((Object)block)) {
            return true;
        }
        return false;
    }

    public static boolean blockInAllAffectedBlocks(Block block) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            if (!instances.get(ID).blockInAffectedBlocks(block)) continue;
            return true;
        }
        return false;
    }

    public static void revertBlock(Block block) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            if (!instances.get(ID).blockInAffectedBlocks(block)) continue;
            CompactColumn.instances.get((Object)Integer.valueOf((int)ID)).affectedblocks.remove((Object)block);
        }
    }

    private boolean canInstantiate() {
        for (Block block : this.affectedblocks.keySet()) {
            if (!CompactColumn.blockInAllAffectedBlocks(block) && !alreadydoneblocks.containsKey((Object)block)) continue;
            return false;
        }
        return true;
    }

    public static void progressAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            instances.get(ID).progress();
        }
    }

    private boolean progress() {
        if (System.currentTimeMillis() - this.time >= interval) {
            this.time = System.currentTimeMillis();
            if (!this.moveEarth()) {
                instances.remove(this.id);
                return false;
            }
        }
        return true;
    }

    private boolean moveEarth() {
        Block block = this.location.getBlock();
        this.location = this.location.add(direction);
        if (block == null || this.location == null || this.distance == 0) {
            return false;
        }
        EarthMethods.moveEarth(this.player, block, direction, this.distance);
        this.loadAffectedBlocks();
        if (this.location.distance(this.origin) >= (double)this.distance) {
            return false;
        }
        return true;
    }

    public static void removeAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            instances.remove(id);
        }
    }
}

