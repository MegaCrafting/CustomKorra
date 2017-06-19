/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.projectkorra.projectkorra.storage;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.projectkorra.projectkorra.ProjectKorra;

public abstract class Database {
    protected final Logger log;
    protected final String prefix;
    protected final String dbprefix;
    protected Connection connection = null;

    public Database(Logger log, String prefix, String dbprefix) {
        this.log = log;
        this.prefix = prefix;
        this.dbprefix = dbprefix;
    }

    protected void printInfo(String message) {
        this.log.info(String.valueOf(this.prefix) + this.dbprefix + message);
    }

    protected void printErr(String message, boolean severe) {
        if (severe) {
            this.log.severe(String.valueOf(this.prefix) + this.dbprefix + message);
        } else {
            this.log.warning(String.valueOf(this.prefix) + this.dbprefix + message);
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    abstract Connection open();

    public void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            this.printErr("There was no SQL connection open.", false);
        }
    }

    public void modifyQuery(final String query) {
        new BukkitRunnable(){

            public void run() {
                try {
                    PreparedStatement stmt = Database.this.connection.prepareStatement(query);
                    stmt.execute();
                    stmt.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously((Plugin)ProjectKorra.plugin);
    }

    public ResultSet readQuery(String query) {
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            return rs;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean tableExists(String table) {
        try {
            DatabaseMetaData dmd = this.connection.getMetaData();
            ResultSet rs = dmd.getTables(null, null, table, null);
            return rs.next();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

