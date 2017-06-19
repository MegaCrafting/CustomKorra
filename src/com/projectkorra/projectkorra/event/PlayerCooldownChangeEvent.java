/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.projectkorra.projectkorra.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerCooldownChangeEvent
extends Event
implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private String ability;
    private Result eventresult;
    private boolean cancelled;

    public PlayerCooldownChangeEvent(Player player, String abilityname, Result result) {
        this.player = player;
        this.ability = abilityname;
        this.eventresult = result;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getAbility() {
        return this.ability;
    }

    public Result getResult() {
        return this.eventresult;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static enum Result {
        REMOVED,
        ADDED;
        

    }

}

