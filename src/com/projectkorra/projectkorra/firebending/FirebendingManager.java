/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.firebending;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.firebending.Combustion;
import com.projectkorra.projectkorra.firebending.Cook;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.firebending.FireBurst;
import com.projectkorra.projectkorra.firebending.FireCombo;
import com.projectkorra.projectkorra.firebending.FireJet;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.FirePassive;
import com.projectkorra.projectkorra.firebending.FireShield;
import com.projectkorra.projectkorra.firebending.FireStream;
import com.projectkorra.projectkorra.firebending.Fireball;
import com.projectkorra.projectkorra.firebending.HeatControl;
import com.projectkorra.projectkorra.firebending.Illumination;
import com.projectkorra.projectkorra.firebending.Lightning;
import com.projectkorra.projectkorra.firebending.WallOfFire;

public class FirebendingManager
implements Runnable {
    public ProjectKorra plugin;

    public FirebendingManager(ProjectKorra plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        FirePassive.handlePassive();
        FireJet.progressAll();
        Cook.progressAll();
        Illumination.progressAll();
        FireBlast.progressAll();
        Fireball.progressAll();
        FireBurst.progressAll();
        FireShield.progressAll();
        Lightning.progressAll();
        WallOfFire.progressAll();
        Combustion.progressAll();
        for (Block block : FireStream.ignitedblocks.keySet()) {
            if (block.getType() == Material.FIRE) continue;
            FireStream.ignitedblocks.remove((Object)block);
        }
        FireMethods.removeFire();
        HeatControl.progressAll();
        FireStream.dissipateAll();
        FireStream.progressAll();
        FireCombo.progressAll();
    }
}

