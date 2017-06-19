/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.FallingBlock
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

public class LavaSurge {
    public static ConcurrentHashMap<Player, LavaSurge> instances = new ConcurrentHashMap();
    public static int impactDamage = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.LavaSurge.Damage");
    public static int cooldown = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.LavaSurge.Cooldown");
    public static int fractureRadius = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.LavaSurge.FractureRadius");
    public static int prepareRange = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.LavaSurge.PrepareRange");
    public static int travelRange = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.LavaSurge.TravelRange");
    public static int maxBlocks = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.LavaSurge.MaxLavaWaves");
    public static boolean canSourceBeEarth = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Earth.LavaSurge.SourceCanBeEarth");
    public static List<FallingBlock> falling = new ArrayList<FallingBlock>();
    public static int particleInterval = 100;
    public static int fallingBlockInterval = 100;
    private Player player;
    private Block sourceBlock;
    private long lastTime;
    private long time;
    private int fallingBlocksCount = 0;
    private boolean surgeStarted = false;
    private boolean fractureOpen;
    private Random randy = new Random();
    private Vector direction;
    private Location startLocation;
    private List<FallingBlock> fblocks = new ArrayList<FallingBlock>();
    private List<Block> fracture = new ArrayList<Block>();
    private List<TempBlock> fracturetb = new ArrayList<TempBlock>();
    private List<TempBlock> movingLava = new ArrayList<TempBlock>();
    private ConcurrentHashMap<FallingBlock, TempBlock> lava = new ConcurrentHashMap();
    private ListIterator<Block> li;

    public LavaSurge(Player player) {
        this.player = player;
        if (!this.isEligible()) {
            return;
        }
        if (GeneralMethods.getBendingPlayer(player.getName()).isOnCooldown("LavaSurge")) {
            return;
        }
        this.lastTime = System.currentTimeMillis();
        if (this.prepare()) {
            instances.put(player, this);
        }
    }

