/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 */
package com.projectkorra.projectkorra.firebending;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigLoadable;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.FireStream;

public class Illumination
implements ConfigLoadable {
    public static ConcurrentHashMap<Player, Illumination> instances = new ConcurrentHashMap();
    public static ConcurrentHashMap<Block, Player> blocks = new ConcurrentHashMap();
    private static int range = config.get().getInt("Abilities.Fire.Illumination.Range");
    private Player player;
    private Block block;
    private Material normaltype;
    private byte normaldata;

    public Illumination(Player player) {
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("Illumination")) {
            return;
        }
        if (instances.containsKey((Object)player)) {
            instances.get((Object)player).remove();
        } else {
            this.player = player;
            this.set();
            instances.put(player, this);
            bPlayer.addCooldown("Illumination", GeneralMethods.getGlobalCooldown());
        }
    }

    public static String getDescription() {
        return "This ability gives firebenders a means of illuminating the area. It is a toggle - clicking will create a torch that follows you around. The torch will only appear on objects that are ignitable and can hold a torch (e.g. not leaves or ice). If you get too far away from the torch, it will disappear, but will reappear when you get on another ignitable block. Clicking again dismisses this torch.";
    }

    public static void revert(Block block) {
        Player player = blocks.get((Object)block);
        instances.get((Object)player).revert();
    }

    public boolean progress() {
        if (!this.player.isOnline() || this.player.isDead()) {
            this.remove();
            return false;
        }
        if (!GeneralMethods.canBend(this.player.getName(), "Illumination")) {
            this.remove();
            return false;
        }
        this.set();
        return true;
    }

    public static void progressAll() {
        for (Illumination ability : instances.values()) {
            ability.progress();
        }
    }

    @Override
    public void reloadVariables() {
        range = config.get().getInt("Abilities.Fire.Illumination.Range");
    }

    public void remove() {
        this.revert();
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (Illumination ability : instances.values()) {
            ability.remove();
        }
    }

    private void revert() {
        if (this.block != null) {
            blocks.remove((Object)this.block);
            this.block.setType(this.normaltype);
            this.block.setData(this.normaldata);
        }
    }

    private void set() {
        Block standingblock = this.player.getLocation().getBlock();
        Block standblock = standingblock.getRelative(BlockFace.DOWN);
        if (standblock.getType() == Material.GLOWSTONE) {
            this.revert();
        } else if (FireStream.isIgnitable(this.player, standingblock) && standblock.getType() != Material.LEAVES && standblock.getType() != Material.LEAVES_2 && this.block == null && !blocks.containsKey((Object)standblock)) {
            this.block = standingblock;
            this.normaltype = this.block.getType();
            this.normaldata = this.block.getData();
            this.block.setType(Material.TORCH);
            blocks.put(this.block, this.player);
        } else if (FireStream.isIgnitable(this.player, standingblock) && standblock.getType() != Material.LEAVES && standblock.getType() != Material.LEAVES_2 && !this.block.equals((Object)standblock) && !blocks.containsKey((Object)standblock) && GeneralMethods.isSolid(standblock)) {
            this.revert();
            this.block = standingblock;
            this.normaltype = this.block.getType();
            this.normaldata = this.block.getData();
            this.block.setType(Material.TORCH);
            blocks.put(this.block, this.player);
        } else {
            if (this.block == null) {
                return;
            }
            if (!this.player.getWorld().equals((Object)this.block.getWorld())) {
                this.revert();
            } else if (this.player.getLocation().distance(this.block.getLocation()) > FireMethods.getFirebendingDayAugment(range, this.player.getWorld())) {
                this.revert();
            }
        }
    }
}

