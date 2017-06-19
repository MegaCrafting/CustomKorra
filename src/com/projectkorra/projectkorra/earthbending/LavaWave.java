/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.earthbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LavaWave {
    public static ConcurrentHashMap<Integer, LavaWave> instances = new ConcurrentHashMap();
    private static final double defaultmaxradius = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaSurge.Radius");
    private static final double defaultfactor = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaSurge.HorizontalPush");
    private static final double upfactor = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaSurge.VerticalPush");
    private static final long interval = 30;
    private static final byte full = 0;
    static double defaultrange = 20.0;
    Player player;
    private Location location = null;
    private Block sourceblock = null;
    private Location targetdestination = null;
    private Vector targetdirection = null;
    private ConcurrentHashMap<Block, Block> wave = new ConcurrentHashMap();
    private ConcurrentHashMap<Block, Block> frozenblocks = new ConcurrentHashMap();
    private long time;
    private double radius = 1.0;
    private double maxradius = defaultmaxradius;
    private double factor = defaultfactor;
    double range = defaultrange;
    boolean progressing = false;
    boolean canhitself = true;

    public LavaWave(Player player) {
        this.player = player;
        if (AvatarState.isAvatarState(player)) {
            this.maxradius = AvatarState.getValue(this.maxradius);
        }
        if (this.prepare()) {
            if (instances.containsKey(player.getEntityId())) {
                instances.get(player.getEntityId()).cancel();
            }
            instances.put(player.getEntityId(), this);
            this.time = System.currentTimeMillis();
        }
    }

    public boolean prepare() {
        this.cancelPrevious();
        Block block = BlockSource.getSourceBlock(this.player, this.range, BlockSource.BlockSourceType.LAVA, ClickType.SHIFT_DOWN);
        if (block != null) {
            this.sourceblock = block;
            this.focusBlock();
            return true;
        }
        return false;
    }

    private void cancelPrevious() {
        if (instances.containsKey(this.player.getEntityId())) {
            LavaWave old = instances.get(this.player.getEntityId());
            if (old.progressing) {
                old.breakBlock();
            } else {
                old.cancel();
            }
        }
    }

    public void cancel() {
        this.unfocusBlock();
    }

    private void focusBlock() {
        this.location = this.sourceblock.getLocation();
    }

    private void unfocusBlock() {
        instances.remove(this.player.getEntityId());
    }

    public void moveLava() {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(this.player.getName());
        if (bPlayer.isOnCooldown("LavaSurge")) {
            return;
        }
        bPlayer.addCooldown("LavaSurge", GeneralMethods.getGlobalCooldown());
        if (this.sourceblock != null) {
            Entity target;
            if (!this.sourceblock.getWorld().equals((Object)this.player.getWorld())) {
                return;
            }
            if (AvatarState.isAvatarState(this.player)) {
                this.factor = AvatarState.getValue(this.factor);
            }
            this.targetdestination = (target = GeneralMethods.getTargetedEntity(this.player, this.range, new ArrayList<Entity>())) == null ? this.player.getTargetBlock(EarthMethods.getTransparentEarthbending(), (int)this.range).getLocation() : ((LivingEntity)target).getEyeLocation();
            if (this.targetdestination.distance(this.location) <= 1.0) {
                this.progressing = false;
                this.targetdestination = null;
            } else {
                this.progressing = true;
                this.targetdirection = this.getDirection(this.sourceblock.getLocation(), this.targetdestination).normalize();
                this.targetdestination = this.location.clone().add(this.targetdirection.clone().multiply(this.range));
                if (!GeneralMethods.isAdjacentToThreeOrMoreSources(this.sourceblock)) {
                    this.sourceblock.setType(Material.AIR);
                }
                this.addLava(this.sourceblock);
            }
        }
    }

    private Vector getDirection(Location location, Location destination) {
        double x1 = destination.getX();
        double y1 = destination.getY();
        double z1 = destination.getZ();
        double x0 = location.getX();
        double y0 = location.getY();
        double z0 = location.getZ();
        return new Vector(x1 - x0, y1 - y0, z1 - z0);
    }

    public static void progressAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            instances.get(ID).progress();
        }
    }

    private boolean progress() {
        if (this.player.isDead() || !this.player.isOnline() || !GeneralMethods.canBend(this.player.getName(), "LavaSurge")) {
            this.breakBlock();
            return false;
        }
        if (System.currentTimeMillis() - this.time >= 30) {
            this.time = System.currentTimeMillis();
            if (GeneralMethods.getBoundAbility(this.player) == null) {
                this.unfocusBlock();
                return false;
            }
            if (!this.progressing && !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("LavaSurge")) {
                this.unfocusBlock();
                return false;
            }
            if (!this.progressing) {
                this.sourceblock.getWorld().playEffect(this.location, Effect.SMOKE, 4, (int)this.range);
                return false;
            }
            if (this.location.getWorld() != this.player.getWorld()) {
                this.breakBlock();
                return false;
            }
            Vector direction = this.targetdirection;
            this.location = this.location.clone().add(direction);
            Block blockl = this.location.getBlock();
            ArrayList<Block> blocks = new ArrayList<Block>();
            if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "LavaSurge", this.location) && (blockl.getType() == Material.AIR || blockl.getType() == Material.FIRE || WaterMethods.isPlant(blockl) || EarthMethods.isLava(blockl) || EarthMethods.isLavabendable(blockl, this.player)) && blockl.getType() != Material.LEAVES) {
                double i = 0.0;
                while (i <= this.radius) {
                    double angle = 0.0;
                    while (angle < 360.0) {
                        Vector vec = GeneralMethods.getOrthogonalVector(this.targetdirection, angle, i);
                        Block block = this.location.clone().add(vec).getBlock();
                        if (!blocks.contains((Object)block) && (block.getType() == Material.AIR || block.getType() == Material.FIRE) || EarthMethods.isLavabendable(block, this.player)) {
                            blocks.add(block);
                            FireBlast.removeFireBlastsAroundPoint(block.getLocation(), 2.0);
                        }
                        angle += 10.0;
                    }
                    i += 0.5;
                }
            }
            for (Block block : this.wave.keySet()) {
                if (blocks.contains((Object)block)) continue;
                this.finalRemoveLava(block);
            }
            for (Block block2 : blocks) {
                if (this.wave.containsKey((Object)block2)) continue;
                this.addLava(block2);
            }
            if (this.wave.isEmpty()) {
                this.breakBlock();
                this.progressing = false;
                return false;
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.0 * this.radius)) {
                boolean knockback = false;
                for (Block block3 : this.wave.keySet()) {
                    if (entity.getLocation().distance(block3.getLocation()) > 2.0 || entity.getEntityId() == this.player.getEntityId() && !this.canhitself) continue;
                    knockback = true;
                }
                if (!knockback) continue;
                Vector dir = direction.clone();
                dir.setY(dir.getY() * upfactor);
                entity.setVelocity(entity.getVelocity().clone().add(dir.clone().multiply(this.factor)));
                entity.setFallDistance(0.0f);
                if (entity.getFireTicks() > 0) {
                    entity.getWorld().playEffect(entity.getLocation(), Effect.EXTINGUISH, 0);
                }
                entity.setFireTicks(0);
            }
            if (!this.progressing) {
                this.breakBlock();
                return false;
            }
            if (this.location.distance(this.targetdestination) < 1.0) {
                this.progressing = false;
                this.breakBlock();
                return false;
            }
            if (this.radius < this.maxradius) {
                this.radius += 0.5;
            }
            return true;
        }
        return false;
    }

    private void breakBlock() {
        for (Block block : this.wave.keySet()) {
            this.finalRemoveLava(block);
        }
        instances.remove(this.player.getEntityId());
    }

    private void finalRemoveLava(Block block) {
        if (this.wave.containsKey((Object)block)) {
            TempBlock.revertBlock(block, Material.AIR);
            this.wave.remove((Object)block);
        }
    }

    private void addLava(Block block) {
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "LavaSurge", block.getLocation())) {
            return;
        }
        if (!TempBlock.isTempBlock(block)) {
            new com.projectkorra.projectkorra.util.TempBlock(block, Material.STATIONARY_LAVA, (byte)8);
            this.wave.put(block, block);
        }
    }

    private void clearWave() {
        for (Block block : this.wave.keySet()) {
            TempBlock.revertBlock(block, Material.AIR);
        }
        this.wave.clear();
    }

    public static void moveLava(Player player) {
        if (instances.containsKey(player.getEntityId())) {
            instances.get(player.getEntityId()).moveLava();
        }
    }

    public static boolean isBlockInWave(Block block) {
        Iterator iterator = instances.keySet().iterator();
        if (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            if (block.getLocation().distance(LavaWave.instances.get((Object)Integer.valueOf((int)ID)).location) <= 2.0 * LavaWave.instances.get((Object)Integer.valueOf((int)ID)).radius) {
                return true;
            }
            return false;
        }
        return false;
    }

    public static boolean isBlockWave(Block block) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            if (!LavaWave.instances.get((Object)Integer.valueOf((int)ID)).wave.containsKey((Object)block)) continue;
            return true;
        }
        return false;
    }

    public static void launch(Player player) {
        LavaWave.moveLava(player);
    }

    public static void removeAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            for (Block block2 : LavaWave.instances.get((Object)Integer.valueOf((int)id)).wave.keySet()) {
                block2.setType(Material.AIR);
                LavaWave.instances.get((Object)Integer.valueOf((int)id)).wave.remove((Object)block2);
            }
            for (Block block2 : LavaWave.instances.get((Object)Integer.valueOf((int)id)).frozenblocks.keySet()) {
                block2.setType(Material.AIR);
                LavaWave.instances.get((Object)Integer.valueOf((int)id)).frozenblocks.remove((Object)block2);
            }
        }
    }
}

