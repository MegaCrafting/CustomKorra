/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Effect
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
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
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireJet;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class FireCombo
implements ConfigLoadable {
    public static final List<String> abilitiesToBlock = new ArrayList<String>(){
        private static final long serialVersionUID = 5395690551860441647L;
    };
    private static boolean enabled = config.get().getBoolean("Abilities.Fire.FireCombo.Enabled");
    private static final double FIRE_WHEEL_STARTING_HEIGHT = 2.0;
    private static final double FIRE_WHEEL_RADIUS = 1.0;
    public static double fireticksFireWheel = config.get().getDouble("Abilities.Fire.FireCombo.FireWheel.FireTicks");
    public static double fireticksJetBlaze = config.get().getDouble("Abilities.Fire.FireCombo.JetBlaze.FireTicks");
    public static double FIRE_KICK_RANGE = config.get().getDouble("Abilities.Fire.FireCombo.FireKick.Range");
    public static double FIRE_KICK_DAMAGE = config.get().getDouble("Abilities.Fire.FireCombo.FireKick.Damage");
    public static double FIRE_SPIN_RANGE = config.get().getDouble("Abilities.Fire.FireCombo.FireSpin.Range");
    public static double FIRE_SPIN_DAMAGE = config.get().getDouble("Abilities.Fire.FireCombo.FireSpin.Damage");
    public static double FIRE_SPIN_KNOCKBACK = config.get().getDouble("Abilities.Fire.FireCombo.FireSpin.Knockback");
    public static double FIRE_WHEEL_DAMAGE = config.get().getDouble("Abilities.Fire.FireCombo.FireWheel.Damage");
    public static double FIRE_WHEEL_RANGE = config.get().getDouble("Abilities.Fire.FireCombo.FireWheel.Range");
    public static double FIRE_WHEEL_SPEED = config.get().getDouble("Abilities.Fire.FireCombo.FireWheel.Speed");
    public static double JET_BLAST_SPEED = config.get().getDouble("Abilities.Fire.FireCombo.JetBlast.Speed");
    public static double JET_BLAZE_SPEED = config.get().getDouble("Abilities.Fire.FireCombo.JetBlaze.Speed");
    public static double JET_BLAZE_DAMAGE = config.get().getDouble("Abilities.Fire.FireCombo.JetBlaze.Damage");
    public static long FIRE_KICK_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.FireKick.Cooldown");
    public static long FIRE_SPIN_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.FireSpin.Cooldown");
    public static long FIRE_WHEEL_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.FireWheel.Cooldown");
    public static long JET_BLAST_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.JetBlast.Cooldown");
    public static long JET_BLAZE_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.JetBlaze.Cooldown");
    public static ArrayList<FireCombo> instances = new ArrayList();
    private Player player;
    private BendingPlayer bplayer;
    private ClickType type;
    private String ability;
    private long time;
    private Location origin;
    private Location currentLoc;
    private Location destination;
    private Vector direction;
    private boolean firstTime = true;
    private ArrayList<LivingEntity> affectedEntities = new ArrayList();
    private ArrayList<FireComboStream> tasks = new ArrayList();
    private int progressCounter = 0;
    private double damage = 0.0;
    private double speed = 0.0;
    private double range = 0.0;
    private long cooldown = 0;

    public FireCombo(Player player, String ability) {
        if (!enabled) {
            return;
        }
        if (!GeneralMethods.getBendingPlayer(player.getName()).hasElement(Element.Fire)) {
            return;
        }
        if (Commands.isToggledForAll) {
            return;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "Blaze", player.getLocation())) {
            return;
        }
        if (!GeneralMethods.getBendingPlayer(player.getName()).isToggled()) {
            return;
        }
        if (!GeneralMethods.canBend(player.getName(), ability)) {
            return;
        }
        this.time = System.currentTimeMillis();
        this.player = player;
        this.ability = ability;
        this.bplayer = GeneralMethods.getBendingPlayer(player.getName());
        if (ability.equalsIgnoreCase("FireKick")) {
            this.damage = FIRE_KICK_DAMAGE;
            this.range = FIRE_KICK_RANGE;
            this.speed = 1.0;
            this.cooldown = FIRE_KICK_COOLDOWN;
        } else if (ability.equalsIgnoreCase("FireSpin")) {
            this.damage = FIRE_SPIN_DAMAGE;
            this.range = FIRE_SPIN_RANGE;
            this.speed = 0.3;
            this.cooldown = FIRE_SPIN_COOLDOWN;
        } else if (ability.equalsIgnoreCase("FireWheel")) {
            this.damage = FIRE_WHEEL_DAMAGE;
            this.range = FIRE_WHEEL_RANGE;
            this.speed = FIRE_WHEEL_SPEED;
            this.cooldown = FIRE_WHEEL_COOLDOWN;
        } else if (ability.equalsIgnoreCase("JetBlast")) {
            this.speed = JET_BLAST_SPEED;
            this.cooldown = JET_BLAST_COOLDOWN;
        } else if (ability.equalsIgnoreCase("JetBlaze")) {
            this.damage = JET_BLAZE_DAMAGE;
            this.speed = JET_BLAZE_SPEED;
            this.cooldown = JET_BLAZE_COOLDOWN;
        }
        if (AvatarState.isAvatarState(player)) {
            this.cooldown = 0;
            this.damage = AvatarState.getValue(this.damage);
            this.range = AvatarState.getValue(this.range);
        }
        instances.add(this);
    }

    public static ArrayList<FireCombo> getFireCombo(Player player) {
        ArrayList<FireCombo> list = new ArrayList<FireCombo>();
        for (FireCombo lf : instances) {
            if (lf.player == null || lf.player != player) continue;
            list.add(lf);
        }
        return list;
    }

    public static ArrayList<FireCombo> getFireCombo(Player player, ClickType type) {
        ArrayList<FireCombo> list = new ArrayList<FireCombo>();
        for (FireCombo lf : instances) {
            if (lf.player == null || lf.player != player || lf.type == null || lf.type != type) continue;
            list.add(lf);
        }
        return list;
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

    public static boolean removeAroundPoint(Player player, String ability, Location loc, double radius) {
        boolean removed = false;
        int i = 0;
        while (i < instances.size()) {
            FireCombo combo = instances.get(i);
            if (!combo.getPlayer().equals((Object)player)) {
                if (ability.equalsIgnoreCase("FireKick") && combo.ability.equalsIgnoreCase("FireKick")) {
                    for (FireComboStream fs : combo.tasks) {
                        if (fs.getLocation() == null || fs.getLocation().getWorld() != loc.getWorld() || Math.abs(fs.getLocation().distance(loc)) > radius) continue;
                        fs.remove();
                        removed = true;
                    }
                } else if (ability.equalsIgnoreCase("FireSpin") && combo.ability.equalsIgnoreCase("FireSpin")) {
                    for (FireComboStream fs : combo.tasks) {
                        if (fs.getLocation() == null || !fs.getLocation().getWorld().equals((Object)loc.getWorld()) || Math.abs(fs.getLocation().distance(loc)) > radius) continue;
                        fs.remove();
                        removed = true;
                    }
                } else if (ability.equalsIgnoreCase("FireWheel") && combo.ability.equalsIgnoreCase("FireWheel") && combo.currentLoc != null && Math.abs(combo.currentLoc.distance(loc)) <= radius) {
                    instances.remove(combo);
                    removed = true;
                }
            }
            ++i;
        }
        return removed;
    }

    public void checkSafeZone() {
        if (this.currentLoc != null && GeneralMethods.isRegionProtectedFromBuild(this.player, "Blaze", this.currentLoc)) {
            this.remove();
        }
    }

    public void collision(LivingEntity entity, Vector direction, FireComboStream fstream) {
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "Blaze", entity.getLocation())) {
            return;
        }
        entity.getLocation().getWorld().playSound(entity.getLocation(), Sound.ENTITY_VILLAGER_HURT, 0.3f, 0.3f);
        if (this.ability.equalsIgnoreCase("FireKick")) {
            GeneralMethods.damageEntity(this.player, (Entity)entity, this.damage, Element.Fire, "FireKick");
            fstream.remove();
        } else if (this.ability.equalsIgnoreCase("FireSpin")) {
            if (entity instanceof Player && Commands.invincible.contains(((Player)entity).getName())) {
                return;
            }
            double knockback = AvatarState.isAvatarState(this.player) ? FIRE_SPIN_KNOCKBACK + 0.5 : FIRE_SPIN_KNOCKBACK;
            GeneralMethods.damageEntity(this.player, (Entity)entity, this.damage, Element.Fire, "FireSpin");
            entity.setVelocity(direction.normalize().multiply(knockback));
            fstream.remove();
        } else if (this.ability.equalsIgnoreCase("JetBlaze")) {
            if (!this.affectedEntities.contains((Object)entity)) {
                this.affectedEntities.add(entity);
                GeneralMethods.damageEntity(this.player, (Entity)entity, this.damage, Element.Fire, "JetBlaze");
                entity.setFireTicks((int)(fireticksJetBlaze * 20.0));
            }
        } else if (this.ability.equalsIgnoreCase("FireWheel") && !this.affectedEntities.contains((Object)entity)) {
            this.affectedEntities.add(entity);
            GeneralMethods.damageEntity(this.player, (Entity)entity, this.damage, Element.Fire, "FireWheel");
            entity.setFireTicks((int)(fireticksFireWheel * 20.0));
            this.remove();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public void progress() {
        FireComboStream fs;
        ++this.progressCounter;
        int i = 0;
        while (i < this.tasks.size()) {
            BukkitRunnable br = this.tasks.get(i);
            if (br instanceof FireComboStream && (fs = (FireComboStream)br).isCancelled()) {
                this.tasks.remove((Object)fs);
            }
            ++i;
        }
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (this.ability.equalsIgnoreCase("FireKick")) {
            if (this.destination == null) {
                if (this.bplayer.isOnCooldown("FireKick") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.bplayer.addCooldown("FireKick", this.cooldown);
                Vector eyeDir = this.player.getEyeLocation().getDirection().normalize().multiply(this.range);
                this.destination = this.player.getEyeLocation().add(eyeDir);
                this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_HORSE_JUMP, 0.5f, 0.0f);
                this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 1.0f);
                int i2 = -30;
                while (i2 <= 30) {
                    Object vec = GeneralMethods.getDirection(this.player.getLocation(), this.destination.clone());
                    vec = GeneralMethods.rotateXZ((Vector)vec, i2);
                    FireComboStream fs2 = new FireComboStream(this, (Vector)vec, this.player.getLocation(), this.range, this.speed, "FireKick");
                    fs2.setSpread(0.2f);
                    fs2.setDensity(5);
                    fs2.setUseNewParticles(true);
                    if (this.tasks.size() % 3 != 0) {
                        fs2.setCollides(false);
                    }
                    fs2.runTaskTimer((Plugin)ProjectKorra.plugin, 0, 1);
                    this.tasks.add(fs2);
                    this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_GENERIC_BURN, 0.5f, 1.0f);
                    i2 += 5;
                }
                this.currentLoc = this.tasks.get(0).getLocation();
                for (FireComboStream stream : this.tasks) {
                    if (!GeneralMethods.blockAbilities(this.player, abilitiesToBlock, stream.currentLoc, 2.0)) continue;
                    stream.remove();
                }
            } else if (this.tasks.size() == 0) {
                this.remove();
                return;
            }
        } else if (this.ability.equalsIgnoreCase("FireSpin")) {
            if (this.destination == null) {
                if (this.bplayer.isOnCooldown("FireSpin") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.bplayer.addCooldown("FireSpin", this.cooldown);
                this.destination = this.player.getEyeLocation().add(this.range, 0.0, this.range);
                this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.5f, 0.5f);
                i = 0;
                while (i <= 360) {
                    Vector vec = GeneralMethods.getDirection(this.player.getLocation(), this.destination.clone());
                    vec = GeneralMethods.rotateXZ(vec, i - 180);
                    vec.setY(0);
                    fs = new FireComboStream(this, vec, this.player.getLocation().clone().add(0.0, 1.0, 0.0), this.range, this.speed, "FireSpin");
                    fs.setSpread(0.0f);
                    fs.setDensity(1);
                    fs.setUseNewParticles(true);
                    if (this.tasks.size() % 10 != 0) {
                        fs.setCollides(false);
                    }
                    fs.runTaskTimer((Plugin)ProjectKorra.plugin, 0, 1);
                    this.tasks.add(fs);
                    i += 5;
                }
            }
            if (this.tasks.size() == 0) {
                this.remove();
                return;
            }
            for (FireComboStream stream : this.tasks) {
                if (FireMethods.isWithinFireShield(stream.getLocation())) {
                    stream.remove();
                }
                if (!AirMethods.isWithinAirShield(stream.getLocation())) continue;
                stream.remove();
            }
        } else if (this.ability.equalsIgnoreCase("JetBlast")) {
            if (System.currentTimeMillis() - this.time > 5000) {
                this.remove();
                return;
            }
            if (FireJet.checkTemporaryImmunity(this.player)) {
                if (this.firstTime) {
                    if (this.bplayer.isOnCooldown("JetBlast") && !AvatarState.isAvatarState(this.player)) {
                        this.remove();
                        return;
                    }
                    this.bplayer.addCooldown("JetBlast", this.cooldown);
                    this.firstTime = false;
                    float spread = 0.0f;
                    ParticleEffect.EXPLOSION_LARGE.display(this.player.getLocation(), spread, spread, spread, 0.0f, 1);
                    this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 15.0f, 0.0f);
                }
                this.player.setVelocity(this.player.getVelocity().normalize().multiply(this.speed));
                FireComboStream fs3 = new FireComboStream(this, this.player.getVelocity().clone().multiply(-1), this.player.getLocation(), 3.0, 0.5, "JetBlast");
                fs3.setDensity(1);
                fs3.setSpread(0.9f);
                fs3.setUseNewParticles(true);
                fs3.setCollides(false);
                fs3.runTaskTimer((Plugin)ProjectKorra.plugin, 0, 1);
                this.tasks.add(fs3);
            }
        } else if (this.ability.equalsIgnoreCase("JetBlaze")) {
            if (this.firstTime) {
                if (this.bplayer.isOnCooldown("JetBlaze") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.bplayer.addCooldown("JetBlaze", this.cooldown);
                this.firstTime = false;
            } else {
                if (System.currentTimeMillis() - this.time > 5000) {
                    this.remove();
                    return;
                }
                if (FireJet.checkTemporaryImmunity(this.player)) {
                    this.direction = this.player.getVelocity().clone().multiply(-1);
                    this.player.setVelocity(this.player.getVelocity().normalize().multiply(this.speed));
                    FireComboStream fs4 = new FireComboStream(this, this.direction, this.player.getLocation(), 5.0, 1.0, "JetBlaze");
                    fs4.setDensity(8);
                    fs4.setSpread(1.0f);
                    fs4.setUseNewParticles(true);
                    fs4.setCollisionRadius(3.0);
                    fs4.setParticleEffect(ParticleEffect.SMOKE_LARGE);
                    if (this.progressCounter % 5 != 0) {
                        fs4.setCollides(false);
                    }
                    fs4.runTaskTimer((Plugin)ProjectKorra.plugin, 0, 1);
                    this.tasks.add(fs4);
                    if (this.progressCounter % 4 == 0) {
                        this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1.0f, 0.0f);
                    }
                }
            }
        } else if (this.ability.equalsIgnoreCase("FireWheel")) {
            if (this.currentLoc == null) {
                if (this.bplayer.isOnCooldown("FireWheel") && !AvatarState.isAvatarState(this.player)) {
                    this.remove();
                    return;
                }
                this.bplayer.addCooldown("FireWheel", this.cooldown);
                this.origin = this.player.getLocation();
                if (GeneralMethods.getTopBlock(this.player.getLocation(), 3, 3) == null) {
                    this.remove();
                    return;
                }
                this.currentLoc = this.player.getLocation();
                this.direction = this.player.getEyeLocation().getDirection().clone().normalize();
                this.direction.setY(0);
            } else if (this.currentLoc.distance(this.origin) > this.range) {
                this.remove();
                return;
            }
            Block topBlock = GeneralMethods.getTopBlock(this.currentLoc, 2, -4);
            if (topBlock == null || WaterMethods.isWaterbendable(topBlock, this.player) && !WaterMethods.isPlant(topBlock)) {
                this.remove();
                return;
            }
            if (topBlock.getType() == Material.FIRE || WaterMethods.isPlant(topBlock)) {
                topBlock = topBlock.getLocation().add(0.0, -1.0, 0.0).getBlock();
            }
            this.currentLoc.setY((double)topBlock.getY() + 2.0);
            FireComboStream fs5 = new FireComboStream(this, this.direction, this.currentLoc.clone().add(0.0, -1.0, 0.0), 5.0, 1.0, "FireWheel");
            fs5.setDensity(0);
            fs5.setSinglePoint(true);
            fs5.setCollisionRadius(1.5);
            fs5.setCollides(true);
            fs5.runTaskTimer((Plugin)ProjectKorra.plugin, 0, 1);
            this.tasks.add(fs5);
            double i3 = -180.0;
            while (i3 <= 180.0) {
                Location tempLoc = this.currentLoc.clone();
                Vector newDir = this.direction.clone().multiply(1.0 * Math.cos(Math.toRadians(i3)));
                tempLoc.add(newDir);
                tempLoc.setY(tempLoc.getY() + 1.0 * Math.sin(Math.toRadians(i3)));
                ParticleEffect.FLAME.display(tempLoc, 0.0f, 0.0f, 0.0f, 0.0f, 1);
                i3 += 3.0;
            }
            this.currentLoc = this.currentLoc.add(this.direction.clone().multiply(this.speed));
            this.currentLoc.getWorld().playSound(this.currentLoc, Sound.ENTITY_GENERIC_SMALL_FALL, 1.0f, 1.0f);
            if (GeneralMethods.blockAbilities(this.player, abilitiesToBlock, this.currentLoc, 2.0)) {
                this.remove();
                return;
            }
        }
        if (this.progressCounter % 3 == 0) {
            this.checkSafeZone();
        }
    }

    @Override
    public void reloadVariables() {
        enabled = config.get().getBoolean("Abilities.Fire.FireCombo.Enabled");
        fireticksFireWheel = config.get().getDouble("Abilities.Fire.FireCombo.FireWheel.FireTicks");
        fireticksJetBlaze = config.get().getDouble("Abilities.Fire.FireCombo.JetBlaze.FireTicks");
        FIRE_KICK_RANGE = config.get().getDouble("Abilities.Fire.FireCombo.FireKick.Range");
        FIRE_KICK_DAMAGE = config.get().getDouble("Abilities.Fire.FireCombo.FireKick.Damage");
        FIRE_SPIN_RANGE = config.get().getDouble("Abilities.Fire.FireCombo.FireSpin.Range");
        FIRE_SPIN_DAMAGE = config.get().getDouble("Abilities.Fire.FireCombo.FireSpin.Damage");
        FIRE_SPIN_KNOCKBACK = config.get().getDouble("Abilities.Fire.FireCombo.FireSpin.Knockback");
        FIRE_WHEEL_DAMAGE = config.get().getDouble("Abilities.Fire.FireCombo.FireWheel.Damage");
        FIRE_WHEEL_RANGE = config.get().getDouble("Abilities.Fire.FireCombo.FireWheel.Range");
        FIRE_WHEEL_SPEED = config.get().getDouble("Abilities.Fire.FireCombo.FireWheel.Speed");
        JET_BLAST_SPEED = config.get().getDouble("Abilities.Fire.FireCombo.JetBlast.Speed");
        JET_BLAZE_SPEED = config.get().getDouble("Abilities.Fire.FireCombo.JetBlaze.Speed");
        JET_BLAZE_DAMAGE = config.get().getDouble("Abilities.Fire.FireCombo.JetBlaze.Damage");
        FIRE_KICK_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.FireKick.Cooldown");
        FIRE_SPIN_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.FireSpin.Cooldown");
        FIRE_WHEEL_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.FireWheel.Cooldown");
        JET_BLAST_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.JetBlast.Cooldown");
        JET_BLAZE_COOLDOWN = config.get().getLong("Abilities.Fire.FireCombo.JetBlaze.Cooldown");
    }

    public void remove() {
        instances.remove(this);
        for (BukkitRunnable task : this.tasks) {
            task.cancel();
        }
    }

    public static class FireComboStream
    extends BukkitRunnable {
        private Vector direction;
        private double speed;
        private Location initialLoc;
        private Location currentLoc;
        private double distance;
        private String ability;
        ParticleEffect particleEffect = ParticleEffect.FLAME;
        private FireCombo fireCombo;
        private float spread = 0.0f;
        private int density = 1;
        private boolean useNewParticles = false;
        private boolean cancelled = false;
        private boolean collides = true;
        private boolean singlePoint = false;
        private double collisionRadius = 2.0;
        private int checkCollisionDelay = 1;
        private int checkCollisionCounter = 0;

        public FireComboStream(FireCombo fireCombo, Vector direction, Location loc, double distance, double speed, String ability) {
            this.fireCombo = fireCombo;
            this.direction = direction;
            this.speed = speed;
            this.initialLoc = loc.clone();
            this.currentLoc = loc.clone();
            this.distance = distance;
            this.ability = ability;
        }

        public void cancel() {
            this.remove();
        }

        public Vector getDirection() {
            return this.direction.clone();
        }

        public Location getLocation() {
            return this.currentLoc;
        }

        public String getAbility() {
            return this.ability;
        }

        public boolean isCancelled() {
            return this.cancelled;
        }

        public void remove() {
            super.cancel();
            this.cancelled = true;
        }

        public void run() {
            Block block = this.currentLoc.getBlock();
            if (block.getRelative(BlockFace.UP).getType() != Material.AIR && !WaterMethods.isPlant(block)) {
                this.remove();
                return;
            }
            int i = 0;
            while (i < this.density) {
                if (this.useNewParticles) {
                    this.particleEffect.display(this.currentLoc, this.spread, this.spread, this.spread, 0.0f, 1);
                } else {
                    this.currentLoc.getWorld().playEffect(this.currentLoc, Effect.MOBSPAWNER_FLAMES, 0, 15);
                }
                ++i;
            }
            this.currentLoc.add(this.direction.normalize().multiply(this.speed));
            if (this.initialLoc.distance(this.currentLoc) > this.distance) {
                this.remove();
                return;
            }
            if (this.collides && this.checkCollisionCounter % this.checkCollisionDelay == 0) {
                for (Entity entity : GeneralMethods.getEntitiesAroundPoint(this.currentLoc, this.collisionRadius)) {
                    if (!(entity instanceof LivingEntity) || entity.equals((Object)this.fireCombo.getPlayer())) continue;
                    this.fireCombo.collision((LivingEntity)entity, this.direction, this);
                }
            }
            ++this.checkCollisionCounter;
            if (this.singlePoint) {
                this.remove();
            }
        }

        public void setCheckCollisionDelay(int delay) {
            this.checkCollisionDelay = delay;
        }

        public void setCollides(boolean b) {
            this.collides = b;
        }

        public void setCollisionRadius(double radius) {
            this.collisionRadius = radius;
        }

        public void setDensity(int density) {
            this.density = density;
        }

        public void setParticleEffect(ParticleEffect effect) {
            this.particleEffect = effect;
        }

        public void setSinglePoint(boolean b) {
            this.singlePoint = b;
        }

        public void setSpread(float spread) {
            this.spread = spread;
        }

        public void setUseNewParticles(boolean b) {
            this.useNewParticles = b;
        }
    }

}

