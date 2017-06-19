/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.projectkorra.projectkorra.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.Database;

public class Preset {
    public static ConcurrentHashMap<UUID, List<Preset>> presets = new ConcurrentHashMap();
    static String loadQuery = "SELECT * FROM pk_presets WHERE uuid = ?";
    static String loadNameQuery = "SELECT * FROM pk_presets WHERE uuid = ? AND name = ?";
    static String deleteQuery = "DELETE FROM pk_presets WHERE uuid = ? AND name = ?";
    static String insertQuery = "INSERT INTO pk_presets (uuid, name) VALUES (?, ?)";
    static String updateQuery1 = "UPDATE pk_presets SET slot";
    static String updateQuery2 = " = ? WHERE uuid = ? AND name = ?";
    UUID uuid;
    HashMap<Integer, String> abilities;
    String name;

    public Preset(UUID uuid, String name, HashMap<Integer, String> abilities) {
        this.uuid = uuid;
        this.name = name;
        this.abilities = abilities;
        if (!presets.containsKey(uuid)) {
            presets.put(uuid, new ArrayList());
        }
        presets.get(uuid).add(this);
    }

    public static void unloadPreset(Player player) {
        UUID uuid = player.getUniqueId();
        presets.remove(uuid);
    }

    public static void loadPresets(Player player) {
        new BukkitRunnable(){

            public void run() {
                UUID uuid = player.getUniqueId();
                if (uuid == null) {
                    return;
                }
                try {
                    PreparedStatement ps = DBConnection.sql.getConnection().prepareStatement(Preset.loadQuery);
                    ps.setString(1, uuid.toString());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int i = 0;
                        do {
                            HashMap<Integer, String> moves = new HashMap<Integer, String>();
                            int total = 1;
                            while (total <= 9) {
                                String slot = rs.getString("slot" + total);
                                if (slot != null) {
                                    moves.put(total, slot);
                                }
                                ++total;
                            }
                            new com.projectkorra.projectkorra.object.Preset(uuid, rs.getString("name"), moves);
                            ++i;
                        } while (rs.next());
                        ProjectKorra.log.info("Loaded " + i + " presets for " + player.getName());
                    }
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }.runTaskAsynchronously((Plugin)ProjectKorra.plugin);
    }

    public static boolean bindPreset(Player player, String name) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer == null) {
            return false;
        }
        if (!presets.containsKey(player.getUniqueId())) {
            return false;
        }
        HashMap abilities = null;
        for (Preset preset : presets.get(player.getUniqueId())) {
            if (!preset.name.equalsIgnoreCase(name)) continue;
            abilities = (HashMap)preset.abilities.clone();
        }
        boolean boundAll = true;
        int i = 1;
        while (i <= 9) {
            if (!GeneralMethods.canBend(player.getName(), (String)abilities.get(i))) {
                abilities.remove(i);
                boundAll = false;
            }
            ++i;
        }
        bPlayer.setAbilities(abilities);
        return boundAll;
    }

    public static boolean presetExists(Player player, String name) {
        if (!presets.containsKey(player.getUniqueId())) {
            return false;
        }
        boolean exists = false;
        for (Preset preset : presets.get(player.getUniqueId())) {
            if (!preset.name.equalsIgnoreCase(name)) continue;
            exists = true;
        }
        return exists;
    }

    public static Preset getPreset(Player player, String name) {
        if (!presets.containsKey(player.getUniqueId())) {
            return null;
        }
        for (Preset preset : presets.get(player.getUniqueId())) {
            if (!preset.name.equalsIgnoreCase(name)) continue;
            return preset;
        }
        return null;
    }

    public static HashMap<Integer, String> getPresetContents(Player player, String name) {
        if (!presets.containsKey(player.getUniqueId())) {
            return null;
        }
        for (Preset preset : presets.get(player.getUniqueId())) {
            if (!preset.name.equalsIgnoreCase(name)) continue;
            return preset.abilities;
        }
        return null;
    }

    public void delete() {
        try {
            PreparedStatement ps = DBConnection.sql.getConnection().prepareStatement(deleteQuery);
            ps.setString(1, this.uuid.toString());
            ps.setString(2, this.name);
            ps.execute();
            presets.get(this.uuid).remove(this);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

    public void save() {
        try {
            PreparedStatement ps = DBConnection.sql.getConnection().prepareStatement(loadNameQuery);
            ps.setString(1, this.uuid.toString());
            ps.setString(2, this.name);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps = DBConnection.sql.getConnection().prepareStatement(insertQuery);
                ps.setString(1, this.uuid.toString());
                ps.setString(2, this.name);
                ps.execute();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        for (final Integer i : this.abilities.keySet()) {
            new BukkitRunnable(){
                PreparedStatement ps;

                public void run() {
                    try {
                        this.ps = DBConnection.sql.getConnection().prepareStatement(String.valueOf(Preset.updateQuery1) + i + Preset.updateQuery2);
                        this.ps.setString(1, Preset.this.abilities.get(i));
                        this.ps.setString(2, Preset.this.uuid.toString());
                        this.ps.setString(3, Preset.this.name);
                        this.ps.execute();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously((Plugin)ProjectKorra.plugin);
        }
    }

}

