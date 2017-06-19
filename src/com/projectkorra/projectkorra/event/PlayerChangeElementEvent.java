/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package com.projectkorra.projectkorra.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.projectkorra.projectkorra.Element;

public class PlayerChangeElementEvent
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private CommandSender sender;
    private Player target;
    private Element element;
    private Result result;

    public PlayerChangeElementEvent(CommandSender sender, Player target, Element element, Result result) {
        this.sender = sender;
        this.target = target;
        this.element = element;
        this.result = result;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public Player getTarget() {
        return this.target;
    }

    public Element getElement() {
        return this.element;
    }

    public Result getResult() {
        return this.result;
    }

    public static enum Result {
        CHOOSE,
        REMOVE,
        ADD,
        PERMAREMOVE;
                
    }

}

