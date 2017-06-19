/*
 * Decompiled with CFR 0_118.
 */
package com.projectkorra.projectkorra.ability;

import com.projectkorra.projectkorra.SubElement;
import com.projectkorra.projectkorra.util.AbilityLoadable;

public abstract class AbilityModule
extends AbilityLoadable
implements Cloneable {
    public AbilityModule(String name) {
        super(name);
    }

    public abstract void onThisLoad();

    public abstract String getVersion();

    public abstract String getElement();

    public abstract String getAuthor();

    public abstract String getDescription();

    public abstract boolean isShiftAbility();

    public abstract boolean isHarmlessAbility();

    public boolean isIgniteAbility() {
        return false;
    }

    public boolean isExplodeAbility() {
        return false;
    }

    public void stop() {
    }

    public SubElement getSubElement() {
        return null;
    }
}

