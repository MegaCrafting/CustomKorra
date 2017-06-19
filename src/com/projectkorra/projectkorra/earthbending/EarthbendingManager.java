/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.projectkorra.projectkorra.earthbending;

import org.bukkit.Bukkit;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.earthbending.Catapult;
import com.projectkorra.projectkorra.earthbending.CompactColumn;
import com.projectkorra.projectkorra.earthbending.EarthArmor;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.earthbending.EarthColumn;
import com.projectkorra.projectkorra.earthbending.EarthPassive;
import com.projectkorra.projectkorra.earthbending.EarthSmash;
import com.projectkorra.projectkorra.earthbending.EarthTunnel;
import com.projectkorra.projectkorra.earthbending.LavaFlow;
import com.projectkorra.projectkorra.earthbending.LavaSurge;
import com.projectkorra.projectkorra.earthbending.MetalClips;
import com.projectkorra.projectkorra.earthbending.SandSpout;
import com.projectkorra.projectkorra.earthbending.Shockwave;
import com.projectkorra.projectkorra.earthbending.Tremorsense;
import com.projectkorra.projectkorra.util.RevertChecker;

public class EarthbendingManager
implements Runnable {
    public ProjectKorra plugin;

    public EarthbendingManager(ProjectKorra plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        EarthPassive.revertSands();
        EarthPassive.handleMetalPassives();
        EarthPassive.sandSpeed();
        RevertChecker.revertEarthBlocks();
        EarthTunnel.progressAll();
        EarthArmor.moveArmorAll();
        Catapult.progressAll();
        Tremorsense.manage(Bukkit.getServer());
        EarthColumn.progressAll();
        CompactColumn.progressAll();
        Shockwave.progressAll();
        EarthBlast.progressAll();
        MetalClips.progressAll();
        LavaSurge.progressAll();
        LavaFlow.progressAll();
        EarthSmash.progressAll();
        SandSpout.spoutAll();
    }
}

