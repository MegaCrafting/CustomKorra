/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.Enflamed;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.util.ParticleEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WallOfFire
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, WallOfFire> instances = new ConcurrentHashMap();
    private static double maxangle = 50.0;
    private static int RANGE = config.get().getInt("Abilities.Fire.WallOfFire.Range");
    private static int HEIGHT = config.get().getInt("Abilities.Fire.WallOfFire.Height");
    private static int WIDTH = config.get().getInt("Abilities.Fire.WallOfFire.Width");
    private static long DURATION = config.get().getLong("Abilities.Fire.WallOfFire.Duration");
    private static int DAMAGE = config.get().getInt("Abilities.Fire.WallOfFire.Damage");
    private static long interval = 250;
    private static long COOLDOWN = config.get().getLong("Abilities.Fire.WallOfFire.Cooldown");
    private static long DAMAGE_INTERVAL = config.get().getLong("Abilities.Fire.WallOfFire.Interval");
    private static double FIRETICKS = config.get().getDouble("Abilities.Fire.WallOfFire.FireTicks");
    private Player player;
    private Location origin;
    private long time;
    private long starttime;
    private boolean active = true;
    private int damagetick = 0;
    private int intervaltick = 0;
    private int range = RANGE;
    private int height = HEIGHT;
    private int width = WIDTH;
    private long duration = DURATION;
    private int damage = DAMAGE;
    private long cooldown = COOLDOWN;
    private long damageinterval = DAMAGE_INTERVAL;
    private List<Block> blocks = new ArrayList<Block>();

    public WallOfFire(Player player) {
        if (instances.containsKey((Object)player) && !AvatarState.isAvatarState(player)) {
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("WallOfFire")) {
            return;
        }
        this.player = player;
        this.origin = GeneralMethods.getTargetedLocation(player, this.range);
        World world = player.getWorld();
        if (FireMethods.isDay(player.getWorld())) {
            this.width = (int)FireMethods.getFirebendingDayAugment(this.width, world);
            this.height = (int)FireMethods.getFirebendingDayAugment(this.height, world);
            this.duration = (long)FireMethods.getFirebendingDayAugment(this.duration, world);
            this.damage = (int)FireMethods.getFirebendingDayAugment(this.damage, world);
        }
        this.starttime = this.time = System.currentTimeMillis();
        Block block = this.origin.getBlock();
        if (block.isLiquid() || GeneralMethods.isSolid(block)) {
            return;
        }
        Vector direction = player.getEyeLocation().getDirection();
        Vector compare = direction.clone();
        compare.setY(0);
        if ((double)Math.abs(direction.angle(compare)) > Math.toRadians(maxangle)) {
            return;
        }
        this.initializeBlocks();
        instances.put(player, this);
        bPlayer.addCooldown("WallOfFire", this.cooldown);
    }

    private void affect(Entity entity) {
        entity.setFireTicks((int)(FIRETICKS * 20.0));
        GeneralMethods.setVelocity(entity, new Vector(0, 0, 0));
        if (entity instanceof LivingEntity) {
            GeneralMethods.damageEntity(this.player, entity, this.damage, "WallOfFire");
            new com.projectkorra.projectkorra.firebending.Enflamed(entity, this.player);
            AirMethods.breakBreathbendingHold(entity);
        }
    }

    private void damage() {
        List<Entity> entities;
        double radius = this.height;
        if (radius < (double)this.width) {
            radius = this.width;
        }
        if ((entities = GeneralMethods.getEntitiesAroundPoint(this.origin, radius += 1.0)).contains((Object)this.player)) {
            entities.remove((Object)this.player);
        }
        block0 : for (Entity entity : entities) {
            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "WallOfFire", entity.getLocation())) continue;
            for (Block block : this.blocks) {
                if (entity.getLocation().distance(block.getLocation()) > 1.5) continue;
                this.affect(entity);
                continue block0;
            }
        }
    }

    private void display() {
        for (Block block : this.blocks) {
            ParticleEffect.FLAME.display(block.getLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 3);
            ParticleEffect.SMOKE_NORMAL.display(block.getLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 1);
            if (GeneralMethods.rand.nextInt(7) != 0) continue;
            FireMethods.playFirebendingSound(block.getLocation());
        }
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public int getDamage() {
        return this.damage;
    }

    public long getDamageinterval() {
        return this.damageinterval;
    }

    public long getDuration() {
        return this.duration;
    }

    public int getHeight() {
        return this.height;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getRange() {
        return this.range;
    }

    public int getWidth() {
        return this.width;
    }

    private void initializeBlocks() {
        Vector direction = this.player.getEyeLocation().getDirection();
        direction = direction.normalize();
        Vector ortholr = GeneralMethods.getOrthogonalVector(direction, 0.0, 1.0);
        ortholr = ortholr.normalize();
        Vector orthoud = GeneralMethods.getOrthogonalVector(direction, 90.0, 1.0);
        orthoud = orthoud.normalize();
        double w = this.width;
        double h = this.height;
        double i = - w;
        while (i <= w) {
            double j = - h;
            while (j <= h) {
                Block block;
                Location location = this.origin.clone().add(orthoud.clone().multiply(j));
                if (!GeneralMethods.isRegionProtectedFromBuild(this.player, "WallOfFire", location = location.add(ortholr.clone().multiply(i))) && !this.blocks.contains((Object)(block = location.getBlock()))) {
                    this.blocks.add(block);
                }
                j += 1.0;
            }
            i += 1.0;
        }
    }

    public boolean progress() {
        this.time = System.currentTimeMillis();
        if (this.time - this.starttime > this.cooldown) {
            this.remove();
            return false;
        }
        if (!this.active) {
            return false;
        }
        if (this.time - this.starttime > this.duration) {
            this.active = false;
            return false;
        }
        if (this.time - this.starttime > (long)this.intervaltick * interval) {
            ++this.intervaltick;
            this.display();
        }
        if (this.time - this.starttime > (long)this.damagetick * this.damageinterval) {
            ++this.damagetick;
            this.damage();
        }
        return true;
    }

    public static void progressAll() {
        for (WallOfFire ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (WallOfFire ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        RANGE = config.get().getInt("Abilities.Fire.WallOfFire.Range");
        HEIGHT = config.get().getInt("Abilities.Fire.WallOfFire.Height");
        WIDTH = config.get().getInt("Abilities.Fire.WallOfFire.Width");
        DURATION = config.get().getLong("Abilities.Fire.WallOfFire.Duration");
        DAMAGE = config.get().getInt("Abilities.Fire.WallOfFire.Damage");
        COOLDOWN = config.get().getLong("Abilities.Fire.WallOfFire.Cooldown");
        DAMAGE_INTERVAL = config.get().getLong("Abilities.Fire.WallOfFire.Interval");
        FIRETICKS = config.get().getDouble("Abilities.Fire.WallOfFire.FireTicks");
        this.range = RANGE;
        this.height = HEIGHT;
        this.width = WIDTH;
        this.duration = DURATION;
        this.damage = DAMAGE;
        this.cooldown = COOLDOWN;
        this.damageinterval = DAMAGE_INTERVAL;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
        if (this.player != null) {
            GeneralMethods.getBendingPlayer(this.player.getName()).addCooldown("WallOfFire", cooldown);
        }
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setDamageinterval(long damageinterval) {
        this.damageinterval = damageinterval;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}

