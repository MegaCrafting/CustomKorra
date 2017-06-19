/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.projectkorra.projectkorra.ability.combo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.combo.ComboAbilityModule;
import com.projectkorra.projectkorra.airbending.AirCombo;
import com.projectkorra.projectkorra.chiblocking.ChiCombo;
import com.projectkorra.projectkorra.firebending.FireCombo;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.waterbending.WaterCombo;

public class ComboManager {
    private static final long CLEANUP_DELAY = 12000;
    public static ConcurrentHashMap<String, ArrayList<AbilityInformation>> recentlyUsedAbilities = new ConcurrentHashMap();
    public static HashMap<String, ComboAbility> comboAbilityList = new HashMap();
    public static HashMap<String, String> authors = new HashMap();
    public static HashMap<String, String> descriptions = new HashMap();
    public static HashMap<String, String> instructions = new HashMap();

    public ComboManager() {
        ArrayList<AbilityInformation> fireKick = new ArrayList<AbilityInformation>();
        fireKick.add(new AbilityInformation("FireBlast", ClickType.LEFT_CLICK));
        fireKick.add(new AbilityInformation("FireBlast", ClickType.LEFT_CLICK));
        fireKick.add(new AbilityInformation("FireBlast", ClickType.SHIFT_DOWN));
        fireKick.add(new AbilityInformation("FireBlast", ClickType.LEFT_CLICK));
        comboAbilityList.put("FireKick", new ComboAbility("FireKick", fireKick, FireCombo.class));
        descriptions.put("FireKick", "A short ranged arc of fire launches from the player's feet dealing moderate damage to enemies.");
        instructions.put("FireKick", "FireBlast > FireBlast > (Hold Shift) > FireBlast.");
        ArrayList<AbilityInformation> fireSpin = new ArrayList<AbilityInformation>();
        fireSpin.add(new AbilityInformation("FireBlast", ClickType.LEFT_CLICK));
        fireSpin.add(new AbilityInformation("FireBlast", ClickType.LEFT_CLICK));
        fireSpin.add(new AbilityInformation("FireShield", ClickType.LEFT_CLICK));
        fireSpin.add(new AbilityInformation("FireShield", ClickType.SHIFT_DOWN));
        fireSpin.add(new AbilityInformation("FireShield", ClickType.SHIFT_UP));
        comboAbilityList.put("FireSpin", new ComboAbility("FireSpin", fireSpin, FireCombo.class));
        descriptions.put("FireSpin", "A circular array of fire that causes damage and massive knockback to nearby enemies.");
        instructions.put("FireSpin", "FireBlast > FireBlast > FireShield > (Tap Shift).");
        ArrayList<AbilityInformation> jetBlast = new ArrayList<AbilityInformation>();
        jetBlast.add(new AbilityInformation("FireJet", ClickType.SHIFT_DOWN));
        jetBlast.add(new AbilityInformation("FireJet", ClickType.SHIFT_UP));
        jetBlast.add(new AbilityInformation("FireJet", ClickType.SHIFT_DOWN));
        jetBlast.add(new AbilityInformation("FireJet", ClickType.SHIFT_UP));
        jetBlast.add(new AbilityInformation("FireShield", ClickType.SHIFT_DOWN));
        jetBlast.add(new AbilityInformation("FireShield", ClickType.SHIFT_UP));
        jetBlast.add(new AbilityInformation("FireJet", ClickType.LEFT_CLICK));
        comboAbilityList.put("JetBlast", new ComboAbility("JetBlast", jetBlast, FireCombo.class));
        descriptions.put("JetBlast", "Create an explosive blast that propels your FireJet at higher speeds.");
        instructions.put("JetBlast", "FireJet (Tap Shift) > FireJet (Tap Shift) > FireShield (Tap Shift) > FireJet.");
        ArrayList<AbilityInformation> jetBlaze = new ArrayList<AbilityInformation>();
        jetBlaze.add(new AbilityInformation("FireJet", ClickType.SHIFT_DOWN));
        jetBlaze.add(new AbilityInformation("FireJet", ClickType.SHIFT_UP));
        jetBlaze.add(new AbilityInformation("FireJet", ClickType.SHIFT_DOWN));
        jetBlaze.add(new AbilityInformation("FireJet", ClickType.SHIFT_UP));
        jetBlaze.add(new AbilityInformation("Blaze", ClickType.SHIFT_DOWN));
        jetBlaze.add(new AbilityInformation("Blaze", ClickType.SHIFT_UP));
        jetBlaze.add(new AbilityInformation("FireJet", ClickType.LEFT_CLICK));
        comboAbilityList.put("JetBlaze", new ComboAbility("JetBlaze", jetBlaze, FireCombo.class));
        descriptions.put("JetBlaze", "Damages and burns all enemies in the proximity of your FireJet.");
        instructions.put("JetBlaze", "FireJet (Tap Shift) > FireJet (Tap Shift) > Blaze (Tap Shift) > FireJet.");
        ArrayList<AbilityInformation> fireWheel = new ArrayList<AbilityInformation>();
        fireWheel.add(new AbilityInformation("FireShield", ClickType.SHIFT_DOWN));
        fireWheel.add(new AbilityInformation("FireShield", ClickType.RIGHT_CLICK));
        fireWheel.add(new AbilityInformation("FireShield", ClickType.RIGHT_CLICK));
        fireWheel.add(new AbilityInformation("Blaze", ClickType.SHIFT_UP));
        comboAbilityList.put("FireWheel", new ComboAbility("FireWheel", fireWheel, FireCombo.class));
        descriptions.put("FireWheel", "A high-speed wheel of fire that travels along the ground for long distances dealing high damage.");
        instructions.put("FireWheel", "FireShield (Hold Shift) > Right Click a block in front of you twice > Switch to Blaze > Release Shift.");
        ArrayList<AbilityInformation> twister = new ArrayList<AbilityInformation>();
        twister.add(new AbilityInformation("AirShield", ClickType.SHIFT_DOWN));
        twister.add(new AbilityInformation("AirShield", ClickType.SHIFT_UP));
        twister.add(new AbilityInformation("Tornado", ClickType.SHIFT_DOWN));
        twister.add(new AbilityInformation("AirBlast", ClickType.LEFT_CLICK));
        comboAbilityList.put("Twister", new ComboAbility("Twister", twister, AirCombo.class));
        descriptions.put("Twister", "Create a cyclone of air that travels along the ground grabbing nearby entities.");
        instructions.put("Twister", "AirShield (Tap Shift) > Tornado (Hold Shift) > AirBlast (Left Click)");
        ArrayList<AbilityInformation> airStream = new ArrayList<AbilityInformation>();
        airStream.add(new AbilityInformation("AirShield", ClickType.SHIFT_DOWN));
        airStream.add(new AbilityInformation("AirSuction", ClickType.LEFT_CLICK));
        airStream.add(new AbilityInformation("AirBlast", ClickType.LEFT_CLICK));
        comboAbilityList.put("AirStream", new ComboAbility("AirStream", airStream, AirCombo.class));
        descriptions.put("AirStream", "Control a large stream of air that grabs onto enemies allowing you to direct them temporarily.");
        instructions.put("AirStream", "AirShield (Hold Shift) > AirSuction (Left Click) > AirBlast (Left Click)");
        ArrayList<AbilityInformation> airSweep = new ArrayList<AbilityInformation>();
        airSweep.add(new AbilityInformation("AirSwipe", ClickType.LEFT_CLICK));
        airSweep.add(new AbilityInformation("AirSwipe", ClickType.LEFT_CLICK));
        airSweep.add(new AbilityInformation("AirBurst", ClickType.SHIFT_DOWN));
        airSweep.add(new AbilityInformation("AirBurst", ClickType.LEFT_CLICK));
        comboAbilityList.put("AirSweep", new ComboAbility("AirSweep", airSweep, AirCombo.class));
        descriptions.put("AirSweep", "Sweep the air in front of you hitting multiple enemies, causing moderate damage and a large knockback. The radius and direction of AirSweep is controlled by moving your mouse in a sweeping motion. For example, if you want to AirSweep upward, then move your mouse upward right after you left click AirBurst");
        instructions.put("AirSweep", "AirSwipe (Left Click) > AirSwipe (Left Click) > AirBurst (Hold Shift) > AirBurst (Left Click)");
        ArrayList<AbilityInformation> iceWave = new ArrayList<AbilityInformation>();
        iceWave.add(new AbilityInformation("WaterSpout", ClickType.SHIFT_UP));
        iceWave.add(new AbilityInformation("PhaseChange", ClickType.LEFT_CLICK));
        comboAbilityList.put("IceWave", new ComboAbility("IceWave", iceWave, WaterCombo.class));
        descriptions.put("IceWave", "PhaseChange your WaterWave into an IceWave that freezes and damages enemies.");
        instructions.put("IceWave", "Create a WaterSpout Wave > PhaseChange (Left Click)");
        ArrayList<AbilityInformation> iceBullet = new ArrayList<AbilityInformation>();
        iceBullet.add(new AbilityInformation("WaterBubble", ClickType.SHIFT_DOWN));
        iceBullet.add(new AbilityInformation("WaterBubble", ClickType.SHIFT_UP));
        iceBullet.add(new AbilityInformation("IceBlast", ClickType.SHIFT_DOWN));
        comboAbilityList.put("IceBullet", new ComboAbility("IceBullet", iceBullet, WaterCombo.class));
        descriptions.put("IceBullet", "Using a large cavern of ice, you can punch ice shards at your opponent causing moderate damage. To rapid fire, you must alternate between Left clicking and right clicking with IceBlast.");
        instructions.put("IceBullet", "WaterBubble (Tap Shift) > IceBlast (Hold Shift) > Wait for ice to Form > Then alternate between Left and Right click with IceBlast");
        ArrayList<AbilityInformation> iceBulletLeft = new ArrayList<AbilityInformation>();
        iceBulletLeft.add(new AbilityInformation("IceBlast", ClickType.LEFT_CLICK));
        comboAbilityList.put("IceBulletLeftClick", new ComboAbility("IceBulletLeftClick", iceBulletLeft, WaterCombo.class));
        ArrayList<AbilityInformation> iceBulletRight = new ArrayList<AbilityInformation>();
        iceBulletRight.add(new AbilityInformation("IceBlast", ClickType.RIGHT_CLICK));
        comboAbilityList.put("IceBulletRightClick", new ComboAbility("IceBulletRightClick", iceBulletRight, WaterCombo.class));
        ArrayList<AbilityInformation> immobilize = new ArrayList<AbilityInformation>();
        immobilize.add(new AbilityInformation("QuickStrike", ClickType.LEFT_CLICK));
        immobilize.add(new AbilityInformation("SwiftKick", ClickType.LEFT_CLICK));
        immobilize.add(new AbilityInformation("QuickStrike", ClickType.LEFT_CLICK));
        immobilize.add(new AbilityInformation("QuickStrike", ClickType.LEFT_CLICK));
        comboAbilityList.put("Immobilize", new ComboAbility("Immobilize", immobilize, ChiCombo.class));
        descriptions.put("Immobilize", "Immobilizes the opponent for several seconds.");
        instructions.put("Immobilize", "QuickStrike (Left Click) > SwiftKick (Left Click) > QuickStrike (Left Click) > QuickStrike (Left Click)");
        ComboManager.startCleanupTask();
    }

