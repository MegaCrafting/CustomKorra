/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.plugin.Plugin
 */
package com.projectkorra.projectkorra.event;

import java.util.jar.JarFile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class AbilityLoadEvent<T>
extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Plugin plugin;
    private final T loadable;
    private final JarFile jarFile;

    public AbilityLoadEvent(Plugin plugin, T loadable, JarFile jarFile) {
        this.plugin = plugin;
        this.loadable = loadable;
        this.jarFile = jarFile;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public JarFile getJarFile() {
        return this.jarFile;
    }

    public T getLoadable() {
        return this.loadable;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}

