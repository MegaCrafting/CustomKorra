/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package com.projectkorra.projectkorra.firebending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.Cook;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

public class HeatControl
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, HeatControl> instances = new ConcurrentHashMap();
    public static double RANGE = config.get().getDouble("Abilities.Fire.HeatControl.Solidify.Range");
    public static int RADIUS = config.get().getInt("Abilities.Fire.HeatControl.Solidify.Radius");
    public static int REVERT_TIME = config.get().getInt("Abilities.Fire.HeatControl.Solidify.RevertTime");
    private Player player;
    private int currentRadius = 1;
    private long delay = 50;
    private long lastBlockTime = 0;
    private long lastParticleTime = 0;
    private Location center;
    private List<TempBlock> tblocks = new ArrayList<TempBlock>();
    public double range = RANGE;
    public int radius = RADIUS;
    public long revertTime = REVERT_TIME;

    public HeatControl(Player player) {
        if (!this.isEligible(player)) {
            return;
        }
        if (EarthMethods.getLavaSourceBlock(player, this.getRange()) == null) {
            new com.projectkorra.projectkorra.firebending.Cook(player);
            return;
        }
        this.player = player;
        this.lastBlockTime = System.currentTimeMillis();
        instances.put(player, this);
    }

    public void freeze(List<Location> area) {
        TempBlock tb;
        if (System.currentTimeMillis() < this.lastBlockTime + this.delay) {
            return;
        }
        ArrayList<Block> lava = new ArrayList<Block>();
        for (Location l : area) {
            if (!EarthMethods.isLava(l.getBlock())) continue;
            lava.add(l.getBlock());
        }
        this.lastBlockTime = System.currentTimeMillis();
        if (lava.size() == 0) {
            ++this.currentRadius;
            return;
        }
        Block b = (Block)lava.get(GeneralMethods.rand.nextInt(lava.size()));
        if (TempBlock.isTempBlock(b)) {
            tb = TempBlock.get(b);
            tb.setType(Material.STONE);
        } else {
            tb = new TempBlock(b, Material.STONE, b.getData());
        }
        if (!this.tblocks.contains(tb)) {
            this.tblocks.add(tb);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getRadius() {
        return this.radius;
    }

    public double getRange() {
        return this.range;
    }

    public long getRevertTime() {
        return this.revertTime;
    }

    public boolean isEligible(Player player) {
        if (!GeneralMethods.canBend(player.getName(), "HeatControl")) {
            return false;
        }
        if (GeneralMethods.getBoundAbility(player) == null) {
            return false;
        }
        if (!GeneralMethods.getBoundAbility(player).equalsIgnoreCase("HeatControl")) {
            return false;
        }
        return true;
    }

    public void particles(List<Location> area) {
        if (System.currentTimeMillis() < this.lastParticleTime + 300) {
            return;
        }
        this.lastParticleTime = System.currentTimeMillis();
        for (Location l : area) {
            if (!EarthMethods.isLava(l.getBlock())) continue;
            ParticleEffect.SMOKE_NORMAL.display(l, 0.0f, 0.0f, 0.0f, 0.1f, 2);
        }
    }

    public boolean progress() {
        if (!this.player.isOnline() || this.player.isDead() || !this.isEligible(this.player) || !this.player.isSneaking()) {
            this.remove();
            return false;
        }
        if (this.currentRadius >= this.getRadius()) {
            this.remove();
            return false;
        }
        Location targetlocation = GeneralMethods.getTargetedLocation(this.player, this.range, new Integer[0]);
        this.resetLocation(targetlocation);
        List<Location> area = GeneralMethods.getCircle(this.center, this.currentRadius, 3, true, true, 0);
        this.particles(area);
        this.freeze(area);
        return true;
    }

    public static void progressAll() {
        for (HeatControl ability : instances.values()) {
            ability.progress();
        }
    }

    @Override
    public void reloadVariables() {
        RANGE = config.get().getDouble("Abilities.Fire.HeatControl.Solidify.Range");
        RADIUS = config.get().getInt("Abilities.Fire.HeatControl.Solidify.Radius");
        REVERT_TIME = config.get().getInt("Abilities.Fire.HeatControl.Solidify.RevertTime");
        this.range = RANGE;
        this.radius = RADIUS;
        this.revertTime = REVERT_TIME;
    }

    public void remove() {
        final HeatControl ability = this;
        ProjectKorra.plugin.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)ProjectKorra.plugin, new Runnable(){

            @Override
            public void run() {
                HeatControl.this.revertAll();
                HeatControl.instances.remove(ability);
            }
        }, this.getRevertTime());
    }

    public void resetLocation(Location loc) {
        if (this.center == null) {
            this.center = loc;
            return;
        }
        if (!loc.equals((Object)this.center)) {
            this.currentRadius = 1;
            this.center = loc;
        }
    }

    public void revertAll() {
        for (TempBlock tb : this.tblocks) {
            tb.revertBlock();
        }
        this.tblocks.clear();
    }

    public void setRadius(int value) {
        this.radius = value;
    }

    public void setRange(double value) {
        this.range = value;
    }

    public void setRevertTime(long value) {
        this.revertTime = value;
    }

}

