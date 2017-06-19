/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package com.projectkorra.projectkorra.airbending;

import org.bukkit.Bukkit;

import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirBlast;
import com.projectkorra.projectkorra.airbending.AirBubble;
import com.projectkorra.projectkorra.airbending.AirBurst;
import com.projectkorra.projectkorra.airbending.AirCombo;
import com.projectkorra.projectkorra.airbending.AirPassive;
import com.projectkorra.projectkorra.airbending.AirScooter;
import com.projectkorra.projectkorra.airbending.AirShield;
import com.projectkorra.projectkorra.airbending.AirSpout;
import com.projectkorra.projectkorra.airbending.AirSuction;
import com.projectkorra.projectkorra.airbending.AirSwipe;
import com.projectkorra.projectkorra.airbending.FlightAbility;
import com.projectkorra.projectkorra.airbending.Suffocate;
import com.projectkorra.projectkorra.airbending.Tornado;

public class AirbendingManager
implements Runnable {
    public ProjectKorra plugin;

    public AirbendingManager(ProjectKorra plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        AirBlast.progressAll();
        AirPassive.handlePassive(Bukkit.getServer());
        AirBurst.progressAll();
        AirScooter.progressAll();
        Suffocate.progressAll();
        AirSpout.progressAll();
        AirBubble.handleBubbles(Bukkit.getServer());
        AirSuction.progressAll();
        AirSwipe.progressAll();
        Tornado.progressAll();
        AirShield.progressAll();
        AirCombo.progressAll();
        FlightAbility.progressAll();
    }
}

