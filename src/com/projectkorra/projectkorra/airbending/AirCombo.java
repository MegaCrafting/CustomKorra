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
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.airbending;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.chiblocking.ChiPassive;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireCombo;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.util.ParticleEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
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

public class AirCombo
implements ConfigLoadable {
    public static ArrayList<AirCombo> instances = new ArrayList();
    public static double twisterSpeed = config.get().getDouble("Abilities.Air.AirCombo.Twister.Speed");
    public static double twisterRange = config.get().getDouble("Abilities.Air.AirCombo.Twister.Range");
    public static double twisterHeight = config.get().getDouble("Abilities.Air.AirCombo.Twister.Height");
    public static double twisterRadius = config.get().getDouble("Abilities.Air.AirCombo.Twister.Radius");
    public static double twisterDegreePerParticle = config.get().getDouble("Abilities.Air.AirCombo.Twister.DegreesPerParticle");
    public static double twisterHeightPerParticle = config.get().getDouble("Abilities.Air.AirCombo.Twister.HeightPerParticle");
    public static long twisterRemoveDelay = config.get().getLong("Abilities.Air.AirCombo.Twister.RemoveDelay");
    public static long twisterCooldown = config.get().getLong("Abilities.Air.AirCombo.Twister.Cooldown");
    public static double airStreamSpeed = config.get().getDouble("Abilities.Air.AirCombo.AirStream.Speed");
    public static double airStreamRange = config.get().getDouble("Abilities.Air.AirCombo.AirStream.Range");
    public static double airStreamEntityHeight = config.get().getDouble("Abilities.Air.AirCombo.AirStream.EntityHeight");
    public static long airStreamEntityDuration = config.get().getLong("Abilities.Air.AirCombo.AirStream.EntityDuration");
    public static long airStreamCooldown = config.get().getLong("Abilities.Air.AirCombo.AirStream.Cooldown");
    public static double airSweepSpeed = config.get().getDouble("Abilities.Air.AirCombo.AirSweep.Speed");
    public static double airSweepRange = config.get().getDouble("Abilities.Air.AirCombo.AirSweep.Range");
    public static double airSweepDamage = config.get().getDouble("Abilities.Air.AirCombo.AirSweep.Damage");
    public static double airSweepKnockback = config.get().getDouble("Abilities.Air.AirCombo.AirSweep.Knockback");
    public static long airSweepCooldown = config.get().getLong("Abilities.Air.AirCombo.AirSweep.Cooldown");
    private static boolean enabled = config.get().getBoolean("Abilities.Air.AirCombo.Enabled");
    public double airSliceSpeed = 0.7;
    public double airSliceRange = 10.0;
    public double airSliceDamage = 3.0;
    public long airSliceCooldown = 500;
    private Player player;
    private BendingPlayer bPlayer;
    private ClickType type;
    private String ability;
    private long time;
    private Location origin;
    private Location currentLoc;
    private Location destination;
    private Vector direction;
    private int progressCounter = 0;
    private double damage = 0.0;
    private double speed = 0.0;
    private double range = 0.0;
    private double knockback = 0.0;
    private long cooldown = 0;
    private AbilityState state;
    private ArrayList<Entity> affectedEntities = new ArrayList();
    private ArrayList<BukkitRunnable> tasks = new ArrayList();
    private ArrayList<Flight> flights = new ArrayList();

    public AirCombo(Player player, String ability) {
        if (!enabled) {
            return;
        }
        if (Commands.isToggledForAll) {
            return;
        }
        this.bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (!this.bPlayer.isToggled()) {
            return;
        }
        if (!this.bPlayer.hasElement(Element.Air)) {
            return;
        }
        if (!GeneralMethods.canBend(player.getName(), ability)) {
            return;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "AirBlast", player.getLocation())) {
            return;
        }
        this.time = System.currentTimeMillis();
        this.player = player;
        this.ability = ability;
        if (ability.equalsIgnoreCase("Twister")) {
            this.damage = 0.0;
            this.range = twisterRange;
            this.speed = twisterSpeed;
            this.cooldown = twisterCooldown;
        } else if (ability.equalsIgnoreCase("AirStream")) {
            this.damage = 0.0;
            this.range = airStreamRange;
            this.speed = airStreamSpeed;
            this.cooldown = airStreamCooldown;
        } else if (ability.equalsIgnoreCase("AirSlice")) {
            this.damage = this.airSliceDamage;
            this.range = this.airSliceRange;
            this.speed = this.airSliceSpeed;
            this.cooldown = this.airSliceCooldown;
        } else if (ability.equalsIgnoreCase("AirSweep")) {
            this.damage = airSweepDamage;
            this.range = airSweepRange;
            this.speed = airSweepSpeed;
            this.knockback = airSweepKnockback;
            this.cooldown = airSweepCooldown;
        }
        if (AvatarState.isAvatarState(player)) {
            this.cooldown = 0;
            this.damage = AvatarState.getValue(this.damage);
            this.range = AvatarState.getValue(this.range);
            this.knockback *= 1.4;
        }
        instances.add(this);
    }

    public void progress() {
        ++this.progressCounter;
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (this.ability.equalsIgnoreCase("Twister")) {
            if (this.destination == null) {
                if (this.bPlayer.isOnCooldown("Twister") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.bPlayer.addCooldown("Twister", this.cooldown);
                this.state = AbilityState.TWISTER_MOVING;
                this.direction = this.player.getEyeLocation().getDirection().clone().normalize();
                this.direction.setY(0);
                this.origin = this.player.getLocation().add(this.direction.clone().multiply(2));
                this.destination = this.player.getLocation().add(this.direction.clone().multiply(this.range));
                this.currentLoc = this.origin.clone();
            }
            if (this.origin.distance(this.currentLoc) < this.origin.distance(this.destination) && this.state == AbilityState.TWISTER_MOVING) {
                this.currentLoc.add(this.direction.clone().multiply(this.speed));
            } else if (this.state == AbilityState.TWISTER_MOVING) {
                this.state = AbilityState.TWISTER_STATIONARY;
                this.time = System.currentTimeMillis();
            } else {
                if (System.currentTimeMillis() - this.time >= twisterRemoveDelay) {
                    this.remove();
                    return;
                }
                if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirBlast", this.currentLoc)) {
                    this.remove();
                    return;
                }
            }
            Block topBlock = GeneralMethods.getTopBlock(this.currentLoc, 3, -3);
            if (topBlock == null) {
                this.remove();
                return;
            }
            this.currentLoc.setY(topBlock.getLocation().getY());
            double height = twisterHeight;
            double radius = twisterRadius;
            double y = 0.0;
            while (y < height) {
                double animRadius = radius / height * y;
                double i = -180.0;
                while (i <= 180.0) {
                    Vector animDir = GeneralMethods.rotateXZ(new Vector(1, 0, 1), i);
                    Location animLoc = this.currentLoc.clone().add(animDir.multiply(animRadius));
                    animLoc.add(0.0, y, 0.0);
                    AirMethods.playAirbendingParticles(animLoc, 1, 0.0f, 0.0f, 0.0f);
                    i += twisterDegreePerParticle;
                }
                y += twisterHeightPerParticle;
            }
            AirMethods.playAirbendingSound(this.currentLoc);
            int i = 0;
            while ((double)i < height) {
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.currentLoc.clone().add(0.0, (double)i, 0.0), radius * 0.75)) {
                    if (this.affectedEntities.contains((Object)entity) || entity.equals((Object)this.player)) continue;
                    this.affectedEntities.add(entity);
                }
                i += 3;
            }
            for (Entity entity : this.affectedEntities) {
                Vector forceDir = GeneralMethods.getDirection(entity.getLocation(), this.currentLoc.clone().add(0.0, height, 0.0));
                if (!(entity instanceof Player) || !Commands.invincible.contains(((Player)entity).getName())) {
                    entity.setVelocity(forceDir.clone().normalize().multiply(2.3));
                    if (!(entity instanceof Player)) continue;
                    ChiPassive.blockChi((Player)entity);
                    continue;
                }
                break;
            }
        } else if (this.ability.equalsIgnoreCase("AirStream")) {
            Entity target;
            if (this.destination == null) {
                if (this.bPlayer.isOnCooldown("AirStream") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.bPlayer.addCooldown("AirStream", this.cooldown);
                this.origin = this.player.getEyeLocation();
                this.currentLoc = this.origin.clone();
            }
            if ((target = GeneralMethods.getTargetedEntity(this.player, this.range, new ArrayList<Entity>())) instanceof Player && Commands.invincible.contains(((Player)target).getName())) {
                return;
            }
            this.destination = target != null && target.getLocation().distance(this.currentLoc) > 7.0 ? target.getLocation() : GeneralMethods.getTargetedLocation(this.player, this.range, EarthMethods.transparentToEarthbending);
            this.direction = GeneralMethods.getDirection(this.currentLoc, this.destination).normalize();
            this.currentLoc.add(this.direction.clone().multiply(this.speed));
            if (!EarthMethods.isTransparentToEarthbending(this.player, this.currentLoc.getBlock())) {
                this.currentLoc.subtract(this.direction.clone().multiply(this.speed));
            }
            if (Math.abs(this.player.getLocation().distance(this.currentLoc)) > this.range) {
                this.remove();
                return;
            }
            if (this.affectedEntities.size() > 0 && System.currentTimeMillis() - this.time >= airStreamEntityDuration) {
                this.remove();
                return;
            }
            if (!this.player.isSneaking()) {
                this.remove();
                return;
            }
            if (!EarthMethods.isTransparentToEarthbending(this.player, this.currentLoc.getBlock())) {
                this.remove();
                return;
            }
            if (this.currentLoc.getY() - this.origin.getY() > airStreamEntityHeight) {
                this.remove();
                return;
            }
            if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirBlast", this.currentLoc)) {
                this.remove();
                return;
            }
            if (FireMethods.isWithinFireShield(this.currentLoc)) {
                this.remove();
                return;
            }
            if (AirMethods.isWithinAirShield(this.currentLoc)) {
                this.remove();
                return;
            }
            
            /*
            while (i < 10) {
                Iterator<Entity> br = new BukkitRunnable(){
                    final Location loc;
                    final Vector dir;

                    public void run() {
                        int angle = -180;
                        while (angle <= 180) {
                            Vector orthog = GeneralMethods.getOrthogonalVector(this.dir.clone(), angle, 0.5);
                            AirMethods.playAirbendingParticles(this.loc.clone().add(orthog), 1, 0.0f, 0.0f, 0.0f);
                            angle += 45;
                        }
                    }
                };
                br.runTaskLater((Plugin)ProjectKorra.plugin, (long)(i * 2));
                this.tasks.add((BukkitRunnable)br);
                ++i;
            }
            */
			for (int i = 0; i < 10; i++) {
				BukkitRunnable br = new BukkitRunnable() {
					final Location loc = currentLoc.clone();
					final Vector dir = direction.clone();

					@Override
					public void run() {
						for (int angle = -180; angle <= 180; angle += 45) {
							Vector orthog = GeneralMethods.getOrthogonalVector(dir.clone(), angle, 0.5);
							AirMethods.playAirbendingParticles(loc.clone().add(orthog), 1, 0F, 0F, 0F);
						}
					}
				};
				br.runTaskLater(ProjectKorra.plugin, i * 2);
				tasks.add(br);
}
            for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.currentLoc, 2.8)) {
                if (this.affectedEntities.size() == 0) {
                    this.time = System.currentTimeMillis();
                }
                if (entity.equals((Object)this.player) || this.affectedEntities.contains((Object)entity)) continue;
                this.affectedEntities.add(entity);
                if (!(entity instanceof Player)) continue;
                this.flights.add(new Flight((Player)entity, this.player));
            }
            for (Entity entity2 : this.affectedEntities) {
                Vector force = GeneralMethods.getDirection(entity2.getLocation(), this.currentLoc);
                entity2.setVelocity(force.clone().normalize().multiply(this.speed));
                entity2.setFallDistance(0.0f);
            }
        } else if (this.ability.equalsIgnoreCase("AirSlice")) {
            if (this.origin == null) {
                if (this.bPlayer.isOnCooldown("AirSlice") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.bPlayer.addCooldown("AirSlice", this.cooldown);
                this.origin = this.player.getLocation();
                this.currentLoc = this.origin.clone();
                this.direction = this.player.getEyeLocation().getDirection();
                double i = -5.0;
                while (i < 10.0) {
                    FireCombo.FireComboStream fs = new FireCombo.FireComboStream(null, this.direction.clone().add(new Vector(0.0, 0.03 * i, 0.0)), this.player.getLocation(), this.range, this.speed, "AirSlice");
                    fs.setDensity(1);
                    fs.setSpread(0.0f);
                    fs.setUseNewParticles(true);
                    fs.setParticleEffect(AirMethods.getAirbendingParticles());
                    fs.setCollides(false);
                    fs.runTaskTimer((Plugin)ProjectKorra.plugin, 0, 1);
                    this.tasks.add(fs);
                    i += 1.0;
                }
            }
            this.manageAirVectors();
            for (Entity entity : this.affectedEntities) {
                if (!(entity instanceof LivingEntity)) continue;
                this.remove();
                return;
            }
        } else if (this.ability.equalsIgnoreCase("AirSweep")) {
            if (this.origin == null) {
                if (this.bPlayer.isOnCooldown("AirSweep") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.bPlayer.addCooldown("AirSweep", this.cooldown);
                this.direction = this.player.getEyeLocation().getDirection().normalize();
                this.origin = this.player.getLocation().add(this.direction.clone().multiply(10));
            }
            if (this.progressCounter < 8) {
                return;
            }
            if (this.destination == null) {
                this.destination = this.player.getLocation().add(this.player.getEyeLocation().getDirection().normalize().multiply(10));
                Vector origToDest = GeneralMethods.getDirection(this.origin, this.destination);
                double i = 0.0;
                while (i < 30.0) {
                    Vector vec = GeneralMethods.getDirection(this.player.getLocation(), this.origin.clone().add(origToDest.clone().multiply(i / 30.0)));
                    FireCombo.FireComboStream fs = new FireCombo.FireComboStream(null, vec, this.player.getLocation(), this.range, this.speed, "AirSweep");
                    fs.setDensity(1);
                    fs.setSpread(0.0f);
                    fs.setUseNewParticles(true);
                    fs.setParticleEffect(AirMethods.getAirbendingParticles());
                    fs.setCollides(false);
                    fs.runTaskTimer((Plugin)ProjectKorra.plugin, (long)(i / 2.5), 1);
                    this.tasks.add(fs);
                    i += 1.0;
                }
            }
            this.manageAirVectors();
        }
    }

    public void manageAirVectors() {
        int i = 0;
        while (i < this.tasks.size()) {
            if (((FireCombo.FireComboStream)this.tasks.get(i)).isCancelled()) {
                this.tasks.remove(i);
                --i;
            }
            ++i;
        }
        if (this.tasks.size() == 0) {
            this.remove();
            return;
        }
        i = 0;
        while (i < this.tasks.size()) {
            FireCombo.FireComboStream fstream = (FireCombo.FireComboStream)this.tasks.get(i);
            Location loc = fstream.getLocation();
            if (!EarthMethods.isTransparentToEarthbending(this.player, loc.getBlock()) && !EarthMethods.isTransparentToEarthbending(this.player, loc.clone().add(0.0, 0.2, 0.0).getBlock())) {
                fstream.remove();
                return;
            }
            if (i % 3 == 0) {
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(loc, 2.5)) {
                    if (GeneralMethods.isRegionProtectedFromBuild(this.player, "AirBlast", entity.getLocation())) {
                        this.remove();
                        return;
                    }
                    if (entity.equals((Object)this.player) || this.affectedEntities.contains((Object)entity)) continue;
                    this.affectedEntities.add(entity);
                    if (this.knockback != 0.0) {
                        Vector force = fstream.getDirection();
                        entity.setVelocity(force.multiply(this.knockback));
                    }
                    if (this.damage == 0.0 || !(entity instanceof LivingEntity)) continue;
                    if (fstream.getAbility().equalsIgnoreCase("AirSweep")) {
                        GeneralMethods.damageEntity(this.player, entity, this.damage, Element.Air, "AirSweep");
                        continue;
                    }
                    GeneralMethods.damageEntity(this.player, entity, this.damage, Element.Air, "AirCombo");
                }
                if (GeneralMethods.blockAbilities(this.player, FireCombo.abilitiesToBlock, loc, 1.0)) {
                    fstream.remove();
                }
            }
            ++i;
        }
    }

    public void remove() {
        instances.remove(this);
        for (BukkitRunnable task : this.tasks) {
            task.cancel();
        }
        int i = 0;
        while (i < this.flights.size()) {
            Flight flight = this.flights.get(i);
            flight.revert();
            flight.remove();
            this.flights.remove(i);
            --i;
            ++i;
        }
    }

    public static void progressAll() {
        int i = instances.size() - 1;
        while (i >= 0) {
            instances.get(i).progress();
            --i;
        }
    }

    public static void removeAll() {
        int i = instances.size() - 1;
        while (i >= 0) {
            instances.get(i).remove();
            --i;
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public static ArrayList<AirCombo> getAirCombo(Player player) {
        ArrayList<AirCombo> list = new ArrayList<AirCombo>();
        for (AirCombo combo : instances) {
            if (combo.player == null || combo.player != player) continue;
            list.add(combo);
        }
        return list;
    }

    public static ArrayList<AirCombo> getAirCombo(Player player, ClickType type) {
        ArrayList<AirCombo> list = new ArrayList<AirCombo>();
        for (AirCombo combo : instances) {
            if (combo.player == null || combo.player != player || combo.type == null || combo.type != type) continue;
            list.add(combo);
        }
        return list;
    }

    public static boolean removeAroundPoint(Player player, String ability, Location loc, double radius) {
        boolean removed = false;
        int i = 0;
        while (i < instances.size()) {
            AirCombo combo = instances.get(i);
            if (!combo.getPlayer().equals((Object)player)) {
                if (ability.equalsIgnoreCase("Twister") && combo.ability.equalsIgnoreCase("Twister")) {
                    if (combo.currentLoc != null && Math.abs(combo.currentLoc.distance(loc)) <= radius) {
                        instances.remove(combo);
                        removed = true;
                    }
                } else if (ability.equalsIgnoreCase("AirStream") && combo.ability.equalsIgnoreCase("AirStream")) {
                    if (combo.currentLoc != null && Math.abs(combo.currentLoc.distance(loc)) <= radius) {
                        instances.remove(combo);
                        removed = true;
                    }
                } else if (ability.equalsIgnoreCase("AirSweep") && combo.ability.equalsIgnoreCase("AirSweep")) {
                    int j = 0;
                    while (j < combo.tasks.size()) {
                        FireCombo.FireComboStream fs = (FireCombo.FireComboStream)combo.tasks.get(j);
                        if (fs.getLocation() != null && fs.getLocation().getWorld().equals((Object)loc.getWorld()) && Math.abs(fs.getLocation().distance(loc)) <= radius) {
                            fs.remove();
                            removed = true;
                        }
                        ++j;
                    }
                }
            }
            ++i;
        }
        return removed;
    }

    @Override
    public void reloadVariables() {
        twisterSpeed = config.get().getDouble("Abilities.Air.AirCombo.Twister.Speed");
        twisterRange = config.get().getDouble("Abilities.Air.AirCombo.Twister.Range");
        twisterHeight = config.get().getDouble("Abilities.Air.AirCombo.Twister.Height");
        twisterRadius = config.get().getDouble("Abilities.Air.AirCombo.Twister.Radius");
        twisterDegreePerParticle = config.get().getDouble("Abilities.Air.AirCombo.Twister.DegreesPerParticle");
        twisterHeightPerParticle = config.get().getDouble("Abilities.Air.AirCombo.Twister.HeightPerParticle");
        twisterRemoveDelay = config.get().getLong("Abilities.Air.AirCombo.Twister.RemoveDelay");
        twisterCooldown = config.get().getLong("Abilities.Air.AirCombo.Twister.Cooldown");
        airStreamSpeed = config.get().getDouble("Abilities.Air.AirCombo.AirStream.Speed");
        airStreamRange = config.get().getDouble("Abilities.Air.AirCombo.AirStream.Range");
        airStreamEntityHeight = config.get().getDouble("Abilities.Air.AirCombo.AirStream.EntityHeight");
        airStreamEntityDuration = config.get().getLong("Abilities.Air.AirCombo.AirStream.EntityDuration");
        airStreamCooldown = config.get().getLong("Abilities.Air.AirCombo.AirStream.Cooldown");
        airSweepSpeed = config.get().getDouble("Abilities.Air.AirCombo.AirSweep.Speed");
        airSweepRange = config.get().getDouble("Abilities.Air.AirCombo.AirSweep.Range");
        airSweepDamage = config.get().getDouble("Abilities.Air.AirCombo.AirSweep.Damage");
        airSweepKnockback = config.get().getDouble("Abilities.Air.AirCombo.AirSweep.Knockback");
        airSweepCooldown = config.get().getLong("Abilities.Air.AirCombo.AirSweep.Cooldown");
        enabled = config.get().getBoolean("Abilities.Air.AirCombo.Enabled");
    }

	public static enum AbilityState {
		TWISTER_MOVING, TWISTER_STATIONARY
	}

}

