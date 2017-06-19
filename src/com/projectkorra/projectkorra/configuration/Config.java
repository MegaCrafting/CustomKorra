/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.FileConfigurationOptions
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package com.projectkorra.projectkorra.configuration;

import java.io.File;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;

import com.projectkorra.projectkorra.ProjectKorra;

public class Config {
    private ProjectKorra plugin = ProjectKorra.plugin;
    private File file;
    private FileConfiguration config;

    public Config(File file) {
        this.file = new File(this.plugin.getDataFolder() + File.separator + file);
        this.config = YamlConfiguration.loadConfiguration((File)this.file);
        this.reload();
    }

    public void create() {
        if (!this.file.getParentFile().exists()) {
            try {
                this.file.getParentFile().mkdir();
                this.plugin.getLogger().info("Generating new directory for " + this.file.getName() + "!");
            }
            catch (Exception e) {
                this.plugin.getLogger().info("Failed to generate directory!");
                e.printStackTrace();
            }
        }
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
                this.plugin.getLogger().info("Generating new " + this.file.getName() + "!");
            }
            catch (Exception e) {
                this.plugin.getLogger().info("Failed to generate " + this.file.getName() + "!");
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration get() {
        return this.config;
    }

    public void reload() {
        this.create();
        try {
            this.config.load(this.file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.config.options().copyDefaults(true);
            this.config.save(this.file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