    public static void addComboAbility(Player player, ClickType type) {
        String abilityName = GeneralMethods.getBoundAbility(player);
        if (abilityName == null) {
            return;
        }
        AbilityInformation info = new AbilityInformation(abilityName, type, System.currentTimeMillis());
        ComboManager.addRecentAbility(player, info);
        ComboAbility comboAbil = ComboManager.checkForValidCombo(player);
        if (comboAbil == null) {
            return;
        }
        if (!player.hasPermission("bending.ability." + comboAbil.getName())) {
            return;
        }
        if (comboAbil.getComboType().equals(FireCombo.class)) {
            new com.projectkorra.projectkorra.firebending.FireCombo(player, comboAbil.getName());
        } else if (comboAbil.getComboType().equals(AirCombo.class)) {
            new com.projectkorra.projectkorra.airbending.AirCombo(player, comboAbil.getName());
        } else if (comboAbil.getComboType().equals(WaterCombo.class)) {
            new com.projectkorra.projectkorra.waterbending.WaterCombo(player, comboAbil.getName());
        } else if (comboAbil.getComboType().equals(ChiCombo.class)) {
            new com.projectkorra.projectkorra.chiblocking.ChiCombo(player, comboAbil.getName());
        } else if (comboAbil.getComboType() instanceof ComboAbilityModule) {
            ((ComboAbilityModule)comboAbil.getComboType()).createNewComboInstance(player);
            return;
        }
    }

