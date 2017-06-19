/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
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
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.FreezeMelt;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterReturn;
import com.projectkorra.projectkorra.util.ClickType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class OctopusForm {
    public static ConcurrentHashMap<Player, OctopusForm> instances = new ConcurrentHashMap();
    private static int RANGE = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.OctopusForm.Range");
    private static double ATTACK_RANGE = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.OctopusForm.AttackRange");
    private static int DAMAGE = ProjectKorra.plugin.getConfig().getInt("Abilities.Water.OctopusForm.Damage");
    private static long INTERVAL = ProjectKorra.plugin.getConfig().getLong("Abilities.Water.OctopusForm.FormDelay");
    private static double KNOCKBACK = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.OctopusForm.Knockback");
    static double RADIUS = ProjectKorra.plugin.getConfig().getDouble("Abilities.Water.OctopusForm.Radius");
    private static final byte full = 0;
    private Player player;
    private Block sourceblock;
    private Location sourcelocation;
    private TempBlock source;
    private long time;
    private double startangle;
    private double angle;
    private double y = 0.0;
    private double dta = 45.0;
    private int animstep = 1;
    private int step = 1;
    private int inc = 3;
    private ArrayList<TempBlock> blocks = new ArrayList();
    private ArrayList<TempBlock> newblocks = new ArrayList();
    private boolean sourceselected = false;
    private boolean settingup = false;
    private boolean forming = false;
    private boolean formed = false;
    private int range = RANGE;
    private double attackRange = ATTACK_RANGE;
    private int damage = DAMAGE;
    private long interval = INTERVAL;
    private double knockback = KNOCKBACK;
    private double radius = RADIUS;

    public OctopusForm(Player player) {
        if (instances.containsKey((Object)player)) {
            if (OctopusForm.instances.get((Object)player).formed) {
                instances.get((Object)player).attack();
                return;
            }
            if (!OctopusForm.instances.get((Object)player).sourceselected) {
                return;
            }
        }
        this.player = player;
        this.time = System.currentTimeMillis();
        this.sourceblock = BlockSource.getWaterSourceBlock(player, this.range, ClickType.LEFT_CLICK, true, true, WaterMethods.canPlantbend(player));
        if (this.sourceblock != null) {
            this.sourcelocation = this.sourceblock.getLocation();
            this.sourceselected = true;
            instances.put(player, this);
        }
    }

    private void incrementStep() {
        if (this.sourceselected) {
            this.sourceselected = false;
            this.settingup = true;
        } else if (this.settingup) {
            this.settingup = false;
            this.forming = true;
        } else if (this.forming) {
            this.forming = false;
            this.formed = true;
        }
    }

    public static void form(Player player) {
        Location eyeloc;
        Block block;
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).form();
        } else if (WaterReturn.hasWaterBottle(player) && EarthMethods.isTransparentToEarthbending(player, block = (eyeloc = player.getEyeLocation()).add(eyeloc.getDirection().normalize()).getBlock()) && EarthMethods.isTransparentToEarthbending(player, eyeloc.getBlock())) {
            block.setType(Material.WATER);
            block.setData((byte)0);
            OctopusForm form = new OctopusForm(player);
            form.form();
            if (form.formed || form.forming || form.settingup) {
                WaterReturn.emptyWaterBottle(player);
            } else {
                block.setType(Material.AIR);
            }
        }
    }

    private void form() {
        this.incrementStep();
        if (WaterMethods.isPlant(this.sourceblock)) {
            new com.projectkorra.projectkorra.waterbending.Plantbending(this.sourceblock);
            this.sourceblock.setType(Material.AIR);
        } else if (!GeneralMethods.isAdjacentToThreeOrMoreSources(this.sourceblock)) {
            this.sourceblock.setType(Material.AIR);
        }
        this.source = new TempBlock(this.sourceblock, Material.STATIONARY_WATER,(byte) 8);
    }

    private void attack() {
        double tentacleangle;
        if (!this.formed) {
            return;
        }
        double tangle = tentacleangle = (double)new Vector(1, 0, 0).angle(this.player.getEyeLocation().getDirection()) + this.dta / 2.0;
        while (tangle < tentacleangle + 360.0) {
            double phi = Math.toRadians(tangle);
            this.affect(this.player.getLocation().clone().add(new Vector(this.radius * Math.cos(phi), 1.0, this.radius * Math.sin(phi))));
            tangle += this.dta;
        }
    }

    private void affect(Location location) {
        for (Entity entity : GeneralMethods.getEntitiesAroundPoint(location, this.attackRange)) {
            if (entity.getEntityId() == this.player.getEntityId() || GeneralMethods.isRegionProtectedFromBuild(this.player, "OctopusForm", entity.getLocation()) || GeneralMethods.isObstructed(location, entity.getLocation())) continue;
            double knock = AvatarState.isAvatarState(this.player) ? AvatarState.getValue(this.knockback) : this.knockback;
            entity.setVelocity(GeneralMethods.getDirection(this.player.getLocation(), location).normalize().multiply(knock));
            if (entity instanceof LivingEntity) {
                GeneralMethods.damageEntity(this.player, entity, this.damage, "OctopusForm");
            }
            AirMethods.breakBreathbendingHold(entity);
        }
    }

    public static void progressAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).progress();
        }
    }

    private void progress() {
        if (!GeneralMethods.canBend(this.player.getName(), "OctopusForm")) {
            this.remove();
            this.returnWater();
            return;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            this.remove();
            this.returnWater();
            return;
        }
        if (!this.player.isSneaking() && !this.sourceselected || !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("OctopusForm")) {
            this.remove();
            this.returnWater();
            return;
        }
        if (!this.sourceblock.getWorld().equals((Object)this.player.getWorld())) {
            this.remove();
            return;
        }
        if (this.sourceblock.getLocation().distance(this.player.getLocation()) > (double)this.range && this.sourceselected) {
            this.remove();
            return;
        }
        if (System.currentTimeMillis() > this.time + this.interval) {
            this.time = System.currentTimeMillis();
            Location location = this.player.getLocation();
            if (this.sourceselected) {
                WaterMethods.playFocusWaterEffect(this.sourceblock);
            } else if (this.settingup) {
                if (this.sourceblock.getY() < location.getBlockY()) {
                    this.source.revertBlock();
                    this.source = null;
                    Block newblock = this.sourceblock.getRelative(BlockFace.UP);
                    this.sourcelocation = newblock.getLocation();
                    if (!GeneralMethods.isSolid(newblock)) {
                        this.source = new TempBlock(newblock, Material.STATIONARY_WATER,(byte) 8);
                        this.sourceblock = newblock;
                    } else {
                        this.remove();
                        this.returnWater();
                    }
                } else if (this.sourceblock.getY() > location.getBlockY()) {
                    this.source.revertBlock();
                    this.source = null;
                    Block newblock = this.sourceblock.getRelative(BlockFace.DOWN);
                    this.sourcelocation = newblock.getLocation();
                    if (!GeneralMethods.isSolid(newblock)) {
                        this.source = new TempBlock(newblock, Material.STATIONARY_WATER, (byte)8);
                        this.sourceblock = newblock;
                    } else {
                        this.remove();
                        this.returnWater();
                    }
                } else if (this.sourcelocation.distance(location) > this.radius) {
                    Vector vector = GeneralMethods.getDirection(this.sourcelocation, location.getBlock().getLocation()).normalize();
                    this.sourcelocation.add(vector);
                    Block newblock = this.sourcelocation.getBlock();
                    if (!newblock.equals((Object)this.sourceblock)) {
                        if (this.source != null) {
                            this.source.revertBlock();
                        }
                        if (!GeneralMethods.isSolid(newblock)) {
                            this.source = new TempBlock(newblock, Material.STATIONARY_WATER, (byte)8);
                            this.sourceblock = newblock;
                        }
                    }
                } else {
                    this.incrementStep();
                    if (this.source != null) {
                        this.source.revertBlock();
                    }
                    this.source = null;
                    Vector vector = new Vector(1, 0, 0);
                    this.angle = this.startangle = (double)vector.angle(GeneralMethods.getDirection(this.sourceblock.getLocation(), location));
                }
            } else if (this.forming) {
                if (this.angle - this.startangle >= 360.0) {
                    this.y += 1.0;
                } else {
                    this.angle += 20.0;
                }
                if (GeneralMethods.rand.nextInt(4) == 0) {
                    WaterMethods.playWaterbendingSound(this.player.getLocation());
                }
                this.formOctopus();
                if (this.y == 2.0) {
                    this.incrementStep();
                }
            } else if (this.formed) {
                if (GeneralMethods.rand.nextInt(7) == 0) {
                    WaterMethods.playWaterbendingSound(this.player.getLocation());
                }
                ++this.step;
                if (this.step % this.inc == 0) {
                    ++this.animstep;
                }
                if (this.animstep > 8) {
                    this.animstep = 1;
                }
                this.formOctopus();
            } else {
                this.remove();
            }
        }
    }

    private void formOctopus() {
        Location location = this.player.getLocation();
        this.newblocks.clear();
        ArrayList<Block> doneblocks = new ArrayList<Block>();
        double theta = this.startangle;
        while (theta < this.startangle + this.angle) {
            double rtheta = Math.toRadians(theta);
            Block block = location.clone().add(new Vector(this.radius * Math.cos(rtheta), 0.0, this.radius * Math.sin(rtheta))).getBlock();
            if (!doneblocks.contains((Object)block)) {
                this.addWater(block);
                doneblocks.add(block);
            }
            theta += 10.0;
        }
        Vector eyedir = this.player.getEyeLocation().getDirection();
        eyedir.setY(0);
        double tentacleangle = Math.toDegrees(new Vector(1, 0, 0).angle(eyedir)) + this.dta / 2.0;
        int astep = this.animstep;
        double tangle = tentacleangle;
        while (tangle < tentacleangle + 360.0) {
            double phi = Math.toRadians(tangle);
            this.tentacle(location.clone().add(new Vector(this.radius * Math.cos(phi), 0.0, this.radius * Math.sin(phi))), ++astep);
            tangle += this.dta;
        }
        for (TempBlock block : this.blocks) {
            if (this.newblocks.contains(block)) continue;
            block.revertBlock();
        }
        this.blocks.clear();
        this.blocks.addAll(this.newblocks);
        if (this.blocks.isEmpty()) {
            this.remove();
        }
    }

    private void tentacle(Location base, int animationstep) {
        Block baseblock;
        if (!TempBlock.isTempBlock(base.getBlock())) {
            return;
        }
        if (!this.blocks.contains(TempBlock.get(base.getBlock()))) {
            return;
        }
        Vector direction = GeneralMethods.getDirection(this.player.getLocation(), base);
        direction.setY(0);
        direction.normalize();
        if (animationstep > 8) {
            animationstep %= 8;
        }
        if (this.y >= 1.0) {
            baseblock = base.clone().add(0.0, 1.0, 0.0).getBlock();
            if (animationstep == 1) {
                this.addWater(baseblock);
            } else if (animationstep == 2 || animationstep == 8) {
                this.addWater(baseblock);
            } else {
                this.addWater(base.clone().add(direction.getX(), 1.0, direction.getZ()).getBlock());
            }
        }
        if (this.y == 2.0) {
            baseblock = base.clone().add(0.0, 2.0, 0.0).getBlock();
            if (animationstep == 1) {
                this.addWater(base.clone().add(- direction.getX(), 2.0, - direction.getZ()).getBlock());
            } else if (animationstep == 3 || animationstep == 7 || animationstep == 2 || animationstep == 8) {
                this.addWater(baseblock);
            } else if (animationstep == 4 || animationstep == 6) {
                this.addWater(base.clone().add(direction.getX(), 2.0, direction.getZ()).getBlock());
            } else {
                this.addWater(base.clone().add(2.0 * direction.getX(), 2.0, 2.0 * direction.getZ()).getBlock());
            }
        }
    }

    private void addWater(Block block) {
        this.clearNearbyWater(block);
        if (GeneralMethods.isRegionProtectedFromBuild(this.player, "OctopusForm", block.getLocation())) {
            return;
        }
        if (TempBlock.isTempBlock(block)) {
            TempBlock tblock = TempBlock.get(block);
            if (!this.newblocks.contains(tblock)) {
                if (!this.blocks.contains(tblock)) {
                    tblock.setType(Material.WATER,(byte) 0);
                }
                this.newblocks.add(tblock);
            }
        } else if (WaterMethods.isWaterbendable(block, this.player) || block.getType() == Material.FIRE || block.getType() == Material.AIR) {
            this.newblocks.add(new TempBlock(block, Material.STATIONARY_WATER, (byte)8));
        }
    }

    private void clearNearbyWater(Block block) {
        BlockFace[] faces;
        BlockFace[] arrblockFace = faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN};
        int n = arrblockFace.length;
        int n2 = 0;
        while (n2 < n) {
            BlockFace face = arrblockFace[n2];
            Block rel = block.getRelative(face);
            if (WaterMethods.isWater(rel) && !TempBlock.isTempBlock(rel)) {
                FreezeMelt.freeze(this.player, rel);
            }
            ++n2;
        }
    }

    public static boolean wasBrokenFor(Player player, Block block) {
        if (instances.containsKey((Object)player)) {
            OctopusForm form = instances.get((Object)player);
            if (form.sourceblock == null) {
                return false;
            }
            if (form.sourceblock.equals((Object)block)) {
                return true;
            }
        }
        return false;
    }

    private void remove() {
        if (this.source != null) {
            this.source.revertBlock();
        }
        for (TempBlock block : this.blocks) {
            block.revertBlock();
        }
        instances.remove((Object)this.player);
    }

    private void returnWater() {
        if (this.source != null) {
            this.source.revertBlock();
            new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, this.source.getLocation().getBlock());
            this.source = null;
        } else {
            Location location = this.player.getLocation();
            double rtheta = Math.toRadians(this.startangle);
            Block block = location.clone().add(new Vector(this.radius * Math.cos(rtheta), 0.0, this.radius * Math.sin(rtheta))).getBlock();
            new com.projectkorra.projectkorra.waterbending.WaterReturn(this.player, block);
        }
    }

    public static void removeAll() {
        for (Player player : instances.keySet()) {
            instances.get((Object)player).remove();
        }
    }

    public static String getDescription() {
        return "This ability allows the waterbender to manipulate a large quantity of water into a form resembling that of an octopus. To use, click to select a water source. Then, hold sneak to channel this ability. While channeling, the water will form itself around you and has a chance to block incoming attacks. Additionally, you can click while channeling to attack things near you, dealing damage and knocking them back. Releasing shift at any time will dissipate the form.";
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getRange() {
        return this.range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getDamage() {
        return this.damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public long getInterval() {
        return this.interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public double getKnockback() {
        return this.knockback;
    }

    public void setKnockback(double knockback) {
        this.knockback = knockback;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getAttackRange() {
        return this.attackRange;
    }

    public void setAttackRange(double attackRange) {
        this.attackRange = attackRange;
    }
}

