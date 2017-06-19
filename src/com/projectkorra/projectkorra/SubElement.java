/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 */
package com.projectkorra.projectkorra;

import java.util.Arrays;
import org.bukkit.ChatColor;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.command.Commands;

public enum SubElement {
    Flight(Element.Air, Commands.flightaliases),
    SpiritualProjection(Element.Air, Commands.spiritualprojectionaliases),
    Bloodbending(Element.Water, Commands.bloodaliases),
    Healing(Element.Water, Commands.healingaliases),
    Icebending(Element.Water, Commands.icealiases),
    Plantbending(Element.Water, Commands.plantaliases),
    Metalbending(Element.Earth, Commands.metalbendingaliases),
    Sandbending(Element.Earth, Commands.sandbendingaliases),
    Lavabending(Element.Earth, Commands.lavabendingaliases),
    Combustion(Element.Fire, Commands.combustionaliases),
    Lightning(Element.Fire, Commands.lightningaliases);
    
    private Element element;
    private String[] aliases;

    SubElement(Element mainElement, String[] aliases) {
		this.element = mainElement;
		this.aliases = aliases;
}
 
    public Element getMainElement() {
        return this.element;
    }

    public ChatColor getChatColor() {
        return this.element.getSubColor();
    }

    public static SubElement getType(String string) {
        SubElement[] arrsubElement = SubElement.values();
        int n = arrsubElement.length;
        int n2 = 0;
        while (n2 < n) {
            SubElement se = arrsubElement[n2];
            if (Arrays.asList(se.aliases).contains(string.toLowerCase())) {
                return se;
            }
            ++n2;
        }
        return null;
    }

    public static SubElement getType(int index) {
        if (index == -1) {
            return null;
        }
        return Arrays.asList(SubElement.values()).get(index);
    }
}

