/*
 * Decompiled with CFR 0_118.
 */
package com.projectkorra.projectkorra.ability.multiability;

import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.util.AbilityLoadable;
import java.util.ArrayList;

public abstract class MultiAbilityModule
extends AbilityLoadable
implements Cloneable {
    public MultiAbilityModule(String name) {
        super(name);
    }

    public abstract void onThisLoad();

    public abstract String getVersion();

    public abstract String getElement();

    public abstract String getAuthor();

    public abstract String getDescription();

    public boolean isShiftAbility() {
        return true;
    }

    public abstract boolean isHarmlessAbility();

    public boolean isIgniteAbility() {
        return false;
    }

    public boolean isExplodeAbility() {
        return false;
    }

    public SubElement getSubElement() {
        return null;
    }

    public abstract ArrayList<MultiAbilityManager.MultiAbilitySub> getAbilities();

    public void stop() {
    }
}

