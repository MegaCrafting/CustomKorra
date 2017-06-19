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
package com.projectkorra.projectkorra.earthbending;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.earthbending.EarthPassive;
import com.projectkorra.projectkorra.firebending.Combustion;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.Information;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class EarthBlast {
    public static ConcurrentHashMap<Integer, EarthBlast> instances = new ConcurrentHashMap();
    private static boolean hitself = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Earth.EarthBlast.CanHitSelf");
    private static double preparerange = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.EarthBlast.PrepareRange");
    private static double RANGE = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.EarthBlast.Range");
    private static double DAMAGE = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.EarthBlast.Damage");
    private static double speed = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.EarthBlast.Speed");
    private static final double deflectrange = 3.0;
    private static boolean revert = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Earth.EarthBlast.Revert");
    private static double PUSH_FACTOR = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.EarthBlast.Push");
    private static long interval = (long)(1000.0 / speed);
    private static int ID = Integer.MIN_VALUE;
    private Player player;
    private int id;
    private Location location = null;
    private Block sourceblock = null;
    private Material sourcetype = null;
    private boolean progressing = false;
    private Location destination = null;
    private Location firstdestination = null;
    private boolean falling = false;
    private long time;
    private boolean settingup = true;
    private double range = RANGE;
    private double damage = DAMAGE;
    private double pushfactor = PUSH_FACTOR;

    public EarthBlast(Player player) {
        this.player = player;
        if (this.prepare()) {
            this.id = ID++;
            if (ID >= Integer.MAX_VALUE) {
                ID = Integer.MIN_VALUE;
            }
            instances.put(this.id, this);
            this.time = System.currentTimeMillis();
        }
    }

    public boolean prepare() {
        this.cancelPrevious();
        Block block = BlockSource.getEarthSourceBlock(this.player, this.range, ClickType.SHIFT_DOWN);
        EarthBlast.block(this.player);
        if (block != null) {
            if (block.getLocation().distance(this.player.getLocation()) > preparerange) {
                return false;
            }
            this.sourceblock = block;
            this.focusBlock();
            return true;
        }
        return false;
    }

    private static Location getTargetLocation(Player player) {
        Entity target = GeneralMethods.getTargetedEntity(player, RANGE, new ArrayList<Entity>());
        Location location = target == null ? GeneralMethods.getTargetedLocation(player, RANGE, new Integer[0]) : ((LivingEntity)target).getEyeLocation();
        return location;
    }

    private void cancelPrevious() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            EarthBlast blast = instances.get(id);
            if (blast.player != this.player || blast.progressing) continue;
            blast.cancel();
        }
    }

    public void cancel() {
        this.unfocusBlock();
    }

    private void focusBlock() {
        if (EarthPassive.isPassiveSand(this.sourceblock)) {
            EarthPassive.revertSand(this.sourceblock);
        }
        if (this.sourceblock.getType() == Material.SAND) {
            this.sourcetype = Material.SAND;
            if (this.sourceblock.getData() == 1) {
                this.sourceblock.setType(Material.RED_SANDSTONE);
            } else {
                this.sourceblock.setType(Material.SANDSTONE);
            }
        } else if (this.sourceblock.getType() == Material.STONE) {
            this.sourceblock.setType(Material.COBBLESTONE);
            this.sourcetype = Material.STONE;
        } else {
            this.sourcetype = this.sourceblock.getType();
            this.sourceblock.setType(Material.STONE);
        }
        this.location = this.sourceblock.getLocation();
    }

    private void unfocusBlock() {
        if (this.destination != null) {
            this.breakBlock();
            return;
        }
		if (sourceblock.getType() == Material.SAND) {
			if (sourceblock.getData() == (byte) 0x1) {
				sourceblock.setType(sourcetype);
				sourceblock.setData((byte) 0x1);
			}
			else{
				sourceblock.setType(sourcetype);
			}
		}
		else {
			sourceblock.setType(sourcetype);
		}
		instances.remove(id);
}

    public void throwEarth() {
        if (this.sourceblock != null && this.sourceblock.getWorld().equals((Object)this.player.getWorld())) {
            Entity target;
            if (EarthMethods.movedearth.containsKey((Object)this.sourceblock) && !revert) {
                EarthMethods.removeRevertIndex(this.sourceblock);
            }
            if ((target = GeneralMethods.getTargetedEntity(this.player, this.range, new ArrayList<Entity>())) == null) {
                this.destination = this.player.getTargetBlock(EarthMethods.getTransparentEarthbending(), (int)this.range).getLocation();
                this.firstdestination = this.sourceblock.getLocation().clone();
                this.firstdestination.setY(this.destination.getY());
            } else {
                this.destination = ((LivingEntity)target).getEyeLocation();
                this.firstdestination = this.sourceblock.getLocation().clone();
                this.firstdestination.setY(this.destination.getY());
                this.destination = GeneralMethods.getPointOnLine(this.firstdestination, this.destination, this.range);
            }
            if (this.destination.distance(this.location) <= 1.0) {
                this.progressing = false;
                this.destination = null;
            } else {
                this.progressing = true;
                EarthMethods.playEarthbendingSound(this.sourceblock.getLocation());
                if (this.sourcetype != Material.SAND && this.sourcetype != Material.GRAVEL) {
                    this.sourceblock.setType(this.sourcetype);
                }
            }
        }
    }

    public static EarthBlast getBlastFromSource(Block block) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            EarthBlast blast = instances.get(id);
            if (!blast.sourceblock.equals((Object)block)) continue;
            return blast;
        }
        return null;
    }

    public static void progressAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int ID = (Integer)iterator.next();
            instances.get(ID).progress();
        }
    }

    private boolean progress() {
        if (this.player.isDead() || !this.player.isOnline() || !GeneralMethods.canBend(this.player.getName(), "EarthBlast")) {
            this.breakBlock();
            return false;
        }
        if (System.currentTimeMillis() - this.time >= interval) {
            this.time = System.currentTimeMillis();
            if (this.falling) {
                this.breakBlock();
                return false;
            }
            if (!EarthMethods.isEarthbendable(this.player, this.sourceblock) && this.sourceblock.getType() != Material.COBBLESTONE) {
                instances.remove(this.id);
                return false;
            }
            if (!this.progressing && !this.falling) {
                if (GeneralMethods.getBoundAbility(this.player) == null) {
                    this.unfocusBlock();
                    return false;
                }
                if (!GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("EarthBlast")) {
                    this.unfocusBlock();
                    return false;
                }
                if (this.sourceblock == null) {
                    instances.remove(this.id);
                    return false;
                }
                if (!this.player.getWorld().equals((Object)this.sourceblock.getWorld())) {
                    this.unfocusBlock();
                    return false;
                }
                if (this.sourceblock.getLocation().distance(this.player.getLocation()) > preparerange) {
                    this.unfocusBlock();
                    return false;
                }
            }
            if (this.falling) {
                this.breakBlock();
            } else {
                if (!this.progressing) {
                    return false;
                }
                if (this.sourceblock.getY() == this.firstdestination.getBlockY()) {
                    this.settingup = false;
                }
                Vector direction = this.settingup ? GeneralMethods.getDirection(this.location, this.firstdestination).normalize() : GeneralMethods.getDirection(this.location, this.destination).normalize();
                this.location = this.location.clone().add(direction);
                WaterMethods.removeWaterSpouts(this.location, this.player);
                AirMethods.removeAirSpouts(this.location, this.player);
                Block block = this.location.getBlock();
                if (block.getLocation().equals((Object)this.sourceblock.getLocation())) {
                    this.location = this.location.clone().add(direction);
                    block = this.location.getBlock();
                }
                if (EarthMethods.isTransparentToEarthbending(this.player, block) && !block.isLiquid()) {
                    GeneralMethods.breakBlock(block);
                } else {
                    if (!this.settingup) {
                        this.breakBlock();
                        return false;
                    }
                    this.location = this.location.clone().subtract(direction);
                    direction = GeneralMethods.getDirection(this.location, this.destination).normalize();
                    this.location = this.location.clone().add(direction);
                    WaterMethods.removeWaterSpouts(this.location, this.player);
                    AirMethods.removeAirSpouts(this.location, this.player);
                    double radius = FireBlast.AFFECTING_RADIUS;
                    Player source = this.player;
                    if (EarthBlast.annihilateBlasts(this.location, radius, source) || WaterManipulation.annihilateBlasts(this.location, radius, source) || FireBlast.annihilateBlasts(this.location, radius, source)) {
                        this.breakBlock();
                        return false;
                    }
                    Combustion.removeAroundPoint(this.location, radius);
                    Block block2 = this.location.getBlock();
                    if (block2.getLocation().equals((Object)this.sourceblock.getLocation())) {
                        this.location = this.location.clone().add(direction);
                        block2 = this.location.getBlock();
                    }
                    if (EarthMethods.isTransparentToEarthbending(this.player, block) && !block.isLiquid()) {
                        GeneralMethods.breakBlock(block);
                    } else {
                        this.breakBlock();
                        return false;
                    }
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.location, FireBlast.AFFECTING_RADIUS)) {
                    if (GeneralMethods.isRegionProtectedFromBuild(this.player, "EarthBlast", entity.getLocation()) || !(entity instanceof LivingEntity) || entity.getEntityId() == this.player.getEntityId() && !hitself) continue;
                    AirMethods.breakBreathbendingHold(entity);
                    Location location = this.player.getEyeLocation();
                    Vector vector = location.getDirection();
                    entity.setVelocity(vector.normalize().multiply(this.pushfactor));
                    double damage = this.damage;
                    if (EarthMethods.isMetal(this.sourceblock) && EarthMethods.canMetalbend(this.player)) {
                        damage = EarthMethods.getMetalAugment(this.damage);
                    }
                    GeneralMethods.damageEntity(this.player, entity, damage, "EarthBlast");
                    this.progressing = false;
                }
                if (!this.progressing) {
                    this.breakBlock();
                    return false;
                }
                if (revert) {
                	if (sourceblock.getType() == Material.RED_SANDSTONE) {
						sourceblock.setType(sourcetype);
						if(sourcetype == Material.SAND)
							sourceblock.setData((byte) 0x1);
					}
					else {
						sourceblock.setType(sourcetype);
}
                    EarthMethods.moveEarthBlock(this.sourceblock, block);
                    if (block.getType() == Material.SAND) {
                        block.setType(Material.SANDSTONE);
                    }
                    if (block.getType() == Material.GRAVEL) {
                        block.setType(Material.STONE);
                    }
                } else {
                    block.setType(this.sourceblock.getType());
                    this.sourceblock.setType(Material.AIR);
                }
                this.sourceblock = block;
                if (this.location.distance(this.destination) < 1.0) {
                    if (this.sourcetype == Material.SAND || this.sourcetype == Material.GRAVEL) {
                        this.progressing = false;
                        if (this.sourceblock.getType() == Material.RED_SANDSTONE) {
                            this.sourcetype = Material.SAND;
                            this.sourceblock.setType(this.sourcetype);
                            sourceblock.setData((byte) 0x1);
                        } else {
                            this.sourceblock.setType(this.sourcetype);
                        }
                    }
                    this.falling = true;
                    this.progressing = false;
                }
                return true;
            }
        }
        return false;
    }

    private void breakBlock() {
        this.sourceblock.setType(this.sourcetype);
        if (revert) {
            EarthMethods.addTempAirBlock(this.sourceblock);
        } else {
            this.sourceblock.breakNaturally();
        }
        instances.remove(this.id);
    }

    public static void throwEarth(Player player) {
        ArrayList<EarthBlast> ignore = new ArrayList<EarthBlast>();
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("EarthBlast")) {
            return;
        }
        boolean cooldown = false;
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            EarthBlast blast = instances.get(id);
            if (blast.player != player || blast.progressing) continue;
            blast.throwEarth();
            cooldown = true;
            ignore.add(blast);
        }
        if (cooldown) {
            bPlayer.addCooldown("EarthBlast", GeneralMethods.getGlobalCooldown());
        }
        EarthBlast.redirectTargettedBlasts(player, ignore);
    }

    public static void removeAll() {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            instances.get(id).breakBlock();
        }
    }

    private static void redirectTargettedBlasts(Player player, ArrayList<EarthBlast> ignore) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            EarthBlast blast = instances.get(id);
            if (!blast.progressing || ignore.contains(blast) || !blast.location.getWorld().equals((Object)player.getWorld()) || GeneralMethods.isRegionProtectedFromBuild(player, "EarthBlast", blast.location)) continue;
            if (blast.player.equals((Object)player)) {
                blast.redirect(player, EarthBlast.getTargetLocation(player));
            }
            Location location = player.getEyeLocation();
            Vector vector = location.getDirection();
            Location mloc = blast.location;
            if (mloc.distance(location) > RANGE || GeneralMethods.getDistanceFromLine(vector, location, blast.location) >= 3.0 || mloc.distance(location.clone().add(vector)) >= mloc.distance(location.clone().add(vector.clone().multiply(-1)))) continue;
            blast.redirect(player, EarthBlast.getTargetLocation(player));
        }
    }

    private void redirect(Player player, Location targetlocation) {
        if (this.progressing && this.location.distance(player.getLocation()) <= this.range) {
            this.settingup = false;
            this.destination = targetlocation;
        }
    }

    private static void block(Player player) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            EarthBlast blast = instances.get(id);
            if (blast.player.equals((Object)player) || !blast.location.getWorld().equals((Object)player.getWorld()) || !blast.progressing || GeneralMethods.isRegionProtectedFromBuild(player, "EarthBlast", blast.location)) continue;
            Location location = player.getEyeLocation();
            Vector vector = location.getDirection();
            Location mloc = blast.location;
            if (mloc.distance(location) > RANGE || GeneralMethods.getDistanceFromLine(vector, location, blast.location) >= 3.0 || mloc.distance(location.clone().add(vector)) >= mloc.distance(location.clone().add(vector.clone().multiply(-1)))) continue;
            blast.breakBlock();
        }
    }

    public static String getDescription() {
        return "To use, place your cursor over an earthbendable object (dirt, rock, ores, etc) and tap sneak (default: shift). The object will temporarily turn to stone, indicating that you have it focused as the source for your ability. After you have selected an origin (you no longer need to be sneaking), simply left-click in any direction and you will see your object launch off in that direction, smashing into any creature in its path. If you look towards a creature when you use this ability, it will target that creature. A collision from Earth Blast both knocks the target back and deals some damage. You cannot have multiple of these abilities flying at the same time.";
    }

    public static void removeAroundPoint(Location location, double radius) {
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            EarthBlast blast = instances.get(id);
            if (!blast.location.getWorld().equals((Object)location.getWorld()) || blast.location.distance(location) > radius) continue;
            blast.breakBlock();
        }
    }

    public static ArrayList<EarthBlast> getAroundPoint(Location location, double radius) {
        ArrayList<EarthBlast> list = new ArrayList<EarthBlast>();
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            EarthBlast blast = instances.get(id);
            if (!blast.location.getWorld().equals((Object)location.getWorld()) || blast.location.distance(location) > radius) continue;
            list.add(blast);
        }
        return list;
    }

    public static boolean annihilateBlasts(Location location, double radius, Player source) {
        boolean broke = false;
        Iterator iterator = instances.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            EarthBlast blast = instances.get(id);
            if (!blast.location.getWorld().equals((Object)location.getWorld()) || source.equals((Object)blast.player) || blast.location.distance(location) > radius) continue;
            blast.breakBlock();
            broke = true;
        }
        return broke;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getPushfactor() {
        return this.pushfactor;
    }

    public void setPushfactor(double pushfactor) {
        this.pushfactor = pushfactor;
    }
}

