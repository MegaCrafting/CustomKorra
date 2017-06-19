/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.Server
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package com.projectkorra.projectkorra.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.Information;

public class RevertChecker
implements Runnable {
    private ProjectKorra plugin;
    private static final FileConfiguration config = ConfigManager.defaultConfig.get();
    private static final boolean safeRevert = config.getBoolean("Properties.Earth.SafeRevert");
    public static Map<Block, Block> earthRevertQueue = new ConcurrentHashMap<Block, Block>();
    static Map<Integer, Integer> airRevertQueue = new ConcurrentHashMap<Integer, Integer>();
    private long time;
    private Future<ArrayList<Chunk>> returnFuture;

    public RevertChecker(ProjectKorra bending) {
        this.plugin = bending;
    }

    public static void revertAirBlocks() {
        Iterator<Integer> iterator = airRevertQueue.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = iterator.next();
            EarthMethods.revertAirBlock(ID);
            airRevertQueue.remove(ID);
        }
    }

    public static void revertEarthBlocks() {
        for (Block block : earthRevertQueue.keySet()) {
            EarthMethods.revertBlock(block);
            earthRevertQueue.remove((Object)block);
        }
    }

    private void addToAirRevertQueue(int i) {
        if (!airRevertQueue.containsKey(i)) {
            airRevertQueue.put(i, i);
        }
    }

    private void addToRevertQueue(Block block) {
        if (!earthRevertQueue.containsKey((Object)block)) {
            earthRevertQueue.put(block, block);
        }
    }

    @Override
    public void run() {
        this.time = System.currentTimeMillis();
        if (config.getBoolean("Properties.Earth.RevertEarthbending")) {
            try {
                this.returnFuture = this.plugin.getServer().getScheduler().callSyncMethod((Plugin)this.plugin, (Callable)new getOccupiedChunks(this.plugin.getServer()));
                ArrayList<Chunk> chunks = this.returnFuture.get();
                HashMap<Block, Information> earth = new HashMap<Block, Information>();
                earth.putAll(EarthMethods.movedearth);
                for (Block block : earth.keySet()) {
                    if (earthRevertQueue.containsKey((Object)block)) continue;
                    boolean remove = true;
                    Information info = (Information)earth.get((Object)block);
                    if (this.time < info.getTime() + config.getLong("Properties.Earth.RevertCheckTime") || chunks.contains((Object)block.getChunk()) && safeRevert) {
                        remove = false;
                    }
                    if (!remove) continue;
                    this.addToRevertQueue(block);
                }
                HashMap<Integer, Information> air = new HashMap<Integer, Information>();
                air.putAll(EarthMethods.tempair);
                for (Integer i : air.keySet()) {
                    if (airRevertQueue.containsKey(i)) continue;
                    boolean remove = true;
                    Information info = (Information)air.get(i);
                    Block block2 = info.getBlock();
                    if (this.time < info.getTime() + config.getLong("Properties.Earth.RevertCheckTime") || chunks.contains((Object)block2.getChunk()) && safeRevert) {
                        remove = false;
                    }
                    if (!remove) continue;
                    this.addToAirRevertQueue(i);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class getOccupiedChunks
    implements Callable<ArrayList<Chunk>> {
        private Server server;

        public getOccupiedChunks(Server server) {
            this.server = server;
        }

        @Override
        public ArrayList<Chunk> call() throws Exception {
            ArrayList<Chunk> chunks = new ArrayList<Chunk>();
            for (Player player : this.server.getOnlinePlayers()) {
                Chunk chunk = player.getLocation().getChunk();
                if (chunks.contains((Object)chunk)) continue;
                chunks.add(chunk);
            }
            return chunks;
        }
    }

}

