/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.waterbending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.Lightning;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.WaterArmsFreeze;
import com.projectkorra.projectkorra.waterbending.WaterArmsSpear;
import com.projectkorra.projectkorra.waterbending.WaterArmsWhip;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterReturn;

public class WaterArms {
    private static FileConfiguration config = ProjectKorra.plugin.getConfig();
    public static ConcurrentHashMap<Player, WaterArms> instances = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Long> revert = new ConcurrentHashMap();
    private static Integer[] unbreakable = new Integer[]{7, 8, 9, 10, 11, 49, 54, 90, 119, 120, 130, 146};
    private Player player;
    private World world;
    private Arm activeArm = Arm.Right;
    private boolean cooldownLeft;
    private boolean cooldownRight;
    private boolean fullSource = true;
    private boolean leftArmConsumed = false;
    private boolean rightArmConsumed = false;
    private int lengthReduction = 0;
    private int initLength = config.getInt("Abilities.Water.WaterArms.Arms.InitialLength");
    private int sourceGrabRange = config.getInt("Abilities.Water.WaterArms.Arms.SourceGrabRange");
    private int maxPunches = config.getInt("Abilities.Water.WaterArms.Arms.MaxAttacks");
    private int maxIceBlasts = config.getInt("Abilities.Water.WaterArms.Arms.MaxIceShots");
    private int maxUses = config.getInt("Abilities.Water.WaterArms.Arms.MaxAlternateUsage");
    private long cooldown = config.getLong("Abilities.Water.WaterArms.Arms.Cooldown");
    private boolean canUsePlantSource = config.getBoolean("Abilities.Water.WaterArms.Arms.AllowPlantSource");
    private boolean lightningEnabled = config.getBoolean("Abilities.Water.WaterArms.Arms.Lightning.Enabled");
    private double lightningDamage = config.getDouble("Abilities.Water.WaterArms.Arms.Lightning.Damage");
    private boolean lightningKill = config.getBoolean("Abilities.Water.WaterArms.Arms.Lightning.KillUser");
    private static String sneakMsg = config.getString("Abilities.Water.WaterArms.SneakMessage");
    private int selectedSlot = 0;
    private int freezeSlot = 4;
    private long lastClickTime;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArms$Arm;

    public WaterArms(Player player) {
        if (instances.containsKey((Object)player)) {
            if (player.isSneaking()) {
                instances.get((Object)player).prepareCancel();
            } else {
                switch (player.getInventory().getHeldItemSlot()) {
                    case 0: {
                        if (!player.hasPermission("bending.ability.WaterArms.Pull")) break;
                        new com.projectkorra.projectkorra.waterbending.WaterArmsWhip(player, WaterArmsWhip.Whip.Pull);
                        break;
                    }
                    case 1: {
                        if (!player.hasPermission("bending.ability.WaterArms.Punch")) break;
                        new com.projectkorra.projectkorra.waterbending.WaterArmsWhip(player, WaterArmsWhip.Whip.Punch);
                        break;
                    }
                    case 2: {
                        if (!player.hasPermission("bending.ability.WaterArms.Grapple")) break;
                        new com.projectkorra.projectkorra.waterbending.WaterArmsWhip(player, WaterArmsWhip.Whip.Grapple);
                        break;
                    }
                    case 3: {
                        if (!player.hasPermission("bending.ability.WaterArms.Grab")) break;
                        new com.projectkorra.projectkorra.waterbending.WaterArmsWhip(player, WaterArmsWhip.Whip.Grab);
                        break;
                    }
                    case 4: {
                        if (!player.hasPermission("bending.ability.WaterArms.Freeze") || !WaterMethods.canIcebend(player)) break;
                        new com.projectkorra.projectkorra.waterbending.WaterArmsFreeze(player);
                        break;
                    }
                    case 5: {
                        if (!player.hasPermission("bending.ability.WaterArms.Spear")) break;
                        if (WaterMethods.canIcebend(player)) {
                            new com.projectkorra.projectkorra.waterbending.WaterArmsSpear(player, true);
                            break;
                        }
                        new com.projectkorra.projectkorra.waterbending.WaterArmsSpear(player, false);
                        break;
                    }
                }
            }
            return;
        }
        this.player = player;
        if (this.canUse(player) && this.prepare()) {
            this.world = player.getWorld();
            instances.put(player, this);
            MultiAbilityManager.bindMultiAbility(player, "WaterArms");
            if (ChatColor.stripColor((String)GeneralMethods.getBoundAbility(player)) == null) {
                this.remove();
                return;
            }
            player.sendMessage((Object)WaterMethods.getWaterColor() + sneakMsg + " " + GeneralMethods.getBoundAbility(player));
        }
    }

