/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 */
package com.projectkorra.projectkorra.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.command.PKCommand;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

public class WhoCommand
extends PKCommand {
    Map<String, String> staff = new HashMap<String, String>();

    public WhoCommand() {
        super("who", "/bending who [Player]", "This command will tell you what element all players that are online are (If you don't specify a player) or give you information about the player that you specify.", new String[]{"who", "w"});
        this.staff.put("8621211e-283b-46f5-87bc-95a66d68880e", (Object)ChatColor.RED + "ProjectKorra Founder");
        this.staff.put("a197291a-cd78-43bb-aa38-52b7c82bc68c", (Object)ChatColor.DARK_PURPLE + "ProjectKorra Lead Developer");
        this.staff.put("929b14fc-aaf1-4f0f-84c2-f20c55493f53", (Object)ChatColor.GREEN + "ProjectKorra Head Concept Designer");
        this.staff.put("15d1a5a7-76ef-49c3-b193-039b27c47e30", (Object)ChatColor.BLUE + "ProjectKorra Digital Director");
        this.staff.put("1553482a-5e86-4270-9262-b57c11151074", (Object)ChatColor.GOLD + "ProjectKorra Head Community Moderator");
        this.staff.put("96f40c81-dd5d-46b6-9afe-365114d4a082", (Object)ChatColor.DARK_PURPLE + "ProjectKorra Developer");
        this.staff.put("833a7132-a9ec-4f0a-ad9c-c3d6b8a1c7eb", (Object)ChatColor.DARK_PURPLE + "ProjectKorra Developer");
        this.staff.put("4eb6315e-9dd1-49f7-b582-c1170e497ab0", (Object)ChatColor.DARK_PURPLE + "ProjectKorra Developer");
        this.staff.put("5031c4e3-8103-49ea-b531-0d6ae71bad69", (Object)ChatColor.DARK_PURPLE + "ProjectKorra Developer");
        this.staff.put("d7757be8-86de-4898-ab4f-2b1b2fbc3dfa", (Object)ChatColor.DARK_PURPLE + "ProjectKorra Developer");
        this.staff.put("a9673c93-9186-367a-96c4-e111a3bbd1b1", (Object)ChatColor.DARK_PURPLE + "ProjectKorra Developer");
        this.staff.put("dedf335b-d282-47ab-8ffc-a80121661cd1", (Object)ChatColor.DARK_PURPLE + "ProjectKorra Developer");
        this.staff.put("623df34e-9cd4-438d-b07c-1905e1fc46b6", (Object)ChatColor.GREEN + "ProjectKorra Concept Designer");
        this.staff.put("80f9072f-e37e-4adc-8675-1ba6af87d63b", (Object)ChatColor.GREEN + "ProjectKorra Concept Designer");
        this.staff.put("57205eec-96bd-4aa3-b73f-c6627429beb2", (Object)ChatColor.GREEN + "ProjectKorra Concept Designer");
        this.staff.put("7daead36-d285-4640-848a-2f105334b792", (Object)ChatColor.GREEN + "ProjectKorra Concept Designer");
        this.staff.put("f30c871e-cd60-446b-b219-e31e00e16857", (Object)ChatColor.GREEN + "ProjectKorra Concept Designer");
        this.staff.put("38217173-8a32-4ba7-9fe1-dd4fed031a74", (Object)ChatColor.GREEN + "ProjectKorra Concept Designer");
        this.staff.put("3d5bc713-ab8b-4125-b5ba-a1c1c2400b2c", (Object)ChatColor.GOLD + "ProjectKorra Community Moderator");
        this.staff.put("2ab334d1-9691-4994-a624-209c7b4f220b", (Object)ChatColor.BLUE + "ProjectKorra Digital Team");
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (!this.hasPermission(sender) || !this.correctLength(sender, args.size(), 0, 1)) {
            return;
        }
        if (args.size() == 1) {
            this.whoPlayer(sender, args.get(0));
        } else if (args.size() == 0) {
            ArrayList<String> players = new ArrayList<String>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerName = player.getName();
                BendingPlayer bp = GeneralMethods.getBendingPlayer(playerName);
                if (bp == null) {
                    GeneralMethods.createBendingPlayer(player.getUniqueId(), player.getName());
                    bp = GeneralMethods.getBendingPlayer(player.getName());
                }
                if (bp.getElements().size() > 1) {
                    players.add((Object)GeneralMethods.getAvatarColor() + playerName);
                    continue;
                }
                if (bp.getElements().size() == 0) {
                    players.add(playerName);
                    continue;
                }
                if (GeneralMethods.isBender(playerName, Element.Air)) {
                    players.add((Object)AirMethods.getAirColor() + playerName);
                    continue;
                }
                if (GeneralMethods.isBender(playerName, Element.Water)) {
                    players.add((Object)WaterMethods.getWaterColor() + playerName);
                    continue;
                }
                if (GeneralMethods.isBender(playerName, Element.Earth)) {
                    players.add((Object)EarthMethods.getEarthColor() + playerName);
                    continue;
                }
                if (GeneralMethods.isBender(playerName, Element.Chi)) {
                    players.add((Object)ChiMethods.getChiColor() + playerName);
                    continue;
                }
                if (!GeneralMethods.isBender(playerName, Element.Fire)) continue;
                players.add((Object)FireMethods.getFireColor() + playerName);
            }
            if (players.isEmpty()) {
                sender.sendMessage((Object)ChatColor.RED + "There is no one online.");
            } else {
                for (String st : players) {
                    sender.sendMessage(st);
                }
            }
        }
    }

    private void whoPlayer(final CommandSender sender, final String playerName) {
        final OfflinePlayer player = Bukkit.getOfflinePlayer((String)playerName);
        if (player == null || !player.hasPlayedBefore()) {
            sender.sendMessage((Object)ChatColor.RED + "Player not found!");
            return;
        }
        if (!player.isOnline() && !BendingPlayer.getPlayers().containsKey(player.getUniqueId())) {
            sender.sendMessage(String.valueOf(player.getName()) + (Object)ChatColor.GRAY + " is currently offline. A lookup is currently being done (this might take a few seconds).");
        }
        Player player_ = (Player)(player.isOnline() ? player : null);
        if (!BendingPlayer.getPlayers().containsKey(player.getUniqueId())) {
            GeneralMethods.createBendingPlayer(player.getUniqueId(), playerName);
            BukkitRunnable runnable = new BukkitRunnable(){

                public void run() {
                    int count = 0;
                    long delay = 200;
                    while (!BendingPlayer.getPlayers().containsKey(player.getUniqueId())) {
                        if ((long)count > 25) {
                            sender.sendMessage((Object)ChatColor.DARK_RED + "The database appears to busy at the moment. Please wait a few seconds and try again.");
                            break;
                        }
                        ++count;
                        try {
                            Thread.sleep(200);
                            continue;
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                            sender.sendMessage((Object)ChatColor.DARK_RED + "The database appears to busy at the moment. Please wait a few seconds and try again.");
                            break;
                        }
                    }
                    WhoCommand.this.whoPlayer(sender, playerName);
                }
            };
            runnable.runTaskAsynchronously((Plugin)ProjectKorra.plugin);
            return;
        }
        if (BendingPlayer.getPlayers().containsKey(player.getUniqueId())) {
            sender.sendMessage(String.valueOf(player.getName()) + (!player.isOnline() ? new StringBuilder().append((Object)ChatColor.RESET).append(" (Offline)").toString() : "") + " - ");
            if (GeneralMethods.isBender(playerName, Element.Air)) {
                sender.sendMessage((Object)AirMethods.getAirColor() + "- Airbender");
                if (player_ != null && AirMethods.canAirFlight((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Air) + "    Can Fly");
                }
                if (player_ != null && AirMethods.canUseSpiritualProjection((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Air) + "    Can use Spiritual Projection");
                }
            }
            if (GeneralMethods.isBender(playerName, Element.Water)) {
                sender.sendMessage((Object)WaterMethods.getWaterColor() + "- Waterbender");
                if (player_ != null && WaterMethods.canPlantbend((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Water) + "    Can Plantbend");
                }
                if (player_ != null && WaterMethods.canBloodbend((Player)player)) {
                    if (WaterMethods.canBloodbendAtAnytime((Player)player)) {
                        sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Water) + "    Can Bloodbend anytime, on any day");
                    } else {
                        sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Water) + "    Can Bloodbend");
                    }
                }
                if (player_ != null && WaterMethods.canIcebend((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Water) + "    Can Icebend");
                }
                if (player_ != null && WaterMethods.canWaterHeal((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Water) + "    Can Heal");
                }
            }
            if (GeneralMethods.isBender(playerName, Element.Earth)) {
                sender.sendMessage((Object)EarthMethods.getEarthColor() + "- Earthbender");
                if (player_ != null && EarthMethods.canMetalbend((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Earth) + "    Can Metalbend");
                }
                if (player_ != null && EarthMethods.canLavabend((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Earth) + "    Can Lavabend");
                }
                if (player_ != null && EarthMethods.canSandbend((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Earth) + "    Can Sandbend");
                }
            }
            if (GeneralMethods.isBender(playerName, Element.Fire)) {
                sender.sendMessage((Object)FireMethods.getFireColor() + "- Firebender");
                if (player_ != null && FireMethods.canCombustionbend((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Fire) + "    Can Combustionbend");
                }
                if (player_ != null && FireMethods.canLightningbend((Player)player)) {
                    sender.sendMessage((Object)GeneralMethods.getSubBendingColor(Element.Fire) + "    Can Lightningbend");
                }
            }
            if (GeneralMethods.isBender(playerName, Element.Chi)) {
                sender.sendMessage((Object)ChiMethods.getChiColor() + "- ChiBlocker");
            }
            BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(playerName);
            UUID uuid = player.getUniqueId();
            if (bPlayer != null) {
                sender.sendMessage("Abilities: ");
                int i = 1;
                while (i <= 9) {
                    String ability = bPlayer.getAbilities().get(i);
                    if (ability != null && !ability.equalsIgnoreCase("null")) {
                        sender.sendMessage(String.valueOf(i) + " - " + (Object)GeneralMethods.getAbilityColor(ability) + ability);
                    }
                    ++i;
                }
            }
            if (this.staff.containsKey(uuid.toString())) {
                sender.sendMessage(this.staff.get(uuid.toString()));
            }
        }
    }

}