    public static void addRecentAbility(Player player, AbilityInformation info) {
        String name = player.getName();
        ArrayList list = recentlyUsedAbilities.containsKey(name) ? recentlyUsedAbilities.get(name) : new ArrayList();
        list.add(info);
        recentlyUsedAbilities.put(name, list);
    }

    public static ComboAbility checkForValidCombo(Player player) {
        ArrayList<AbilityInformation> playerCombo = ComboManager.getRecentlyUsedAbilities(player, 8);
        for (String ability : comboAbilityList.keySet()) {
            ComboAbility customAbility = comboAbilityList.get(ability);
            ArrayList<AbilityInformation> abilityCombo = customAbility.getAbilities();
            int size = abilityCombo.size();
            if (playerCombo.size() < size) continue;
            boolean isValid = true;
            int i = 1;
            while (i <= size) {
                if (!playerCombo.get(playerCombo.size() - i).equalsWithoutTime(abilityCombo.get(abilityCombo.size() - i))) {
                    isValid = false;
                    break;
                }
                ++i;
            }
            if (!isValid) continue;
            return customAbility;
        }
        return null;
    }

    public static void cleanupOldCombos() {
        recentlyUsedAbilities.clear();
    }

    public static ArrayList<AbilityInformation> getRecentlyUsedAbilities(Player player, int amount) {
        String name = player.getName();
        if (!recentlyUsedAbilities.containsKey(name)) {
            return new ArrayList<AbilityInformation>();
        }
        ArrayList<AbilityInformation> list = recentlyUsedAbilities.get(name);
        if (list.size() < amount) {
            return new ArrayList<AbilityInformation>(list);
        }
        ArrayList<AbilityInformation> tempList = new ArrayList<AbilityInformation>();
        int i = 0;
        while (i < amount) {
            tempList.add(0, list.get(list.size() - 1 - i));
            ++i;
        }
        return tempList;
    }

