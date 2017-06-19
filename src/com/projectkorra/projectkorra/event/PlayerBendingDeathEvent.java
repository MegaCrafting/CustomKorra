/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.projectkorra.projectkorra.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.SubElement;

public class PlayerBendingDeathEvent
extends Event {
    public static final HandlerList handlers = new HandlerList();
    private Player victim;
    private Player attacker;
    private String ability;
    private double damage;
    private Element element;
    private SubElement sub;

    public PlayerBendingDeathEvent(Player victim, Player attacker, double damage, Element element, SubElement sub, String ability) {
        this.victim = victim;
        this.attacker = attacker;
        this.ability = ability;
        this.damage = damage;
        this.element = element;
        this.sub = sub;
    }

    public Player getVictim() {
        return this.victim;
    }

    public Player getAttacker() {
        return this.attacker;
    }

    public String getAbility() {
        return this.ability;
    }

    public double getDamage() {
        return this.damage;
    }

    public Element getElement() {
        return this.element;
    }

    public SubElement getSubElement() {
        return this.sub;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

