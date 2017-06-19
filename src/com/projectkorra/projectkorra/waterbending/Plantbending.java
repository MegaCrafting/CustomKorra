/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;

public class Plantbending {
    private static ConcurrentHashMap<Integer, Plantbending> instances = new ConcurrentHashMap();
    private static final long regrowtime = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.Plantbending.RegrowTime");
    private static int ID = Integer.MIN_VALUE;
    private Block block;
    private Material type;
    private byte data;
    private long time;
    private int id;

    public Plantbending(Block block) {
        if (regrowtime != 0) {
            this.block = block;
            this.type = block.getType();
            this.data = block.getData();
            this.time = System.currentTimeMillis() + regrowtime / 2 + (long)(Math.random() * (double)regrowtime) / 2;
            this.id = ID;
            instances.put(this.id, this);
            ID = ID >= Integer.MAX_VALUE ? Integer.MIN_VALUE : ++ID;
        }
    }

    public void revert() {
        if (this.block.getType() == Material.AIR) {
            this.block.setType(this.type);
            this.block.setData(this.data);
        } else {
            GeneralMethods.dropItems(this.block, GeneralMethods.getDrops(this.block, this.type, this.data, null));
        }
        instances.remove(this.id);
    }

    public static void regrow() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            Plantbending plantbending = instances.get(id);
            if (plantbending.time >= System.currentTimeMillis()) continue;
            plantbending.revert();
        }
    }

    public static void regrowAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            instances.get(id).revert();
        }
    }
}

