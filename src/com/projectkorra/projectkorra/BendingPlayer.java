/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  de.slikey.effectlib.Effect
 *  org.bukkit.Bukkit
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.plugin.PluginManager
 */
package com.projectkorra.projectkorra;

import de.slikey.effectlib.Effect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.PKListener;
import com.projectkorra.projectkorra.event.PlayerCooldownChangeEvent;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.Database;

public class BendingPlayer {
    private static ConcurrentHashMap<UUID, BendingPlayer> players = new ConcurrentHashMap();
    public static ConcurrentHashMap<Effect, String> effects = new ConcurrentHashMap();
    public static List<Item> Shurikens = new ArrayList<Item>();
    private List<Item> pumpkins = new ArrayList<Item>();
    public static boolean eviltouch = false;
    public static boolean drainlife = false;
    public static boolean isghost = false;
    public static int teleportcount = 0;
    private UUID uuid;
    private String name;
    public ArrayList<Element> elements;
    private HashMap<Integer, String> abilities;
    private ConcurrentHashMap<String, Long> cooldowns;
    private ConcurrentHashMap<Element, Boolean> toggledElements;
    private boolean permaRemoved;
    private boolean toggled = true;
    private long slowTime = 0;
    private boolean tremorSense = true;
    private boolean chiBlocked = false;
    public static Block tempsnow = null;

    public BendingPlayer(UUID uuid, String playerName, ArrayList<Element> elements, HashMap<Integer, String> abilities, boolean permaRemoved) {
        this.uuid = uuid;
        this.name = playerName;
        this.elements = elements;
        this.setAbilities(abilities);
        this.permaRemoved = permaRemoved;
        this.cooldowns = new ConcurrentHashMap();
        this.toggledElements = new ConcurrentHashMap();
        this.toggledElements.put(Element.Air, true);
        this.toggledElements.put(Element.Earth, true);
        this.toggledElements.put(Element.Fire, true);
        this.toggledElements.put(Element.Water, true);
        this.toggledElements.put(Element.Chi, true);
        this.toggledElements.put(Element.Scarecrow, true);
        this.toggledElements.put(Element.Snowman, true);
        this.toggledElements.put(Element.Sunshine, true);
        players.put(uuid, this);
        PKListener.login(this);
    }

    public static ConcurrentHashMap<UUID, BendingPlayer> getPlayers() {
        return players;
    }

    public void addCooldown(String ability, long cooldown) {
        PlayerCooldownChangeEvent event = new PlayerCooldownChangeEvent(Bukkit.getPlayer((UUID)this.uuid), ability, PlayerCooldownChangeEvent.Result.ADDED);
        Bukkit.getServer().getPluginManager().callEvent((Event)event);
        if (!event.isCancelled()) {
            this.cooldowns.put(ability, cooldown + System.currentTimeMillis());
        }
    }

    public void addElement(Element e) {
        this.elements.add(e);
    }

    public void blockChi() {
        this.chiBlocked = true;
    }

    public boolean canBeSlowed() {
        if (System.currentTimeMillis() > this.slowTime) {
            return true;
        }
        return false;
    }

    public List<Item> getPumpkins() {
        return this.pumpkins;
    }

    public void setPumpkins(List<Item> pumpkins) {
        this.pumpkins = pumpkins;
    }

    public HashMap<Integer, String> getAbilities() {
        return this.abilities;
    }

    public long getCooldown(String ability) {
        if (this.cooldowns.containsKey(ability)) {
            return this.cooldowns.get(ability);
        }
        return -1;
    }

    public ConcurrentHashMap<String, Long> getCooldowns() {
        return this.cooldowns;
    }

    public List<Element> getElements() {
        return this.elements;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getUUIDString() {
        return this.uuid.toString();
    }

    public boolean hasElement(Element e) {
        return this.elements.contains((Object)e);
    }

    public boolean isChiBlocked() {
        return this.chiBlocked;
    }

    public boolean isElementToggled(Element e) {
        if (e != null) {
            return this.toggledElements.get((Object)e);
        }
        return true;
    }

    public boolean isOnCooldown(String ability) {
        return this.cooldowns.containsKey(ability);
    }

    public boolean isPermaRemoved() {
        return this.permaRemoved;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public boolean isTremorSensing() {
        return this.tremorSense;
    }

    public void removeCooldown(String ability) {
        PlayerCooldownChangeEvent event = new PlayerCooldownChangeEvent(Bukkit.getPlayer((UUID)this.uuid), ability, PlayerCooldownChangeEvent.Result.REMOVED);
        Bukkit.getServer().getPluginManager().callEvent((Event)event);
        if (!event.isCancelled()) {
            this.cooldowns.remove(ability);
        }
    }

    public void setAbilities(HashMap<Integer, String> abilities) {
        this.abilities = abilities;
        int i = 1;
        while (i <= 9) {
            DBConnection.sql.modifyQuery("UPDATE pk_players SET slot" + i + " = '" + abilities.get(i) + "' WHERE uuid = '" + this.uuid + "'");
            ++i;
        }
    }

    public void setElement(Element e) {
        this.elements.clear();
        this.elements.add(e);
    }

    public void setPermaRemoved(boolean permaRemoved) {
        this.permaRemoved = permaRemoved;
    }

    public void slow(long cooldown) {
        this.slowTime = System.currentTimeMillis() + cooldown;
    }

    public void toggleBending() {
        this.toggled = !this.toggled;
    }

    public void toggleElement(Element e) {
        this.toggledElements.put(e, this.toggledElements.get((Object)e) == false);
    }

    public void toggleTremorSense() {
        this.tremorSense = !this.tremorSense;
    }

    public void unblockChi() {
        this.chiBlocked = false;
    }
}

