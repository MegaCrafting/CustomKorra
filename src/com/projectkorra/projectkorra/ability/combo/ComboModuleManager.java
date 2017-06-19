/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.projectkorra.projectkorra.ability.combo;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.combo.ComboAbilityModule;
import com.projectkorra.projectkorra.ability.combo.ComboManager;
import com.projectkorra.projectkorra.util.AbilityLoader;
import com.projectkorra.projectkorra.util.AbilityLoadable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.plugin.Plugin;

public class ComboModuleManager {
    private final AbilityLoader<ComboAbilityModule> loader;
    public static List<ComboAbilityModule> combo;

    public ComboModuleManager() {
        File path = new File(String.valueOf(ProjectKorra.plugin.getDataFolder().toString()) + "/Combos/");
        if (!path.exists()) {
            path.mkdir();
        }
        this.loader = new AbilityLoader((Plugin)ProjectKorra.plugin, path, new Object[0]);
        combo = this.loader.load(ComboAbilityModule.class);
        this.loadComboModules();
    }

    private void loadComboModules() {
        for (ComboAbilityModule cm : combo) {
            cm.onThisLoad();
            ComboManager.comboAbilityList.put(cm.getName(), new ComboManager.ComboAbility(cm.getName(), cm.getCombination(), cm));
            ComboManager.descriptions.put(cm.getName(), cm.getDescription());
            ComboManager.instructions.put(cm.getName(), cm.getInstructions());
            ComboManager.authors.put(cm.getName(), cm.getAuthor());
        }
    }
}

