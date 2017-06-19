/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.projectkorra.projectkorra.ability.multiability;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.ability.StockAbility;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityModule;
import com.projectkorra.projectkorra.util.AbilityLoader;
import com.projectkorra.projectkorra.util.AbilityLoadable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.bukkit.plugin.Plugin;

public class MultiAbilityModuleManager {
    private final AbilityLoader<MultiAbilityModule> loader;
    public static List<MultiAbilityModule> multiAbility;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$projectkorra$projectkorra$SubElement;

    public MultiAbilityModuleManager() {
        File path = new File(String.valueOf(ProjectKorra.plugin.getDataFolder().toString()) + "/MultiAbilities/");
        if (!path.exists()) {
            path.mkdir();
        }
        this.loader = new AbilityLoader((Plugin)ProjectKorra.plugin, path, new Object[0]);
        multiAbility = this.loader.load(MultiAbilityModule.class);
        this.loadMAModules();
    }

    private void loadMAModules() {
        for (MultiAbilityModule mam : multiAbility) {
            mam.onThisLoad();
            AbilityModuleManager.abilities.add(mam.getName());
            StockAbility[] arrstockAbility = StockAbility.values();
            int n = arrstockAbility.length;
            int n2 = 0;
            while (n2 < n) {
                StockAbility a = arrstockAbility[n2];
                if (a.name().equalsIgnoreCase(mam.getName())) {
                    AbilityModuleManager.disabledStockAbilities.add(a.name());
                }
                ++n2;
            }
            if (mam.getElement() == Element.Air.toString()) {
                AbilityModuleManager.airbendingabilities.add(mam.getName());
            }
            if (mam.getElement() == Element.Water.toString()) {
                AbilityModuleManager.waterbendingabilities.add(mam.getName());
            }
            if (mam.getElement() == Element.Earth.toString()) {
                AbilityModuleManager.earthbendingabilities.add(mam.getName());
            }
            if (mam.getElement() == Element.Fire.toString()) {
                AbilityModuleManager.firebendingabilities.add(mam.getName());
            }
            if (mam.getElement() == Element.Chi.toString()) {
                AbilityModuleManager.chiabilities.add(mam.getName());
            }
            AbilityModuleManager.shiftabilities.add(mam.getName());
            if (mam.isHarmlessAbility()) {
                AbilityModuleManager.harmlessabilities.add(mam.getName());
            }
            if (mam.getSubElement() != null) {
                AbilityModuleManager.subabilities.add(mam.getName());
                switch (MultiAbilityModuleManager.$SWITCH_TABLE$com$projectkorra$projectkorra$SubElement()[mam.getSubElement().ordinal()]) {
                    case 3: {
                        AbilityModuleManager.bloodabilities.add(mam.getName());
                        break;
                    }
                    case 10: {
                        AbilityModuleManager.combustionabilities.add(mam.getName());
                        break;
                    }
                    case 1: {
                        AbilityModuleManager.flightabilities.add(mam.getName());
                        break;
                    }
                    case 4: {
                        AbilityModuleManager.healingabilities.add(mam.getName());
                        break;
                    }
                    case 5: {
                        AbilityModuleManager.iceabilities.add(mam.getName());
                        break;
                    }
                    case 9: {
                        AbilityModuleManager.lavaabilities.add(mam.getName());
                        break;
                    }
                    case 11: {
                        AbilityModuleManager.lightningabilities.add(mam.getName());
                        break;
                    }
                    case 7: {
                        AbilityModuleManager.metalabilities.add(mam.getName());
                        break;
                    }
                    case 6: {
                        AbilityModuleManager.plantabilities.add(mam.getName());
                        break;
                    }
                    case 8: {
                        AbilityModuleManager.sandabilities.add(mam.getName());
                        break;
                    }
                    case 2: {
                        AbilityModuleManager.spiritualprojectionabilities.add(mam.getName());
                    }
                }
            }
            MultiAbilityManager.multiAbilityList.add(new MultiAbilityManager.MultiAbility(mam.getName(), mam.getAbilities()));
            AbilityModuleManager.descriptions.put(mam.getName(), mam.getDescription());
            AbilityModuleManager.authors.put(mam.getName(), mam.getAuthor());
        }
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

