/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
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
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.TorrentBurst;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterReturn;
import com.projectkorra.projectkorra.util.ClickType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Torrent {
    public static ConcurrentHashMap<Player, Torrent> instances = new ConcurrentHashMap();
    private static ConcurrentHashMap<TempBlock, Player> frozenblocks = new ConcurrentHashMap();
    static FileConfiguration config = ProjectKorra.plugin.getConfig();
    static long interval = 30;
    static double RANGE = config.getInt("Abilities.Water.Torrent.Range");
    private static int defaultrange = 20;
    private static int selectrange = 10;
    private static int DAMAGE = config.getInt("Abilities.Water.Torrent.Damage");
    private static int DEFLECT_DAMAGE = config.getInt("Abilities.Water.Torrent.DeflectDamage");
    private static int maxlayer = 3;
    private static double factor = 1.0;
    private static double radius = 3.0;
    private static double ylimit = 0.2;
    private static final byte full = 0;
    private Block sourceblock;
    private TempBlock source;
    private Location location;
    private Player player;
    private long time;
    private double startangle = 0.0;
    private double angle = 20.0;
    private int layer = 0;
    private boolean sourceselected = false;
    private boolean settingup = false;
    private boolean forming = false;
    private boolean formed = false;
    private boolean launch = false;
    private boolean launching = false;
    public boolean freeze = false;
    private double range = RANGE;
    private int damage = DAMAGE;
    private int deflectdamage = DEFLECT_DAMAGE;
    private ArrayList<TempBlock> blocks = new ArrayList();
    public ArrayList<TempBlock> launchblocks = new ArrayList();
    private ArrayList<Entity> hurtentities = new ArrayList();

    public Torrent(Player player) {
        if (instances.containsKey((Object)player)) {
            Torrent torrent = instances.get((Object)player);
            if (!torrent.sourceselected) {
                instances.get((Object)player).use();
                return;
            }
        }
        this.player = player;
        this.time = System.currentTimeMillis();
        this.sourceblock = BlockSource.getWaterSourceBlock(player, selectrange, ClickType.LEFT_CLICK, true, true, WaterMethods.canPlantbend(player));
        if (this.sourceblock != null) {
            this.sourceselected = true;
            instances.put(player, this);
        }
    }

    private void freeze() {
        if (this.layer == 0) {
            return;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "PhaseChange")) {
            return;
        }
        List<Block> ice = GeneralMethods.getBlocksAroundPoint(this.location, this.layer);
        for (Block block : ice) {
            if (!EarthMethods.isTransparentToEarthbending(this.player, block) || block.getType() == Material.ICE) continue;
            TempBlock tblock = new TempBlock(block, Material.ICE, (byte)0);
            frozenblocks.put(tblock, this.player);
            WaterMethods.playIcebendingSound(block.getLocation());
        }
    }

    private void progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "Torrent")) {
            this.remove();
            return;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            this.remove();
            if (this.location != null) {
                this.returnWater(this.location);
            }
            return;
        }
        if (!GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("Torrent")) {
            this.remove();
            if (this.location != null) {
                this.returnWater(this.location);
            }
            return;
        }
        if (System.currentTimeMillis() > this.time + interval) {
            this.time = System.currentTimeMillis();
            if (this.sourceselected) {
                if (!this.sourceblock.getWorld().equals((Object)this.player.getWorld())) {
                    this.remove();
                    return;
                }
                if (this.sourceblock.getLocation().distance(this.player.getLocation()) > (double)selectrange) {
                    return;
                }
                if (this.player.isSneaking()) {
                    this.sourceselected = false;
                    this.settingup = true;
                    if (WaterMethods.isPlant(this.sourceblock)) {
                        new com.projectkorra.projectkorra.waterbending.Plantbending(this.sourceblock);
                        this.sourceblock.setType(Material.AIR);
                    } else if (!GeneralMethods.isAdjacentToThreeOrMoreSources(this.sourceblock)) {
                        this.sourceblock.setType(Material.AIR);
                    }
                    this.source = new TempBlock(this.sourceblock, Material.STATIONARY_WATER,(byte) 8);
                    this.location = this.sourceblock.getLocation();
                } else {
                    WaterMethods.playFocusWaterEffect(this.sourceblock);
                    return;
                }
            }
            if (this.settingup) {
                Vector direction;
                if (!this.player.isSneaking()) {
                    this.remove();
                    this.returnWater(this.source.getLocation());
                    return;
                }
                Location eyeloc = this.player.getEyeLocation();
                double startangle = this.player.getEyeLocation().getDirection().angle(new Vector(1, 0, 0));
                double dx = radius * Math.cos(startangle);
                double dy = 0.0;
                double dz = radius * Math.sin(startangle);
                Location setup = eyeloc.clone().add(dx, dy, dz);
                if (!this.location.getWorld().equals((Object)this.player.getWorld())) {
                    this.remove();
                    return;
                }
                if (this.location.distance(setup) > (double)defaultrange) {
                    this.remove();
                    return;
                }
                if (this.location.getBlockY() > setup.getBlockY()) {
                    direction = new Vector(0, -1, 0);
                    this.location = this.location.clone().add(direction);
                } else if (this.location.getBlockY() < setup.getBlockY()) {
                    direction = new Vector(0, 1, 0);
                    this.location = this.location.clone().add(direction);
                } else {
                    direction = GeneralMethods.getDirection(this.location, setup).normalize();
                    this.location = this.location.clone().add(direction);
                }
                if (this.location.distance(setup) <= 1.0) {
                    this.settingup = false;
                    this.source.revertBlock();
                    this.source = null;
                    this.forming = true;
                } else if (!this.location.getBlock().equals((Object)this.source.getLocation().getBlock())) {
                    this.source.revertBlock();
                    this.source = null;
                    Block block = this.location.getBlock();
                    if (!EarthMethods.isTransparentToEarthbending(this.player, block) || block.isLiquid()) {
                        this.remove();
                        return;
                    }
                    this.source = new TempBlock(this.location.getBlock(), Material.STATIONARY_WATER,(byte) 8);
                }
            }
            if (this.forming && !this.player.isSneaking()) {
                this.remove();
                this.returnWater(this.player.getEyeLocation().add(radius, 0.0, 0.0));
                return;
            }
            if (this.forming || this.formed) {
                if (GeneralMethods.rand.nextInt(4) == 0) {
                    WaterMethods.playWaterbendingSound(this.location);
                }
                if (this.angle < 220.0) {
                    this.angle += 20.0;
                } else {
                    this.forming = false;
                    this.formed = true;
                }
                this.formRing();
                if (this.blocks.isEmpty()) {
                    this.remove();
                    return;
                }
            }
            if (this.formed && !this.player.isSneaking() && !this.launch) {
                new com.projectkorra.projectkorra.waterbending.TorrentBurst(this.player, radius);
                this.remove();
                return;
            }
            if (this.launch && this.formed) {
                this.launching = true;
                this.launch = false;
                this.formed = false;
                if (!this.launch()) {
                    this.returnWater(this.location);
                    this.remove();
                    return;
                }
            }
            if (this.launching) {
                if (!this.player.isSneaking()) {
                    this.remove();
                    return;
                }
                if (!this.launch()) {
                    this.remove();
                    this.returnWater(this.location);
                    return;
                }
            }
        }
    }

    private boolean launch() {
        if (this.launchblocks.isEmpty() && this.blocks.isEmpty()) {
            return false;
        }
        if (this.launchblocks.isEmpty()) {
            this.clearRing();
            Location loc = this.player.getEyeLocation();
            ArrayList<Block> doneblocks = new ArrayList<Block>();
            double theta = this.startangle;
            while (theta < this.angle + this.startangle) {
                Block block;
                double phi = Math.toRadians(theta);
                double dx = Math.cos(phi) * radius;
                double dy = 0.0;
                double dz = Math.sin(phi) * radius;
                Location blockloc = loc.clone().add(dx, dy, dz);
                if (Math.abs(theta - this.startangle) < 10.0) {
                    this.location = blockloc.clone();
                }
                if (!doneblocks.contains((Object)(block = blockloc.getBlock())) && !GeneralMethods.isRegionProtectedFromBuild(this.player, "Torrent", blockloc)) {
                    if (EarthMethods.isTransparentToEarthbending(this.player, block) && !block.isLiquid()) {
                        this.launchblocks.add(new TempBlock(block, Material.STATIONARY_WATER,(byte) 8));
                        doneblocks.add(block);
                    } else if (!EarthMethods.isTransparentToEarthbending(this.player, block)) break;
                }
                theta += 20.0;
            }
            if (this.launchblocks.isEmpty()) {
                return false;
            }
            return true;
        }
        Entity target = GeneralMethods.getTargetedEntity(this.player, this.range, this.hurtentities);
        Location targetloc = this.player.getTargetBlock(EarthMethods.getTransparentEarthbending(), (int)this.range).getLocation();
        if (target != null) {
            targetloc = target.getLocation();
        }
        ArrayList<TempBlock> newblocks = new ArrayList<TempBlock>();
        List<Entity> entities = GeneralMethods.getEntitiesAroundPoint(this.player.getLocation(), this.range + 5.0);
        ArrayList<Entity> affectedentities = new ArrayList<Entity>();
        Block realblock = this.launchblocks.get(0).getBlock();
        Vector dir = GeneralMethods.getDirection(this.location, targetloc).normalize();
        if (target != null) {
            targetloc = this.location.clone().add(dir.clone().multiply(10));
        }
        if (this.layer == 0) {
            this.location = this.location.clone().add(dir);
        }
        Block b = this.location.getBlock();
        if (this.location.distance(this.player.getLocation()) > this.range || GeneralMethods.isRegionProtectedFromBuild(this.player, "Torrent", this.location)) {
            if (this.layer < maxlayer && (this.freeze || this.layer < 1)) {
                ++this.layer;
            }
            if (this.launchblocks.size() == 1) {
                this.remove();
                this.returnWater(this.location);
                return false;
            }
        } else if (!EarthMethods.isTransparentToEarthbending(this.player, b)) {
            if (this.layer < maxlayer) {
                if (this.layer == 0) {
                    this.hurtentities.clear();
                }
                if (this.freeze || this.layer < 1) {
                    ++this.layer;
                }
            }
            if (this.freeze) {
                this.freeze();
            } else if (this.launchblocks.size() == 1) {
                this.remove();
                this.returnWater(realblock.getLocation());
                return false;
            }
        } else {
            if (b.equals((Object)realblock) && this.layer == 0) {
                return true;
            }
            if (b.getLocation().distance(targetloc) > 1.0) {
                if (WaterMethods.isWater(b)) {
                    ParticleEffect.WATER_BUBBLE.display((float)Math.random(), (float)Math.random(), (float)Math.random(), 0.0f, 5, b.getLocation().clone().add(0.5, 0.5, 0.5), 257.0);
                }
                newblocks.add(new TempBlock(b, Material.STATIONARY_WATER, (byte)8));
            } else {
                if (this.layer < maxlayer) {
                    if (this.layer == 0) {
                        this.hurtentities.clear();
                    }
                    if (this.freeze || this.layer < 1) {
                        ++this.layer;
                    }
                }
                if (this.freeze) {
                    this.freeze();
                }
            }
        }
        int i = 0;
        while (i < this.launchblocks.size()) {
            TempBlock block = this.launchblocks.get(i);
            if (i == this.launchblocks.size() - 1) {
                block.revertBlock();
            } else {
                newblocks.add(block);
                for (Entity entity : entities) {
                    if (entity.getWorld() != block.getBlock().getWorld() || entity.getLocation().distance(block.getLocation()) > 1.5 || affectedentities.contains((Object)entity)) continue;
                    if (i == 0) {
                        this.affect(entity, dir);
                    } else {
                        this.affect(entity, GeneralMethods.getDirection(block.getLocation(), this.launchblocks.get(i - 1).getLocation()).normalize());
                    }
                    affectedentities.add(entity);
                }
            }
            ++i;
        }
        this.launchblocks.clear();
        this.launchblocks.addAll(newblocks);
        if (this.launchblocks.isEmpty()) {
            return false;
        }
        return true;
    }

    private void formRing() {
        this.clearRing();
        this.startangle += 30.0;
        Location loc = this.player.getEyeLocation();
        ArrayList<Block> doneblocks = new ArrayList<Block>();
        List<Entity> entities = GeneralMethods.getEntitiesAroundPoint(loc, radius + 2.0);
        ArrayList affectedentities = new ArrayList();
        double theta = this.startangle;
        while (theta < this.angle + this.startangle) {
            double phi = Math.toRadians(theta);
            double dx = Math.cos(phi) * radius;
            double dy = 0.0;
            double dz = Math.sin(phi) * radius;
            Location blockloc = loc.clone().add(dx, dy, dz);
            Block block = blockloc.getBlock();
            if (!doneblocks.contains((Object)block) && EarthMethods.isTransparentToEarthbending(this.player, block) && !block.isLiquid()) {
                this.blocks.add(new TempBlock(block, Material.STATIONARY_WATER,(byte) 8));
                doneblocks.add(block);
                for (Entity entity : entities) {
                    if (entity.getWorld() != blockloc.getWorld() || affectedentities.contains((Object)entity) || entity.getLocation().distance(blockloc) > 1.5) continue;
                    this.deflect(entity);
                }
            }
            theta += 20.0;
        }
    }

    private void clearRing() {
        for (TempBlock block : this.blocks) {
            block.revertBlock();
        }
        this.blocks.clear();
    }

    public void remove() {
        this.clearRing();
        for (TempBlock block : this.launchblocks) {
            block.revertBlock();
        }
        this.launchblocks.clear();
        if (this.source != null) {
            this.source.revertBlock();
        }
        instances.remove((Object)this.player);
    }

    private void returnWater(Location location) {
        new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, location.getBlock());
    }

    public static void use(Player player) {
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).use();
        }
    }

    public static void create(Player player) {
        Location eyeloc;
        Block block;
        if (instances.containsKey((Object)player)) {
            return;
        }
        if (WaterReturn.hasWaterBottle(player) && EarthMethods.isTransparentToEarthbending(player, block = (eyeloc = player.getEyeLocation()).add(eyeloc.getDirection().normalize()).getBlock()) && EarthMethods.isTransparentToEarthbending(player, eyeloc.getBlock())) {
            block.setType(Material.WATER);
            block.setData((byte)0);
            Torrent tor = new Torrent(player);
            if (tor.sourceselected || tor.settingup) {
                WaterReturn.emptyWaterBottle(player);
            } else {
                block.setType(Material.AIR);
            }
        }
    }

    private void use() {
        this.launch = true;
        if (this.launching) {
            this.freeze = true;
        }
    }

    private void deflect(Entity entity) {
        if (entity.getEntityId() == this.player.getEntityId()) {
            return;
        }
        double angle = 50.0;
        angle = Math.toRadians(angle);
        double x = entity.getLocation().getX() - this.player.getLocation().getX();
        double z = entity.getLocation().getZ() - this.player.getLocation().getZ();
        double mag = Math.sqrt(x * x + z * z);
        double vx = (x * Math.cos(angle) - z * Math.sin(angle)) / mag;
        double vz = (x * Math.sin(angle) + z * Math.cos(angle)) / mag;
        Vector vec = new Vector(vx, 0.0, vz).normalize().multiply(factor);
        Vector velocity = entity.getVelocity();
        if (AvatarState.isAvatarState(this.player)) {
            velocity.setX(AvatarState.getValue(vec.getX()));
            velocity.setZ(AvatarState.getValue(vec.getZ()));
        } else {
            velocity.setX(vec.getX());
            velocity.setZ(vec.getY());
        }
        GeneralMethods.setVelocity(entity, velocity);
        entity.setFallDistance(0.0f);
        if (entity instanceof LivingEntity) {
            World world = this.player.getWorld();
            int damagedealt = this.deflectdamage;
            if (WaterMethods.isNight(world)) {
                damagedealt = (int)(WaterMethods.getWaterbendingNightAugment(world) * (double)this.deflectdamage);
            }
            GeneralMethods.damageEntity(this.player, entity, damagedealt, "Torrent");
            AirMethods.breakBreathbendingHold(entity);
        }
    }

    private void affect(Entity entity, Vector direction) {
        if (entity.getEntityId() == this.player.getEntityId()) {
            return;
        }
        if (direction.getY() > ylimit) {
            direction.setY(ylimit);
        }
        if (!this.freeze) {
            entity.setVelocity(direction.multiply(factor));
        }
        if (entity instanceof LivingEntity && !this.hurtentities.contains((Object)entity)) {
            World world = this.player.getWorld();
            int damagedealt = this.damage;
            if (WaterMethods.isNight(world)) {
                damagedealt = (int)(WaterMethods.getWaterbendingNightAugment(world) * (double)this.damage);
            }
            GeneralMethods.damageEntity(this.player, entity, damagedealt, "Torrent");
            AirMethods.breakBreathbendingHold(entity);
            this.hurtentities.add(entity);
            ((LivingEntity)entity).setNoDamageTicks(0);
        }
    }

    public static void progressAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).progress();
        }
        for (TempBlock block : frozenblocks.keySet()) {
            Player player2 = frozenblocks.get(block);
            if (block.getBlock().getType() != Material.ICE) {
                frozenblocks.remove(block);
                continue;
            }
            if (block.getBlock().getWorld() != player2.getWorld()) {
                Torrent.thaw(block);
                continue;
            }
            if (block.getLocation().distance(player2.getLocation()) <= RANGE && GeneralMethods.canBend(player2.getName(), "Torrent")) continue;
            Torrent.thaw(block);
        }
    }

    public static void thaw(Block block) {
        TempBlock tblock;
        if (TempBlock.isTempBlock(block) && frozenblocks.containsKey(tblock = TempBlock.get(block))) {
            Torrent.thaw(tblock);
        }
    }

    public static void thaw(TempBlock block) {
        block.revertBlock();
        frozenblocks.remove(block);
    }

    public static boolean canThaw(Block block) {
        if (TempBlock.isTempBlock(block)) {
            TempBlock tblock = TempBlock.get(block);
            return !frozenblocks.containsKey(tblock);
        }
        return true;
    }

    public static void removeAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).remove();
        }
        for (TempBlock block : frozenblocks.keySet()) {
            Torrent.thaw(block);
        }
    }

    public static boolean wasBrokenFor(Player player, Block block) {
        if (instances.containsKey((Object)player)) {
            Torrent torrent = instances.get((Object)player);
            if (torrent.sourceblock == null) {
                return false;
            }
            if (torrent.sourceblock.equals((Object)block)) {
                return true;
            }
        }
        return false;
    }

    public static String getDescription() {
        return "Torrent is one of the strongest moves in a waterbender's arsenal. To use, first click a source block to select it; then hold shift to begin streaming the water around you. Water flowing around you this way will damage and knock back nearby enemies and projectiles. If you release shift during this, you will create a large wave that expands outwards from you, launching anything in its path back. Instead, if you click you release the water and channel it to flow towards your cursor. Anything caught in the blast will be tossed about violently and take damage. Finally, if you click again when the water is torrenting, it will freeze the area around it when it is obstructed.";
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getDamage() {
        return this.damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDeflectdamage() {
        return this.deflectdamage;
    }

    public void setDeflectdamage(int deflectdamage) {
        this.deflectdamage = deflectdamage;
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
    }
}

