/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitTask
 */
package com.projectkorra.projectkorra.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.projectkorra.projectkorra.ProjectKorra;

public class LogFilter
implements Filter {
    private List<String> loggedRecords = new ArrayList<String>();

    @Override
    public boolean isLoggable(LogRecord record) {
        if (record.getMessage() == null && record.getThrown() == null) {
            return false;
        }
        String recordString = "";
        if (record.getMessage() != null) {
            if (!record.getMessage().contains("ProjectKorra")) {
                if (record.getThrown() == null) {
                    return false;
                }
                if (record.getThrown().getMessage() == null) {
                    return false;
                }
                if (!record.getThrown().getMessage().contains("ProjectKorra")) {
                    return false;
                }
            }
            recordString = this.buildString(record);
        } else if (record.getThrown() != null) {
            if (record.getThrown().getMessage() == null) {
                return false;
            }
            if (!record.getThrown().getMessage().contains("ProjectKorra")) {
                return false;
            }
            recordString = this.buildString(record);
        }
        if (this.loggedRecords.contains(recordString)) {
            return false;
        }
        final String toRecord = recordString;
        Bukkit.getScheduler().runTaskLater((Plugin)ProjectKorra.plugin, new Runnable(){

            @Override
            public void run() {
                LogFilter.this.loggedRecords.add(toRecord);
            }
        }, 10);
        return true;
    }

    private String buildString(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        if (record.getMessage() != null) {
            builder.append(record.getMessage());
        }
        if (record.getThrown() != null) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            builder.append(writer);
        }
        return builder.toString();
    }

}

