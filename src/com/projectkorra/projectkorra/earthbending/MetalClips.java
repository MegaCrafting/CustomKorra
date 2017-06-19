/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Skeleton
 *  org.bukkit.entity.Zombie
 *  org.bukkit.inventory.EntityEquipment
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.earthbending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.EarthMethods;

public class MetalClips {
    public static ConcurrentHashMap<Player, MetalClips> instances = new ConcurrentHashMap();
    public static ConcurrentHashMap<Entity, Integer> clipped = new ConcurrentHashMap();
    public static int armorTime = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.MetalClips.Duration");
    public static int crushInterval = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.MetalClips.DamageInterval");
    public static int cooldown = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.MetalClips.Cooldown");
    public static int crushDamage = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.MetalClips.Damage");
    public static int magnetRange = ProjectKorra.plugin.getConfig().getInt("Abilities.Earth.MetalClips.MagnetRange");
    public static double magnetPower = ProjectKorra.plugin.getConfig().getDouble("Abilities.Earth.MetalClips.MagnetPower");
    public static Material[] metalItems = new Material[]{Material.IRON_INGOT, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.IRON_BLOCK, Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_SWORD, Material.IRON_HOE, Material.IRON_SPADE, Material.IRON_DOOR};
    private static Player player;
    private static LivingEntity targetent;
    private boolean isBeingWorn = false;
    private boolean isControlling = false;
    private boolean canThrow = false;
    private boolean magnetized = false;
    public int metalclips = 0;
    public int var;
    private long startTime;
    private long time;
    private double lastDistanceCheck;
    private static ItemStack[] oldarmor;
    private List<Item> trackedIngots = new ArrayList<Item>();

    public MetalClips(Player p, int var) {
        if (instances.containsKey((Object)p)) {
            return;
        }
        player = p;
        this.canThrow = ProjectKorra.plugin.getConfig().getBoolean("Abilities.Earth.MetalClips.ThrowEnabled") && player.hasPermission("bending.ability.metalclips.throw");
        this.var = var;
        if (!this.isEligible()) {
            return;
        }
        if (var == 0) {
            this.shootMetal();
        } else if (var == 1) {
            this.magnet();
        }
        instances.put(p, this);
    }

    public static ItemStack getOriginalHelmet(LivingEntity ent) {
        if (clipped.containsKey((Object)ent)) {
            return oldarmor[3];
        }
        return null;
    }

    public static ItemStack getOriginalChestplate(LivingEntity ent) {
        if (clipped.containsKey((Object)ent)) {
            return oldarmor[2];
        }
        return null;
    }

    public static ItemStack getOriginalLeggings(LivingEntity ent) {
        if (clipped.containsKey((Object)ent)) {
            return oldarmor[1];
        }
        return null;
    }

    public static ItemStack getOriginalBoots(LivingEntity ent) {
        if (clipped.containsKey((Object)ent)) {
            return oldarmor[0];
        }
        return null;
    }

