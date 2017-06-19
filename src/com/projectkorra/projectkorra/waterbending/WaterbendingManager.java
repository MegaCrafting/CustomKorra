/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.projectkorra.projectkorra.waterbending;

import org.bukkit.Bukkit;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.waterbending.Bloodbending;
import com.projectkorra.projectkorra.waterbending.FreezeMelt;
import com.projectkorra.projectkorra.waterbending.HealingWaters;
import com.projectkorra.projectkorra.waterbending.IceBlast;
import com.projectkorra.projectkorra.waterbending.IceSpike;
import com.projectkorra.projectkorra.waterbending.IceSpike2;
import com.projectkorra.projectkorra.waterbending.OctopusForm;
import com.projectkorra.projectkorra.waterbending.PlantArmor;
import com.projectkorra.projectkorra.waterbending.Plantbending;
import com.projectkorra.projectkorra.waterbending.Torrent;
import com.projectkorra.projectkorra.waterbending.TorrentBurst;
import com.projectkorra.projectkorra.waterbending.WaterArms;
import com.projectkorra.projectkorra.waterbending.WaterCombo;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterPassive;
import com.projectkorra.projectkorra.waterbending.WaterReturn;
import com.projectkorra.projectkorra.waterbending.WaterSpout;
import com.projectkorra.projectkorra.waterbending.WaterWall;
import com.projectkorra.projectkorra.waterbending.WaterWave;
import com.projectkorra.projectkorra.waterbending.Wave;

public class WaterbendingManager
implements Runnable {
    public ProjectKorra plugin;

    public WaterbendingManager(ProjectKorra plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        WaterPassive.handlePassive();
        Plantbending.regrow();
        PlantArmor.progressAll();
        Bloodbending.progressAll();
        WaterSpout.handleSpouts(Bukkit.getServer());
        FreezeMelt.handleFrozenBlocks();
        OctopusForm.progressAll();
        Torrent.progressAll();
        TorrentBurst.progressAll();
        HealingWaters.heal(Bukkit.getServer());
        WaterReturn.progressAll();
        WaterManipulation.progressAll();
        WaterWall.progressAll();
        Wave.progressAll();
        IceSpike.progressAll();
        IceSpike2.progressAll();
        IceBlast.progressAll();
        WaterWave.progressAll();
        WaterCombo.progressAll();
        WaterArms.progressAll();
    }
}

