/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.FallingBlock
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.Slime
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockFadeEvent
 *  org.bukkit.event.block.BlockFormEvent
 *  org.bukkit.event.block.BlockFromToEvent
 *  org.bukkit.event.block.BlockIgniteEvent
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityChangeBlockEvent
 *  org.bukkit.event.entity.EntityCombustEvent
 *  org.bukkit.event.entity.EntityDamageByBlockEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.EntityInteractEvent
 *  org.bukkit.event.entity.EntityShootBowEvent
 *  org.bukkit.event.entity.EntityTargetEvent
 *  org.bukkit.event.entity.EntityTargetLivingEntityEvent
 *  org.bukkit.event.entity.EntityTeleportEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.entity.ProjectileHitEvent
 *  org.bukkit.event.entity.ProjectileLaunchEvent
 *  org.bukkit.event.entity.SlimeSplitEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.event.inventory.InventoryType$SlotType
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerAnimationEvent
 *  org.bukkit.event.player.PlayerGameModeChangeEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerMoveEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerToggleFlightEvent
 *  org.bukkit.event.player.PlayerToggleSneakEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitRunnable
 *  org.bukkit.scheduler.BukkitTask
 *  org.bukkit.util.Vector
 */
package com.projectkorra.projectkorra;

import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.combo.ComboManager;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.airbending.AirBlast;
import com.projectkorra.projectkorra.airbending.AirBubble;
import com.projectkorra.projectkorra.airbending.AirBurst;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.airbending.AirScooter;
import com.projectkorra.projectkorra.airbending.AirSpout;
import com.projectkorra.projectkorra.airbending.AirSuction;
import com.projectkorra.projectkorra.airbending.AirSwipe;
import com.projectkorra.projectkorra.airbending.FlightAbility;
import com.projectkorra.projectkorra.airbending.Suffocate;
import com.projectkorra.projectkorra.airbending.Tornado;
import com.projectkorra.projectkorra.chiblocking.ChiCombo;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.chiblocking.ChiPassive;
import com.projectkorra.projectkorra.chiblocking.Paralyze;
import com.projectkorra.projectkorra.chiblocking.Smokescreen;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.earthbending.EarthArmor;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.earthbending.EarthGrab;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.earthbending.EarthPassive;
import com.projectkorra.projectkorra.earthbending.LavaFlow;
import com.projectkorra.projectkorra.earthbending.LavaSurge;
import com.projectkorra.projectkorra.earthbending.LavaWave;
import com.projectkorra.projectkorra.earthbending.MetalClips;
import com.projectkorra.projectkorra.earthbending.SandSpout;
import com.projectkorra.projectkorra.earthbending.Shockwave;
import com.projectkorra.projectkorra.event.HorizontalVelocityChangeEvent;
import com.projectkorra.projectkorra.event.PlayerBendingDeathEvent;
import com.projectkorra.projectkorra.event.PlayerChangeElementEvent;
import com.projectkorra.projectkorra.firebending.Combustion;
import com.projectkorra.projectkorra.firebending.Enflamed;
import com.projectkorra.projectkorra.firebending.Extinguish;
import com.projectkorra.projectkorra.firebending.FireBurst;
import com.projectkorra.projectkorra.firebending.FireJet;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.FireShield;
import com.projectkorra.projectkorra.firebending.FireStream;
import com.projectkorra.projectkorra.firebending.Fireball;
import com.projectkorra.projectkorra.firebending.Illumination;
import com.projectkorra.projectkorra.object.HorizontalVelocityTracker;
import com.projectkorra.projectkorra.object.Preset;
import com.projectkorra.projectkorra.util.BlockSource;
import com.projectkorra.projectkorra.util.ClickType;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Bloodbending;
import com.projectkorra.projectkorra.waterbending.FreezeMelt;
import com.projectkorra.projectkorra.waterbending.IceBlast;
import com.projectkorra.projectkorra.waterbending.IceSpike2;
import com.projectkorra.projectkorra.waterbending.OctopusForm;
import com.projectkorra.projectkorra.waterbending.PlantArmor;
import com.projectkorra.projectkorra.waterbending.Torrent;
import com.projectkorra.projectkorra.waterbending.WaterArms;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterPassive;
import com.projectkorra.projectkorra.waterbending.WaterSpout;
import com.projectkorra.projectkorra.waterbending.WaterWall;
import com.projectkorra.projectkorra.waterbending.WaterWave;
import com.projectkorra.projectkorra.waterbending.Wave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PKListener
implements Listener {
    ProjectKorra plugin;
    public static HashMap<Player, String> bendingDeathPlayer = new HashMap();

    public PKListener(ProjectKorra plugin) {
        this.plugin = plugin;
    }

    public static void login(BendingPlayer pl) {
        ProjectKorra plugin = ProjectKorra.plugin;
        Player player = Bukkit.getPlayer((UUID)pl.getUUID());
        if (player == null) {
            return;
        }
        if (GeneralMethods.toggedOut.contains(player.getUniqueId())) {
            GeneralMethods.getBendingPlayer(player.getName()).toggleBending();
            player.sendMessage((Object)ChatColor.YELLOW + "Reminder, you toggled your bending before signing off. Enable it again with /bending toggle.");
        }
        Preset.loadPresets(player);
        String append = "";
        ChatColor color = null;
        boolean chatEnabled = ProjectKorra.plugin.getConfig().getBoolean("Properties.Chat.Enable");
        if ((player.hasPermission("bending.avatar") || GeneralMethods.getBendingPlayer(player.getName()).getElements().size() > 1) && chatEnabled) {
            append = plugin.getConfig().getString("Properties.Chat.Prefixes.Avatar");
            color = ChatColor.valueOf((String)plugin.getConfig().getString("Properties.Chat.Colors.Avatar"));
        } else if (GeneralMethods.isBender(player.getName(), Element.Air) && chatEnabled) {
            append = plugin.getConfig().getString("Properties.Chat.Prefixes.Air");
            color = AirMethods.getAirColor();
        } else if (GeneralMethods.isBender(player.getName(), Element.Water) && chatEnabled) {
            append = plugin.getConfig().getString("Properties.Chat.Prefixes.Water");
            color = WaterMethods.getWaterColor();
        } else if (GeneralMethods.isBender(player.getName(), Element.Earth) && chatEnabled) {
            append = plugin.getConfig().getString("Properties.Chat.Prefixes.Earth");
            color = EarthMethods.getEarthColor();
        } else if (GeneralMethods.isBender(player.getName(), Element.Fire) && chatEnabled) {
            append = plugin.getConfig().getString("Properties.Chat.Prefixes.Fire");
            color = FireMethods.getFireColor();
        } else if (GeneralMethods.isBender(player.getName(), Element.Chi) && chatEnabled) {
            append = plugin.getConfig().getString("Properties.Chat.Prefixes.Chi");
            color = ChiMethods.getChiColor();
        }
        if (chatEnabled) {
            player.setDisplayName(player.getName());
            player.setDisplayName((Object)color + append + (Object)ChatColor.RESET + player.getDisplayName());
        }
        if (player.getGameMode() != GameMode.CREATIVE) {
            HashMap<Integer, String> bound = GeneralMethods.getBendingPlayer(player.getName()).getAbilities();
            for (String str : bound.values()) {
                if (!str.equalsIgnoreCase("AirSpout") && !str.equalsIgnoreCase("WaterSpout") && !str.equalsIgnoreCase("SandSpout")) continue;
                Player fplayer = player;
                new BukkitRunnable(){

                    public void run() {
                        fplayer.setFlying(false);
                        fplayer.setAllowFlight(false);
                    }
                }.runTaskLater((Plugin)ProjectKorra.plugin, 2);
                break;
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (WaterWall.wasBrokenFor(player, block) || OctopusForm.wasBrokenFor(player, block) || Torrent.wasBrokenFor(player, block) || WaterWave.wasBrokenFor(player, block)) {
            event.setCancelled(true);
            return;
        }
        EarthBlast blast = EarthBlast.getBlastFromSource(block);
        if (blast != null) {
            blast.cancel();
        }
        if (FreezeMelt.frozenblocks.containsKey((Object)block)) {
            FreezeMelt.thaw(block);
            event.setCancelled(true);
        } else if (WaterWall.wallblocks.containsKey((Object)block)) {
            WaterWall.thaw(block);
            event.setCancelled(true);
        } else if (Illumination.blocks.containsKey((Object)block)) {
            event.setCancelled(true);
        } else if (!Wave.canThaw(block)) {
            Wave.thaw(block);
            event.setCancelled(true);
        } else if (EarthMethods.movedearth.containsKey((Object)block)) {
            EarthMethods.removeRevertIndex(block);
        } else if (TempBlock.isTempBlock(block)) {
            TempBlock.revertBlock(block, Material.AIR);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockFlowTo(BlockFromToEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Block toblock = event.getToBlock();
        Block fromblock = event.getBlock();
        if (EarthMethods.isLava(fromblock)) {
            event.setCancelled(!EarthPassive.canFlowFromTo(fromblock, toblock));
        }
        if (WaterMethods.isWater(fromblock)) {
            event.setCancelled(!AirBubble.canFlowTo(toblock));
            if (!event.isCancelled()) {
                event.setCancelled(!WaterManipulation.canFlowFromTo(fromblock, toblock));
            }
            if (!event.isCancelled() && Illumination.blocks.containsKey((Object)toblock)) {
                toblock.setType(Material.AIR);
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockForm(BlockFormEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (TempBlock.isTempBlock(event.getBlock())) {
            event.setCancelled(true);
        }
        if (!WaterManipulation.canPhysicsChange(event.getBlock())) {
            event.setCancelled(true);
        }
        if (!EarthPassive.canPhysicsChange(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.isCancelled()) {
            return;
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockMeltEvent(BlockFadeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        if (block.getType() == Material.FIRE) {
            return;
        }
        event.setCancelled(Illumination.blocks.containsKey((Object)block));
        if (!event.isCancelled()) {
            event.setCancelled(!WaterManipulation.canPhysicsChange(block));
        }
        if (!event.isCancelled()) {
            event.setCancelled(!EarthPassive.canPhysicsChange(block));
        }
        if (!event.isCancelled()) {
            event.setCancelled(FreezeMelt.frozenblocks.containsKey((Object)block));
        }
        if (!event.isCancelled()) {
            event.setCancelled(!Wave.canThaw(block));
        }
        if (!event.isCancelled()) {
            event.setCancelled(!Torrent.canThaw(block));
        }
        if (FireStream.ignitedblocks.containsKey((Object)block)) {
            FireStream.remove(block);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        event.setCancelled(!WaterManipulation.canPhysicsChange(block));
        event.setCancelled(!EarthPassive.canPhysicsChange(block));
        if (!event.isCancelled()) {
            event.setCancelled(Illumination.blocks.containsKey((Object)block));
        }
        if (!event.isCancelled()) {
            event.setCancelled(EarthMethods.tempnophysics.contains((Object)block));
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (Paralyze.isParalyzed((Entity)player) || ChiCombo.isParalyzed(player) || Bloodbending.isBloodbended((Entity)player) || Suffocate.isBreathbent((Entity)player)) {
            event.setCancelled(true);
        }
        BendingPlayer bp = GeneralMethods.getBendingPlayer(event.getPlayer().getName());
        bp.addCooldown("RightClickFix", 40);
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onElementChange(PlayerChangeElementEvent event) {
        Player player = event.getTarget();
        Element e = event.getElement();
        String append = "";
        ChatColor color = null;
        boolean chatEnabled = ProjectKorra.plugin.getConfig().getBoolean("Properties.Chat.Enable");
        if (GeneralMethods.getBendingPlayer(player.getName()).getElements().size() > 1) {
            append = this.plugin.getConfig().getString("Properties.Chat.Prefixes.Avatar");
            color = ChatColor.valueOf((String)this.plugin.getConfig().getString("Properties.Chat.Colors.Avatar"));
        } else if (e == Element.Air && chatEnabled) {
            append = this.plugin.getConfig().getString("Properties.Chat.Prefixes.Air");
            color = AirMethods.getAirColor();
        } else if (e == Element.Water && chatEnabled) {
            append = this.plugin.getConfig().getString("Properties.Chat.Prefixes.Water");
            color = WaterMethods.getWaterColor();
        } else if (e == Element.Earth && chatEnabled) {
            append = this.plugin.getConfig().getString("Properties.Chat.Prefixes.Earth");
            color = EarthMethods.getEarthColor();
        } else if (e == Element.Fire && chatEnabled) {
            append = this.plugin.getConfig().getString("Properties.Chat.Prefixes.Fire");
            color = FireMethods.getFireColor();
        } else if (e == Element.Chi && chatEnabled) {
            append = this.plugin.getConfig().getString("Properties.Chat.Prefixes.Chi");
            color = ChiMethods.getChiColor();
        }
        if (chatEnabled) {
            player.setDisplayName(player.getName());
            player.setDisplayName((Object)color + append + (Object)ChatColor.RESET + player.getDisplayName());
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (Paralyze.isParalyzed(entity) || ChiCombo.isParalyzed(entity) || Bloodbending.isBloodbended(entity) || Suffocate.isBreathbent(entity)) {
            event.setCancelled(true);
        }
        if (event.getEntityType() == EntityType.FALLING_BLOCK && LavaSurge.falling.contains((Object)entity)) {
            LavaSurge.falling.remove((Object)entity);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        Block block = entity.getLocation().getBlock();
        if (FireStream.ignitedblocks.containsKey((Object)block) && entity instanceof LivingEntity) {
            new com.projectkorra.projectkorra.firebending.Enflamed(entity, FireStream.ignitedblocks.get((Object)block));
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityDamageBlock(EntityDamageByBlockEvent event) {
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getCause().equals((Object)EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) && event.getDamager() == null) {
            event.setCancelled(true);
        }
        if (event.getDamager() != null && LavaWave.isBlockInWave(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE && FireStream.ignitedblocks.containsKey((Object)entity.getLocation().getBlock())) {
            new com.projectkorra.projectkorra.firebending.Enflamed(entity, FireStream.ignitedblocks.get((Object)entity.getLocation().getBlock()));
        }
        if (Enflamed.isEnflamed(entity) && event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            event.setCancelled(true);
            Enflamed.dealFlameDamage(entity);
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            if (GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Fire)) {
                return;
            }
            if (GeneralMethods.getBoundAbility(player) != null && GeneralMethods.getBoundAbility(player).equalsIgnoreCase("HeatControl") && (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)) {
                player.setFireTicks(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (MetalClips.clipped.containsKey((Object)event.getEntity())) {
            List drops = event.getDrops();
            ArrayList<ItemStack> newdrops = new ArrayList<ItemStack>();
            int i = 0;
            while (i < drops.size()) {
                if (((ItemStack)drops.get(i)).getType() != Material.IRON_HELMET && ((ItemStack)drops.get(i)).getType() != Material.IRON_CHESTPLATE && ((ItemStack)drops.get(i)).getType() != Material.IRON_LEGGINGS && ((ItemStack)drops.get(i)).getType() != Material.IRON_BOOTS && ((ItemStack)drops.get(i)).getType() != Material.AIR) {
                    newdrops.add((ItemStack)drops.get(i));
                }
                ++i;
            }
            newdrops.add(new ItemStack(Material.IRON_INGOT, MetalClips.clipped.get((Object)event.getEntity()).intValue()));
            newdrops.add(MetalClips.getOriginalHelmet(event.getEntity()));
            newdrops.add(MetalClips.getOriginalChestplate(event.getEntity()));
            newdrops.add(MetalClips.getOriginalLeggings(event.getEntity()));
            newdrops.add(MetalClips.getOriginalBoots(event.getEntity()));
            event.getDrops().clear();
            event.getDrops().addAll(newdrops);
            MetalClips.clipped.remove((Object)event.getEntity());
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        for (Block block : event.blockList()) {
            EarthBlast blast = EarthBlast.getBlastFromSource(block);
            if (blast != null) {
                blast.cancel();
            }
            if (FreezeMelt.frozenblocks.containsKey((Object)block)) {
                FreezeMelt.thaw(block);
            }
            if (WaterWall.wallblocks.containsKey((Object)block)) {
                block.setType(Material.AIR);
            }
            if (!Wave.canThaw(block)) {
                Wave.thaw(block);
            }
            if (!EarthMethods.movedearth.containsKey((Object)block)) continue;
            EarthMethods.removeRevertIndex(block);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityExplodeEvent(EntityExplodeEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity != null && (Paralyze.isParalyzed(entity) || ChiCombo.isParalyzed(entity) || Bloodbending.isBloodbended(entity) || Suffocate.isBreathbent(entity))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityInteractEvent(EntityInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (Paralyze.isParalyzed(entity) || ChiCombo.isParalyzed(entity) || Bloodbending.isBloodbended(entity) || Suffocate.isBreathbent(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityProjectileLaunchEvent(ProjectileLaunchEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Projectile entity = event.getEntity();
        if (Paralyze.isParalyzed((Entity)entity) || ChiCombo.isParalyzed((Entity)entity) || Bloodbending.isBloodbended((Entity)entity) || Suffocate.isBreathbent((Entity)entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityShootBowEvent(EntityShootBowEvent event) {
        if (event.isCancelled()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (Paralyze.isParalyzed((Entity)entity) || ChiCombo.isParalyzed((Entity)entity) || Bloodbending.isBloodbended((Entity)entity) || Suffocate.isBreathbent((Entity)entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntitySlimeSplitEvent(SlimeSplitEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Slime entity = event.getEntity();
        if (Paralyze.isParalyzed((Entity)entity) || ChiCombo.isParalyzed((Entity)entity) || Bloodbending.isBloodbended((Entity)entity) || Suffocate.isBreathbent((Entity)entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntitySuffocatedByTempBlocks(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && TempBlock.isTempBlock(event.getEntity().getLocation().add(0.0, 1.0, 0.0).getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (Paralyze.isParalyzed(entity) || ChiCombo.isParalyzed(entity) || Bloodbending.isBloodbended(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityTargetLiving(EntityTargetLivingEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (Paralyze.isParalyzed(entity) || ChiCombo.isParalyzed(entity) || Bloodbending.isBloodbended(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onEntityTeleportEvent(EntityTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Entity entity = event.getEntity();
        if (Paralyze.isParalyzed(entity) || ChiCombo.isParalyzed(entity) || Bloodbending.isBloodbended(entity) || Suffocate.isBreathbent(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHorizontalCollision(HorizontalVelocityChangeEvent e) {
        if (!this.plugin.getConfig().getBoolean("Properties.HorizontalCollisionPhysics.Enabled")) {
            return;
        }
        if (e.getEntity() instanceof LivingEntity && e.getEntity().getEntityId() != e.getInstigator().getEntityId()) {
            double minimumDistance = this.plugin.getConfig().getDouble("Properties.HorizontalCollisionPhysics.WallDamageMinimumDistance");
            double maxDamage = this.plugin.getConfig().getDouble("Properties.HorizontalCollisionPhysics.WallDamageCap");
            double damage = (e.getDistanceTraveled() - minimumDistance < 0.0 ? 0.0 : e.getDistanceTraveled() - minimumDistance) / e.getDifference().length();
            if (damage > 0.0) {
                if (damage <= maxDamage) {
                    GeneralMethods.damageEntity(e.getInstigator(), e.getEntity(), damage, e.getElement(), e.getSubElement(), e.getAbility());
                } else {
                    GeneralMethods.damageEntity(e.getInstigator(), e.getEntity(), maxDamage, e.getElement(), e.getSubElement(), e.getAbility());
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }
        for (Player p : MetalClips.instances.keySet()) {
            if (MetalClips.instances.get((Object)p).getTarget() == null || MetalClips.instances.get((Object)p).getTarget().getEntityId() != event.getWhoClicked().getEntityId()) continue;
            event.setCancelled(true);
        }
        if (event.getSlotType() == InventoryType.SlotType.ARMOR && !EarthArmor.canRemoveArmor((Player)event.getWhoClicked())) {
            event.setCancelled(true);
        }
        if (event.getSlotType() == InventoryType.SlotType.ARMOR && !PlantArmor.canRemoveArmor((Player)event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPlayerBendingDeath(PlayerBendingDeathEvent event) {
        if (ConfigManager.deathMsgConfig.get().getBoolean("Properties.Enabled") && event.getAbility() != null) {
            StringBuilder sb = new StringBuilder();
            if (event.getSubElement() != null) {
                sb.append((Object)event.getSubElement().getChatColor());
            } else if (event.getElement() != null) {
                sb.append((Object)event.getElement().getChatColor());
            }
            sb.append(event.getAbility());
            bendingDeathPlayer.put(event.getVictim(), sb.toString());
            final Player player = event.getVictim();
            new BukkitRunnable(){

                public void run() {
                    PKListener.bendingDeathPlayer.remove((Object)player);
                }
            }.runTaskLater((Plugin)ProjectKorra.plugin, 20);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!this.plugin.getConfig().getBoolean("Properties.Chat.Enable")) {
            return;
        }
        Player player = event.getPlayer();
        ChatColor color = ChatColor.WHITE;
        if (player.hasPermission("bending.avatar") || GeneralMethods.getBendingPlayer(player.getName()).getElements().size() > 1) {
            color = ChatColor.valueOf((String)this.plugin.getConfig().getString("Properties.Chat.Colors.Avatar"));
        } else if (GeneralMethods.isBender(player.getName(), Element.Air)) {
            color = ChatColor.valueOf((String)this.plugin.getConfig().getString("Properties.Chat.Colors.Air"));
        } else if (GeneralMethods.isBender(player.getName(), Element.Water)) {
            color = ChatColor.valueOf((String)this.plugin.getConfig().getString("Properties.Chat.Colors.Water"));
        } else if (GeneralMethods.isBender(player.getName(), Element.Earth)) {
            color = ChatColor.valueOf((String)this.plugin.getConfig().getString("Properties.Chat.Colors.Earth"));
        } else if (GeneralMethods.isBender(player.getName(), Element.Fire)) {
            color = ChatColor.valueOf((String)this.plugin.getConfig().getString("Properties.Chat.Colors.Fire"));
        } else if (GeneralMethods.isBender(player.getName(), Element.Chi)) {
            color = ChatColor.valueOf((String)this.plugin.getConfig().getString("Properties.Chat.Colors.Chi"));
        }
        String format = this.plugin.getConfig().getString("Properties.Chat.Format");
        format = format.replace("<message>", "%2$s");
        format = format.replace("<name>", (Object)color + player.getDisplayName() + (Object)ChatColor.RESET);
        event.setFormat(format);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            Player source;
            Player player = (Player)event.getEntity();
            if (GeneralMethods.isBender(player.getName(), Element.Earth) && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Shockwave.fallShockwave(player);
            }
            if (GeneralMethods.isBender(player.getName(), Element.Air) && event.getCause() == EntityDamageEvent.DamageCause.FALL && GeneralMethods.canBendPassive(player.getName(), Element.Air)) {
                new com.projectkorra.projectkorra.util.Flight(player);
                player.setAllowFlight(true);
                AirBurst.fallBurst(player);
                player.setFallDistance(0.0f);
                event.setDamage(0.0);
                event.setCancelled(true);
            }
            if (!event.isCancelled() && GeneralMethods.isBender(player.getName(), Element.Water) && event.getCause() == EntityDamageEvent.DamageCause.FALL && GeneralMethods.canBendPassive(player.getName(), Element.Water) && WaterPassive.applyNoFall(player)) {
                new com.projectkorra.projectkorra.util.Flight(player);
                player.setAllowFlight(true);
                player.setFallDistance(0.0f);
                event.setDamage(0.0);
                event.setCancelled(true);
            }
            if (!event.isCancelled() && GeneralMethods.isBender(player.getName(), Element.Earth) && event.getCause() == EntityDamageEvent.DamageCause.FALL && GeneralMethods.canBendPassive(player.getName(), Element.Earth) && EarthPassive.softenLanding(player)) {
                new com.projectkorra.projectkorra.util.Flight(player);
                player.setAllowFlight(true);
                player.setFallDistance(0.0f);
                event.setDamage(0.0);
                event.setCancelled(true);
            }
            if (!event.isCancelled() && GeneralMethods.isBender(player.getName(), Element.Chi) && event.getCause() == EntityDamageEvent.DamageCause.FALL && GeneralMethods.canBendPassive(player.getName(), Element.Chi)) {
                if (player.isSprinting()) {
                    event.setDamage(0.0);
                    event.setCancelled(true);
                } else {
                    double initdamage = event.getDamage();
                    double newdamage = event.getDamage() * ChiPassive.FallReductionFactor;
                    double finaldamage = initdamage - newdamage;
                    event.setDamage(finaldamage);
                }
            }
            if (!event.isCancelled() && event.getCause() == EntityDamageEvent.DamageCause.FALL && (source = Flight.getLaunchedBy(player)) != null) {
                event.setCancelled(true);
                GeneralMethods.damageEntity(source, (Entity)player, event.getDamage(), null);
            }
            if (GeneralMethods.canBendPassive(player.getName(), Element.Fire) && GeneralMethods.isBender(player.getName(), Element.Fire) && (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)) {
                event.setCancelled(!Extinguish.canBurn(player));
            }
            if (GeneralMethods.isBender(player.getName(), Element.Earth) && event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && TempBlock.isTempBlock(player.getEyeLocation().getBlock())) {
                event.setDamage(0.0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent e) {
        Entity en;
        if (e.isCancelled()) {
            return;
        }
        Entity source = e.getDamager();
        Entity entity = e.getEntity();
        Fireball fireball = Fireball.getFireball(source);
        if (fireball != null) {
            e.setCancelled(true);
            fireball.dealDamage(entity);
            return;
        }
        if (Paralyze.isParalyzed(e.getDamager()) || ChiCombo.isParalyzed(e.getDamager())) {
            e.setCancelled(true);
            return;
        }
        if (entity instanceof Player) {
            Suffocate.remove((Player)entity);
        }
        if ((en = e.getEntity()) instanceof Player && e.getDamager() instanceof Player) {
            Player sourceplayer = (Player)e.getDamager();
            Player targetplayer = (Player)e.getEntity();
            if (GeneralMethods.canBendPassive(sourceplayer.getName(), Element.Chi) && GeneralMethods.isBender(sourceplayer.getName(), Element.Chi) && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getDamage() == 1.0 && ChiMethods.isChiAbility(GeneralMethods.getBoundAbility(sourceplayer))) {
                if (GeneralMethods.isWeapon(sourceplayer.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Chi.CanBendWithWeapons")) {
                    return;
                }
                if (GeneralMethods.getBendingPlayer(sourceplayer.getName()).isElementToggled(Element.Chi)) {
                    if (GeneralMethods.getBoundAbility(sourceplayer) != null && GeneralMethods.getBoundAbility(sourceplayer).equalsIgnoreCase("Paralyze")) {
                        new com.projectkorra.projectkorra.chiblocking.Paralyze(sourceplayer, (Entity)targetplayer);
                    } else if (ChiPassive.willChiBlock(sourceplayer, targetplayer)) {
                        ChiPassive.blockChi(targetplayer);
                    }
                }
            }
            if (GeneralMethods.canBendPassive(sourceplayer.getName(), Element.Chi)) {
                if (GeneralMethods.isWeapon(sourceplayer.getInventory().getItemInMainHand().getType()) && !ProjectKorra.plugin.getConfig().getBoolean("Properties.Chi.CanBendWithWeapons")) {
                    return;
                }
                if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && GeneralMethods.getBendingPlayer(sourceplayer.getName()).isElementToggled(Element.Chi) && GeneralMethods.getBoundAbility(sourceplayer) != null && GeneralMethods.getBoundAbility(sourceplayer).equalsIgnoreCase("Paralyze") && e.getDamage() == 1.0 && sourceplayer.getWorld().equals((Object)targetplayer.getWorld()) && Math.abs(sourceplayer.getLocation().distance(targetplayer.getLocation())) < 3.0) {
                    new com.projectkorra.projectkorra.chiblocking.Paralyze(sourceplayer, (Entity)targetplayer);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        ItemStack[] arritemStack;
        List drops;
        int n;
        int n2;
        int i;
        ArrayList<ItemStack> newdrops;
        if (event.getEntity().getKiller() == null) {
            return;
        }
        if (EarthArmor.instances.containsKey((Object)event.getEntity())) {
            drops = event.getDrops();
            newdrops = new ArrayList<ItemStack>();
            i = 0;
            while (i < drops.size()) {
                if (((ItemStack)drops.get(i)).getType() != Material.LEATHER_BOOTS && ((ItemStack)drops.get(i)).getType() != Material.LEATHER_CHESTPLATE && ((ItemStack)drops.get(i)).getType() != Material.LEATHER_HELMET && ((ItemStack)drops.get(i)).getType() != Material.LEATHER_LEGGINGS && ((ItemStack)drops.get(i)).getType() != Material.AIR) {
                    newdrops.add((ItemStack)drops.get(i));
                }
                ++i;
            }
            if (EarthArmor.instances.get((Object)event.getEntity()).oldarmor != null) {
                arritemStack = EarthArmor.instances.get((Object)event.getEntity()).oldarmor;
                n2 = arritemStack.length;
                n = 0;
                while (n < n2) {
                    ItemStack is = arritemStack[n];
                    if (is.getType() != Material.AIR) {
                        newdrops.add(is);
                    }
                    ++n;
                }
            }
            event.getDrops().clear();
            event.getDrops().addAll(newdrops);
            EarthArmor.removeEffect(event.getEntity());
        }
        if (PlantArmor.instances.containsKey((Object)event.getEntity())) {
            drops = event.getDrops();
            newdrops = new ArrayList();
            i = 0;
            while (i < drops.size()) {
                if (((ItemStack)drops.get(i)).getType() != Material.LEATHER_BOOTS && ((ItemStack)drops.get(i)).getType() != Material.LEATHER_CHESTPLATE && ((ItemStack)drops.get(i)).getType() != Material.LEAVES && ((ItemStack)drops.get(i)).getType() != Material.LEAVES_2 && ((ItemStack)drops.get(i)).getType() != Material.LEATHER_LEGGINGS && ((ItemStack)drops.get(i)).getType() != Material.AIR) {
                    newdrops.add((ItemStack)drops.get(i));
                }
                ++i;
            }
            if (PlantArmor.instances.get((Object)event.getEntity()).oldarmor != null) {
                arritemStack = PlantArmor.instances.get((Object)event.getEntity()).oldarmor;
                n2 = arritemStack.length;
                n = 0;
                while (n < n2) {
                    ItemStack is = arritemStack[n];
                    if (is.getType() != Material.AIR) {
                        newdrops.add(is);
                    }
                    ++n;
                }
            }
            event.getDrops().clear();
            event.getDrops().addAll(newdrops);
            PlantArmor.removeEffect(event.getEntity());
        }
        if (MetalClips.clipped.containsKey((Object)event.getEntity())) {
            drops = event.getDrops();
            newdrops = new ArrayList();
            i = 0;
            while (i < drops.size()) {
                if (((ItemStack)drops.get(i)).getType() != Material.IRON_HELMET && ((ItemStack)drops.get(i)).getType() != Material.IRON_CHESTPLATE && ((ItemStack)drops.get(i)).getType() != Material.IRON_LEGGINGS && ((ItemStack)drops.get(i)).getType() != Material.IRON_BOOTS && ((ItemStack)drops.get(i)).getType() != Material.AIR) {
                    newdrops.add((ItemStack)drops.get(i));
                }
                ++i;
            }
            newdrops.add(MetalClips.getOriginalHelmet((LivingEntity)event.getEntity()));
            newdrops.add(MetalClips.getOriginalChestplate((LivingEntity)event.getEntity()));
            newdrops.add(MetalClips.getOriginalLeggings((LivingEntity)event.getEntity()));
            newdrops.add(MetalClips.getOriginalBoots((LivingEntity)event.getEntity()));
            event.getDrops().clear();
            event.getDrops().addAll(newdrops);
            MetalClips.clipped.remove((Object)event.getEntity());
        }
        if (bendingDeathPlayer.containsKey((Object)event.getEntity())) {
            String message = ConfigManager.deathMsgConfig.get().getString("Properties.Default");
            String ability = bendingDeathPlayer.get((Object)event.getEntity());
            String tempAbility = ChatColor.stripColor((String)ability).replaceAll(" ", "");
            Element element = null;
            boolean isAvatarAbility = false;
            if (GeneralMethods.abilityExists(tempAbility)) {
                element = GeneralMethods.getAbilityElement(tempAbility);
                if (element == null) {
                    isAvatarAbility = true;
                    ability = (Object)GeneralMethods.getAvatarColor() + tempAbility;
                }
            } else if (ChatColor.getByChar((String)ability.substring(1, 2)) != null) {
                element = Element.getFromChatColor(ChatColor.getByChar((String)ability.substring(1, 2)));
            }
            if (HorizontalVelocityTracker.hasBeenDamagedByHorizontalVelocity((Entity)event.getEntity()) && Arrays.asList(HorizontalVelocityTracker.abils).contains(tempAbility)) {
                if (ConfigManager.deathMsgConfig.get().contains("HorizontalVelocity." + tempAbility)) {
                    message = ConfigManager.deathMsgConfig.get().getString("HorizontalVelocity." + tempAbility);
                }
            } else if (element != null) {
                if (ConfigManager.deathMsgConfig.get().contains(String.valueOf(element.toString()) + "." + tempAbility)) {
                    message = ConfigManager.deathMsgConfig.get().getString((Object)((Object)element) + "." + tempAbility);
                } else if (ConfigManager.deathMsgConfig.get().contains("Combo." + tempAbility)) {
                    message = ConfigManager.deathMsgConfig.get().getString("Combo." + tempAbility);
                }
            } else if (isAvatarAbility) {
                if (ConfigManager.deathMsgConfig.get().contains("Avatar." + tempAbility)) {
                    message = ConfigManager.deathMsgConfig.get().getString("Avatar." + tempAbility);
                }
            } else if (ConfigManager.deathMsgConfig.get().contains("Combo." + tempAbility)) {
                message = ConfigManager.deathMsgConfig.get().getString("Combo." + tempAbility);
            }
            message = message.replace("{victim}", event.getEntity().getName()).replace("{attacker}", event.getEntity().getKiller().getName()).replace("{ability}", ability);
            event.setDeathMessage(message);
            bendingDeathPlayer.remove((Object)event.getEntity());
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerInteraction(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            String ability = GeneralMethods.getBoundAbility(player);
            ComboManager.addComboAbility(player, ClickType.RIGHT_CLICK);
            if (ability != null && ability.equalsIgnoreCase("EarthSmash")) {
                new com.projectkorra.projectkorra.earthbending.EarthSmash(player, ClickType.RIGHT_CLICK);
            }
        }
        if (Paralyze.isParalyzed((Entity)player) || ChiCombo.isParalyzed(player) || Bloodbending.isBloodbended((Entity)player) || Suffocate.isBreathbent((Entity)player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        GeneralMethods.createBendingPlayer(e.getPlayer().getUniqueId(), player.getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) {
            return;
        }
        FlightAbility.remove(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        if (Paralyze.isParalyzed((Entity)player)) {
            event.setCancelled(true);
            return;
        }
        if (ChiCombo.isParalyzed(player)) {
            event.setCancelled(true);
            return;
        }
        if (WaterSpout.instances.containsKey((Object)event.getPlayer()) || AirSpout.getPlayers().contains((Object)event.getPlayer()) || SandSpout.getPlayers().contains((Object)event.getPlayer())) {
            Vector vel = new Vector();
            vel.setX(event.getTo().getX() - event.getFrom().getX());
            vel.setY(event.getTo().getY() - event.getFrom().getY());
            vel.setZ(event.getTo().getZ() - event.getFrom().getZ());
            double currspeed = vel.length();
            double maxspeed = 0.15;
            if (currspeed > maxspeed) {
                vel = vel.normalize().multiply(maxspeed);
                event.getPlayer().setVelocity(vel);
            }
        }
        if (Bloodbending.isBloodbended((Entity)player)) {
            Location loc = Bloodbending.getBloodbendingLocation((Entity)player);
            double distance1 = event.getFrom().distance(loc);
            double distance2 = event.getTo().distance(loc);
            if (distance2 > distance1) {
                player.setVelocity(new Vector(0, 0, 0));
            }
        }
        if (FlightAbility.contains(event.getPlayer()) && FlightAbility.isHovering(event.getPlayer())) {
            Location loc = event.getFrom();
            Location toLoc = player.getLocation();
            if (loc.getX() != toLoc.getX() || loc.getY() != toLoc.getY() || loc.getZ() != toLoc.getZ()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerGamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.SPECTATOR) {
            if (!Commands.invincible.contains(player.getName())) {
                Commands.invincible.add(player.getName());
            }
        } else if (event.getNewGameMode() != GameMode.SPECTATOR && Commands.invincible.contains(player.getName())) {
            Commands.invincible.remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer != null) {
            if (GeneralMethods.toggedOut.contains(player.getUniqueId()) && bPlayer.isToggled()) {
                GeneralMethods.toggedOut.remove(player.getUniqueId());
            }
            if (!bPlayer.isToggled()) {
                GeneralMethods.toggedOut.add(player.getUniqueId());
            }
        }
        if (Commands.invincible.contains(event.getPlayer().getName())) {
            Commands.invincible.remove(event.getPlayer().getName());
        }
        Preset.unloadPreset(player);
        if (EarthArmor.instances.containsKey((Object)event.getPlayer())) {
            EarthArmor.removeEffect(event.getPlayer());
            event.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        }
        if (PlantArmor.instances.containsKey((Object)event.getPlayer())) {
            PlantArmor.removeEffect(event.getPlayer());
        }
        for (Player p : MetalClips.instances.keySet()) {
            if (MetalClips.instances.get((Object)p).getTarget() == null) continue;
            MetalClips.instances.get((Object)p).remove();
        }
        MultiAbilityManager.remove(player);
        FlightAbility.remove(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) {
            return;
        }
        if (player.isSneaking()) {
            ComboManager.addComboAbility(player, ClickType.SHIFT_UP);
        } else {
            ComboManager.addComboAbility(player, ClickType.SHIFT_DOWN);
        }
        if (!(!Suffocate.isBreathbent((Entity)player) || GeneralMethods.getBoundAbility(player).equalsIgnoreCase("AirSwipe") && GeneralMethods.getBoundAbility(player).equalsIgnoreCase("FireBlast") && GeneralMethods.getBoundAbility(player).equalsIgnoreCase("EarthBlast") && GeneralMethods.getBoundAbility(player).equalsIgnoreCase("WaterManipulation"))) {
            event.setCancelled(true);
        }
        if (Paralyze.isParalyzed((Entity)player) || ChiCombo.isParalyzed(player) || Bloodbending.isBloodbended((Entity)player)) {
            event.setCancelled(true);
            return;
        }
        if (!player.isSneaking()) {
            BlockSource.update(player, ClickType.SHIFT_DOWN);
        }
        if (!player.isSneaking() && WaterArms.hasPlayer(player)) {
            WaterArms.displayBoundMsg(player);
            return;
        }
        AirScooter.check(player);
        String abil = GeneralMethods.getBoundAbility(player);
        if (abil == null) {
            return;
        }
        if (ChiMethods.isChiBlocked(player.getName())) {
            event.setCancelled(true);
            return;
        }
        if (!player.isSneaking() && GeneralMethods.canBend(player.getName(), abil)) {
            if (GeneralMethods.isDisabledStockAbility(abil)) {
                return;
            }
            if (AirMethods.isAirAbility(abil) && GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Air)) {
                if (GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Air.CanBendWithWeapons")) {
                    return;
                }
                if (abil.equalsIgnoreCase("Tornado")) {
                    new com.projectkorra.projectkorra.airbending.Tornado(player);
                }
                if (abil.equalsIgnoreCase("AirBlast")) {
                    AirBlast.setOrigin(player);
                }
                if (abil.equalsIgnoreCase("AirBurst")) {
                    new com.projectkorra.projectkorra.airbending.AirBurst(player);
                }
                if (abil.equalsIgnoreCase("AirSuction")) {
                    AirSuction.setOrigin(player);
                }
                if (abil.equalsIgnoreCase("AirSwipe")) {
                    AirSwipe.charge(player);
                }
                if (abil.equalsIgnoreCase("AirShield")) {
                    new com.projectkorra.projectkorra.airbending.AirShield(player);
                }
                if (abil.equalsIgnoreCase("Suffocate")) {
                    new com.projectkorra.projectkorra.airbending.Suffocate(player);
                }
                if (abil.equalsIgnoreCase("Flight")) {
                    if (player.isSneaking() || !AirMethods.canAirFlight(player)) {
                        return;
                    }
                    new com.projectkorra.projectkorra.airbending.FlightAbility(player);
                }
            }
            if (WaterMethods.isWaterAbility(abil) && GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Water)) {
                if (GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Water.CanBendWithWeapons")) {
                    return;
                }
                if (abil.equalsIgnoreCase("Bloodbending")) {
                    new com.projectkorra.projectkorra.waterbending.Bloodbending(player);
                }
                if (abil.equalsIgnoreCase("IceBlast")) {
                    new com.projectkorra.projectkorra.waterbending.IceBlast(player);
                }
                if (abil.equalsIgnoreCase("IceSpike")) {
                    new com.projectkorra.projectkorra.waterbending.IceSpike2(player);
                }
                if (abil.equalsIgnoreCase("OctopusForm")) {
                    OctopusForm.form(player);
                }
                if (abil.equalsIgnoreCase("PhaseChange")) {
                    new com.projectkorra.projectkorra.waterbending.Melt(player);
                }
                if (abil.equalsIgnoreCase("WaterManipulation")) {
                    new com.projectkorra.projectkorra.waterbending.WaterManipulation(player);
                }
                if (abil.equalsIgnoreCase("Surge")) {
                    WaterWall.form(player);
                }
                if (abil.equalsIgnoreCase("Torrent")) {
                    Torrent.create(player);
                }
                if (abil.equalsIgnoreCase("WaterArms")) {
                    new com.projectkorra.projectkorra.waterbending.WaterArms(player);
                }
            }
            if (EarthMethods.isEarthAbility(abil) && GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Earth)) {
                if (GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Earth.CanBendWithWeapons")) {
                    return;
                }
                if (abil.equalsIgnoreCase("EarthBlast")) {
                    new com.projectkorra.projectkorra.earthbending.EarthBlast(player);
                }
                if (abil.equalsIgnoreCase("RaiseEarth")) {
                    new com.projectkorra.projectkorra.earthbending.EarthWall(player);
                }
                if (abil.equalsIgnoreCase("Collapse")) {
                    new com.projectkorra.projectkorra.earthbending.Collapse(player);
                }
                if (abil.equalsIgnoreCase("Shockwave")) {
                    new com.projectkorra.projectkorra.earthbending.Shockwave(player);
                }
                if (abil.equalsIgnoreCase("EarthGrab")) {
                    EarthGrab.EarthGrabSelf(player);
                }
                if (abil.equalsIgnoreCase("EarthTunnel")) {
                    new com.projectkorra.projectkorra.earthbending.EarthTunnel(player);
                }
                if (abil.equalsIgnoreCase("Tremorsense")) {
                    GeneralMethods.getBendingPlayer(player.getName()).toggleTremorSense();
                }
                if (abil.equalsIgnoreCase("Extraction")) {
                    new com.projectkorra.projectkorra.earthbending.Extraction(player);
                }
                if (abil.equalsIgnoreCase("MetalClips")) {
                    if (MetalClips.instances.containsKey((Object)player)) {
                        if (MetalClips.instances.get((Object)player).getTarget() == null) {
                            MetalClips.instances.get((Object)player).magnet();
                        } else {
                            MetalClips.instances.get((Object)player).control();
                        }
                    } else {
                        new com.projectkorra.projectkorra.earthbending.MetalClips(player, 1);
                    }
                }
                if (abil.equalsIgnoreCase("LavaFlow")) {
                    new com.projectkorra.projectkorra.earthbending.LavaFlow(player, LavaFlow.AbilityType.SHIFT);
                }
                if (abil.equalsIgnoreCase("EarthSmash")) {
                    new com.projectkorra.projectkorra.earthbending.EarthSmash(player, ClickType.SHIFT_DOWN);
                }
            }
            if (FireMethods.isFireAbility(abil) && GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Fire)) {
                if (GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Fire.CanBendWithWeapons")) {
                    return;
                }
                if (abil.equalsIgnoreCase("Blaze")) {
                    new com.projectkorra.projectkorra.firebending.RingOfFire(player);
                }
                if (abil.equalsIgnoreCase("FireBlast")) {
                    new com.projectkorra.projectkorra.firebending.Fireball(player);
                }
                if (abil.equalsIgnoreCase("HeatControl")) {
                    new com.projectkorra.projectkorra.firebending.HeatControl(player);
                }
                if (abil.equalsIgnoreCase("FireBurst")) {
                    new com.projectkorra.projectkorra.firebending.FireBurst(player);
                }
                if (abil.equalsIgnoreCase("FireShield")) {
                    FireShield.shield(player);
                }
                if (abil.equalsIgnoreCase("Lightning")) {
                    new com.projectkorra.projectkorra.firebending.Lightning(player);
                }
                if (abil.equalsIgnoreCase("Combustion")) {
                    new com.projectkorra.projectkorra.firebending.Combustion(player);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerSwing(PlayerAnimationEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        BendingPlayer bp = GeneralMethods.getBendingPlayer(player.getName());
        if (bp.isOnCooldown("RightClickFix")) {
            return;
        }
        ComboManager.addComboAbility(player, ClickType.LEFT_CLICK);
        if (!(!Suffocate.isBreathbent((Entity)player) || GeneralMethods.getBoundAbility(player) == null && GeneralMethods.getBoundAbility(player).equalsIgnoreCase("AirSwipe") && GeneralMethods.getBoundAbility(player).equalsIgnoreCase("FireBlast") && GeneralMethods.getBoundAbility(player).equalsIgnoreCase("EarthBlast") && GeneralMethods.getBoundAbility(player).equalsIgnoreCase("WaterManipulation"))) {
            event.setCancelled(true);
        }
        if (Bloodbending.isBloodbended((Entity)player) || Paralyze.isParalyzed((Entity)player) || ChiCombo.isParalyzed(player)) {
            event.setCancelled(true);
            return;
        }
        if (ChiMethods.isChiBlocked(player.getName())) {
            event.setCancelled(true);
            return;
        }
        if (GeneralMethods.isInteractable(player.getTargetBlock((Set<Material>)null, 5))) {
            event.setCancelled(true);
            return;
        }
        BlockSource.update(player, ClickType.LEFT_CLICK);
        AirScooter.check(player);
        String abil = GeneralMethods.getBoundAbility(player);
        if (abil == null && !MultiAbilityManager.hasMultiAbilityBound(player)) {
            return;
        }
        if (GeneralMethods.canBend(player.getName(), abil)) {
            if (GeneralMethods.isDisabledStockAbility(abil)) {
                return;
            }
            if (AirMethods.isAirAbility(abil) && GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Air)) {
                if (GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Air.CanBendWithWeapons")) {
                    return;
                }
                if (abil.equalsIgnoreCase("AirBlast")) {
                    new com.projectkorra.projectkorra.airbending.AirBlast(player);
                }
                if (abil.equalsIgnoreCase("AirSuction")) {
                    new com.projectkorra.projectkorra.airbending.AirSuction(player);
                }
                if (abil.equalsIgnoreCase("AirBurst")) {
                    AirBurst.coneBurst(player);
                }
                if (abil.equalsIgnoreCase("AirScooter")) {
                    new com.projectkorra.projectkorra.airbending.AirScooter(player);
                }
                if (abil.equalsIgnoreCase("AirSpout")) {
                    new com.projectkorra.projectkorra.airbending.AirSpout(player);
                }
                if (abil.equalsIgnoreCase("AirSwipe")) {
                    new com.projectkorra.projectkorra.airbending.AirSwipe(player);
                }
                if (abil.equalsIgnoreCase("Flight")) {
                    if (!ProjectKorra.plugin.getConfig().getBoolean("Abilities.Air.Flight.HoverEnabled") || !AirMethods.canAirFlight(player)) {
                        return;
                    }
                    if (FlightAbility.contains(event.getPlayer())) {
                        if (FlightAbility.isHovering(event.getPlayer())) {
                            FlightAbility.setHovering(event.getPlayer(), false);
                        } else {
                            FlightAbility.setHovering(event.getPlayer(), true);
                        }
                    }
                }
            }
            if (WaterMethods.isWaterAbility(abil) && GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Water)) {
                if (GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Water.CanBendWithWeapons")) {
                    return;
                }
                if (abil.equalsIgnoreCase("Bloodbending")) {
                    Bloodbending.launch(player);
                }
                if (abil.equalsIgnoreCase("IceBlast")) {
                    IceBlast.activate(player);
                }
                if (abil.equalsIgnoreCase("IceSpike")) {
                    IceSpike2.activate(player);
                }
                if (abil.equalsIgnoreCase("OctopusForm")) {
                    new com.projectkorra.projectkorra.waterbending.OctopusForm(player);
                }
                if (abil.equalsIgnoreCase("PhaseChange")) {
                    new com.projectkorra.projectkorra.waterbending.FreezeMelt(player);
                }
                if (abil.equalsIgnoreCase("PlantArmor")) {
                    new com.projectkorra.projectkorra.waterbending.PlantArmor(player);
                }
                if (abil.equalsIgnoreCase("WaterSpout")) {
                    new com.projectkorra.projectkorra.waterbending.WaterSpout(player);
                }
                if (abil.equalsIgnoreCase("WaterManipulation")) {
                    WaterManipulation.moveWater(player);
                }
                if (abil.equalsIgnoreCase("Surge")) {
                    new com.projectkorra.projectkorra.waterbending.WaterWall(player);
                }
                if (abil.equalsIgnoreCase("Torrent")) {
                    new com.projectkorra.projectkorra.waterbending.Torrent(player);
                }
            }
            if (EarthMethods.isEarthAbility(abil) && GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Earth)) {
                if (GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Earth.CanBendWithWeapons")) {
                    return;
                }
                if (abil.equalsIgnoreCase("Catapult")) {
                    new com.projectkorra.projectkorra.earthbending.Catapult(player);
                }
                if (abil.equalsIgnoreCase("EarthBlast")) {
                    EarthBlast.throwEarth(player);
                }
                if (abil.equalsIgnoreCase("RaiseEarth")) {
                    new com.projectkorra.projectkorra.earthbending.EarthColumn(player);
                }
                if (abil.equalsIgnoreCase("Collapse")) {
                    new com.projectkorra.projectkorra.earthbending.CompactColumn(player);
                }
                if (abil.equalsIgnoreCase("Shockwave")) {
                    Shockwave.coneShockwave(player);
                }
                if (abil.equalsIgnoreCase("EarthArmor")) {
                    new com.projectkorra.projectkorra.earthbending.EarthArmor(player);
                }
                if (abil.equalsIgnoreCase("EarthGrab")) {
                    new com.projectkorra.projectkorra.earthbending.EarthGrab(player);
                }
                if (abil.equalsIgnoreCase("Tremorsense")) {
                    new com.projectkorra.projectkorra.earthbending.Tremorsense(player);
                }
                if (abil.equalsIgnoreCase("MetalClips")) {
                    if (!MetalClips.instances.containsKey((Object)player)) {
                        new com.projectkorra.projectkorra.earthbending.MetalClips(player, 0);
                    } else if (MetalClips.instances.containsKey((Object)player)) {
                        if (MetalClips.instances.get((Object)player).metalclips < (player.hasPermission("bending.ability.MetalClips.4clips") ? 4 : 3)) {
                            MetalClips.instances.get((Object)player).shootMetal();
                        } else if (MetalClips.isControllingEntity(player)) {
                            MetalClips.instances.get((Object)player).launch();
                        }
                    }
                }
                if (abil.equalsIgnoreCase("LavaSurge") && LavaSurge.instances.containsKey((Object)player)) {
                    LavaSurge.instances.get((Object)player).launch();
                }
                if (abil.equalsIgnoreCase("LavaFlow")) {
                    new com.projectkorra.projectkorra.earthbending.LavaFlow(player, LavaFlow.AbilityType.CLICK);
                }
                if (abil.equalsIgnoreCase("EarthSmash")) {
                    new com.projectkorra.projectkorra.earthbending.EarthSmash(player, ClickType.LEFT_CLICK);
                }
                if (abil.equalsIgnoreCase("SandSpout")) {
                    new com.projectkorra.projectkorra.earthbending.SandSpout(player);
                }
            }
            if (FireMethods.isFireAbility(abil) && GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Fire)) {
                if (GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Fire.CanBendWithWeapons")) {
                    return;
                }
                if (abil.equalsIgnoreCase("Blaze")) {
                    new com.projectkorra.projectkorra.firebending.ArcOfFire(player);
                }
                if (abil.equalsIgnoreCase("FireBlast")) {
                    new com.projectkorra.projectkorra.firebending.FireBlast(player);
                }
                if (abil.equalsIgnoreCase("FireJet")) {
                    new com.projectkorra.projectkorra.firebending.FireJet(player);
                }
                if (abil.equalsIgnoreCase("HeatControl")) {
                    new com.projectkorra.projectkorra.firebending.Extinguish(player);
                }
                if (abil.equalsIgnoreCase("Illumination")) {
                    new com.projectkorra.projectkorra.firebending.Illumination(player);
                }
                if (abil.equalsIgnoreCase("FireBurst")) {
                    FireBurst.coneBurst(player);
                }
                if (abil.equalsIgnoreCase("FireShield")) {
                    new com.projectkorra.projectkorra.firebending.FireShield(player);
                }
                if (abil.equalsIgnoreCase("WallOfFire")) {
                    new com.projectkorra.projectkorra.firebending.WallOfFire(player);
                }
                if (abil.equalsIgnoreCase("Combustion")) {
                    Combustion.explode(player);
                }
            }
            if (ChiMethods.isChiAbility(abil) && GeneralMethods.getBendingPlayer(player.getName()).isElementToggled(Element.Chi)) {
                if (GeneralMethods.isWeapon(player.getInventory().getItemInMainHand().getType()) && !this.plugin.getConfig().getBoolean("Properties.Chi.CanBendWithWeapons")) {
                    return;
                }
                if (abil.equalsIgnoreCase("HighJump")) {
                    new com.projectkorra.projectkorra.chiblocking.HighJump(player);
                }
                if (abil.equalsIgnoreCase("RapidPunch")) {
                    new com.projectkorra.projectkorra.chiblocking.RapidPunch(player);
                }
                if (abil.equalsIgnoreCase("Smokescreen")) {
                    new com.projectkorra.projectkorra.chiblocking.Smokescreen(player);
                }
                if (abil.equalsIgnoreCase("WarriorStance")) {
                    new com.projectkorra.projectkorra.chiblocking.WarriorStance(player);
                }
                if (abil.equalsIgnoreCase("AcrobatStance")) {
                    new com.projectkorra.projectkorra.chiblocking.AcrobatStance(player);
                }
                if (abil.equalsIgnoreCase("QuickStrike")) {
                    new com.projectkorra.projectkorra.chiblocking.QuickStrike(player);
                }
                if (abil.equalsIgnoreCase("SwiftKick")) {
                    new com.projectkorra.projectkorra.chiblocking.SwiftKick(player);
                }
            }
            if (abil.equalsIgnoreCase("AvatarState")) {
                new com.projectkorra.projectkorra.ability.AvatarState(player);
            }
        }
        if (MultiAbilityManager.hasMultiAbilityBound(player) && (abil = MultiAbilityManager.getBoundMultiAbility(player)).equalsIgnoreCase("WaterArms")) {
            new com.projectkorra.projectkorra.waterbending.WaterArms(player);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player p = event.getPlayer();
        if (Tornado.getPlayers().contains((Object)p) || Bloodbending.isBloodbended((Entity)p) || Suffocate.isBreathbent((Entity)p) || FireJet.getPlayers().contains((Object)p) || AvatarState.getPlayers().contains((Object)p)) {
            event.setCancelled(p.getGameMode() != GameMode.CREATIVE);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Integer id = event.getEntity().getEntityId();
        if (Smokescreen.snowballs.contains(id)) {
            Location loc = event.getEntity().getLocation();
            Smokescreen.playEffect(loc);
            for (Entity en : GeneralMethods.getEntitiesAroundPoint(loc, Smokescreen.radius)) {
                Smokescreen.applyBlindness(en);
            }
            Smokescreen.snowballs.remove(id);
        }
    }

}