    public static ArrayList<String> getCombosForElement(Element element) {
        ArrayList<String> list = new ArrayList<String>();
        for (String comboab : descriptions.keySet()) {
            if (GeneralMethods.getAbilityElement(comboAbilityList.get(comboab).getAbilities().get(0).getAbilityName()) != element) continue;
            list.add(comboab);
        }
        Collections.sort(list);
        return list;
    }

    public static void startCleanupTask() {
        new BukkitRunnable(){

            public void run() {
                ComboManager.cleanupOldCombos();
            }
        }.runTaskTimer((Plugin)ProjectKorra.plugin, 0, 12000);
    }

    public static class AbilityInformation {
        private String abilityName;
        private ClickType clickType;
        private long time;

        public AbilityInformation(String name, ClickType type) {
            this(name, type, 0);
        }

        public AbilityInformation(String name, ClickType type, long time) {
            this.abilityName = name;
            this.clickType = type;
            this.time = time;
        }

        public boolean equalsWithoutTime(AbilityInformation info) {
            if (this.getAbilityName().equals(info.getAbilityName()) && this.getClickType().equals((Object)info.getClickType())) {
                return true;
            }
            return false;
        }

        public String getAbilityName() {
            return this.abilityName;
        }

        public ClickType getClickType() {
            return this.clickType;
        }

        public long getTime() {
            return this.time;
        }

        public void setAbilityName(String abilityName) {
            this.abilityName = abilityName;
        }

        public void setClickType(ClickType clickType) {
            this.clickType = clickType;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String toString() {
            return String.valueOf(this.abilityName) + " " + (Object)((Object)this.clickType) + " " + this.time;
        }
    }

    public static class ComboAbility {
        private String name;
        private ArrayList<AbilityInformation> abilities;
        private Object comboType;

        public ComboAbility(String name, ArrayList<AbilityInformation> abilities, Object comboType) {
            this.name = name;
            this.abilities = abilities;
            this.comboType = comboType;
        }

        public ArrayList<AbilityInformation> getAbilities() {
            return this.abilities;
        }

        public Object getComboType() {
            return this.comboType;
        }

        public String getName() {
            return this.name;
        }

        public void setAbilities(ArrayList<AbilityInformation> abilities) {
            this.abilities = abilities;
        }

        public void setComboType(Object comboType) {
            this.comboType = comboType;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

}

