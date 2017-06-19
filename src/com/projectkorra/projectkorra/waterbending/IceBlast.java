/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.util.TempPotionEffect;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterReturn;
import com.projectkorra.projectkorra.util.ClickType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class IceBlast {
    public static ConcurrentHashMap<Integer, IceBlast> instances = new ConcurrentHashMap();
    private static double defaultrange = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.IceBlast.Range");
    private static int DAMAGE = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.IceBlast.Damage");
    private static int COOLDOWN = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.IceBlast.Cooldown");
    private static int ID = Integer.MIN_VALUE;
    private static final long interval = 20;
    private static final byte data = 0;
    private static final double affectingradius = 2.0;
    private static final double deflectrange = 3.0;
    private int id;
    private double range;
    private boolean prepared = false;
    private boolean settingup = false;
    private boolean progressing = false;
    private long time;
    private Location location;
    private Location firstdestination;
    private Location destination;
    private Block sourceblock;
    private Player player;
    public TempBlock source;
    private double defaultdamage = DAMAGE;
    private long cooldown = COOLDOWN;

    public IceBlast(Player player) {
        if (!WaterMethods.canIcebend(player)) {
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("IceBlast")) {
            return;
        }
        IceBlast.block(player);
        this.range = WaterMethods.waterbendingNightAugment(defaultrange, player.getWorld());
        this.player = player;
        Block sourceblock = BlockSource.getWaterSourceBlock(player, this.range, ClickType.SHIFT_DOWN, false, true, false);
        if (sourceblock == null) {
            return;
        }
        if (TempBlock.isTempBlock(sourceblock)) {
            return;
        }
        this.prepare(sourceblock);
    }

    private void prepare(Block block) {
        for (IceBlast ice : IceBlast.getInstances(this.player)) {
            if (!ice.prepared) continue;
            ice.cancel();
        }
        this.sourceblock = block;
        this.location = this.sourceblock.getLocation();
        this.prepared = true;
        if (IceBlast.getInstances(this.player).isEmpty()) {
            this.createInstance();
        }
    }

    private void createInstance() {
        this.id = ID++;
        instances.put(this.id, this);
        if (ID >= Integer.MAX_VALUE) {
            ID = Integer.MIN_VALUE;
        }
    }

    private static ArrayList<IceBlast> getInstances(Player player) {
        ArrayList<IceBlast> list = new ArrayList<IceBlast>();
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            IceBlast ice = instances.get(id);
            if (!ice.player.equals((Object)player)) continue;
            list.add(ice);
        }
        return list;
    }

    private static void block(Player player) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            IceBlast ice = instances.get(id);
            if (ice.player.equals((Object)player) || !ice.location.getWorld().equals((Object)player.getWorld()) || !ice.progressing || GeneralMethods.isRegionProtectedFromBuild(player, "IceBlast", ice.location)) continue;
            Location location = player.getEyeLocation();
            Vector vector = location.getDirection();
            Location mloc = ice.location;
            if (mloc.distance(location) > defaultrange || GeneralMethods.getDistanceFromLine(vector, location, ice.location) >= 3.0 || mloc.distance(location.clone().add(vector)) >= mloc.distance(location.clone().add(vector.clone().multiply(-1)))) continue;
            ice.cancel();
        }
    }

    public static void activate(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("IceBlast")) {
            return;
        }
        for (IceBlast ice : IceBlast.getInstances(player)) {
            if (!ice.prepared) continue;
            ice.throwIce();
        }
    }

    private void cancel() {
        BendingPlayer bPlayer;
        if (this.progressing) {
            if (this.source != null) {
                this.source.revertBlock();
            }
            this.progressing = false;
        }
        if ((bPlayer = GeneralMethods.getBendingPlayer(this.player.getName())) != null) {
            bPlayer.addCooldown("IceBlast", this.cooldown);
        }
        instances.remove(this.id);
    }

    private void returnWater() {
        new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, this.sourceblock);
    }

    public static void removeAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            instances.get(id).cancel();
        }
        instances.clear();
    }

    private void affect(LivingEntity entity) {
        int damage = (int)WaterMethods.waterbendingNightAugment(this.defaultdamage, this.player.getWorld());
        if (entity instanceof Player) {
            BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(this.player.getName());
            if (bPlayer.canBeSlowed()) {
                PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 70, 2);
                new com.projectkorra.projectkorra.util.TempPotionEffect(entity, effect);
                bPlayer.slow(10);
                GeneralMethods.damageEntity(this.player, (Entity)entity, damage, "IceBlast");
            }
        } else {
            PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 70, 2);
            new com.projectkorra.projectkorra.util.TempPotionEffect(entity, effect);
            GeneralMethods.damageEntity(this.player, (Entity)entity, damage, "IceBlast");
        }
        AirMethods.breakBreathbendingHold((Entity)entity);
        int x = 0;
        while (x < 30) {
            ParticleEffect.ITEM_CRACK.display((ParticleEffect.ParticleData)new ParticleEffect.ItemData(Material.ICE, (byte) 0), new Vector((Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5), 0.3f, this.location, 257.0);
            ++x;
        }
    }

    private void throwIce() {
        if (!this.prepared) {
            return;
        }
        LivingEntity target = (LivingEntity)GeneralMethods.getTargetedEntity(this.player, this.range, new ArrayList<Entity>());
        this.destination = target == null ? GeneralMethods.getTargetedLocation(this.player, this.range, EarthMethods.transparentToEarthbending) : target.getEyeLocation();
        this.location = this.sourceblock.getLocation();
        if (this.destination.distance(this.location) < 1.0) {
            return;
        }
        this.firstdestination = this.location.clone();
        if (this.destination.getY() - this.location.getY() > 2.0) {
            this.firstdestination.setY(this.destination.getY() - 1.0);
        } else {
            this.firstdestination.add(0.0, 2.0, 0.0);
        }
        this.destination = GeneralMethods.getPointOnLine(this.firstdestination, this.destination, this.range);
        this.progressing = true;
        this.settingup = true;
        this.prepared = false;
        new com.projectkorra.projectkorra.util.TempBlock(this.sourceblock, Material.AIR, (byte)0);
        this.source = new TempBlock(this.sourceblock, Material.PACKED_ICE, (byte)0);
    }

    private void progress() {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(this.player.getName());
        if (this.player.isDead() || !this.player.isOnline() || !GeneralMethods.canBend(this.player.getName(), "IceBlast") || bPlayer.isOnCooldown("IceBlast")) {
            this.cancel();
            return;
        }
        if (!this.player.getWorld().equals((Object)this.location.getWorld())) {
            this.cancel();
            return;
        }
        if (this.player.getEyeLocation().distance(this.location) >= this.range) {
            if (this.progressing) {
                this.breakParticles(20);
                this.cancel();
                this.returnWater();
            } else {
                this.breakParticles(20);
                this.cancel();
            }
            return;
        }
        if ((GeneralMethods.getBoundAbility(this.player) == null || !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("IceBlast")) && this.prepared) {
            this.cancel();
            return;
        }
        if (System.currentTimeMillis() < this.time + 20) {
            return;
        }
        this.time = System.currentTimeMillis();
        if (this.progressing) {
            if (this.location.getBlockY() == this.firstdestination.getBlockY()) {
                this.settingup = false;
            }
            if (this.location.distance(this.destination) <= 2.0) {
                this.cancel();
                this.returnWater();
                return;
            }
            Vector direction = this.settingup ? GeneralMethods.getDirection(this.location, this.firstdestination).normalize() : GeneralMethods.getDirection(this.location, this.destination).normalize();
            this.location.add(direction);
            Block block = this.location.getBlock();
            if (block.equals((Object)this.sourceblock)) {
                return;
            }
            this.source.revertBlock();
            this.source = null;
            if (EarthMethods.isTransparentToEarthbending(this.player, block) && !block.isLiquid()) {
                GeneralMethods.breakBlock(block);
            } else if (!WaterMethods.isWater(block)) {
                this.breakParticles(20);
                this.cancel();
                this.returnWater();
                return;
            }
            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "IceBlast", this.location)) {
                this.cancel();
                this.returnWater();
                return;
            }
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, 2.0)) {
                if (entity.getEntityId() == this.player.getEntityId() || !(entity instanceof LivingEntity)) continue;
                this.affect((LivingEntity)entity);
                this.progressing = false;
                this.returnWater();
            }
            if (!this.progressing) {
                this.cancel();
                return;
            }
            this.sourceblock = block;
            this.source = new TempBlock(this.sourceblock, Material.PACKED_ICE, (byte)0);
            int x = 0;
            while (x < 10) {
                ParticleEffect.ITEM_CRACK.display((ParticleEffect.ParticleData)new ParticleEffect.ItemData(Material.ICE, (byte)0), new Vector((Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5), 0.5f, this.location, 257.0);
                ParticleEffect.SNOW_SHOVEL.display(this.location, (float)(Math.random() - 0.5), (float)(Math.random() - 0.5), (float)(Math.random() - 0.5), 0.0f, 5);
                ++x;
            }
            if (GeneralMethods.rand.nextInt(4) == 0) {
                WaterMethods.playIcebendingSound(this.location);
            }
            this.location = this.location.add(direction.clone());
        } else if (this.prepared) {
            WaterMethods.playFocusWaterEffect(this.sourceblock);
        }
    }

    public static void progressAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            instances.get(id).progress();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getDefaultdamage() {
        return this.defaultdamage;
    }

    public void setDefaultdamage(double defaultdamage) {
        this.defaultdamage = defaultdamage;
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void breakParticles(int amount) {
        int x = 0;
        while (x < amount) {
            ParticleEffect.ITEM_CRACK.display((ParticleEffect.ParticleData)new ParticleEffect.ItemData(Material.ICE, (byte) 0), new Vector((Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5, (Math.random() - 0.5) * 0.5), 2.0f, this.location, 257.0);
            ParticleEffect.SNOW_SHOVEL.display(this.location, (float)Math.random(), (float)Math.random(), (float)Math.random(), 0.0f, 2);
            ++x;
        }
        this.location.getWorld().playSound(this.location, Sound.BLOCK_GLASS_PLACE, 5.0f, 1.3f);
    }
}