    private boolean canUse(Player player) {
        if (GeneralMethods.getBoundAbility(player) == null) {
            return false;
        }
        if (!GeneralMethods.canBend(player.getName(), "WaterArms")) {
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "WaterArms", player.getLocation())) {
            return false;
        }
        if (GeneralMethods.getBendingPlayer(player.getName()).isOnCooldown("WaterArms")) {
            return false;
        }
        if (GeneralMethods.getBoundAbility(player).equalsIgnoreCase("WaterArms")) {
            return true;
        }
        return false;
    }

    private boolean prepare() {
        Block sourceblock = WaterMethods.getWaterSourceBlock(this.player, this.sourceGrabRange, this.canUsePlantSource);
        if (sourceblock != null) {
            if (WaterMethods.isPlant(sourceblock)) {
                this.fullSource = false;
            }
            ParticleEffect.SMOKE_NORMAL.display(WaterMethods.getWaterSourceBlock(this.player, this.sourceGrabRange, this.canUsePlantSource).getLocation().clone().add(0.5, 0.5, 0.5), 0.0f, 0.0f, 0.0f, 0.0f, 4);
            return true;
        }
        if (WaterReturn.hasWaterBottle(this.player)) {
            WaterReturn.emptyWaterBottle(this.player);
            this.fullSource = false;
            return true;
        }
        return false;
    }

    private void progress() {
        if (!instances.containsKey((Object)this.player)) {
            return;
        }
        if (this.player.isDead() || !this.player.isOnline() || !this.world.equals((Object)this.player.getWorld())) {
            this.remove();
            return;
        }
        if (!GeneralMethods.getBendingPlayer(this.player.getName()).isToggled()) {
            this.remove();
            return;
        }
        if (!MultiAbilityManager.hasMultiAbilityBound(this.player, "WaterArms")) {
            this.remove();
            return;
        }
        if (this.maxPunches == 0 || this.maxUses == 0 || this.maxIceBlasts == 0 || this.leftArmConsumed && this.rightArmConsumed) {
            this.remove();
            return;
        }
        this.selectedSlot = this.player.getInventory().getHeldItemSlot();
        this.displayRightArm();
        this.displayLeftArm();
        if (this.lightningEnabled) {
            this.checkIfZapped();
        }
    }

    private boolean canPlaceBlock(Block block) {
        if (!(EarthMethods.isTransparentToEarthbending(this.player, block) || WaterMethods.isWater(block) && TempBlock.isTempBlock(block))) {
            return false;
        }
        return true;
    }

    public boolean displayRightArm() {
        Location r2;
        if (this.rightArmConsumed) {
            return false;
        }
        Location r1 = GeneralMethods.getRightSide(this.player.getLocation(), 1.0).add(0.0, 1.5, 0.0);
        if (!this.canPlaceBlock(r1.getBlock())) {
            return false;
        }
        if (!this.getRightHandPos().getBlock().getLocation().equals((Object)r1.getBlock().getLocation())) {
            new com.projectkorra.projectkorra.util.TempBlock(r1.getBlock(), Material.STATIONARY_WATER, (byte)5);
            revert.put(r1.getBlock(), 0L);
        }
        if (!this.canPlaceBlock((r2 = GeneralMethods.getRightSide(this.player.getLocation(), 2.0).add(0.0, 1.5, 0.0)).getBlock())) {
            return false;
        }
        new com.projectkorra.projectkorra.util.TempBlock(r2.getBlock(), Material.STATIONARY_WATER,(byte) 8);
        revert.put(r2.getBlock(), 0L);
        int j = 0;
        while (j <= this.initLength) {
            Location r3 = r2.clone().toVector().add(this.player.getLocation().clone().getDirection().multiply(j)).toLocation(this.player.getWorld());
            if (!this.canPlaceBlock(r3.getBlock())) {
                if (this.selectedSlot != this.freezeSlot || !r3.getBlock().getType().equals((Object)Material.ICE)) {
                    return false;
                }
            } else if (j >= 1 && this.selectedSlot == this.freezeSlot && WaterMethods.canIcebend(this.player)) {
                new com.projectkorra.projectkorra.util.TempBlock(r3.getBlock(), Material.ICE,(byte) 0);
                revert.put(r3.getBlock(), (long) 0);
            } else {
                new com.projectkorra.projectkorra.util.TempBlock(r3.getBlock(), Material.STATIONARY_WATER, (byte)8);
                revert.put(r3.getBlock(),(long) 0);
            }
            ++j;
        }
        return true;
    }

    public boolean displayLeftArm() {
        Location l2;
        if (this.leftArmConsumed) {
            return false;
        }
        Location l1 = GeneralMethods.getLeftSide(this.player.getLocation(), 1.0).add(0.0, 1.5, 0.0);
        if (!this.canPlaceBlock(l1.getBlock())) {
            return false;
        }
        if (!this.getLeftHandPos().getBlock().getLocation().equals((Object)l1.getBlock().getLocation())) {
            new com.projectkorra.projectkorra.util.TempBlock(l1.getBlock(), Material.STATIONARY_WATER, (byte)5);
            revert.put(l1.getBlock(), (long)0);
        }
        if (!this.canPlaceBlock((l2 = GeneralMethods.getLeftSide(this.player.getLocation(), 2.0).add(0.0, 1.5, 0.0)).getBlock())) {
            return false;
        }
        new com.projectkorra.projectkorra.util.TempBlock(l2.getBlock(), Material.STATIONARY_WATER,(byte) 8);
        revert.put(l2.getBlock(),(long) 0);
        int j = 0;
        while (j <= this.initLength) {
            Location l3 = l2.clone().toVector().add(this.player.getLocation().clone().getDirection().multiply(j)).toLocation(this.player.getWorld());
            if (!this.canPlaceBlock(l3.getBlock())) {
                if (this.selectedSlot != this.freezeSlot || !l3.getBlock().getType().equals((Object)Material.ICE)) {
                    return false;
                }
            } else if (j >= 1 && this.selectedSlot == this.freezeSlot && WaterMethods.canIcebend(this.player)) {
                new com.projectkorra.projectkorra.util.TempBlock(l3.getBlock(), Material.ICE,(byte) 0);
                revert.put(l3.getBlock(), 0L);
            } else {
                new com.projectkorra.projectkorra.util.TempBlock(l3.getBlock(), Material.STATIONARY_WATER,(byte) 8);
                revert.put(l3.getBlock(), 0L);
            }
            ++j;
        }
        return true;
    }

    private Location getRightHandPos() {
        return GeneralMethods.getRightSide(this.player.getLocation(), 0.34).add(0.0, 1.5, 0.0);
    }

    private Location getLeftHandPos() {
        return GeneralMethods.getLeftSide(this.player.getLocation(), 0.34).add(0.0, 1.5, 0.0);
    }

    public Location getRightArmEnd() {
        Location r1 = GeneralMethods.getRightSide(this.player.getLocation(), 2.0).add(0.0, 1.5, 0.0);
        return r1.clone().add(this.player.getLocation().getDirection().normalize().multiply(this.initLength));
    }

    public Location getLeftArmEnd() {
        Location l1 = GeneralMethods.getLeftSide(this.player.getLocation(), 2.0).add(0.0, 1.5, 0.0);
        return l1.clone().add(this.player.getLocation().getDirection().normalize().multiply(this.initLength));
    }

    private static void progressRevert(boolean ignoreTime) {
        for (Block block : revert.keySet()) {
            long time = revert.get((Object)block);
            if (System.currentTimeMillis() <= time && !ignoreTime) continue;
            if (TempBlock.isTempBlock(block)) {
                TempBlock.revertBlock(block, Material.AIR);
            }
            revert.remove((Object)block);
        }
    }

    private void checkIfZapped() {
        for (Lightning l : Lightning.instances.values()) {
            for (Lightning.Arc arc : l.getArcs()) {
                for (Block arm : revert.keySet()) {
                    for (Location loc : arc.getPoints()) {
                        if (arm.getLocation().getWorld() != loc.getWorld() || loc.distance(arm.getLocation()) > 2.5) continue;
                        for (Location l1 : WaterArms.getOffsetLocations(4, arm.getLocation(), 1.25)) {
                            FireMethods.playLightningbendingParticle(l1);
                        }
                        if (this.lightningKill) {
                            GeneralMethods.damageEntity(l.getPlayer(), (Entity)this.player, 60.0, Element.Water, "Electrocution");
                            continue;
                        }
                        GeneralMethods.damageEntity(l.getPlayer(), (Entity)this.player, this.lightningDamage, Element.Water, "Electrocution");
                    }
                }
            }
        }
    }

    private static List<Location> getOffsetLocations(int amount, Location location, double offset) {
        ArrayList<Location> locations = new ArrayList<Location>();
        int i = 0;
        while (i < amount) {
            locations.add(location.clone().add((double)((float)(Math.random() * offset)), (double)((float)(Math.random() * offset)), (double)((float)(Math.random() * offset))));
            ++i;
        }
        return locations;
    }

    public static void remove(Player player) {
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).remove();
        }
    }

    public void remove() {
        MultiAbilityManager.unbindMultiAbility(this.player);
        if (this.player.isOnline()) {
            GeneralMethods.getBendingPlayer(this.player.getName()).addCooldown("WaterArms", this.cooldown);
        }
        instances.remove((Object)this.player);
    }

    public void prepareCancel() {
        if (System.currentTimeMillis() < this.lastClickTime + 500) {
            this.remove();
        } else {
            this.lastClickTime = System.currentTimeMillis();
        }
    }

    public static void progressAll() {
        WaterArms.progressRevert(false);
        for (Player p : instances.keySet()) {
            instances.get((Object)p).progress();
        }
        WaterArmsWhip.progressAll();
        WaterArmsFreeze.progressAll();
        WaterArmsSpear.progressAll();
    }

    public static void removeAll() {
        WaterArms.progressRevert(true);
        revert.clear();
        instances.clear();
        WaterArmsWhip.removeAll();
        WaterArmsFreeze.removeAll();
        WaterArmsSpear.removeAll();
    }

    public static boolean isUnbreakable(Block block) {
        if (Arrays.asList(unbreakable).contains(block.getTypeId())) {
            return true;
        }
        return false;
    }

    public static void displayBoundMsg(Player player) {
        player.sendMessage((Object)WaterMethods.getWaterColor() + sneakMsg + " " + GeneralMethods.getBoundAbility(player));
    }

    public void displayBoundMsg() {
        this.player.sendMessage((Object)WaterMethods.getWaterColor() + sneakMsg + " " + GeneralMethods.getBoundAbility(this.player));
    }

    public Arm getActiveArm() {
        return this.activeArm;
    }

    public void switchActiveArm() {
        this.activeArm = this.activeArm.equals((Object)Arm.Right) ? Arm.Left : Arm.Right;
    }

    public Arm switchPreferredArm() {
        this.switchActiveArm();
        if (this.activeArm.equals((Object)Arm.Left) && !this.displayLeftArm()) {
            this.switchActiveArm();
        }
        if (this.activeArm.equals((Object)Arm.Right) && !this.displayRightArm()) {
            this.switchActiveArm();
        }
        return this.getActiveArm();
    }

    public boolean canDisplayActiveArm() {
        switch (WaterArms.$SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArms$Arm()[this.activeArm.ordinal()]) {
            case 2: {
                return this.displayLeftArm();
            }
            case 1: {
                return this.displayRightArm();
            }
        }
        return false;
    }

    public Location getActiveArmEnd() {
        switch (WaterArms.$SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArms$Arm()[this.activeArm.ordinal()]) {
            case 2: {
                return this.getLeftArmEnd();
            }
            case 1: {
                return this.getRightArmEnd();
            }
        }
        return null;
    }

    public static boolean hasPlayer(Player player) {
        if (instances.containsKey((Object)player)) {
            return true;
        }
        return false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Boolean isFullSource() {
        return this.fullSource;
    }

    public boolean getLeftArmConsumed() {
        return this.leftArmConsumed;
    }

    public void setLeftArmConsumed(boolean consumed) {
        this.leftArmConsumed = consumed;
    }

    public boolean getRightArmConsumed() {
        return this.rightArmConsumed;
    }

    public void setRightArmConsumed(boolean consumed) {
        this.rightArmConsumed = consumed;
    }

    public Integer getLengthReduction() {
        return this.lengthReduction;
    }

    public void setLengthReduction(int lengthReduction) {
        this.lengthReduction = lengthReduction;
    }

    public Integer getMaxPunches() {
        return this.maxPunches;
    }

    public void setMaxPunches(int maxPunches) {
        this.maxPunches = maxPunches;
    }

    public Integer getMaxUses() {
        return this.maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public Integer getMaxIceBlasts() {
        return this.maxIceBlasts;
    }

    public void setMaxIceBlasts(int maxIceBlasts) {
        this.maxIceBlasts = maxIceBlasts;
    }

    public boolean canLightningDamage() {
        return this.lightningEnabled;
    }

    public void setCanLightningDamage(boolean lightningEnabled) {
        this.lightningEnabled = lightningEnabled;
    }

    public double getLightningDamage() {
        return this.lightningDamage;
    }

    public void setLightningDamage(double lightningDamage) {
        this.lightningDamage = lightningDamage;
    }

    public boolean isLeftArmCooldown() {
        return this.cooldownLeft;
    }

    public void setLeftArmCooldown(boolean cooldown) {
        this.cooldownLeft = cooldown;
    }

    public boolean isRightArmCooldown() {
        return this.cooldownRight;
    }

    public void setRightArmCooldown(boolean cooldown) {
        this.cooldownRight = cooldown;
    }

    public void setActiveArmCooldown(boolean cooldown) {
        switch (WaterArms.$SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArms$Arm()[this.activeArm.ordinal()]) {
            case 2: {
                this.setLeftArmCooldown(cooldown);
                return;
            }
            case 1: {
                this.setRightArmCooldown(cooldown);
                return;
            }
        }
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArms$Arm() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArms$Arm;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[Arm.values().length];
        try {
            arrn[Arm.Left.ordinal()] = 2;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[Arm.Right.ordinal()] = 1;
        }
        catch (NoSuchFieldError v2) {}
        $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArms$Arm = arrn;
        return $SWITCH_TABLE$com$projectkorra$projectkorra$waterbending$WaterArms$Arm;
    }

    public static enum Arm {
        Right,
        Left;
        

    }

}

