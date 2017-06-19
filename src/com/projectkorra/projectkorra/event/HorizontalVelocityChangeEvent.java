/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra.event;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.SubElement;

public class HorizontalVelocityChangeEvent
extends Event
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;
    private Entity entity;
    private Player instigator;
    private Vector from;
    private Vector to;
    private Vector difference;
    private Location start;
    private Location end;
    private String abil;
    private Element element;
    private SubElement sub;

    @Deprecated
    public HorizontalVelocityChangeEvent(Entity entity, Player instigator, Vector from, Vector to, Vector difference) {
        this.entity = entity;
        this.instigator = instigator;
        this.from = from;
        this.to = to;
        this.difference = difference;
    }

    public HorizontalVelocityChangeEvent(Entity entity, Player instigator, Vector from, Vector to, Vector difference, Location start, Location end, String ability, Element element, SubElement sub) {
        this.entity = entity;
        this.instigator = instigator;
        this.from = from;
        this.to = to;
        this.difference = difference;
        this.start = start;
        this.end = end;
        this.abil = ability;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public Player getInstigator() {
        return this.instigator;
    }

    public Vector getFrom() {
        return this.from;
    }

    public Vector getTo() {
        return this.to;
    }

    public Location getStartPoint() {
        return this.start;
    }

    public Location getEndPoint() {
        return this.end;
    }

    public double getDistanceTraveled() {
        return this.start.distance(this.end);
    }

    public Vector getDifference() {
        return this.difference;
    }

    public String getAbility() {
        return this.abil;
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

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean value) {
        this.isCancelled = value;
    }
}

