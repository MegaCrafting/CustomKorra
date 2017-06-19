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
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingManager;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Lightning
implements ConfigLoadable {
    public static ConcurrentHashMap<Integer, Lightning> instances = new ConcurrentHashMap();
    public static boolean SELF_HIT_WATER = config.get().getBoolean("Abilities.Fire.Lightning.SelfHitWater");
    public static boolean SELF_HIT_CLOSE = config.get().getBoolean("Abilities.Fire.Lightning.SelfHitClose");
    public static boolean ARC_ON_ICE = config.get().getBoolean("Abilities.Fire.Lightning.ArcOnIce");
    public static double RANGE = config.get().getDouble("Abilities.Fire.Lightning.Range");
    public static double DAMAGE = config.get().getDouble("Abilities.Fire.Lightning.Damage");
    public static double MAX_ARC_ANGLE = config.get().getDouble("Abilities.Fire.Lightning.MaxArcAngle");
    public static double SUB_ARC_CHANCE = config.get().getDouble("Abilities.Fire.Lightning.SubArcChance");
    public static double CHAIN_ARC_RANGE = config.get().getDouble("Abilities.Fire.Lightning.ChainArcRange");
    public static double CHAIN_ARC_CHANCE = config.get().getDouble("Abilities.Fire.Lightning.ChainArcChance");
    public static double WATER_ARC_RANGE = config.get().getDouble("Abilities.Fire.Lightning.WaterArcRange");
    public static double STUN_CHANCE = config.get().getDouble("Abilities.Fire.Lightning.StunChance");
    public static double STUN_DURATION = config.get().getDouble("Abilities.Fire.Lightning.StunDuration");
    public static int MAX_CHAIN_ARCS = (int)config.get().getDouble("Abilities.Fire.Lightning.MaxChainArcs");
    public static int WATER_ARCS = (int)config.get().getDouble("Abilities.Fire.Lightning.WaterArcs");
    public static long CHARGETIME = (long)config.get().getDouble("Abilities.Fire.Lightning.ChargeTime");
    public static long COOLDOWN = (long)config.get().getDouble("Abilities.Fire.Lightning.Cooldown");
    private static final int POINT_GENERATION = 5;
    private static int idCounter = 0;
    private Player player;
    private BendingPlayer bplayer;
    private Location origin;
    private Location destination;
    private int id;
    private double range;
    private double chargeTime;
    private double cooldown;
    private double subArcChance;
    private double damage;
    private double chainArcs;
    private double chainRange;
    private double waterRange;
    private double chainArcChance;
    private double stunChance;
    private double stunDuration;
    private long time;
    private boolean charged;
    private boolean hitWater;
    private boolean hitIce;
    private State state = State.START;
    private ArrayList<Entity> affectedEntities = new ArrayList();
    private ArrayList<Arc> arcs = new ArrayList();
    private ArrayList<BukkitRunnable> tasks = new ArrayList();
    private double i = 0.0;
    private double newY;

    public Lightning(Player player) {
        this.player = player;
        this.bplayer = GeneralMethods.getBendingPlayer(player.getName());
        this.charged = false;
        this.hitWater = false;
        this.hitIce = false;
        this.time = System.currentTimeMillis();
        this.range = FireMethods.getFirebendingDayAugment(RANGE, player.getWorld());
        this.subArcChance = FireMethods.getFirebendingDayAugment(SUB_ARC_CHANCE, player.getWorld());
        this.damage = FireMethods.getFirebendingDayAugment(DAMAGE, player.getWorld());
        this.chainArcs = FireMethods.getFirebendingDayAugment(MAX_CHAIN_ARCS, player.getWorld());
        this.chainArcChance = FireMethods.getFirebendingDayAugment(CHAIN_ARC_CHANCE, player.getWorld());
        this.chainRange = FireMethods.getFirebendingDayAugment(CHAIN_ARC_RANGE, player.getWorld());
        this.waterRange = FireMethods.getFirebendingDayAugment(WATER_ARC_RANGE, player.getWorld());
        this.stunChance = FireMethods.getFirebendingDayAugment(STUN_CHANCE, player.getWorld());
        this.stunDuration = FireMethods.getFirebendingDayAugment(STUN_DURATION, player.getWorld());
        this.chargeTime = CHARGETIME;
        this.cooldown = COOLDOWN;
        if (AvatarState.isAvatarState(player)) {
            this.chargeTime = 0.0;
            this.cooldown = 0.0;
            this.damage = AvatarState.getValue(this.damage);
            this.chainArcs = AvatarState.getValue(this.chainArcs);
            this.chainArcChance = AvatarState.getValue(this.chainArcChance);
            this.chainRange = AvatarState.getValue(this.chainRange);
            this.stunChance = AvatarState.getValue(this.stunChance);
        } else if (BendingManager.events.get((Object)player.getWorld()).equalsIgnoreCase("SozinsComet")) {
            this.chargeTime = 0.0;
            this.cooldown = 0.0;
        }
        instances.put(idCounter, this);
        this.id = idCounter;
        idCounter = (idCounter + 1) % Integer.MAX_VALUE;
    }

    public static ArrayList<Arc> getAllArcs() {
        ArrayList<Arc> a = new ArrayList<Arc>();
        for (Lightning light : instances.values()) {
            for (Arc arcs : light.getArcs()) {
                a.add(arcs);
            }
        }
        return a;
    }

    public static Lightning getLightning(Player player) {
        for (Lightning light : instances.values()) {
            if (light.player != player) continue;
            return light;
        }
        return null;
    }

    public static boolean isIce(Location loc) {
        Material mat = loc.getBlock().getType();
        if (mat != Material.ICE && mat != Material.PACKED_ICE) {
            return false;
        }
        return true;
    }

    public static boolean isWater(Location loc) {
        Material mat = loc.getBlock().getType();
        if (mat != Material.WATER && mat != Material.STATIONARY_WATER) {
            return false;
        }
        return true;
    }

    public static boolean isWaterOrIce(Location loc) {
        if (!Lightning.isIce(loc) && !Lightning.isWater(loc)) {
            return false;
        }
        return true;
    }

    public void electrocute(LivingEntity lent) {
        lent.getWorld().playSound(lent.getLocation(), Sound.ENTITY_CREEPER_HURT, 1.0f, 0.0f);
        this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_CREEPER_HURT, 1.0f, 0.0f);
        GeneralMethods.damageEntity(this.player, (Entity)lent, this.damage, "Lightning");
        if (Math.random() < this.stunChance) {
            final Location lentLoc = lent.getLocation();
            final LivingEntity flent = lent;
            new BukkitRunnable(){
                int count;

                public void run() {
                    if (flent.isDead() || flent instanceof Player && !((Player)flent).isOnline()) {
                        this.cancel();
                        return;
                    }
                    Location tempLoc = lentLoc.clone();
                    Vector tempVel = flent.getVelocity();
                    tempVel.setY(Math.min(0.0, tempVel.getY()));
                    tempLoc.setY(flent.getLocation().getY());
                    flent.teleport(tempLoc);
                    flent.setVelocity(tempVel);
                    ++this.count;
                    if ((double)this.count > Lightning.this.stunDuration) {
                        this.cancel();
                    }
                }
            }.runTaskTimer((Plugin)ProjectKorra.plugin, 0, 1);
        }
    }

    public ArrayList<Arc> getArcs() {
        return this.arcs;
    }

    public double getChainArcChance() {
        return this.chainArcChance;
    }

    public double getChainArcs() {
        return this.chainArcs;
    }

    public double getChainRange() {
        return this.chainRange;
    }

    public double getChargeTime() {
        return this.chargeTime;
    }

    public double getCooldown() {
        return this.cooldown;
    }

    public double getDamage() {
        return this.damage;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getRange() {
        return this.range;
    }

    public double getStunChance() {
        return this.stunChance;
    }

    public double getStunDuration() {
        return this.stunDuration;
    }

    public double getSubArcChance() {
        return this.subArcChance;
    }

    public double getWaterRange() {
        return this.waterRange;
    }

    public boolean isCharged() {
        return this.charged;
    }

    public boolean isHitIce() {
        return this.hitIce;
    }

    public boolean isHitWater() {
        return this.hitWater;
    }

    public boolean isTransparent(Player player, Block block) {
        if (Arrays.asList(EarthMethods.transparentToEarthbending).contains(block.getTypeId())) {
            if (GeneralMethods.isRegionProtectedFromBuild(player, "Lightning", block.getLocation())) {
                return false;
            }
            if (Lightning.isIce(block.getLocation())) {
                return ARC_ON_ICE;
            }
            return true;
        }
        return false;
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.removeWithTasks();
            return false;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null || !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("Lightning")) {
            this.remove();
            return false;
        }
        if (this.state == State.START) {
            if (this.bplayer.isOnCooldown("Lightning")) {
                this.remove();
                return false;
            }
            if ((double)(System.currentTimeMillis() - this.time) > this.chargeTime) {
                this.charged = true;
            }
            if (this.charged) {
                if (this.player.isSneaking()) {
                    Location loc = this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().normalize().multiply(1.2));
                    loc.add(0.0, 0.3, 0.0);
                    FireMethods.playLightningbendingParticle(loc, 0.2f, 0.2f, 0.2f);
                } else {
                    this.state = State.MAINBOLT;
                    this.bplayer.addCooldown("Lightning", (long)this.cooldown);
                    Entity target = GeneralMethods.getTargetedEntity(this.player, this.range, new ArrayList<Entity>());
                    this.origin = this.player.getEyeLocation();
                    this.destination = target != null ? target.getLocation() : this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().normalize().multiply(this.range));
                }
            } else {
                if (!this.player.isSneaking()) {
                    this.remove();
                    return false;
                }
                double d1 = 0.1570796326794897;
                double d2 = 0.06283185307179587;
                double d3 = 1.0;
                double d4 = 1.0;
                Location localLocation1 = this.player.getLocation();
                double d5 = d1 * this.i;
                double d6 = d2 * this.i;
                this.newY = localLocation1.getY() + 1.0 + d4 * Math.cos(d6);
                double d7 = localLocation1.getX() + d4 * Math.cos(d5);
                double d8 = localLocation1.getZ() + d4 * Math.sin(d5);
                Location localLocation2 = new Location(this.player.getWorld(), d7, this.newY, d8);
                FireMethods.playLightningbendingParticle(localLocation2);
                this.i += 1.0 / d3;
            }
        } else if (this.state == State.MAINBOLT) {
            Arc mainArc = new Arc(this.origin, this.destination);
            mainArc.generatePoints(5);
            this.arcs.add(mainArc);
            ArrayList<Arc> subArcs = mainArc.generateArcs(this.subArcChance, this.range / 2.0);
            this.arcs.addAll(subArcs);
            this.state = State.STRIKE;
        } else if (this.state == State.STRIKE) {
            int i = 0;
            while (i < this.arcs.size()) {
                Arc arc = this.arcs.get(i);
                int j = 0;
                while (j < arc.getAnimLocs().size() - 1) {
                    Location iterLoc = arc.getAnimLocs().get(j).getLoc().clone();
                    Location dest = arc.getAnimLocs().get(j + 1).getLoc().clone();
                    if (SELF_HIT_CLOSE && this.player.getLocation().distance(iterLoc) < 3.0 && !this.isTransparent(this.player, iterLoc.getBlock()) && !this.affectedEntities.contains((Object)this.player)) {
                        this.affectedEntities.add((Entity)this.player);
                        this.electrocute((LivingEntity)this.player);
                    }
                    while (iterLoc.distance(dest) > 0.15) {
                        LightningParticle task = new LightningParticle(arc, iterLoc.clone());
                        double timer = arc.getAnimLocs().get(j).getAnimCounter() / 2;
                        task.runTaskTimer((Plugin)ProjectKorra.plugin, (long)timer, 1);
                        this.tasks.add(task);
                        iterLoc.add(GeneralMethods.getDirection(iterLoc, dest).normalize().multiply(0.15));
                    }
                    ++j;
                }
                this.arcs.remove(i);
                --i;
                ++i;
            }
            if (this.tasks.size() == 0) {
                this.remove();
                return false;
            }
        }
        return true;
    }

    public static void progressAll() {
        for (Lightning ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove(this.id);
    }

    public static void removeAll() {
        for (Lightning ability : instances.values()) {
            ability.remove();
        }
    }

    @Override
    public void reloadVariables() {
        SELF_HIT_WATER = config.get().getBoolean("Abilities.Fire.Lightning.SelfHitWater");
        SELF_HIT_CLOSE = config.get().getBoolean("Abilities.Fire.Lightning.SelfHitClose");
        ARC_ON_ICE = config.get().getBoolean("Abilities.Fire.Lightning.ArcOnIce");
        RANGE = config.get().getDouble("Abilities.Fire.Lightning.Range");
        DAMAGE = config.get().getDouble("Abilities.Fire.Lightning.Damage");
        MAX_ARC_ANGLE = config.get().getDouble("Abilities.Fire.Lightning.MaxArcAngle");
        SUB_ARC_CHANCE = config.get().getDouble("Abilities.Fire.Lightning.SubArcChance");
        CHAIN_ARC_RANGE = config.get().getDouble("Abilities.Fire.Lightning.ChainArcRange");
        CHAIN_ARC_CHANCE = config.get().getDouble("Abilities.Fire.Lightning.ChainArcChance");
        WATER_ARC_RANGE = config.get().getDouble("Abilities.Fire.Lightning.WaterArcRange");
        STUN_CHANCE = config.get().getDouble("Abilities.Fire.Lightning.StunChance");
        STUN_DURATION = config.get().getDouble("Abilities.Fire.Lightning.StunDuration");
        MAX_CHAIN_ARCS = (int)config.get().getDouble("Abilities.Fire.Lightning.MaxChainArcs");
        WATER_ARCS = (int)config.get().getDouble("Abilities.Fire.Lightning.WaterArcs");
        CHARGETIME = (long)config.get().getDouble("Abilities.Fire.Lightning.ChargeTime");
        COOLDOWN = (long)config.get().getDouble("Abilities.Fire.Lightning.Cooldown");
    }

    public void removeWithTasks() {
        int i = 0;
        while (i < this.tasks.size()) {
            this.tasks.get(i).cancel();
            --i;
            ++i;
        }
        this.remove();
    }

    public void setChainArcChance(double chainArcChance) {
        this.chainArcChance = chainArcChance;
    }

    public void setChainArcs(double chainArcs) {
        this.chainArcs = chainArcs;
    }

    public void setChainRange(double chainRange) {
        this.chainRange = chainRange;
    }

    public void setCharged(boolean charged) {
        this.charged = charged;
    }

    public void setChargeTime(double chargeTime) {
        this.chargeTime = chargeTime;
    }

    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
        if (this.player != null) {
            this.bplayer.addCooldown("Lightning", (long)cooldown);
        }
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setHitIce(boolean hitIce) {
        this.hitIce = hitIce;
    }

    public void setHitWater(boolean hitWater) {
        this.hitWater = hitWater;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setStunChance(double stunChance) {
        this.stunChance = stunChance;
    }

    public void setStunDuration(double stunDuration) {
        this.stunDuration = stunDuration;
    }

    public void setSubArcChance(double subArcChance) {
        this.subArcChance = subArcChance;
    }

    public void setWaterRange(double waterRange) {
        this.waterRange = waterRange;
    }

    static /* synthetic */ void access$3(Lightning lightning, boolean bl) {
        lightning.hitWater = bl;
    }

    static /* synthetic */ void access$4(Lightning lightning, boolean bl) {
        lightning.hitIce = bl;
    }

    static /* synthetic */ void access$6(Lightning lightning, Location location) {
        lightning.destination = location;
    }

    static /* synthetic */ void access$12(Lightning lightning, boolean bl) {
        lightning.charged = bl;
    }

    static /* synthetic */ void access$15(Lightning lightning, double d) {
        lightning.chainArcs = d;
    }

    static /* synthetic */ void access$17(Lightning lightning, Location location) {
        lightning.origin = location;
    }

    public class AnimLocation {
        private Location loc;
        private int animCounter;

        public AnimLocation(Location loc, int animCounter) {
            this.loc = loc;
            this.animCounter = animCounter;
        }

        public int getAnimCounter() {
            return this.animCounter;
        }

        public Location getLoc() {
            return this.loc;
        }

        public void setAnimCounter(int animCounter) {
            this.animCounter = animCounter;
        }

        public void setLoc(Location loc) {
            this.loc = loc;
        }
    }

    public class Arc {
        private ArrayList<Location> points;
        private ArrayList<AnimLocation> animLocs;
        private ArrayList<LightningParticle> particles;
        private ArrayList<Arc> subArcs;
        private Vector direction;
        private int animCounter;

        public Arc(Location startPoint, Location endPoint) {
            this.points = new ArrayList();
            this.points.add(startPoint.clone());
            this.points.add(endPoint.clone());
            this.direction = GeneralMethods.getDirection(startPoint, endPoint);
            this.particles = new ArrayList();
            this.subArcs = new ArrayList();
            this.animLocs = new ArrayList();
            this.animCounter = 0;
        }

        public void cancel() {
            int i = 0;
            while (i < this.particles.size()) {
                this.particles.get(i).cancel();
                ++i;
            }
            for (Arc subArc : this.subArcs) {
                subArc.cancel();
            }
        }

        public ArrayList<Arc> generateArcs(double chance, double range) {
            ArrayList<Arc> arcs = new ArrayList<Arc>();
            int i = 0;
            while (i < this.animLocs.size()) {
                if (Math.random() < chance) {
                    Location loc = this.animLocs.get(i).getLoc();
                    double angle = (Math.random() - 0.5) * Lightning.MAX_ARC_ANGLE * 2.0;
                    Vector dir = GeneralMethods.rotateXZ(this.direction.clone(), angle);
                    double randRange = Math.random() * range + range / 3.0;
                    Location loc2 = loc.clone().add(dir.normalize().multiply(randRange));
                    Arc arc = new Arc(loc, loc2);
                    this.subArcs.add(arc);
                    arc.setAnimCounter(this.animLocs.get(i).getAnimCounter());
                    arc.generatePoints(5);
                    arcs.add(arc);
                    arcs.addAll(arc.generateArcs(chance / 2.0, range / 2.0));
                }
                ++i;
            }
            return arcs;
        }

        public void generatePoints(int times) {
			for (int i = 0; i < times; i++) {
				for (int j = 0; j < points.size() - 1; j += 2) {
					Location loc1 = points.get(j);
					Location loc2 = points.get(j + 1);
					double adjac = loc1.distance(loc2) / 2;
					double angle = (Math.random() - 0.5) * MAX_ARC_ANGLE;
					angle += angle >= 0 ? 10 : -10;
					double radians = Math.toRadians(angle);
					double hypot = adjac / Math.cos(radians);
					Vector dir = GeneralMethods.rotateXZ(direction.clone(), angle);
					Location newLoc = loc1.clone().add(dir.normalize().multiply(hypot));
					newLoc.add(0, (Math.random() - 0.5) / 2.0, 0);
					points.add(j + 1, newLoc);
				}
			}
			for (int i = 0; i < points.size(); i++) {
				animLocs.add(new AnimLocation(points.get(i), animCounter));
				animCounter++;
			}
}

        public int getAnimCounter() {
            return this.animCounter;
        }

        public ArrayList<AnimLocation> getAnimLocs() {
            return this.animLocs;
        }

        public Vector getDirection() {
            return this.direction;
        }

        public ArrayList<LightningParticle> getParticles() {
            return this.particles;
        }

        public ArrayList<Location> getPoints() {
            return this.points;
        }

        public void setAnimCounter(int animCounter) {
            this.animCounter = animCounter;
        }

        public void setAnimLocs(ArrayList<AnimLocation> animLocs) {
            this.animLocs = animLocs;
        }

        public void setDirection(Vector direction) {
            this.direction = direction;
        }

        public void setParticles(ArrayList<LightningParticle> particles) {
            this.particles = particles;
        }

        public void setPoints(ArrayList<Location> points) {
            this.points = points;
        }
    }

    public class LightningParticle
    extends BukkitRunnable {
        private Arc arc;
        private Location loc;
        private int count;

        public LightningParticle(Arc arc, Location loc) {
            this.count = 0;
            this.arc = arc;
            this.loc = loc;
            arc.particles.add(this);
        }

        public void cancel() {
            super.cancel();
            Lightning.this.tasks.remove((Object)this);
        }

        public void run() {
            FireMethods.playLightningbendingParticle(this.loc, 0.0f, 0.0f, 0.0f);
            ++this.count;
            if (this.count > 5) {
                this.cancel();
            } else if (this.count == 1) {
                if (!Lightning.this.isTransparent(Lightning.this.player, this.loc.getBlock())) {
                    this.arc.cancel();
                    return;
                }
                if (!Lightning.this.hitWater && (Lightning.isWater(this.loc) || Lightning.ARC_ON_ICE && Lightning.isIce(this.loc))) {
                    Lightning.access$3(Lightning.this, true);
                    if (Lightning.isIce(this.loc)) {
                        Lightning.access$4(Lightning.this, true);
                    }
                    int i = 0;
                    while (i < Lightning.WATER_ARCS) {
                        Location origin = this.loc.clone();
                        origin.add(new Vector((Math.random() - 0.5) * 2.0, 0.0, (Math.random() - 0.5) * 2.0));
                        Lightning.access$6(Lightning.this, origin.clone().add(new Vector((Math.random() - 0.5) * Lightning.this.waterRange, Math.random() - 0.7, (Math.random() - 0.5) * Lightning.this.waterRange)));
                        Arc newArc = new Arc(origin, Lightning.this.destination);
                        newArc.generatePoints(5);
                        Lightning.this.arcs.add(newArc);
                        ++i;
                    }
                }
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.loc, 2.5)) {
                    if (entity.equals((Object)Lightning.this.player) && (!Lightning.SELF_HIT_WATER || !Lightning.this.hitWater || !Lightning.isWater(Lightning.this.player.getLocation())) && (!Lightning.SELF_HIT_WATER || !Lightning.this.hitIce) || !(entity instanceof LivingEntity) || Lightning.this.affectedEntities.contains((Object)entity)) continue;
                    Lightning.this.affectedEntities.add(entity);
                    LivingEntity lent = (LivingEntity)entity;
                    if (lent instanceof Player) {
                        lent.getWorld().playSound(lent.getLocation(), Sound.ENTITY_CREEPER_HURT, 1.0f, 0.0f);
                        Lightning.this.player.getWorld().playSound(Lightning.this.player.getLocation(), Sound.ENTITY_CREEPER_HURT, 1.0f, 0.0f);
                        Player p = (Player)lent;
                        Lightning light = Lightning.getLightning(p);
                        if (light != null && light.state == State.START) {
                            Lightning.access$12(light, true);
                            Lightning.this.remove();
                            return;
                        }
                    }
                    Lightning.this.electrocute(lent);
                    if (Lightning.this.chainArcs < 1.0 || Math.random() > Lightning.this.chainArcChance) continue;
                    Lightning lightning = Lightning.this;
                    Lightning.access$15(lightning, lightning.chainArcs - 1.0);
                    for (Entity ent : GeneralMethods.getEntitiesAroundPoint(lent.getLocation(), Lightning.this.chainRange)) {
                        if (ent.equals((Object)Lightning.this.player) || ent.equals((Object)lent) || !(ent instanceof LivingEntity) || Lightning.this.affectedEntities.contains((Object)ent)) continue;
                        Lightning.access$17(Lightning.this, lent.getLocation().add(0.0, 1.0, 0.0));
                        Lightning.access$6(Lightning.this, ent.getLocation().add(0.0, 1.0, 0.0));
                        Arc newArc = new Arc(Lightning.this.origin, Lightning.this.destination);
                        newArc.generatePoints(5);
                        Lightning.this.arcs.add(newArc);
                        this.cancel();
                        return;
                    }
                }
            }
        }
    }

    public static enum State {
        START,
        STRIKE,
        MAINBOLT;
        

        
    }

}

