/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package com.projectkorra.projectkorra.ability;

import com.projectkorra.projectkorra.ability.AbilityModule;
import com.projectkorra.projectkorra.ability.StockAbility;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.util.AbilityLoader;
import com.projectkorra.projectkorra.util.AbilityLoadable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AbilityModuleManager {
    static ProjectKorra plugin;
    public static List<AbilityModule> ability;
    private final AbilityLoader<AbilityModule> loader;
    public static HashSet<String> abilities;
    public static HashSet<String> disabledStockAbilities;
    public static List<String> waterbendingabilities;
    public static List<String> airbendingabilities;
    public static List<String> earthbendingabilities;
    public static List<String> firebendingabilities;
    public static List<String> ScarecrowAbilities;
    public static List<String> SnowmanAbilities;
    public static List<String> SunshineAbilities;
    public static List<String> chiabilities;
    public static List<String> shiftabilities;
    public static HashMap<String, String> authors;
    public static List<String> harmlessabilities;
    public static List<String> igniteabilities;
    public static List<String> explodeabilities;
    public static List<String> metalbendingabilities;
    public static List<String> earthsubabilities;
    public static List<String> subabilities;
    public static List<String> lightningabilities;
    public static List<String> combustionabilities;
    public static List<String> lavaabilities;
    public static List<String> sandabilities;
    public static List<String> metalabilities;
    public static List<String> flightabilities;
    public static List<String> spiritualprojectionabilities;
    public static List<String> iceabilities;
    public static List<String> healingabilities;
    public static List<String> plantabilities;
    public static List<String> bloodabilities;
    public static HashMap<String, String> descriptions;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$projectkorra$projectkorra$SubElement;

    public AbilityModuleManager(ProjectKorra plugin) {
        AbilityModuleManager.plugin = plugin;
        File path = new File(String.valueOf(plugin.getDataFolder().toString()) + "/Abilities/");
        if (!path.exists()) {
            path.mkdir();
        }
        this.loader = new AbilityLoader((Plugin)plugin, path, new Object[0]);
        abilities = new HashSet();
        waterbendingabilities = new ArrayList<String>();
        airbendingabilities = new ArrayList<String>();
        earthbendingabilities = new ArrayList<String>();
        firebendingabilities = new ArrayList<String>();
        chiabilities = new ArrayList<String>();
        SnowmanAbilities = new ArrayList<String>();
        SunshineAbilities = new ArrayList<String>();
        ScarecrowAbilities = new ArrayList<String>();
        shiftabilities = new ArrayList<String>();
        descriptions = new HashMap();
        authors = new HashMap();
        harmlessabilities = new ArrayList<String>();
        explodeabilities = new ArrayList<String>();
        igniteabilities = new ArrayList<String>();
        metalbendingabilities = new ArrayList<String>();
        earthsubabilities = new ArrayList<String>();
        subabilities = new ArrayList<String>();
        ability = this.loader.load(AbilityModule.class);
        disabledStockAbilities = new HashSet();
        lightningabilities = new ArrayList<String>();
        combustionabilities = new ArrayList<String>();
        flightabilities = new ArrayList<String>();
        spiritualprojectionabilities = new ArrayList<String>();
        metalabilities = new ArrayList<String>();
        sandabilities = new ArrayList<String>();
        lavaabilities = new ArrayList<String>();
        healingabilities = new ArrayList<String>();
        plantabilities = new ArrayList<String>();
        iceabilities = new ArrayList<String>();
        bloodabilities = new ArrayList<String>();
        this.fill();
    }

    private void fill() {

		for (StockAbility a : StockAbility.values()) {
			if (StockAbility.isAirbending(a)) {
				if (ProjectKorra.plugin.getConfig().getBoolean("Abilities.Air." + a.name() + ".Enabled")) {
					abilities.add(a.name());
					airbendingabilities.add(a.name());
					descriptions.put(a.name(), ProjectKorra.plugin.getConfig().getString("Abilities.Air." + a.name() + ".Description"));
					if (a == StockAbility.AirScooter)
						harmlessabilities.add(a.name());
					if (a == StockAbility.AirSpout)
						harmlessabilities.add(a.name());
					if (a == StockAbility.Tornado)
						shiftabilities.add(a.name());
					if (a == StockAbility.AirSuction)
						shiftabilities.add(a.name());
					if (a == StockAbility.AirSwipe)
						shiftabilities.add(a.name());
					if (a == StockAbility.AirBlast)
						shiftabilities.add(a.name());
					if (a == StockAbility.AirBurst)
						shiftabilities.add(a.name());
					if (a == StockAbility.AirShield)
						shiftabilities.add(a.name());
					if (a == StockAbility.Flight)
						shiftabilities.add(a.name());

					// Air Sub Abilities
					if (a == StockAbility.Flight)
						subabilities.add(a.name());
					if (a == StockAbility.Flight)
						flightabilities.add(a.name());
				}
			} else if (StockAbility.isWaterbending(a)) {
				if (ProjectKorra.plugin.getConfig().getBoolean("Abilities.Water." + a.name() + ".Enabled")) {
					abilities.add(a.name());
					waterbendingabilities.add(a.name());
					descriptions.put(a.name(), ProjectKorra.plugin.getConfig().getString("Abilities.Water." + a.name() + ".Description"));
					if (a == StockAbility.WaterSpout)
						harmlessabilities.add(a.name());
					if (a == StockAbility.HealingWaters)
						harmlessabilities.add(a.name());
					if (a == StockAbility.Surge)
						shiftabilities.add(a.name());
					if (a == StockAbility.Bloodbending)
						shiftabilities.add(a.name());
					if (a == StockAbility.PhaseChange)
						shiftabilities.add(a.name());
					if (a == StockAbility.HealingWaters)
						shiftabilities.add(a.name());
					if (a == StockAbility.OctopusForm)
						shiftabilities.add(a.name());
					if (a == StockAbility.Torrent)
						shiftabilities.add(a.name());
					if (a == StockAbility.WaterManipulation)
						shiftabilities.add(a.name());
					if (a == StockAbility.IceSpike)
						shiftabilities.add(a.name());
					if (a == StockAbility.IceBlast)
						shiftabilities.add(a.name());
					if (a == StockAbility.WaterArms)
						shiftabilities.add(a.name());

					// Water Sub Abilities
					if (a == StockAbility.HealingWaters)
						subabilities.add(a.name());
					if (a == StockAbility.Bloodbending)
						subabilities.add(a.name());
					if (a == StockAbility.PhaseChange)
						subabilities.add(a.name());
					if (a == StockAbility.IceSpike)
						subabilities.add(a.name());
					if (a == StockAbility.IceBlast)
						subabilities.add(a.name());
					if (a == StockAbility.PlantArmor)
						subabilities.add(a.name());

					if (a == StockAbility.HealingWaters)
						healingabilities.add(a.name());
					if (a == StockAbility.Bloodbending)
						bloodabilities.add(a.name());
					if (a == StockAbility.PhaseChange)
						iceabilities.add(a.name());
					if (a == StockAbility.IceSpike)
						iceabilities.add(a.name());
					if (a == StockAbility.IceBlast)
						iceabilities.add(a.name());
					if (a == StockAbility.PlantArmor)
						plantabilities.add(a.name());
				}
			} else if (StockAbility.isEarthbending(a)) {
				if (ProjectKorra.plugin.getConfig().getBoolean("Abilities.Earth." + a.name() + ".Enabled")) {
					abilities.add(a.name());
					earthbendingabilities.add(a.name());
					descriptions.put(a.name(), ProjectKorra.plugin.getConfig().getString("Abilities.Earth." + a.name() + ".Description"));
					if (a == StockAbility.Tremorsense)
						harmlessabilities.add(a.name());
					if (a == StockAbility.RaiseEarth)
						shiftabilities.add(a.name());
					if (a == StockAbility.Collapse)
						shiftabilities.add(a.name());
					if (a == StockAbility.EarthBlast)
						shiftabilities.add(a.name());
					if (a == StockAbility.Shockwave)
						shiftabilities.add(a.name());
					if (a == StockAbility.EarthTunnel)
						shiftabilities.add(a.name());
					if (a == StockAbility.EarthGrab)
						shiftabilities.add(a.name());
					if (a == StockAbility.LavaFlow)
						shiftabilities.add(a.name());
					if (a == StockAbility.MetalClips)
						shiftabilities.add(a.name());
					if (a == StockAbility.EarthSmash)
						shiftabilities.add(a.name());
					if (a == StockAbility.SandSpout)
						shiftabilities.add(a.name());

					// Earth Sub Abilities
					if (a == StockAbility.MetalClips)
						subabilities.add(a.name());
					if (a == StockAbility.Extraction)
						subabilities.add(a.name());
					if (a == StockAbility.LavaFlow)
						subabilities.add(a.name());
					if (a == StockAbility.SandSpout)
						subabilities.add(a.name());

					if (a == StockAbility.MetalClips)
						metalabilities.add(a.name());
					if (a == StockAbility.Extraction)
						metalabilities.add(a.name());
					if (a == StockAbility.LavaFlow)
						lavaabilities.add(a.name());
					if (a == StockAbility.SandSpout)
						sandabilities.add(a.name());
					//					if (a == StockAbility.LavaSurge) earthsubabilities.add(a.name());

				}
			}  else if (StockAbility.isSnowman(a)) {
				if (ProjectKorra.plugin.getConfig().getBoolean("Abilities.Snow." + a.name() + ".Enabled")) {
					abilities.add(a.name());
					SnowmanAbilities.add(a.name());
					descriptions.put(a.name(),
							ProjectKorra.plugin.getConfig().getString("Abilities.Snow." + a.name() + ".Description"));

				}
			} else if (StockAbility.isSunshine(a)) {
				if (ProjectKorra.plugin.getConfig().getBoolean("Abilities.Sunshine." + a.name() + ".Enabled")) {
					abilities.add(a.name());
					SunshineAbilities.add(a.name());
					descriptions.put(a.name(),
							ProjectKorra.plugin.getConfig().getString("Abilities.Sunshine." + a.name() + ".Description"));

				}
			} else if (StockAbility.isScarecrow(a)) {
				if (ProjectKorra.plugin.getConfig().getBoolean("Abilities.Scare." + a.name() + ".Enabled")) {
					abilities.add(a.name());
					ScarecrowAbilities.add(a.name());
					descriptions.put(a.name(),
							ProjectKorra.plugin.getConfig().getString("Abilities.Scare." + a.name() + ".Description"));

				}
			} else if (StockAbility.isFirebending(a)) {
				if (ProjectKorra.plugin.getConfig().getBoolean("Abilities.Fire." + a.name() + ".Enabled")) {
					abilities.add(a.name());
					firebendingabilities.add(a.name());
					descriptions.put(a.name(), ProjectKorra.plugin.getConfig().getString("Abilities.Fire." + a.name() + ".Description"));
					if (a == StockAbility.Illumination)
						harmlessabilities.add(a.name());
					if (a == StockAbility.Blaze)
						igniteabilities.add(a.name());
					if (a == StockAbility.FireBlast)
						explodeabilities.add(a.name());
					if (a == StockAbility.Lightning)
						explodeabilities.add(a.name());
					if (a == StockAbility.Combustion)
						explodeabilities.add(a.name());
					if (a == StockAbility.HeatControl)
						shiftabilities.add(a.name());
					if (a == StockAbility.Lightning)
						shiftabilities.add(a.name());
					if (a == StockAbility.FireBlast)
						shiftabilities.add(a.name());
					if (a == StockAbility.Blaze)
						shiftabilities.add(a.name());
					if (a == StockAbility.FireBurst)
						shiftabilities.add(a.name());

					// Fire Sub Abilities
					if (a == StockAbility.Lightning)
						subabilities.add(a.name());
					if (a == StockAbility.Combustion)
						subabilities.add(a.name());

					if (a == StockAbility.Lightning)
						lightningabilities.add(a.name());
					if (a == StockAbility.Combustion)
						combustionabilities.add(a.name());
				}
			} else if (StockAbility.isChiBlocking(a)) {
				if (ProjectKorra.plugin.getConfig().getBoolean("Abilities.Chi." + a.name() + ".Enabled")) {
					abilities.add(a.name());
					chiabilities.add(a.name());
					descriptions.put(a.name(), ProjectKorra.plugin.getConfig().getString("Abilities.Chi." + a.name() + ".Description"));
					if (a == StockAbility.HighJump)
						harmlessabilities.add(a.name());
				}
			} else {
				if (ProjectKorra.plugin.getConfig().getBoolean("Abilities." + a.name() + ".Enabled")) {
					abilities.add(a.name()); // AvatarState, etc.
					descriptions.put(a.name(), ProjectKorra.plugin.getConfig().getString("Abilities." + a.name() + ".Description"));
				}
			}
		}
		for (AbilityModule ab : ability) {
			try {
				//To check if EarthBlast == Earthblast or for example, EarthBlast == EARTHBLAST
				boolean succes = true;
				for (String enabledAbility : abilities) {
					if (enabledAbility.equalsIgnoreCase(ab.getName())) {
						succes = false;
					}
				}
				if (!succes)
					continue;
				ab.onThisLoad();
				abilities.add(ab.getName());
				for (StockAbility a : StockAbility.values()) {
					if (a.name().equalsIgnoreCase(ab.getName())) {
						disabledStockAbilities.add(a.name());
					}
				}
				if (ab.getElement() == Element.Air.toString())
					airbendingabilities.add(ab.getName());
				if (ab.getElement() == Element.Water.toString())
					waterbendingabilities.add(ab.getName());
				if (ab.getElement() == Element.Earth.toString())
					earthbendingabilities.add(ab.getName());
				if (ab.getElement() == Element.Fire.toString())
					firebendingabilities.add(ab.getName());
				if (ab.getElement() == Element.Chi.toString())
					chiabilities.add(ab.getName());
				if (ab.getElement() == Element.Scarecrow.toString())
					ScarecrowAbilities.add(ab.getName());
				if (ab.getElement() == Element.Snowman.toString()) 
					SnowmanAbilities.add(ab.getName());
				if (ab.getElement() == Element.Sunshine.toString()) 
					SunshineAbilities.add(ab.getName());
				if (ab.isShiftAbility())
					shiftabilities.add(ab.getName());
				if (ab.isHarmlessAbility())
					harmlessabilities.add(ab.getName());

				if (ab.getSubElement() != null) {
					subabilities.add(ab.getName());
					switch (ab.getSubElement()) {
						case Bloodbending:
							bloodabilities.add(ab.getName());
							break;
						case Combustion:
							combustionabilities.add(ab.getName());
							break;
						case Flight:
							flightabilities.add(ab.getName());
							break;
						case Healing:
							healingabilities.add(ab.getName());
							break;
						case Icebending:
							iceabilities.add(ab.getName());
							break;
						case Lavabending:
							lavaabilities.add(ab.getName());
							break;
						case Lightning:
							lightningabilities.add(ab.getName());
							break;
						case Metalbending:
							metalabilities.add(ab.getName());
							break;
						case Plantbending:
							plantabilities.add(ab.getName());
							break;
						case Sandbending:
							sandabilities.add(ab.getName());
							break;
						case SpiritualProjection:
							spiritualprojectionabilities.add(ab.getName());
							break;
					}
				}

				// if (ab.isMetalbendingAbility()) metalbendingabilities.add(ab.getName());
				descriptions.put(ab.getName(), ab.getDescription());
				authors.put(ab.getName(), ab.getAuthor());
			}
			catch (Exception | Error e) { //If triggered means ability was made before specific version .
				ProjectKorra.log.warning("The ability " + ab.getName() + " was not able to load, if this message shows again please remove it!");
				//ProjectKorra.log.warning("The ability " + ab.getName() + " is either broken or outdated. Please remove it!");
				e.printStackTrace();
				ab.stop();
				abilities.remove(ab.getName());
				final AbilityModule skill = ab;
				//Bellow to avoid ConcurrentModificationException
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						ability.remove(skill);
					}
				}, 10);
				continue;
			}
		}

		Collections.sort(airbendingabilities);
		Collections.sort(firebendingabilities);
		Collections.sort(earthbendingabilities);
		Collections.sort(waterbendingabilities);
		Collections.sort(chiabilities);
		Collections.sort(iceabilities);
		Collections.sort(lavaabilities);
		Collections.sort(bloodabilities);
		Collections.sort(sandabilities);
		Collections.sort(metalabilities);
		Collections.sort(lightningabilities);
		Collections.sort(combustionabilities);
		Collections.sort(healingabilities);
		Collections.sort(flightabilities);
		Collections.sort(plantabilities);
		Collections.sort(spiritualprojectionabilities);
}

    public List<String> getAbilities(String element) {
        element = element.toLowerCase();
        if (Arrays.asList(Commands.wateraliases).contains(element)) {
            return waterbendingabilities;
        }
        if (Arrays.asList(Commands.icealiases).contains(element)) {
            return iceabilities;
        }
        if (Arrays.asList(Commands.plantaliases).contains(element)) {
            return plantabilities;
        }
        if (Arrays.asList(Commands.healingaliases).contains(element)) {
            return healingabilities;
        }
        if (Arrays.asList(Commands.bloodaliases).contains(element)) {
            return bloodabilities;
        }
        if (Arrays.asList(Commands.airaliases).contains(element)) {
            return airbendingabilities;
        }
        if (Arrays.asList(Commands.flightaliases).contains(element)) {
            return flightabilities;
        }
        if (Arrays.asList(Commands.spiritualprojectionaliases).contains(element)) {
            return spiritualprojectionabilities;
        }
        if (Arrays.asList(Commands.earthaliases).contains(element)) {
            return earthbendingabilities;
        }
        if (Arrays.asList(Commands.lavabendingaliases).contains(element)) {
            return lavaabilities;
        }
        if (Arrays.asList(Commands.metalbendingaliases).contains(element)) {
            return metalabilities;
        }
        if (Arrays.asList(Commands.sandbendingaliases).contains(element)) {
            return sandabilities;
        }
        if (Arrays.asList(Commands.firealiases).contains(element)) {
            return firebendingabilities;
        }
        if (Arrays.asList(Commands.combustionaliases).contains(element)) {
            return combustionabilities;
        }
        if (Arrays.asList(Commands.lightningaliases).contains(element)) {
            return lightningabilities;
        }
        if (Arrays.asList(Commands.chialiases).contains(element)) {
            return chiabilities;
        }
        if (Arrays.asList(Commands.snowmanaliases).contains(element)) {
            return SnowmanAbilities;
        }
        if (Arrays.asList(Commands.sunshinealiases).contains(element)) {
            return SunshineAbilities;
        }
        if (Arrays.asList(Commands.scarecrowaliases).contains(element)) {
            return ScarecrowAbilities;
        }
        return null;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$projectkorra$projectkorra$SubElement() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$com$projectkorra$projectkorra$SubElement;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[SubElement.values().length];
        try {
            arrn[SubElement.Bloodbending.ordinal()] = 3;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[SubElement.Combustion.ordinal()] = 10;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[SubElement.Flight.ordinal()] = 1;
        }
        catch (NoSuchFieldError v3) {}
        try {
            arrn[SubElement.Healing.ordinal()] = 4;
        }
        catch (NoSuchFieldError v4) {}
        try {
            arrn[SubElement.Icebending.ordinal()] = 5;
        }
        catch (NoSuchFieldError v5) {}
        try {
            arrn[SubElement.Lavabending.ordinal()] = 9;
        }
        catch (NoSuchFieldError v6) {}
        try {
            arrn[SubElement.Lightning.ordinal()] = 11;
        }
        catch (NoSuchFieldError v7) {}
        try {
            arrn[SubElement.Metalbending.ordinal()] = 7;
        }
        catch (NoSuchFieldError v8) {}
        try {
            arrn[SubElement.Plantbending.ordinal()] = 6;
        }
        catch (NoSuchFieldError v9) {}
        try {
            arrn[SubElement.Sandbending.ordinal()] = 8;
        }
        catch (NoSuchFieldError v10) {}
        try {
            arrn[SubElement.SpiritualProjection.ordinal()] = 2;
        }
        catch (NoSuchFieldError v11) {}
        $SWITCH_TABLE$com$projectkorra$projectkorra$SubElement = arrn;
        return $SWITCH_TABLE$com$projectkorra$projectkorra$SubElement;
    }

}

