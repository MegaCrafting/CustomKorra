/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
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

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.earthbending.EarthMethods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Ripple {
    public static ConcurrentHashMap<Integer, Ripple> instances = new ConcurrentHashMap();
    private static ConcurrentHashMap<Integer[], Block> blocks = new ConcurrentHashMap();
    static final double RADIUS = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Shockwave.Range");
    private static final double DAMAGE = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Shockwave.Damage");
    private static int ID = Integer.MIN_VALUE;
    private static double KNOCKBACK = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.Shockwave.Knockback");
    private Player player;
    private Vector direction;
    private Location origin;
    private Location location;
    private Block block1;
    private Block block2;
    private Block block3;
    private Block block4;
    private int id;
    private int step = 0;
    private int maxstep;
    private double radius = RADIUS;
    private double damage = DAMAGE;
    private double knockback = KNOCKBACK;
    private ArrayList<Location> locations = new ArrayList();
    private ArrayList<Entity> entities = new ArrayList();

    public Ripple(Player player, Vector direction) {
        this(player, Ripple.getInitialLocation(player, direction), direction);
    }

    public Ripple(Player player, Location origin, Vector direction) {
        this.player = player;
        if (origin == null) {
            return;
        }
        this.direction = direction.clone().normalize();
        this.origin = origin.clone();
        this.location = origin.clone();
        this.initializeLocations();
        this.maxstep = this.locations.size();
        if (EarthMethods.isEarthbendable(player, origin.getBlock())) {
            this.id = ID++;
            if (ID >= Integer.MAX_VALUE) {
                ID = Integer.MIN_VALUE;
            }
            instances.put(this.id, this);
        }
    }

    private static Location getInitialLocation(Player player, Vector direction) {
        Location location = player.getLocation().clone().add(0.0, -1.0, 0.0);
        direction = direction.normalize();
        Block block1 = location.getBlock();
        while (location.getBlock().equals((Object)block1)) {
            location = location.clone().add(direction);
        }
        int[] arrn = new int[5];
        arrn[0] = 1;
        arrn[1] = 2;
        arrn[2] = 3;
        arrn[4] = -1;
        int[] arrn2 = arrn;
        int n = arrn.length;
        int n2 = 0;
        while (n2 < n) {
            int i = arrn2[n2];
            Location loc = location.clone().add(0.0, (double)i, 0.0);
            Block topblock = loc.getBlock();
            Block botblock = loc.clone().add(0.0, -1.0, 0.0).getBlock();
            if (EarthMethods.isTransparentToEarthbending(player, topblock) && EarthMethods.isEarthbendable(player, botblock)) {
                location = loc.clone().add(0.0, -1.0, 0.0);
                return location;
            }
            ++n2;
        }
        return null;
    }

    private void progress() {
        if (this.step < this.maxstep) {
            Location newlocation = this.locations.get(this.step);
            Block block = this.location.getBlock();
            this.location = newlocation.clone();
            if (!newlocation.getBlock().equals((Object)block)) {
                this.block1 = this.block2;
                this.block2 = this.block3;
                this.block3 = this.block4;
                this.block4 = newlocation.getBlock();
                if (this.block1 != null && Ripple.hasAnyMoved(this.block1)) {
                    this.block1 = null;
                }
                if (this.block2 != null && Ripple.hasAnyMoved(this.block2)) {
                    this.block2 = null;
                }
                if (this.block3 != null && Ripple.hasAnyMoved(this.block3)) {
                    this.block3 = null;
                }
                if (this.block4 != null && Ripple.hasAnyMoved(this.block4)) {
                    this.block4 = null;
                }
                if (this.step == 0) {
                    if (this.increase(this.block4)) {
                        this.block4 = this.block4.getRelative(BlockFace.UP);
                    }
                } else if (this.step == 1) {
                    if (this.increase(this.block3)) {
                        this.block3 = this.block3.getRelative(BlockFace.UP);
                    }
                    if (this.increase(this.block4)) {
                        this.block4 = this.block4.getRelative(BlockFace.UP);
                    }
                } else if (this.step == 2) {
                    if (this.decrease(this.block2)) {
                        this.block2 = this.block2.getRelative(BlockFace.DOWN);
                    }
                    if (this.increase(this.block3)) {
                        this.block3 = this.block3.getRelative(BlockFace.UP);
                    }
                    if (this.increase(this.block4)) {
                        this.block4 = this.block4.getRelative(BlockFace.UP);
                    }
                } else {
                    if (this.decrease(this.block1)) {
                        this.block1 = this.block1.getRelative(BlockFace.DOWN);
                    }
                    if (this.decrease(this.block2)) {
                        this.block2 = this.block2.getRelative(BlockFace.DOWN);
                    }
                    if (this.increase(this.block3)) {
                        this.block3 = this.block3.getRelative(BlockFace.UP);
                    }
                    if (this.increase(this.block4)) {
                        this.block4 = this.block4.getRelative(BlockFace.UP);
                    }
                }
            }
        } else if (this.step == this.maxstep) {
            if (this.decrease(this.block2)) {
                this.block2 = this.block2.getRelative(BlockFace.DOWN);
            }
            if (this.decrease(this.block3)) {
                this.block3 = this.block3.getRelative(BlockFace.DOWN);
            }
            if (this.increase(this.block4)) {
                this.block4 = this.block4.getRelative(BlockFace.UP);
            }
        } else if (this.step == this.maxstep + 1) {
            if (this.decrease(this.block3)) {
                this.block3 = this.block3.getRelative(BlockFace.DOWN);
            }
            if (this.decrease(this.block4)) {
                this.block4 = this.block4.getRelative(BlockFace.DOWN);
            }
        } else if (this.step == this.maxstep + 2) {
            if (this.decrease(this.block4)) {
                this.block4 = this.block4.getRelative(BlockFace.DOWN);
            }
            this.remove();
        }
        ++this.step;
        for (Entity entity : this.entities) {
            this.affect(entity);
        }
        this.entities.clear();
    }

    private void remove() {
        instances.remove(this.id);
    }

    private void initializeLocations() {
        Location location = this.origin.clone();
        this.locations.add(location);
        block0 : while (location.distance(this.origin) < this.radius) {
            location = location.clone().add(this.direction);
            int[] arrn = new int[5];
            arrn[0] = 1;
            arrn[1] = 2;
            arrn[2] = 3;
            arrn[4] = -1;
            int[] arrn2 = arrn;
            int n = arrn.length;
            int n2 = 0;
            while (n2 < n) {
                int i = arrn2[n2];
                Location loc = location.clone().add(0.0, (double)i, 0.0);
                Block topblock = loc.getBlock();
                Block botblock = loc.clone().add(0.0, -1.0, 0.0).getBlock();
                if (EarthMethods.isTransparentToEarthbending(this.player, topblock) && !topblock.isLiquid() && EarthMethods.isEarthbendable(this.player, botblock)) {
                    location = loc.clone().add(0.0, -1.0, 0.0);
                    this.locations.add(location);
                    continue block0;
                }
                if (i == -1) {
                    return;
                }
                ++n2;
            }
        }
    }

    private boolean decrease(Block block) {
        if (block == null) {
            return false;
        }
        if (Ripple.hasAnyMoved(block)) {
            return false;
        }
        Ripple.setMoved(block);
        Block botblock = block.getRelative(BlockFace.DOWN);
        int length = 1;
        if (EarthMethods.isEarthbendable(this.player, botblock)) {
            length = 2;
            block = botblock;
        }
        return EarthMethods.moveEarth(this.player, block, new Vector(0, -1, 0), length, false);
    }

    private boolean increase(Block block) {
        if (block == null) {
            return false;
        }
        if (Ripple.hasAnyMoved(block)) {
            return false;
        }
        Ripple.setMoved(block);
        Block botblock = block.getRelative(BlockFace.DOWN);
        int length = 1;
        if (EarthMethods.isEarthbendable(this.player, botblock)) {
            length = 2;
        }
        if (EarthMethods.moveEarth(this.player, block, new Vector(0, 1, 0), length, false)) {
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(block.getLocation().clone().add(0.0, 1.0, 0.0), 2.0)) {
                if (entity.getEntityId() == this.player.getEntityId() || this.entities.contains((Object)entity) || entity instanceof FallingBlock) continue;
                this.entities.add(entity);
            }
            return true;
        }
        return false;
    }

    private void affect(Entity entity) {
        if (entity instanceof LivingEntity) {
            GeneralMethods.damageEntity(this.player, entity, this.damage, "Shockwave");
        }
        Vector vector = this.direction.clone();
        vector.setY(0.5);
        double knock = AvatarState.isAvatarState(this.player) ? AvatarState.getValue(this.knockback) : this.knockback;
        entity.setVelocity(vector.clone().normalize().multiply(knock));
        AirMethods.breakBreathbendingHold(entity);
    }

    private static void setMoved(Block block) {
        int x = block.getX();
        int z = block.getZ();
        Integer[] pair = new Integer[]{x, z};
        blocks.put(pair, block);
    }

    private static boolean hasAnyMoved(Block block) {
        int x = block.getX();
        int z = block.getZ();
        Integer[] pair = new Integer[]{x, z};
        if (blocks.containsKey(pair)) {
            return true;
        }
        return false;
    }

    public static void progressAll() {
        blocks.clear();
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            instances.get(id).progress();
        }
    }

    public static void removeAll() {
        instances.clear();
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getKnockback() {
        return this.knockback;
    }

    public void setKnockback(double knockback) {
        this.knockback = knockback;
    }
}