    public boolean isEligible() {
        BendingPlayer bplayer = GeneralMethods.getBendingPlayer(player.getName());
        if (!GeneralMethods.canBend(player.getName(), "MetalClips")) {
            return false;
        }
        if (GeneralMethods.getBoundAbility(player) == null) {
            return false;
        }
        if (!GeneralMethods.getBoundAbility(player).equalsIgnoreCase("MetalClips")) {
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "MetalClips", player.getLocation())) {
            return false;
        }
        if (!EarthMethods.canMetalbend(player)) {
            return false;
        }
        if (bplayer.isOnCooldown("MetalClips")) {
            return false;
        }
        return true;
    }

    public void magnet() {
        this.magnetized = true;
    }

    public void shootMetal() {
        ItemStack is = new ItemStack(Material.IRON_INGOT, 1);
        if (GeneralMethods.getBendingPlayer(player.getName()).isOnCooldown("MetalClips")) {
            return;
        }
        if (!player.getInventory().containsAtLeast(is, 1)) {
            this.remove();
            return;
        }
        Item ii = player.getWorld().dropItemNaturally(player.getLocation().add(0.0, 1.0, 0.0), is);
        Vector v = GeneralMethods.getTargetedEntity(player, 10.0, new ArrayList<Entity>()) != null ? GeneralMethods.getDirection(player.getLocation(), GeneralMethods.getTargetedEntity(player, 10.0, new ArrayList<Entity>()).getLocation()) : GeneralMethods.getDirection(player.getLocation(), GeneralMethods.getTargetedLocation(player, 10));
        ii.setVelocity(v.normalize().add(new Vector(0.0, 0.1, 0.0).multiply(1.2)));
        this.trackedIngots.add(ii);
        player.getInventory().removeItem(new ItemStack[]{is});
        GeneralMethods.getBendingPlayer(player.getName()).addCooldown("MetalClips", cooldown);
    }

    public void formArmor() {
        if (this.metalclips >= 4) {
            return;
        }
        if (this.metalclips == 3 && !player.hasPermission("bending.ability.MetalClips.4clips")) {
            return;
        }
        int n = this.metalclips = this.metalclips < 4 ? this.metalclips + 1 : 4;
        if (targetent instanceof Player) {
            Player target = (Player)targetent;
            if (oldarmor == null) {
                oldarmor = target.getInventory().getArmorContents();
            }
            ItemStack[] metalarmor = new ItemStack[4];
            metalarmor[2] = this.metalclips >= 1 ? new ItemStack(Material.IRON_CHESTPLATE, 1) : oldarmor[2];
            metalarmor[0] = this.metalclips >= 2 ? new ItemStack(Material.IRON_BOOTS, 1) : oldarmor[0];
            metalarmor[1] = this.metalclips >= 3 ? new ItemStack(Material.IRON_LEGGINGS, 1) : oldarmor[1];
            metalarmor[3] = this.metalclips >= 4 ? new ItemStack(Material.IRON_HELMET, 1) : oldarmor[3];
            clipped.put((Entity)target, this.metalclips);
            target.getInventory().setArmorContents(metalarmor);
        } else {
            if (oldarmor == null) {
                oldarmor = targetent.getEquipment().getArmorContents();
            }
            ItemStack[] metalarmor = new ItemStack[4];
            metalarmor[2] = this.metalclips >= 1 ? new ItemStack(Material.IRON_CHESTPLATE, 1) : oldarmor[2];
            metalarmor[0] = this.metalclips >= 2 ? new ItemStack(Material.IRON_BOOTS, 1) : oldarmor[0];
            metalarmor[1] = this.metalclips >= 3 ? new ItemStack(Material.IRON_LEGGINGS, 1) : oldarmor[1];
            metalarmor[3] = this.metalclips >= 4 ? new ItemStack(Material.IRON_HELMET, 1) : oldarmor[3];
            clipped.put((Entity)targetent, this.metalclips);
            targetent.getEquipment().setArmorContents(metalarmor);
        }
        if (this.metalclips == 4) {
            this.time = System.currentTimeMillis();
            this.lastDistanceCheck = player.getLocation().distance(targetent.getLocation());
        }
        this.startTime = System.currentTimeMillis();
        this.isBeingWorn = true;
    }

    public void resetArmor() {
        if (targetent == null || oldarmor == null || targetent.isDead()) {
            return;
        }
        if (targetent instanceof Player) {
            ((Player)targetent).getInventory().setArmorContents(oldarmor);
        } else {
            targetent.getEquipment().setArmorContents(oldarmor);
        }
        player.getWorld().dropItem(targetent.getLocation(), new ItemStack(Material.IRON_INGOT, this.metalclips));
        this.isBeingWorn = false;
    }

    public void control() {
        this.isControlling = true;
    }

    public void ceaseControl() {
        this.isControlling = false;
    }

    public boolean controlling() {
        return this.isControlling;
    }

    public void launch() {
        if (!this.canThrow) {
            return;
        }
        Location location = player.getLocation();
        Location target = targetent.getLocation().clone();
        double dx = target.getX() - location.getX();
        double dy = target.getY() - location.getY();
        double dz = target.getZ() - location.getZ();
        Vector vector = new Vector(dx, dy, dz);
        vector.normalize();
        targetent.setVelocity(vector.multiply(2));
        this.remove();
    }

    public void progress() {
        if (!player.isOnline() || player.isDead()) {
            this.remove();
            return;
        }
        if (GeneralMethods.getBoundAbility(player) == null || !GeneralMethods.getBoundAbility(player).equalsIgnoreCase("MetalClips")) {
            this.remove();
            return;
        }
        if (targetent != null && (targetent instanceof Player && !((Player)targetent).isOnline() || targetent.isDead())) {
            this.remove();
            return;
        }
        if (!player.isSneaking()) {
            this.isControlling = false;
            this.magnetized = false;
        }
        if (this.magnetized) {
            if (GeneralMethods.getEntitiesAroundPoint(player.getLocation(), magnetRange).size() == 0) {
                this.remove();
                return;
            }
            for (Entity e : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), magnetRange)) {
                ItemStack is;
                int n;
                Vector v = GeneralMethods.getDirection(e.getLocation(), player.getLocation());
                if (e instanceof Player && player.hasPermission("bending.ability.MetalClips.loot") && player.getInventory().getItemInMainHand().getType() == Material.IRON_INGOT && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("Magnet")) {
                    ItemStack[] inventory;
                    ItemStack[] armor;
                    Player p = (Player)e;
                    if (p.getEntityId() == player.getEntityId()) continue;
                    ItemStack[] arritemStack = inventory = p.getInventory().getContents();
                    n = arritemStack.length;
                    int n2 = 0;
                    while (n2 < n) {
                        is = arritemStack[n2];
                        if (is != null && Arrays.asList(metalItems).contains((Object)is.getType())) {
                            p.getWorld().dropItem(p.getLocation(), is);
                            is.setType(Material.AIR);
                            is.setAmount(0);
                        }
                        ++n2;
                    }
                    p.getInventory().setContents(inventory);
                    ItemStack[] arritemStack2 = armor = p.getInventory().getArmorContents();
                    int n3 = arritemStack2.length;
                    n = 0;
                    while (n < n3) {
                        ItemStack is2 = arritemStack2[n];
                        if (Arrays.asList(metalItems).contains((Object)is2.getType())) {
                            p.getWorld().dropItem(p.getLocation(), is2);
                            is2.setType(Material.AIR);
                        }
                        ++n;
                    }
                    p.getInventory().setArmorContents(armor);
                    if (Arrays.asList(metalItems).contains((Object)p.getInventory().getItemInMainHand().getType())) {
                        p.getWorld().dropItem(p.getLocation(), p.getEquipment().getItemInMainHand());
                        p.getEquipment().setItemInMainHand(new ItemStack(Material.AIR, 1));
                    }
                }
                if ((e instanceof Zombie || e instanceof Skeleton) && player.hasPermission("bending.ability.MetalClips.loot") && player.getInventory().getItemInMainHand().getType() == Material.IRON_INGOT && player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("Magnet")) {
                    ItemStack[] armor;
                    LivingEntity le = (LivingEntity)e;
                    ItemStack[] arritemStack = armor = le.getEquipment().getArmorContents();
                    n = arritemStack.length;
                    int is2 = 0;
                    while (is2 < n) {
                        is = arritemStack[is2];
                        if (Arrays.asList(metalItems).contains((Object)is.getType())) {
                            le.getWorld().dropItem(le.getLocation(), is);
                            is.setType(Material.AIR);
                        }
                        ++is2;
                    }
                    le.getEquipment().setArmorContents(armor);
                    if (Arrays.asList(metalItems).contains((Object)le.getEquipment().getItemInMainHand().getType())) {
                        le.getWorld().dropItem(le.getLocation(), le.getEquipment().getItemInMainHand());
                        le.getEquipment().setItemInMainHand(new ItemStack(Material.AIR, 1));
                    }
                }
                if (!(e instanceof Item)) continue;
                Item iron = (Item)e;
                if (!Arrays.asList(metalItems).contains((Object)iron.getItemStack().getType())) continue;
                iron.setVelocity(v.normalize().multiply(magnetPower));
            }
        }
        if (this.isBeingWorn && System.currentTimeMillis() > this.startTime + (long)armorTime) {
            this.remove();
            return;
        }
        if (this.isControlling && player.isSneaking()) {
            Location loc;
            double height;
            Location oldLocation;
            Vector v;
            double distance;
            if (this.metalclips == 1) {
                oldLocation = targetent.getLocation();
                loc = GeneralMethods.getTargetedLocation(player, (int)player.getLocation().distance(oldLocation));
                double distance2 = loc.distance(oldLocation);
                v = GeneralMethods.getDirection(targetent.getLocation(), player.getLocation());
                if (distance2 > 0.5) {
                    targetent.setVelocity(v.normalize().multiply(0.2));
                }
            }
            if (this.metalclips == 2) {
                oldLocation = targetent.getLocation();
                loc = GeneralMethods.getTargetedLocation(player, (int)player.getLocation().distance(oldLocation));
                double distance3 = loc.distance(oldLocation);
                v = GeneralMethods.getDirection(targetent.getLocation(), GeneralMethods.getTargetedLocation(player, 10));
                if (distance3 > 1.2) {
                    targetent.setVelocity(v.normalize().multiply(0.2));
                }
            }
            if (this.metalclips >= 3) {
                oldLocation = targetent.getLocation();
                loc = GeneralMethods.getTargetedLocation(player, (int)player.getLocation().distance(oldLocation));
                double distance4 = loc.distance(oldLocation);
                v = GeneralMethods.getDirection(oldLocation, GeneralMethods.getTargetedLocation(player, 10));
                if (distance4 > 1.2) {
                    targetent.setVelocity(v.normalize().multiply(0.5));
                } else {
                    targetent.setVelocity(new Vector(0, 0, 0));
                }
                targetent.setFallDistance(0.0f);
            }
            if (this.metalclips == 4 && player.hasPermission("bending.ability.MetalClips.4clips") && (distance = player.getLocation().distance(targetent.getLocation())) < this.lastDistanceCheck - 0.3 && (height = targetent.getLocation().getY()) > player.getEyeLocation().getY()) {
                this.lastDistanceCheck = distance;
                if (System.currentTimeMillis() > this.time + (long)crushInterval) {
                    this.time = System.currentTimeMillis();
                    GeneralMethods.damageEntity(player, (Entity)targetent, (double)crushDamage + (double)crushDamage * 1.2, "MetalClips");
                }
            }
        }
        int i = 0;
        while (i < this.trackedIngots.size()) {
            Item ii = this.trackedIngots.get(i);
            if (ii.isOnGround()) {
                this.trackedIngots.remove(i);
            } else if (ii.getItemStack().getType() == Material.IRON_INGOT) {
                if (GeneralMethods.getEntitiesAroundPoint(ii.getLocation(), 2.0).size() == 0) {
                    this.remove();
                    return;
                }
                for (Entity e : GeneralMethods.getEntitiesAroundPoint(ii.getLocation(), 2.0)) {
                    if (!(e instanceof LivingEntity) || e.getEntityId() == player.getEntityId()) continue;
                    if (e instanceof Player || e instanceof Zombie || e instanceof Skeleton) {
                        targetent = (LivingEntity)e;
                        this.formArmor();
                    } else {
                        GeneralMethods.damageEntity(player, e, 5.0, "MetalClips");
                        ii.getWorld().dropItem(ii.getLocation(), ii.getItemStack());
                        this.remove();
                    }
                    ii.remove();
                }
            }
            ++i;
        }
        this.removeDeadIngots();
    }

    public void removeDeadIngots() {
        int i = 0;
        while (i < this.trackedIngots.size()) {
            Item ii = this.trackedIngots.get(i);
            if (ii.isDead()) {
                this.trackedIngots.remove((Object)ii);
            }
            ++i;
        }
    }

    public LivingEntity getTarget() {
        return targetent;
    }

    public void remove() {
        for (Item i : this.trackedIngots) {
            i.remove();
        }
        this.resetArmor();
        this.trackedIngots.clear();
        instances.remove((Object)player);
        this.metalclips = 0;
        if (targetent != null) {
            clipped.remove((Object)targetent);
        }
    }

    public static void removeAll() {
        for (Player p : instances.keySet()) {
            instances.get((Object)p).remove();
        }
        if (!clipped.isEmpty()) {
            clipped.clear();
        }
    }

    public static void progressAll() {
        for (Player p : instances.keySet()) {
            instances.get((Object)p).progress();
        }
    }

    public static boolean isControlled(Player player) {
        for (Player p : instances.keySet()) {
            if (instances.get((Object)p).getTarget() == null || instances.get((Object)p).getTarget().getEntityId() != player.getEntityId()) continue;
            return true;
        }
        return false;
    }

    public static boolean isControllingEntity(Player player) {
        if (instances.containsKey((Object)player) && player.isSneaking() && targetent != null) {
            return true;
        }
        return false;
    }
}

