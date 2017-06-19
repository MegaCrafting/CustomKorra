/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginManager
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitScheduler
 *  org.bukkit.scheduler.BukkitTask
 */
package com.projectkorra.projectkorra;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.BendingManager;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.PKListener;
import com.projectkorra.projectkorra.ability.combo.ComboManager;
import com.projectkorra.projectkorra.ability.combo.ComboModuleManager;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityModuleManager;
import com.projectkorra.projectkorra.airbending.AirbendingManager;
import com.projectkorra.projectkorra.chiblocking.ChiblockingManager;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.earthbending.EarthbendingManager;
import com.projectkorra.projectkorra.firebending.FirebendingManager;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.Database;
import com.projectkorra.projectkorra.util.RevertChecker;
import com.projectkorra.projectkorra.util.logging.PKLogHandler;
import com.projectkorra.projectkorra.waterbending.WaterbendingManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class ProjectKorra
extends JavaPlugin {
    public static ProjectKorra plugin;
    public static Logger log;
    public static PKLogHandler handler;
    public static long time_step;
    public AbilityModuleManager abManager;

    static {
        time_step = 1;
    }

    public void onEnable() {
        plugin = this;
        log = this.getLogger();
        try {
            File logFolder = new File(this.getDataFolder(), "Logs");
            if (!logFolder.exists()) {
                logFolder.mkdirs();
            }
            handler = new PKLogHandler(logFolder + File.separator + "ERROR.%g.log");
            log.getParent().addHandler(handler);
        }
        catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        new com.projectkorra.projectkorra.configuration.ConfigManager();
        new com.projectkorra.projectkorra.GeneralMethods(this);
        new com.projectkorra.projectkorra.command.Commands(this);
        this.abManager = new AbilityModuleManager(this);
        new com.projectkorra.projectkorra.ability.multiability.MultiAbilityModuleManager();
        new com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager();
        new com.projectkorra.projectkorra.ability.combo.ComboModuleManager();
        new com.projectkorra.projectkorra.ability.combo.ComboManager();
        DBConnection.host = this.getConfig().getString("Storage.MySQL.host");
        DBConnection.port = this.getConfig().getInt("Storage.MySQL.port");
        DBConnection.pass = this.getConfig().getString("Storage.MySQL.pass");
        DBConnection.db = this.getConfig().getString("Storage.MySQL.db");
        DBConnection.user = this.getConfig().getString("Storage.MySQL.user");
        DBConnection.init();
        if (!DBConnection.isOpen()) {
            return;
        }
        this.getServer().getPluginManager().registerEvents((Listener)new PKListener(this), (Plugin)this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new BendingManager(), 0, 1);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new AirbendingManager(this), 0, 1);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new WaterbendingManager(this), 0, 1);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new EarthbendingManager(this), 0, 1);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new FirebendingManager(this), 0, 1);
        this.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin)this, (Runnable)new ChiblockingManager(this), 0, 1);
        this.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)new RevertChecker(this), 0, 200);
        for (Player player : Bukkit.getOnlinePlayers()) {
            GeneralMethods.createBendingPlayer(player.getUniqueId(), player.getName());
        }
        GeneralMethods.deserializeFile();
        GeneralMethods.startCacheCleaner(GeneralMethods.CACHE_TIME);
    }

    public void onDisable() {
        GeneralMethods.stopBending();
        if (DBConnection.isOpen) {
            DBConnection.sql.close();
        }
        handler.close();
    }
}

