/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.ability.combo;

import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.ability.combo.ComboManager;
import com.projectkorra.projectkorra.util.AbilityLoadable;
import java.util.ArrayList;
import org.bukkit.entity.Player;

public abstract class ComboAbilityModule
extends AbilityLoadable
implements Cloneable {
    public ComboAbilityModule(String name) {
        super(name);
    }

    public abstract void onThisLoad();

    public abstract String getVersion();

    public abstract String getElement();

    public abstract String getAuthor();

    public abstract String getDescription();

    public abstract String getInstructions();

    public abstract Object createNewComboInstance(Player var1);

    public abstract ArrayList<ComboManager.AbilityInformation> getCombination();

    public void stop() {
    }

    public SubElement getSubElement() {
        return null;
    }
}

