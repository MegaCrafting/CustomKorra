/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.projectkorra.projectkorra.earthbending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LavaFlow {
    public static final Material REVERT_MATERIAL = Material.STONE;
    public static final ArrayList<LavaFlow> instances = new ArrayList();
    public static final ArrayList<TempBlock> TEMP_LAVA_BLOCKS = new ArrayList();
    public static final ArrayList<TempBlock> TEMP_LAND_BLOCKS = new ArrayList();
    public static final long SHIFT_COOLDOWN = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.LavaFlow.ShiftCooldown");
    public static final long CLICK_LAVA_DELAY = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.LavaFlow.ClickLavaStartDelay");
    public static final long CLICK_LAND_DELAY = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.LavaFlow.ClickLandStartDelay");
    public static final long CLICK_LAVA_COOLDOWN = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.LavaFlow.ClickLavaCooldown");
    public static final long CLICK_LAND_COOLDOWN = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.LavaFlow.ClickLandCooldown");
    public static final long CLICK_LAVA_CLEANUP_DELAY = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.LavaFlow.ClickLavaCleanupDelay");
    public static final long CLICK_LAND_CLEANUP_DELAY = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.LavaFlow.ClickLandCleanupDelay");
    public static final long SHIFT_REMOVE_DELAY = ProjectKorra.plugin.getConfig().getLong("Abilities.Earth.LavaFlow.ShiftCleanupDelay");
    public static final double CLICK_RANGE = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ClickRange");
    public static final double CLICK_LAVA_RADIUS = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ClickRadius");
    public static final double CLICK_LAND_RADIUS = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ClickRadius");
    public static final double SHIFT_PLATFORM_RADIUS = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ShiftPlatformRadius");
    public static final double SHIFT_MAX_RADIUS = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ShiftRadius");
    public static final double SHIFT_REMOVE_SPEED = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ShiftRemoveSpeed");
    public static final double LAVA_CREATE_SPEED = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ClickLavaCreateSpeed");
    public static final double LAND_CREATE_SPEED = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ClickLandCreateSpeed");
    public static final double SHIFT_FLOW_SPEED = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ShiftFlowSpeed");
    public static final int UPWARD_FLOW = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.LavaFlow.UpwardFlow");
    public static final int DOWNWARD_FLOW = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.LavaFlow.DownwardFlow");
    public static final boolean ALLOW_NATURAL_FLOW = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Earth.LavaFlow.AllowNaturalFlow");
    public static final double PARTICLE_DENSITY = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.LavaFlow.ParticleDensity");
    private static final double PARTICLE_OFFSET = 3.0;
    private Player player;
    private BendingPlayer bplayer;
    private long time;
    private int shiftCounter;
    private boolean removing;
    private boolean makeLava;
    private boolean clickIsFinished;
    private double currentRadius;
    private double shiftPlatformRadius;
    private double shiftMaxRadius;
    private double shiftFlowSpeed;
    private double shiftRemoveSpeed;
    private double shiftRemoveDelay;
    private double particleDensity;
    private double clickRange;
    private double clickLavaRadius;
    private double clickLandRadius;
    private long clickLavaDelay;
    private long clickLandDelay;
    private long clickLavaCooldown;
    private long clickLandCooldown;
    private long shiftCooldown;
    private long clickLavaCleanupDelay;
    private long clickLandCleanupDelay;
    private double lavaCreateSpeed;
    private double landCreateSpeed;
    private int upwardFlow;
    private int downwardFlow;
    private boolean shiftIsFinished;
    private boolean allowNaturalFlow;
    private AbilityType type;
    private Location origin;
    private ArrayList<TempBlock> affectedBlocks;
    private ArrayList<BukkitRunnable> tasks;

    public LavaFlow(Player player, AbilityType type) {
        if (!EarthMethods.canLavabend(player)) {
            return;
        }
        this.time = System.currentTimeMillis();
        this.player = player;
        this.type = type;
        this.bplayer = GeneralMethods.getBendingPlayer(player.getName());
        this.shiftCounter = 0;
        this.currentRadius = 0.0;
        this.removing = false;
        this.makeLava = true;
        this.clickIsFinished = false;
        this.affectedBlocks = new ArrayList();
        this.tasks = new ArrayList();
        this.shiftCooldown = SHIFT_COOLDOWN;
        this.shiftPlatformRadius = SHIFT_PLATFORM_RADIUS;
        this.shiftMaxRadius = SHIFT_MAX_RADIUS;
        this.shiftFlowSpeed = SHIFT_FLOW_SPEED;
        this.shiftRemoveSpeed = SHIFT_REMOVE_SPEED;
        this.shiftRemoveDelay = SHIFT_REMOVE_DELAY;
        this.particleDensity = PARTICLE_DENSITY;
        this.clickRange = CLICK_RANGE;
        this.clickLavaRadius = CLICK_LAVA_RADIUS;
        this.clickLandRadius = CLICK_LAND_RADIUS;
        this.clickLavaDelay = CLICK_LAVA_DELAY;
        this.clickLandDelay = CLICK_LAND_DELAY;
        this.clickLavaCooldown = CLICK_LAVA_COOLDOWN;
        this.clickLandCooldown = CLICK_LAND_COOLDOWN;
        this.clickLavaCleanupDelay = CLICK_LAVA_CLEANUP_DELAY;
        this.clickLandCleanupDelay = CLICK_LAND_CLEANUP_DELAY;
        this.lavaCreateSpeed = LAVA_CREATE_SPEED;
        this.landCreateSpeed = LAND_CREATE_SPEED;
        this.upwardFlow = UPWARD_FLOW;
        this.downwardFlow = DOWNWARD_FLOW;
        this.allowNaturalFlow = ALLOW_NATURAL_FLOW;
        if (AvatarState.isAvatarState(player)) {
            this.shiftCooldown = 0;
            this.clickLavaCooldown = 0;
            this.clickLandCooldown = 0;
            this.shiftPlatformRadius = AvatarState.getValue(this.shiftPlatformRadius);
            this.shiftMaxRadius = AvatarState.getValue(this.shiftMaxRadius);
            this.shiftFlowSpeed = AvatarState.getValue(this.shiftFlowSpeed);
            this.shiftRemoveDelay = AvatarState.getValue(this.shiftRemoveDelay);
            this.clickRange = AvatarState.getValue(this.clickRange);
            this.clickLavaRadius = AvatarState.getValue(this.clickLavaRadius);
            this.clickLandRadius = AvatarState.getValue(this.clickLandRadius);
            this.clickLavaCleanupDelay = (long)AvatarState.getValue(this.clickLavaCleanupDelay);
            this.clickLandCleanupDelay = (long)AvatarState.getValue(this.clickLandCleanupDelay);
            this.lavaCreateSpeed = AvatarState.getValue(this.lavaCreateSpeed);
            this.landCreateSpeed = AvatarState.getValue(this.landCreateSpeed);
            this.upwardFlow = AvatarState.getValue(this.upwardFlow);
            this.downwardFlow = AvatarState.getValue(this.downwardFlow);
        }
        if (type == AbilityType.SHIFT) {
            ArrayList<LavaFlow> shiftFlows = LavaFlow.getLavaFlow(player, AbilityType.SHIFT);
            if (shiftFlows.size() > 0 && !player.isSneaking()) {
                for (LavaFlow lf : shiftFlows) {
                    ++lf.shiftCounter;
                }
            }
            if (this.bplayer.isOnCooldown("lavaflowshift")) {
                this.remove();
                return;
            }
            instances.add(this);
        } else if (type == AbilityType.CLICK) {
            long cooldown;
            Block sourceBlock = BlockSource.getEarthOrLavaSourceBlock(player, this.clickRange, ClickType.LEFT_CLICK);
            if (sourceBlock == null) {
                this.remove();
                return;
            }
            this.origin = sourceBlock.getLocation();
            this.makeLava = !LavaFlow.isLava(sourceBlock);
            long l = cooldown = this.makeLava ? this.clickLavaCooldown : this.clickLandCooldown;
            if (this.makeLava) {
                if (this.bplayer.isOnCooldown("lavaflowmakelava")) {
                    this.remove();
                    return;
                }
                this.bplayer.addCooldown("lavaflowmakelava", cooldown);
            }
            if (!this.makeLava) {
                if (this.bplayer.isOnCooldown("lavaflowmakeland")) {
                    this.remove();
                    return;
                }
                this.bplayer.addCooldown("lavaflowmakeland", cooldown);
            }
            instances.add(this);
        }
    }

    public void progress() {
        if (this.shiftCounter > 0 && this.type == AbilityType.SHIFT) {
            this.remove();
            return;
        }
        if (this.removing) {
            return;
        }
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return;
        }
        if (this.type == AbilityType.SHIFT) {
            if ((double)(System.currentTimeMillis() - this.time) > this.shiftRemoveDelay) {
                this.remove();
                return;
            }
            if (!this.player.isSneaking() && !this.removing) {
                if (this.affectedBlocks.size() > 0) {
                    this.removeOnDelay();
                    this.removing = true;
                    this.bplayer.addCooldown("lavaflowshift", this.shiftCooldown);
                } else {
                    this.remove();
                }
                return;
            }
            String ability = GeneralMethods.getBoundAbility(this.player);
            if (ability == null) {
                this.remove();
                return;
            }
            if (!ability.equalsIgnoreCase("LavaFlow") || !GeneralMethods.canBend(this.player.getName(), "LavaFlow")) {
                this.remove();
                return;
            }
            if (this.origin == null) {
                this.origin = this.player.getLocation().clone().add(0.0, -1.0, 0.0);
                if (!EarthMethods.isEarthbendable(this.player, this.origin.getBlock()) && this.origin.getBlock().getType() != Material.GLOWSTONE) {
                    this.remove();
                    return;
                }
            }
            double x = - this.currentRadius;
            while (x <= this.currentRadius + 3.0) {
                double z = - this.currentRadius;
                while (z < this.currentRadius + 3.0) {
                    Location loc = this.origin.clone().add(x, 0.0, z);
                    Block block = GeneralMethods.getTopBlock(loc, this.upwardFlow, this.downwardFlow);
                    if (block != null) {
                        double dSquared = LavaFlow.distanceSquaredXZ(block.getLocation(), this.origin);
                        if (!LavaFlow.isLava(block) && dSquared > Math.pow(this.shiftPlatformRadius, 2.0)) {
                            if (dSquared < Math.pow(this.currentRadius, 2.0) && !GeneralMethods.isRegionProtectedFromBuild(this.player, "LavaFlow", block.getLocation())) {
                                if (dSquared < this.shiftPlatformRadius * 4.0 || LavaFlow.getAdjacentLavaBlocks(block.getLocation()).size() > 0) {
                                    this.createLava(block);
                                }
                            } else if (Math.random() < this.particleDensity && dSquared < Math.pow(this.currentRadius + this.particleDensity, 2.0) && this.currentRadius + this.particleDensity < this.shiftMaxRadius && GeneralMethods.rand.nextInt(3) == 0) {
                                ParticleEffect.LAVA.display(loc, (float)Math.random(), (float)Math.random(), (float)Math.random(), 0.0f, 1);
                            }
                        }
                    }
                    z += 1.0;
                }
                if (!this.shiftIsFinished && GeneralMethods.rand.nextInt(10) == 0) {
                    ParticleEffect.LAVA.display(this.player.getLocation(), (float)Math.random(), (float)Math.random(), (float)Math.random(), 0.0f, 1);
                }
                this.currentRadius += this.shiftFlowSpeed;
                if (this.currentRadius > this.shiftMaxRadius) {
                    this.currentRadius = this.shiftMaxRadius;
                    this.shiftIsFinished = true;
                }
                x += 1.0;
            }
        } else if (this.type == AbilityType.CLICK) {
            long curTime = System.currentTimeMillis() - this.time;
            double delay = this.makeLava ? this.clickLavaDelay : this.clickLandDelay;
            if (this.makeLava && curTime > this.clickLavaCleanupDelay) {
                this.remove();
                return;
            }
            if (!this.makeLava && curTime > this.clickLandCleanupDelay) {
                this.remove();
                return;
            }
            if (!this.makeLava && (double)curTime < delay) {
                return;
            }
            if (this.makeLava && (double)curTime < delay) {
                double x = - this.clickLavaRadius;
                while (x <= this.clickLavaRadius) {
                    double z = - this.clickLavaRadius;
                    while (z <= this.clickLavaRadius) {
                        Location loc = this.origin.clone().add(x, 0.0, z);
                        Block tempBlock = GeneralMethods.getTopBlock(loc, this.upwardFlow, this.downwardFlow);
                        if (tempBlock != null && !LavaFlow.isLava(tempBlock) && Math.random() < PARTICLE_DENSITY && tempBlock.getLocation().distanceSquared(this.origin) <= Math.pow(this.clickLavaRadius, 2.0) && GeneralMethods.rand.nextInt(3) == 0) {
                            ParticleEffect.LAVA.display(loc, (float)Math.random(), (float)Math.random(), (float)Math.random(), 0.0f, 1);
                        }
                        z += 1.0;
                    }
                    x += 1.0;
                }
                return;
            }
            if (!this.clickIsFinished) {
                this.clickIsFinished = true;
                double radius = this.makeLava ? this.clickLavaRadius : this.clickLandRadius;
                double x = - radius;
                while (x <= radius) {
                    double z = - radius;
                    while (z <= radius) {
                        double dSquared;
                        Location loc = this.origin.clone().add(x, 0.0, z);
                        Block tempBlock = GeneralMethods.getTopBlock(loc, this.upwardFlow, this.downwardFlow);
                        if (tempBlock != null && (dSquared = LavaFlow.distanceSquaredXZ(tempBlock.getLocation(), this.origin)) < Math.pow(radius, 2.0) && !GeneralMethods.isRegionProtectedFromBuild(this.player, "LavaFlow", loc)) {
                            if (this.makeLava && !LavaFlow.isLava(tempBlock)) {
                                this.clickIsFinished = false;
                                if (Math.random() < this.lavaCreateSpeed) {
                                    this.createLava(tempBlock);
                                } else if (GeneralMethods.rand.nextInt(4) == 0) {
                                    ParticleEffect.LAVA.display(loc, (float)Math.random(), (float)Math.random(), (float)Math.random(), 0.0f, 1);
                                }
                            } else if (!this.makeLava && LavaFlow.isLava(tempBlock)) {
                                this.clickIsFinished = false;
                                if (Math.random() < this.landCreateSpeed) {
                                    this.removeLava(tempBlock);
                                }
                            }
                        }
                        z += 1.0;
                    }
                    x += 1.0;
                }
                return;
            }
        }
    }

    public void createLava(Block block) {
        if (LavaFlow.isEarthbendableMaterial(block.getType(), this.player)) {
            TempBlock tblock = new TempBlock(block, Material.STATIONARY_LAVA, (byte) 0);
            TEMP_LAVA_BLOCKS.add(tblock);
            this.affectedBlocks.add(tblock);
            if (this.allowNaturalFlow) {
                TempBlock.instances.remove((Object)block);
            }
        }
    }

    public void removeLava(Block testBlock) {
        int i = 0;
        while (i < TEMP_LAVA_BLOCKS.size()) {
            TempBlock tblock = TEMP_LAVA_BLOCKS.get(i);
            Block block = tblock.getBlock();
            if (block.equals((Object)testBlock)) {
                tblock.revertBlock();
                TEMP_LAVA_BLOCKS.remove(i);
                this.affectedBlocks.remove(tblock);
                return;
            }
            ++i;
        }
        TempBlock tblock = new TempBlock(testBlock, REVERT_MATERIAL, testBlock.getData());
        this.affectedBlocks.add(tblock);
        TEMP_LAND_BLOCKS.add(tblock);
    }

    public void removeOnDelay() {
        BukkitRunnable br = new BukkitRunnable(){

            public void run() {
                LavaFlow.this.remove();
            }
        };
        br.runTaskLater((Plugin)ProjectKorra.plugin, (long)(this.shiftRemoveDelay / 1000.0 * 20.0));
        this.tasks.add(br);
    }

    public void remove() {
        instances.remove(this);
        int i = this.affectedBlocks.size() - 1;
        while (i > -1) {
            final TempBlock tblock = this.affectedBlocks.get(i);
            new BukkitRunnable(){

                public void run() {
                    tblock.revertBlock();
                }
            }.runTaskLater((Plugin)ProjectKorra.plugin, (long)((double)i / this.shiftRemoveSpeed));
            if (TEMP_LAVA_BLOCKS.contains(tblock)) {
                this.affectedBlocks.remove(tblock);
                TEMP_LAVA_BLOCKS.remove(tblock);
            }
            if (TEMP_LAND_BLOCKS.contains(tblock)) {
                this.affectedBlocks.remove(tblock);
                TEMP_LAND_BLOCKS.remove(tblock);
            }
            --i;
        }
        for (BukkitRunnable task : this.tasks) {
            task.cancel();
        }
    }

    public void removeInstantly() {
        instances.remove(this);
        int i = this.affectedBlocks.size() - 1;
        while (i > -1) {
            TempBlock tblock = this.affectedBlocks.get(i);
            tblock.revertBlock();
            if (TEMP_LAVA_BLOCKS.contains(tblock)) {
                this.affectedBlocks.remove(tblock);
                TEMP_LAVA_BLOCKS.remove(tblock);
            }
            if (TEMP_LAND_BLOCKS.contains(tblock)) {
                this.affectedBlocks.remove(tblock);
                TEMP_LAND_BLOCKS.remove(tblock);
            }
            --i;
        }
        for (BukkitRunnable task : this.tasks) {
            task.cancel();
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
            instances.get(i).removeInstantly();
            --i;
        }
    }

    public static ArrayList<Block> getAdjacentBlocks(Location loc) {
        ArrayList<Block> list = new ArrayList<Block>();
        Block block = loc.getBlock();
        int x = -1;
        while (x <= 1) {
            int y = -1;
            while (y <= 1) {
                int z = -1;
                while (z <= 1) {
                    if (x != 0 || y != 0 || z != 0) {
                        list.add(block.getLocation().add((double)x, (double)y, (double)z).getBlock());
                    }
                    ++z;
                }
                ++y;
            }
            ++x;
        }
        return list;
    }

    public static ArrayList<Block> getAdjacentLavaBlocks(Location loc) {
        ArrayList<Block> list = LavaFlow.getAdjacentBlocks(loc);
        int i = 0;
        while (i < list.size()) {
            Block block = list.get(i);
            if (!LavaFlow.isLava(block)) {
                list.remove(i);
                --i;
            }
            ++i;
        }
        return list;
    }

    public static boolean isEarthbendableMaterial(Material mat, Player player) {
        for (String s : ProjectKorra.plugin.getConfig().getStringList("Properties.Earth.EarthbendableBlocks")) {
            if (mat != Material.getMaterial((String)s)) continue;
            return true;
        }
        if (ProjectKorra.plugin.getConfig().getStringList("Properties.Earth.MetalBlocks").contains(mat.toString()) && EarthMethods.canMetalbend(player)) {
            return true;
        }
        return false;
    }

    public static boolean isLava(Block block) {
        if (block.getType() != Material.LAVA && block.getType() != Material.STATIONARY_LAVA) {
            return false;
        }
        return true;
    }

    public static double distanceSquaredXZ(Location l1, Location l2) {
        Location temp1 = l1.clone();
        Location temp2 = l2.clone();
        temp1.setY(0.0);
        temp2.setY(0.0);
        return temp1.distanceSquared(temp2);
    }

    public static ArrayList<LavaFlow> getLavaFlow(Player player) {
        ArrayList<LavaFlow> list = new ArrayList<LavaFlow>();
        for (LavaFlow lf : instances) {
            if (lf.player == null || lf.player != player) continue;
            list.add(lf);
        }
        return list;
    }

    public static ArrayList<LavaFlow> getLavaFlow(Player player, AbilityType type) {
        ArrayList<LavaFlow> list = new ArrayList<LavaFlow>();
        for (LavaFlow lf : instances) {
            if (lf.player == null || lf.player != player || lf.type == null || lf.type != type) continue;
            list.add(lf);
        }
        return list;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRemoving() {
        return this.removing;
    }

    public void setRemoving(boolean removing) {
        this.removing = removing;
    }

    public boolean isMakeLava() {
        return this.makeLava;
    }

    public void setMakeLava(boolean makeLava) {
        this.makeLava = makeLava;
    }

    public boolean isClickIsFinished() {
        return this.clickIsFinished;
    }

    public void setClickIsFinished(boolean clickIsFinished) {
        this.clickIsFinished = clickIsFinished;
    }

    public double getCurrentRadius() {
        return this.currentRadius;
    }

    public void setCurrentRadius(double currentRadius) {
        this.currentRadius = currentRadius;
    }

    public double getShiftPlatformRadius() {
        return this.shiftPlatformRadius;
    }

    public void setShiftPlatformRadius(double shiftPlatformRadius) {
        this.shiftPlatformRadius = shiftPlatformRadius;
    }

    public double getShiftMaxRadius() {
        return this.shiftMaxRadius;
    }

    public void setShiftMaxRadius(double shiftMaxRadius) {
        this.shiftMaxRadius = shiftMaxRadius;
    }

    public double getShiftFlowSpeed() {
        return this.shiftFlowSpeed;
    }

    public void setShiftFlowSpeed(double shiftFlowSpeed) {
        this.shiftFlowSpeed = shiftFlowSpeed;
    }

    public double getShiftRemoveSpeed() {
        return this.shiftRemoveSpeed;
    }

    public void setShiftRemoveSpeed(double shiftRemoveSpeed) {
        this.shiftRemoveSpeed = shiftRemoveSpeed;
    }

    public double getShiftRemoveDelay() {
        return this.shiftRemoveDelay;
    }

    public void setShiftRemoveDelay(double shiftRemoveDelay) {
        this.shiftRemoveDelay = shiftRemoveDelay;
    }

    public double getParticleDensity() {
        return this.particleDensity;
    }

    public void setParticleDensity(double particleDensity) {
        this.particleDensity = particleDensity;
    }

    public double getClickRange() {
        return this.clickRange;
    }

    public void setClickRange(double clickRange) {
        this.clickRange = clickRange;
    }

    public double getClickLavaRadius() {
        return this.clickLavaRadius;
    }

    public void setClickLavaRadius(double clickLavaRadius) {
        this.clickLavaRadius = clickLavaRadius;
    }

    public double getClickLandRadius() {
        return this.clickLandRadius;
    }

    public void setClickLandRadius(double clickLandRadius) {
        this.clickLandRadius = clickLandRadius;
    }

    public long getClickLavaDelay() {
        return this.clickLavaDelay;
    }

    public void setClickLavaDelay(long clickLavaDelay) {
        this.clickLavaDelay = clickLavaDelay;
    }

    public long getClickLandDelay() {
        return this.clickLandDelay;
    }

    public void setClickLandDelay(long clickLandDelay) {
        this.clickLandDelay = clickLandDelay;
    }

    public long getClickLavaCooldown() {
        return this.clickLavaCooldown;
    }

    public void setClickLavaCooldown(long clickLavaCooldown) {
        this.clickLavaCooldown = clickLavaCooldown;
        if (this.player != null) {
            this.bplayer.addCooldown("lavaflowmakelava", clickLavaCooldown);
        }
    }

    public long getClickLandCooldown() {
        return this.clickLandCooldown;
    }

    public void setClickLandCooldown(long clickLandCooldown) {
        this.clickLandCooldown = clickLandCooldown;
        if (this.player != null) {
            this.bplayer.addCooldown("lavaflowmakeland", clickLandCooldown);
        }
    }

    public long getShiftCooldown() {
        return this.shiftCooldown;
    }

    public void setShiftCooldown(long shiftCooldown) {
        this.shiftCooldown = shiftCooldown;
        if (this.player != null) {
            this.bplayer.addCooldown("lavaflowshift", shiftCooldown);
        }
    }

    public long getClickLavaCleanupDelay() {
        return this.clickLavaCleanupDelay;
    }

    public void setClickLavaCleanupDelay(long clickLavaCleanupDelay) {
        this.clickLavaCleanupDelay = clickLavaCleanupDelay;
    }

    public long getClickLandCleanupDelay() {
        return this.clickLandCleanupDelay;
    }

    public void setClickLandCleanupDelay(long clickLandCleanupDelay) {
        this.clickLandCleanupDelay = clickLandCleanupDelay;
    }

    public double getLavaCreateSpeed() {
        return this.lavaCreateSpeed;
    }

    public void setLavaCreateSpeed(double lavaCreateSpeed) {
        this.lavaCreateSpeed = lavaCreateSpeed;
    }

    public double getLandCreateSpeed() {
        return this.landCreateSpeed;
    }

    public void setLandCreateSpeed(double landCreateSpeed) {
        this.landCreateSpeed = landCreateSpeed;
    }

    public int getUpwardFlow() {
        return this.upwardFlow;
    }

    public void setUpwardFlow(int upwardFlow) {
        this.upwardFlow = upwardFlow;
    }

    public int getDownwardFlow() {
        return this.downwardFlow;
    }

    public void setDownwardFlow(int downwardFlow) {
        this.downwardFlow = downwardFlow;
    }

    public boolean isAllowNaturalFlow() {
        return this.allowNaturalFlow;
    }

    public void setAllowNaturalFlow(boolean allowNaturalFlow) {
        this.allowNaturalFlow = allowNaturalFlow;
    }

    public AbilityType getType() {
        return this.type;
    }

    public void setType(AbilityType type) {
        this.type = type;
    }

    public Location getOrigin() {
        return this.origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public ArrayList<TempBlock> getAffectedBlocks() {
        return this.affectedBlocks;
    }

    public void setAffectedBlocks(ArrayList<TempBlock> affectedBlocks) {
        this.affectedBlocks = affectedBlocks;
    }

    public ArrayList<BukkitRunnable> getTasks() {
        return this.tasks;
    }

    public void setTasks(ArrayList<BukkitRunnable> tasks) {
        this.tasks = tasks;
    }

    public int getShiftCounter() {
        return this.shiftCounter;
    }

    public void setShiftCounter(int shiftCounter) {
        this.shiftCounter = shiftCounter;
    }

    public static enum AbilityType {
        SHIFT,
        CLICK;
    }

}