    public boolean isEligible() {
        BendingPlayer bplayer = GeneralMethods.getBendingPlayer(this.player.getName());
        if (!GeneralMethods.canBend(this.player.getName(), "LavaSurge")) {
            return false;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            return false;
        }
        if (!GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("LavaSurge")) {
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "LavaSurge", this.player.getLocation())) {
            return false;
        }
        if (!EarthMethods.canLavabend(this.player)) {
            return false;
        }
        if (bplayer.isOnCooldown("LavaSurge")) {
            return false;
        }
        return true;
    }

    public boolean prepare() {
        Block targetBlock = BlockSource.getEarthSourceBlock(this.player, prepareRange, ClickType.SHIFT_DOWN);
        if (targetBlock == null || targetBlock.getRelative(BlockFace.UP).getType() != Material.AIR && !this.isLava(targetBlock.getRelative(BlockFace.UP))) {
            return false;
        }
        if (instances.containsKey((Object)this.player)) {
            instances.get((Object)this.player).revertFracture();
        }
        if (canSourceBeEarth && EarthMethods.isEarthbendable(this.player, targetBlock) || EarthMethods.isLavabendable(targetBlock, this.player)) {
            this.startLocation = targetBlock.getLocation().add(0.0, 1.0, 0.0);
            this.sourceBlock = targetBlock;
            return true;
        }
        return false;
    }

    public boolean isLava(Block b) {
        if (b.getType() == Material.STATIONARY_LAVA || b.getType() == Material.LAVA) {
            return true;
        }
        return false;
    }

    public void launch() {
        Location targetLocation = GeneralMethods.getTargetedLocation(this.player, travelRange * 2);
        try {
            targetLocation = GeneralMethods.getTargetedEntity(this.player, travelRange * 2, null).getLocation();
        }
        catch (NullPointerException var2_2) {
            // empty catch block
        }
        if (targetLocation == null) {
            this.remove();
            return;
        }
        this.time = System.currentTimeMillis();
        this.direction = GeneralMethods.getDirection(this.startLocation, targetLocation).multiply(0.07);
        if (this.direction.getY() < 0.0) {
            this.direction.setY(0);
        }
        if (canSourceBeEarth) {
            this.openFracture();
        } else {
            this.skipFracture();
        }
    }

    public void openFracture() {
        List<Block> affectedBlocks = GeneralMethods.getBlocksAroundPoint(this.sourceBlock.getLocation(), fractureRadius);
        for (Block b : affectedBlocks) {
            if (!EarthMethods.isEarthbendable(this.player, b)) continue;
            this.fracture.add(b);
        }
        this.li = this.fracture.listIterator();
        this.fractureOpen = true;
        GeneralMethods.getBendingPlayer(this.player.getName()).addCooldown("LavaSurge", cooldown);
    }

    public void skipFracture() {
        this.li = this.fracture.listIterator();
        this.fractureOpen = true;
    }

    public void revertFracture() {
        for (TempBlock tb : this.fracturetb) {
            tb.revertBlock();
        }
        this.fracture.clear();
    }

    public void remove() {
        this.revertFracture();
        instances.remove((Object)this.player);
    }

    public boolean canMoveThrough(Block block) {
        if (EarthMethods.isTransparentToEarthbending(this.player, this.startLocation.getBlock()) || EarthMethods.isEarthbendable(this.player, this.startLocation.getBlock()) || EarthMethods.isLavabendable(this.startLocation.getBlock(), this.player)) {
            return true;
        }
        return false;
    }

    public void removeLava() {
        for (TempBlock tb : this.lava.values()) {
            tb.revertBlock();
        }
        this.movingLava.clear();
    }

    public void progress() {
        long curTime = System.currentTimeMillis();
        if (!this.player.isOnline() || this.player.isDead()) {
            this.remove();
            return;
        }
        if (!this.surgeStarted && !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("LavaSurge")) {
            this.remove();
            return;
        }
        if (!this.surgeStarted && this.sourceBlock != null && curTime > this.lastTime + (long)particleInterval) {
            this.lastTime = curTime;
            ParticleEffect.LAVA.display(this.sourceBlock.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 1);
        } else if (this.surgeStarted && curTime > this.lastTime + (long)particleInterval) {
            this.lastTime = curTime;
            for (FallingBlock fblock : this.fblocks) {
                ParticleEffect.LAVA.display(fblock.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 1);
            }
        }
        if (this.fractureOpen && !this.surgeStarted) {
            if (!this.li.hasNext()) {
                this.surgeStarted = true;
            } else {
                Block b = this.li.next();
                EarthMethods.playEarthbendingSound(b.getLocation());
                int i = 0;
                while (i < 2) {
                    TempBlock tb = new TempBlock(b, Material.STATIONARY_LAVA,  (byte) 0);
                    
                    this.fracturetb.add(tb);
                    ++i;
                }
            }
        }
        if (this.surgeStarted) {
            if (this.fallingBlocksCount >= maxBlocks) {
                return;
            }
            if (curTime > this.time + (long)(fallingBlockInterval * this.fallingBlocksCount)) {
                FallingBlock fbs = GeneralMethods.spawnFallingBlock(this.sourceBlock.getLocation().add(0.0, 1.0, 0.0), 11, (byte) 0);
                this.fblocks.add(fbs);
                falling.add(fbs);
                double x = this.randy.nextDouble() / 5.0;
                double z = this.randy.nextDouble() / 5.0;
                x = this.randy.nextBoolean() ? - x : x;
                z = this.randy.nextBoolean() ? - z : z;
                fbs.setVelocity(this.direction.clone().add(new Vector(x, 0.2, z)).multiply(1.2));
                fbs.setDropItem(false);
                for (Block b : this.fracture) {
                    if (!this.randy.nextBoolean() || b == this.sourceBlock) continue;
                    FallingBlock fb = GeneralMethods.spawnFallingBlock(b.getLocation().add(new Vector(0, 1, 0)), 11, (byte) 0);
                    falling.add(fb);
                    this.fblocks.add(fb);
                    fb.setVelocity(this.direction.clone().add(new Vector(this.randy.nextDouble() / 10.0, 0.1, this.randy.nextDouble() / 10.0)).multiply(1.2));
                    fb.setDropItem(false);
                }
                ++this.fallingBlocksCount;
            }
            for (FallingBlock fb : this.fblocks) {
                for (Entity e : GeneralMethods.getEntitiesAroundPoint(fb.getLocation(), 2.0)) {
                    if (!(e instanceof LivingEntity) || e.getEntityId() == this.player.getEntityId()) continue;
                    GeneralMethods.damageEntity(this.player, e, impactDamage, "LavaSurge");
                    e.setFireTicks(100);
                    GeneralMethods.setVelocity(e, this.direction.clone());
                }
            }
        }
    }

    public static void progressAll() {
        for (Player p : instances.keySet()) {
            instances.get((Object)p).progress();
        }
    }
}

