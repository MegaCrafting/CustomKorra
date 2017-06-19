/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.airbending;

import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirBlast;
import com.projectkorra.projectkorra.airbending.AirBubble;
import com.projectkorra.projectkorra.airbending.AirBurst;
import com.projectkorra.projectkorra.airbending.AirCombo;
import com.projectkorra.projectkorra.airbending.AirScooter;
import com.projectkorra.projectkorra.airbending.AirShield;
import com.projectkorra.projectkorra.airbending.AirSpout;
import com.projectkorra.projectkorra.airbending.AirSuction;
import com.projectkorra.projectkorra.airbending.AirSwipe;
import com.projectkorra.projectkorra.airbending.FlightAbility;
import com.projectkorra.projectkorra.airbending.Suffocate;
import com.projectkorra.projectkorra.airbending.Tornado;
import com.projectkorra.projectkorra.util.ParticleEffect;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AirMethods {
    private static ProjectKorra plugin;
    private static FileConfiguration config;

    static {
        config = ProjectKorra.plugin.getConfig();
    }

    public AirMethods(ProjectKorra plugin) {
        AirMethods.plugin = plugin;
    }

    public static boolean canAirFlight(Player player) {
        if (player.hasPermission("bending.air.flight")) {
            return true;
        }
        return false;
    }

    public static boolean canUseSpiritualProjection(Player player) {
        if (player.hasPermission("bending.air.spiritualprojection")) {
            return true;
        }
        return false;
    }

    public static ChatColor getAirColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.Air"));
    }

    public static ChatColor getAirSubColor() {
        return ChatColor.valueOf((String)config.getString("Properties.Chat.Colors.AirSub"));
    }

    public static boolean isAirAbility(String ability) {
        return AbilityModuleManager.airbendingabilities.contains(ability);
    }

    public static boolean isFlightAbility(String ability) {
        return AbilityModuleManager.flightabilities.contains(ability);
    }

    public static boolean isSpiritualProjectionAbility(String ability) {
        return AbilityModuleManager.spiritualprojectionabilities.contains(ability);
    }

    public static ParticleEffect getAirbendingParticles() {
        String particle = plugin.getConfig().getString("Properties.Air.Particles");
        if (particle == null) {
            return ParticleEffect.CLOUD;
        }
        if (particle.equalsIgnoreCase("spell")) {
            return ParticleEffect.SPELL;
        }
        if (particle.equalsIgnoreCase("blacksmoke")) {
            return ParticleEffect.SMOKE_LARGE;
        }
        if (particle.equalsIgnoreCase("smoke")) {
            return ParticleEffect.CLOUD;
        }
        if (particle.equalsIgnoreCase("smallsmoke")) {
            return ParticleEffect.SNOW_SHOVEL;
        }
        return ParticleEffect.CLOUD;
    }

    public static void playAirbendingParticles(Location loc, int amount) {
        AirMethods.playAirbendingParticles(loc, amount, (float)Math.random(), (float)Math.random(), (float)Math.random());
    }

    public static void playAirbendingParticles(Location loc, int amount, float xOffset, float yOffset, float zOffset) {
    	if(amount == 0)
    		amount = 1;
    	
        AirMethods.getAirbendingParticles().display(loc, xOffset, yOffset, zOffset, 0.001f, amount);
    }

    public static void removeAirSpouts(Location loc, double radius, Player source) {
        AirSpout.removeSpouts(loc, radius, source);
    }

    public static void removeAirSpouts(Location loc, Player source) {
        AirMethods.removeAirSpouts(loc, 1.5, source);
    }

    public static void stopBending() {
        AirBlast.removeAll();
        AirBubble.removeAll();
        AirShield.removeAll();
        AirSuction.removeAll();
        AirScooter.removeAll();
        AirSpout.removeAll();
        AirSwipe.removeAll();
        Tornado.removeAll();
        AirBurst.removeAll();
        Suffocate.removeAll();
        AirCombo.removeAll();
        FlightAbility.removeAll();
    }

    public static void breakBreathbendingHold(Entity entity) {
        Player player;
        if (Suffocate.isBreathbent(entity)) {
            Suffocate.breakSuffocate(entity);
            return;
        }
        if (entity instanceof Player && Suffocate.isChannelingSphere(player = (Player)entity)) {
            Suffocate.remove(player);
        }
    }

    public static void playAirbendingSound(Location loc) {
        if (plugin.getConfig().getBoolean("Properties.Air.PlaySound")) {
            loc.getWorld().playSound(loc, Sound.ENTITY_CREEPER_HURT, 1.0f, 5.0f);
        }
    }

    public static boolean isWithinAirShield(Location loc) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("AirShield");
        return GeneralMethods.blockAbilities(null, list, loc, 0.0);
    }

    public static boolean canFly(Player player, boolean first, boolean hovering) {
        BendingPlayer bender = GeneralMethods.getBendingPlayer(player.getName());
        if (!player.isOnline()) {
            return false;
        }
        if (!(player.isSneaking() || first || hovering)) {
            return false;
        }
        if (bender.isChiBlocked()) {
            return false;
        }
        if (!player.isOnline()) {
            return false;
        }
        if (bender.isPermaRemoved()) {
            return false;
        }
        if (!bender.getElements().contains((Object)Element.Air)) {
            return false;
        }
        if (!GeneralMethods.canBend(player.getName(), "Flight")) {
            return false;
        }
        if (!GeneralMethods.getBoundAbility(player).equalsIgnoreCase("Flight")) {
            return false;
        }
        if (GeneralMethods.isRegionProtectedFromBuild(player, "Flight", player.getLocation())) {
            return false;
        }
        if (player.getLocation().subtract(0.0, 0.5, 0.0).getBlock().getType() != Material.AIR) {
            return false;
        }
        return true;
    }
}

