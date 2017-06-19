package com.projectkorra.projectkorra;

import com.google.common.reflect.ClassPath;
import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.massivecraft.factions.engine.EngineMain;
import com.massivecraft.massivecore.ps.PS;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.PlayerCache.TownBlockStatus;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.war.flagwar.TownyWar;
import com.palmergames.bukkit.towny.war.flagwar.TownyWarConfig;
import com.projectkorra.projectkorra.ability.AbilityModule;
import com.projectkorra.projectkorra.ability.AbilityModuleManager;
import com.projectkorra.projectkorra.ability.StockAbility;
import com.projectkorra.projectkorra.ability.api.Ability;
import com.projectkorra.projectkorra.ability.combo.ComboAbilityModule;
import com.projectkorra.projectkorra.ability.combo.ComboManager;
import com.projectkorra.projectkorra.ability.combo.ComboModuleManager;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityManager;
import com.projectkorra.projectkorra.ability.multiability.MultiAbilityModuleManager;
import com.projectkorra.projectkorra.airbending.AirBlast;
import com.projectkorra.projectkorra.airbending.AirCombo;
import com.projectkorra.projectkorra.airbending.AirMethods;
import com.projectkorra.projectkorra.airbending.AirShield;
import com.projectkorra.projectkorra.airbending.AirSpout;
import com.projectkorra.projectkorra.airbending.AirSuction;
import com.projectkorra.projectkorra.airbending.AirSwipe;
import com.projectkorra.projectkorra.chiblocking.ChiMethods;
import com.projectkorra.projectkorra.chiblocking.Paralyze;
import com.projectkorra.projectkorra.command.Commands;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.earthbending.EarthBlast;
import com.projectkorra.projectkorra.earthbending.EarthMethods;
import com.projectkorra.projectkorra.earthbending.EarthPassive;
import com.projectkorra.projectkorra.earthbending.MetalClips;
import com.projectkorra.projectkorra.event.BendingReloadEvent;
import com.projectkorra.projectkorra.event.PlayerBendingDeathEvent;
import com.projectkorra.projectkorra.firebending.Combustion;
import com.projectkorra.projectkorra.firebending.FireBlast;
import com.projectkorra.projectkorra.firebending.FireCombo;
import com.projectkorra.projectkorra.firebending.FireMethods;
import com.projectkorra.projectkorra.firebending.FireShield;
import com.projectkorra.projectkorra.scarecrow.ScareMethods;
import com.projectkorra.projectkorra.snowman.SnowMethods;
import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.sunshine.SunshineMethods;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;
import com.projectkorra.projectkorra.waterbending.Bloodbending;
import com.projectkorra.projectkorra.waterbending.FreezeMelt;
import com.projectkorra.projectkorra.waterbending.WaterCombo;
import com.projectkorra.projectkorra.waterbending.WaterManipulation;
import com.projectkorra.projectkorra.waterbending.WaterMethods;
import com.projectkorra.projectkorra.waterbending.WaterSpout;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import main.RollbackAPI;
import main.dTools;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.sacredlabyrinth.Phaed.PreciousStones.PreciousStones;
import net.sacredlabyrinth.Phaed.PreciousStones.field.FieldFlag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GeneralMethods
{
  public static List<Ability> invincible = new ArrayList();
  static ProjectKorra plugin;
  private static FileConfiguration config = ProjectKorra.plugin.getConfig();
  public static Random rand = new Random();
  public static double CACHE_TIME = config.getDouble("Properties.RegionProtection.CacheBlockTime");
  public static ConcurrentHashMap<String, Long> cooldowns = new ConcurrentHashMap();
  public static ConcurrentHashMap<String, ConcurrentHashMap<Block, BlockCacheElement>> blockProtectionCache = new ConcurrentHashMap();
  public static Integer[] nonOpaque = { Integer.valueOf(0), Integer.valueOf(6), Integer.valueOf(8), Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(27), Integer.valueOf(28), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(50), Integer.valueOf(51), Integer.valueOf(55), Integer.valueOf(59), Integer.valueOf(66), Integer.valueOf(68), Integer.valueOf(69), Integer.valueOf(70), Integer.valueOf(72), Integer.valueOf(75), Integer.valueOf(76), Integer.valueOf(77), Integer.valueOf(78), Integer.valueOf(83), Integer.valueOf(90), Integer.valueOf(93), Integer.valueOf(94), Integer.valueOf(104), Integer.valueOf(105), Integer.valueOf(106), Integer.valueOf(111), Integer.valueOf(115), Integer.valueOf(119), Integer.valueOf(127), Integer.valueOf(131), Integer.valueOf(132), Integer.valueOf(175) };
  public static Material[] interactable = { Material.ACACIA_DOOR, Material.ACACIA_FENCE_GATE, Material.ANVIL, Material.ARMOR_STAND, Material.BEACON, Material.BED, Material.BED_BLOCK, Material.BIRCH_DOOR, Material.BIRCH_FENCE_GATE, Material.BOAT, Material.BREWING_STAND, Material.BURNING_FURNACE, Material.CAKE_BLOCK, Material.CHEST, Material.COMMAND, Material.DARK_OAK_DOOR, Material.DARK_OAK_FENCE_GATE, Material.DISPENSER, Material.DRAGON_EGG, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.ENDER_CHEST, Material.ENDER_PORTAL_FRAME, Material.FENCE_GATE, Material.FURNACE, Material.HOPPER, Material.HOPPER_MINECART, Material.COMMAND_MINECART, Material.ITEM_FRAME, Material.JUKEBOX, Material.JUNGLE_DOOR, Material.JUNGLE_FENCE_GATE, Material.LEVER, Material.MINECART, Material.NOTE_BLOCK, Material.PAINTING, Material.SPRUCE_DOOR, Material.SPRUCE_FENCE_GATE, Material.STONE_BUTTON, Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.WOOD_BUTTON, Material.WOOD_DOOR, Material.WORKBENCH };
  public static List<UUID> toggedOut = new ArrayList();
  
  public GeneralMethods(ProjectKorra plugin)
  {
    this.plugin = plugin;
    new AirMethods(plugin);
    new ChiMethods(plugin);
    new EarthMethods(plugin);
    new FireMethods(plugin);
    new WaterMethods(plugin);
  }
  
  public static boolean abilityExists(String string)
  {
    for (String st : AbilityModuleManager.abilities) {
      if (string.equalsIgnoreCase(st)) {
        return true;
      }
    }
    return false;
  }
  
  public static void bindAbility(Player player, String ability)
  {
    int slot = player.getInventory().getHeldItemSlot() + 1;
    bindAbility(player, ability, slot);
  }
  
  public static void bindAbility(Player player, String ability, int slot)
  {
    if (MultiAbilityManager.playerAbilities.containsKey(player))
    {
      player.sendMessage(ChatColor.RED + "You can't edit your binds right now!");
      return;
    }
    BendingPlayer bPlayer = getBendingPlayer(player.getName());
    bPlayer.getAbilities().put(Integer.valueOf(slot), ability);
    if (AirMethods.isAirAbility(ability)) {
      player.sendMessage(AirMethods.getAirColor() + "Succesfully bound " + ability + " to slot " + slot);
    } else if (WaterMethods.isWaterAbility(ability)) {
      player.sendMessage(WaterMethods.getWaterColor() + "Succesfully bound " + ability + " to slot " + slot);
    } else if (EarthMethods.isEarthAbility(ability)) {
      player.sendMessage(EarthMethods.getEarthColor() + "Succesfully bound " + ability + " to slot " + slot);
    } else if (FireMethods.isFireAbility(ability)) {
      player.sendMessage(FireMethods.getFireColor() + "Succesfully bound " + ability + " to slot " + slot);
    } else if (ChiMethods.isChiAbility(ability)) {
      player.sendMessage(ChiMethods.getChiColor() + "Succesfully bound " + ability + " to slot " + slot);
    } else if (SnowMethods.isSnowAbility(ability)) {
      player.sendMessage(SnowMethods.getSnowColor() + "Succesfully bound " + ability + " to slot " + slot);
    } else if (ScareMethods.isScareAbility(ability)) {
      player.sendMessage(ScareMethods.getScareColor() + "Succesfully bound " + ability + " to slot " + slot);
    } else if (SunshineMethods.isSunshineAbility(ability)) {
        player.sendMessage(SunshineMethods.getSunshineColor() + "Succesfully bound " + ability + " to slot " + slot);
    } else {
      player.sendMessage(getAvatarColor() + "Successfully bound " + ability + " to slot " + slot);
    }
    saveAbility(bPlayer, slot, ability);
  }
  
  public static boolean blockAbilities(Player player, List<String> abilitiesToBlock, Location loc, double radius)
  {
    boolean hasBlocked = false;
    for (String ability : abilitiesToBlock) {
      if (ability.equalsIgnoreCase("FireBlast")) {
        hasBlocked = (FireBlast.annihilateBlasts(loc, radius, player)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("EarthBlast")) {
        hasBlocked = (EarthBlast.annihilateBlasts(loc, radius, player)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("WaterManipulation")) {
        hasBlocked = (WaterManipulation.annihilateBlasts(loc, radius, player)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("AirSwipe")) {
        hasBlocked = (AirSwipe.removeSwipesAroundPoint(loc, radius)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("AirBlast")) {
        hasBlocked = (AirBlast.removeAirBlastsAroundPoint(loc, radius)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("AirSuction")) {
        hasBlocked = (AirSuction.removeAirSuctionsAroundPoint(loc, radius)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("Combustion")) {
        hasBlocked = (Combustion.removeAroundPoint(loc, radius)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("FireShield")) {
        hasBlocked = (FireShield.isWithinShield(loc)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("AirShield")) {
        hasBlocked = (AirShield.isWithinShield(loc)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("WaterSpout")) {
        hasBlocked = (WaterSpout.removeSpouts(loc, radius, player)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("AirSpout")) {
        hasBlocked = (AirSpout.removeSpouts(loc, radius, player)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("Twister")) {
        hasBlocked = (AirCombo.removeAroundPoint(player, "Twister", loc, radius)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("AirStream")) {
        hasBlocked = (AirCombo.removeAroundPoint(player, "AirStream", loc, radius)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("AirSweep")) {
        hasBlocked = (AirCombo.removeAroundPoint(player, "AirSweep", loc, radius)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("FireKick")) {
        hasBlocked = (FireCombo.removeAroundPoint(player, "FireKick", loc, radius)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("FireSpin")) {
        hasBlocked = (FireCombo.removeAroundPoint(player, "FireSpin", loc, radius)) || (hasBlocked);
      } else if (ability.equalsIgnoreCase("FireWheel")) {
        hasBlocked = (FireCombo.removeAroundPoint(player, "FireWheel", loc, radius)) || (hasBlocked);
      }
    }
    return hasBlocked;
  }
  
  public static void breakBlock(Block block)
  {
    block.breakNaturally(new ItemStack(Material.AIR));
  }
  
  public static boolean canBend(String player, String ability)
  {
    BendingPlayer bPlayer = getBendingPlayer(player);
    Player p = Bukkit.getPlayer(player);
    if (bPlayer == null) {
      return false;
    }
    if (p == null) {
      return false;
    }
    if ((plugin.getConfig().getStringList("Properties.DisabledWorlds") != null) && (p.getWorld() != null) && (plugin.getConfig().getStringList("Properties.DisabledWorlds").contains(p.getWorld().getName()))) {
      return false;
    }
    if (Commands.isToggledForAll) {
      return false;
    }
    if (!bPlayer.isToggled()) {
      return false;
    }
    if (p.getGameMode() == GameMode.SPECTATOR) {
      return false;
    }
    if (cooldowns.containsKey(p.getName()))
    {
      if (((Long)cooldowns.get(p.getName())).longValue() + ProjectKorra.plugin.getConfig().getLong("Properties.GlobalCooldown") >= System.currentTimeMillis()) {
        return false;
      }
      cooldowns.remove(p.getName());
    }
    if (bPlayer.isChiBlocked()) {
      return false;
    }
    if (!p.hasPermission("bending.ability." + ability)) {
      return false;
    }
    if (!canBind(player, ability)) {
      return false;
    }
    if (!bPlayer.isElementToggled(getAbilityElement(ability))) {
      return false;
    }
    if (isRegionProtectedFromBuild(p, ability, p.getLocation())) {
      return false;
    }
    if ((Paralyze.isParalyzed(p)) || (Bloodbending.isBloodbended(p))) {
      return false;
    }
    if (MetalClips.isControlled(p)) {
      return false;
    }
    if ((BendingManager.events.get(p.getWorld()) != null) && (((String)BendingManager.events.get(p.getWorld())).equalsIgnoreCase("SolarEclipse")) && (FireMethods.isFireAbility(ability))) {
      return false;
    }
    if ((BendingManager.events.get(p.getWorld()) != null) && (((String)BendingManager.events.get(p.getWorld())).equalsIgnoreCase("LunarEclipse")) && (WaterMethods.isWaterAbility(ability))) {
      return false;
    }
    return true;
  }
  
  public static boolean canBendPassive(String player, Element element)
  {
    BendingPlayer bPlayer = getBendingPlayer(player);
    Player p = Bukkit.getPlayer(player);
    
    if (bPlayer == null) {
      return false;
    }
    
    if (!p.hasPermission("bending." + element.toString().toLowerCase() + ".passive")) {
      return false;
    }
    if (!bPlayer.isToggled()) {
      return false;
    }
    if (!bPlayer.hasElement(element)) {
      return false;
    }
    if (!bPlayer.isElementToggled(element)) {
      return false;
    }
    
/*    if (isRegionProtectedFromBuild(p, null, p.getLocation())) {
      return false;
    }
    */
    if (bPlayer.isChiBlocked()) {
      return false;
    }
    dTools dtools = (dTools)Bukkit.getServer().getPluginManager().getPlugin("dTools");
    if ((dtools != null) && 
      (RollbackAPI.isBendingRestrictedRegion(p, p.getLocation()))) {
      return false;
    }
    return true;
  }
  
  public static boolean canBind(String player, String ability)
  {
    Player p = Bukkit.getPlayer(player);
    if (p == null) {
      return false;
    }
    if (!p.hasPermission("bending.ability." + ability)) {
      return false;
    }
    if ((AirMethods.isAirAbility(ability)) && (!isBender(player, Element.Air))) {
      return false;
    }
    if ((WaterMethods.isWaterAbility(ability)) && (!isBender(player, Element.Water))) {
      return false;
    }
    if ((EarthMethods.isEarthAbility(ability)) && (!isBender(player, Element.Earth))) {
      return false;
    }
    if ((FireMethods.isFireAbility(ability)) && (!isBender(player, Element.Fire))) {
      return false;
    }
    if ((ChiMethods.isChiAbility(ability)) && (!isBender(player, Element.Chi))) {
      return false;
    }
    if ((SnowMethods.isSnowAbility(ability)) && (!isBender(player, Element.Snowman))) {
      return false;
    }
    if ((ScareMethods.isScareAbility(ability)) && (!isBender(player, Element.Scarecrow))) {
      return false;
    }
    if ((SunshineMethods.isSunAbility(ability)) && (!isBender(player, Element.Sunshine))) {
        return false;
      }
    if ((!EarthMethods.canLavabend(p)) && (EarthMethods.isLavabendingAbility(ability))) {
      return false;
    }
    if ((!EarthMethods.canMetalbend(p)) && (EarthMethods.isMetalbendingAbility(ability))) {
      return false;
    }
    if ((!EarthMethods.canSandbend(p)) && (EarthMethods.isSandbendingAbility(ability))) {
      return false;
    }
    if ((!AirMethods.canAirFlight(p)) && (AirMethods.isFlightAbility(ability))) {
      return false;
    }
    if ((!AirMethods.canUseSpiritualProjection(p)) && (AirMethods.isSpiritualProjectionAbility(ability))) {
      return false;
    }
    if ((!FireMethods.canCombustionbend(p)) && (FireMethods.isCombustionbendingAbility(ability))) {
      return false;
    }
    if ((!FireMethods.canLightningbend(p)) && (FireMethods.isLightningbendingAbility(ability))) {
      return false;
    }
    if ((!WaterMethods.canBloodbend(p)) && (WaterMethods.isBloodbendingAbility(ability))) {
      return false;
    }
    if ((!WaterMethods.canIcebend(p)) && (WaterMethods.isIcebendingAbility(ability))) {
      return false;
    }
    if ((!WaterMethods.canWaterHeal(p)) && (WaterMethods.isHealingAbility(ability))) {
      return false;
    }
    if ((!WaterMethods.canPlantbend(p)) && (WaterMethods.isPlantbendingAbility(ability))) {
      return false;
    }
    return true;
  }
  
  public static boolean canView(Player player, String ability)
  {
    return player.hasPermission("bending.ability." + ability);
  }
  
  public static boolean comboExists(String string)
  {
    for (String s : ComboManager.descriptions.keySet()) {
      if (s.equalsIgnoreCase(string)) {
        return true;
      }
    }
    return false;
  }
  
  public static void createBendingPlayer(UUID uuid, final String player)
  {
    new BukkitRunnable()
    {
      public void run()
      {
        GeneralMethods.createBendingPlayerAsynchronously(uuid, player);
      }
    }.runTaskAsynchronously(ProjectKorra.plugin);
  }
  
  private static void createBendingPlayerAsynchronously(UUID uuid, final String player)
  {
    ResultSet rs2 = DBConnection.sql.readQuery("SELECT * FROM pk_players WHERE uuid = '" + uuid.toString() + "'");
    try
    {
      if (!rs2.next())
      {
        new BendingPlayer(uuid, player, new ArrayList(), new HashMap(), false);
        DBConnection.sql.modifyQuery("INSERT INTO pk_players (uuid, player) VALUES ('" + uuid.toString() + "', '" + player + "')");
        ProjectKorra.log.info("Created new BendingPlayer for " + player);
      }
      else
      {
        String player2 = rs2.getString("player");
        if (!player.equalsIgnoreCase(player2))
        {
          DBConnection.sql.modifyQuery("UPDATE pk_players SET player = '" + player + "' WHERE uuid = '" + uuid.toString() + "'");
          
          ProjectKorra.log.info("Updating Player Name for " + player);
        }
        String element = rs2.getString("element");
        String permaremoved = rs2.getString("permaremoved");
        boolean p = false;
        final ArrayList<Element> elements = new ArrayList();
        if (element != null)
        {
          if (element.contains("a")) {
            elements.add(Element.Air);
          }
          if (element.contains("w")) {
            elements.add(Element.Water);
          }
          if (element.contains("e")) {
            elements.add(Element.Earth);
          }
          if (element.contains("f")) {
            elements.add(Element.Fire);
          }
          if (element.contains("c")) {
            elements.add(Element.Chi);
          }
          if (element.contains("sc")) {
            elements.add(Element.Scarecrow);
          }
          if (element.contains("sn")) {
            elements.add(Element.Snowman);
          }
          if (element.contains("su")) {
              elements.add(Element.Sunshine);
            }
        }
        final HashMap<Integer, String> abilities = new HashMap();
        for (int i = 1; i <= 9; i++)
        {
          String slot = rs2.getString("slot" + i);
          if ((slot != null) && (!slot.equalsIgnoreCase("null"))) {
            abilities.put(Integer.valueOf(i), slot);
          }
        }
        p = (permaremoved != null) && (permaremoved.equals("true"));
        
        final boolean boolean_p = p;
        new BukkitRunnable()
        {
          public void run()
          {
            new BendingPlayer(uuid, player, elements, abilities, boolean_p);
          }
        }.runTask(ProjectKorra.plugin);
      }
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static void damageEntity(Player player, Entity entity, double damage, String ability)
  {
    if ((ability != null) && (abilityExists(ability))) {
      damageEntity(player, entity, damage, getAbilityElement(ability), getAbilitySubElement(ability), ability);
    } else {
      damageEntity(player, entity, damage, null, null, ability);
    }
  }
  
  public static void damageEntity(Player player, Entity entity, double damage, Element element, String ability)
  {
    damageEntity(player, entity, damage, element, null, ability);
  }
  
  public static void damageEntity(Player player, Entity entity, double damage, SubElement sub, String ability)
  {
    damageEntity(player, entity, damage, null, sub, ability);
  }
  
  public static void damageEntity(Player player, Entity entity, double damage, Element element, SubElement sub, String ability) {
		if (entity instanceof LivingEntity) {
			if (entity instanceof Player) {
				if (Commands.invincible.contains(entity.getName()))
					return;
			}
			if (Bukkit.getPluginManager().isPluginEnabled("NoCheatPlus")) {
				NCPExemptionManager.exemptPermanently(player, CheckType.FIGHT_REACH);
			}
			if (((LivingEntity) entity).getHealth() - damage <= 0 && entity instanceof Player && !entity.isDead()) {
				PlayerBendingDeathEvent event = new PlayerBendingDeathEvent((Player) entity, player, damage, element, sub, ability);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
			((LivingEntity) entity).damage(damage, player);
			entity.setLastDamageCause(new EntityDamageByEntityEvent(player, entity, DamageCause.CUSTOM, damage));
			if (Bukkit.getPluginManager().isPluginEnabled("NoCheatPlus")) {
				NCPExemptionManager.unexempt(player);
			}
		}
}
  public static void deserializeFile()
  {
    // Byte code:
    //   0: new 1041	java/io/File
    //   3: dup
    //   4: ldc_w 1043
    //   7: ldc_w 1045
    //   10: invokespecial 1047	java/io/File:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   13: astore_0
    //   14: new 1041	java/io/File
    //   17: dup
    //   18: ldc_w 1043
    //   21: ldc_w 1050
    //   24: invokespecial 1047	java/io/File:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   27: astore_1
    //   28: aload_0
    //   29: invokevirtual 1052	java/io/File:exists	()Z
    //   32: ifeq +319 -> 351
    //   35: aconst_null
    //   36: astore_2
    //   37: aconst_null
    //   38: astore_3
    //   39: new 1055	java/io/DataInputStream
    //   42: dup
    //   43: new 1057	java/io/FileInputStream
    //   46: dup
    //   47: aload_0
    //   48: invokespecial 1059	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   51: invokespecial 1062	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   54: astore 4
    //   56: new 1065	java/io/BufferedReader
    //   59: dup
    //   60: new 1067	java/io/InputStreamReader
    //   63: dup
    //   64: aload 4
    //   66: invokespecial 1069	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   69: invokespecial 1070	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   72: astore 5
    //   74: new 1073	java/io/DataOutputStream
    //   77: dup
    //   78: new 1075	java/io/FileOutputStream
    //   81: dup
    //   82: aload_1
    //   83: invokespecial 1077	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   86: invokespecial 1078	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   89: astore 6
    //   91: new 1081	java/io/BufferedWriter
    //   94: dup
    //   95: new 1083	java/io/OutputStreamWriter
    //   98: dup
    //   99: aload 6
    //   101: invokespecial 1085	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
    //   104: invokespecial 1086	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   107: astore 7
    //   109: goto +43 -> 152
    //   112: aload 8
    //   114: invokevirtual 1089	java/lang/String:trim	()Ljava/lang/String;
    //   117: ldc_w 1092
    //   120: invokevirtual 894	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   123: ifne +29 -> 152
    //   126: aload 7
    //   128: new 307	java/lang/StringBuilder
    //   131: dup
    //   132: aload 8
    //   134: invokestatic 1094	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   137: invokespecial 614	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   140: ldc_w 1097
    //   143: invokevirtual 322	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: invokevirtual 325	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: invokevirtual 1099	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   152: aload 5
    //   154: invokevirtual 1102	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   157: dup
    //   158: astore 8
    //   160: ifnonnull -48 -> 112
    //   163: aload 7
    //   165: ifnull +24 -> 189
    //   168: aload 7
    //   170: invokevirtual 1105	java/io/BufferedWriter:close	()V
    //   173: goto +16 -> 189
    //   176: astore_2
    //   177: aload 7
    //   179: ifnull +8 -> 187
    //   182: aload 7
    //   184: invokevirtual 1105	java/io/BufferedWriter:close	()V
    //   187: aload_2
    //   188: athrow
    //   189: aload 6
    //   191: ifnull +43 -> 234
    //   194: aload 6
    //   196: invokevirtual 1108	java/io/DataOutputStream:close	()V
    //   199: goto +35 -> 234
    //   202: astore_3
    //   203: aload_2
    //   204: ifnonnull +8 -> 212
    //   207: aload_3
    //   208: astore_2
    //   209: goto +13 -> 222
    //   212: aload_2
    //   213: aload_3
    //   214: if_acmpeq +8 -> 222
    //   217: aload_2
    //   218: aload_3
    //   219: invokevirtual 1109	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   222: aload 6
    //   224: ifnull +8 -> 232
    //   227: aload 6
    //   229: invokevirtual 1108	java/io/DataOutputStream:close	()V
    //   232: aload_2
    //   233: athrow
    //   234: aload 5
    //   236: ifnull +43 -> 279
    //   239: aload 5
    //   241: invokevirtual 1115	java/io/BufferedReader:close	()V
    //   244: goto +35 -> 279
    //   247: astore_3
    //   248: aload_2
    //   249: ifnonnull +8 -> 257
    //   252: aload_3
    //   253: astore_2
    //   254: goto +13 -> 267
    //   257: aload_2
    //   258: aload_3
    //   259: if_acmpeq +8 -> 267
    //   262: aload_2
    //   263: aload_3
    //   264: invokevirtual 1109	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   267: aload 5
    //   269: ifnull +8 -> 277
    //   272: aload 5
    //   274: invokevirtual 1115	java/io/BufferedReader:close	()V
    //   277: aload_2
    //   278: athrow
    //   279: aload 4
    //   281: ifnull +70 -> 351
    //   284: aload 4
    //   286: invokevirtual 1116	java/io/DataInputStream:close	()V
    //   289: goto +62 -> 351
    //   292: astore_3
    //   293: aload_2
    //   294: ifnonnull +8 -> 302
    //   297: aload_3
    //   298: astore_2
    //   299: goto +13 -> 312
    //   302: aload_2
    //   303: aload_3
    //   304: if_acmpeq +8 -> 312
    //   307: aload_2
    //   308: aload_3
    //   309: invokevirtual 1109	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   312: aload 4
    //   314: ifnull +8 -> 322
    //   317: aload 4
    //   319: invokevirtual 1116	java/io/DataInputStream:close	()V
    //   322: aload_2
    //   323: athrow
    //   324: astore_3
    //   325: aload_2
    //   326: ifnonnull +8 -> 334
    //   329: aload_3
    //   330: astore_2
    //   331: goto +13 -> 344
    //   334: aload_2
    //   335: aload_3
    //   336: if_acmpeq +8 -> 344
    //   339: aload_2
    //   340: aload_3
    //   341: invokevirtual 1109	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   344: aload_2
    //   345: athrow
    //   346: astore_2
    //   347: aload_2
    //   348: invokevirtual 1117	java/io/IOException:printStackTrace	()V
    //   351: return
    // Line number table:
    //   Java source line #605	-> byte code offset #0
    //   Java source line #606	-> byte code offset #14
    //   Java source line #607	-> byte code offset #28
    //   Java source line #608	-> byte code offset #35
    //   Java source line #608	-> byte code offset #39
    //   Java source line #610	-> byte code offset #74
    //   Java source line #613	-> byte code offset #109
    //   Java source line #614	-> byte code offset #112
    //   Java source line #615	-> byte code offset #126
    //   Java source line #613	-> byte code offset #152
    //   Java source line #618	-> byte code offset #163
    //   Java source line #619	-> byte code offset #346
    //   Java source line #620	-> byte code offset #347
    //   Java source line #623	-> byte code offset #351
    // Local variable table:
    //   start	length	slot	name	signature
    //   13	35	0	readFile	File
    //   27	56	1	writeFile	File
    //   36	1	2	localObject1	Object
    //   176	28	2	localObject2	Object
    //   208	137	2	localObject3	Object
    //   346	2	2	e	IOException
    //   38	1	3	localObject4	Object
    //   202	17	3	localThrowable1	Throwable
    //   247	17	3	localThrowable2	Throwable
    //   292	17	3	localThrowable3	Throwable
    //   324	17	3	localThrowable4	Throwable
    //   54	264	4	input	java.io.DataInputStream
    //   72	201	5	reader	java.io.BufferedReader
    //   89	139	6	output	java.io.DataOutputStream
    //   107	76	7	writer	java.io.BufferedWriter
    //   112	21	8	line	String
    //   158	3	8	line	String
    // Exception table:
    //   from	to	target	type
    //   109	163	176	finally
    //   91	189	202	finally
    //   74	234	247	finally
    //   56	279	292	finally
    //   39	324	324	finally
    //   35	346	346	java/io/IOException
  }
  
  public static void displayColoredParticle(Location loc, ParticleEffect type, String hexVal, float xOffset, float yOffset, float zOffset)
  {
    int R = 0;
    int G = 0;
    int B = 0;
    if (hexVal.length() <= 6)
    {
      R = Integer.valueOf(hexVal.substring(0, 2), 16).intValue();
      G = Integer.valueOf(hexVal.substring(2, 4), 16).intValue();
      B = Integer.valueOf(hexVal.substring(4, 6), 16).intValue();
      if (R <= 0) {
        R = 1;
      }
    }
    else if ((hexVal.length() <= 7) && (hexVal.substring(0, 1).equals("#")))
    {
      R = Integer.valueOf(hexVal.substring(1, 3), 16).intValue();
      G = Integer.valueOf(hexVal.substring(3, 5), 16).intValue();
      B = Integer.valueOf(hexVal.substring(5, 7), 16).intValue();
      if (R <= 0) {
        R = 1;
      }
    }
    loc.setX(loc.getX() + Math.random() * (xOffset / 2.0F - -(xOffset / 2.0F)));
    loc.setY(loc.getY() + Math.random() * (yOffset / 2.0F - -(yOffset / 2.0F)));
    loc.setZ(loc.getZ() + Math.random() * (zOffset / 2.0F - -(zOffset / 2.0F)));
    if ((type == ParticleEffect.REDSTONE) || (type == ParticleEffect.REDSTONE)) {
      ParticleEffect.REDSTONE.display(R, G, B, 0.004F, 0, loc, 257.0D);
    } else if ((type == ParticleEffect.SPELL_MOB) || (type == ParticleEffect.SPELL_MOB)) {
      ParticleEffect.SPELL_MOB.display(255.0F - R, 255.0F - G, 255.0F - B, 1.0F, 0, loc, 257.0D);
    } else if ((type == ParticleEffect.SPELL_MOB_AMBIENT) || (type == ParticleEffect.SPELL_MOB_AMBIENT)) {
      ParticleEffect.SPELL_MOB_AMBIENT.display(255.0F - R, 255.0F - G, 255.0F - B, 1.0F, 0, loc, 257.0D);
    } else {
      ParticleEffect.REDSTONE.display(0.0F, 0.0F, 0.0F, 0.004F, 0, loc, 257.0D);
    }
  }
  
  public static void displayColoredParticle(Location loc, String hexVal)
  {
    int R = 0;
    int G = 0;
    int B = 0;
    if (hexVal.length() <= 6)
    {
      R = Integer.valueOf(hexVal.substring(0, 2), 16).intValue();
      G = Integer.valueOf(hexVal.substring(2, 4), 16).intValue();
      B = Integer.valueOf(hexVal.substring(4, 6), 16).intValue();
      if (R <= 0) {
        R = 1;
      }
    }
    else if ((hexVal.length() <= 7) && (hexVal.substring(0, 1).equals("#")))
    {
      R = Integer.valueOf(hexVal.substring(1, 3), 16).intValue();
      G = Integer.valueOf(hexVal.substring(3, 5), 16).intValue();
      B = Integer.valueOf(hexVal.substring(5, 7), 16).intValue();
      if (R <= 0) {
        R = 1;
      }
    }
    ParticleEffect.REDSTONE.display(R, G, B, 0.004F, 0, loc, 257.0D);
  }
  
  public static void displayColoredParticle(Location loc, String hexVal, float xOffset, float yOffset, float zOffset)
  {
    int R = 0;
    int G = 0;
    int B = 0;
    if (hexVal.length() <= 6)
    {
      R = Integer.valueOf(hexVal.substring(0, 2), 16).intValue();
      G = Integer.valueOf(hexVal.substring(2, 4), 16).intValue();
      B = Integer.valueOf(hexVal.substring(4, 6), 16).intValue();
      if (R <= 0) {
        R = 1;
      }
    }
    else if ((hexVal.length() <= 7) && (hexVal.substring(0, 1).equals("#")))
    {
      R = Integer.valueOf(hexVal.substring(1, 3), 16).intValue();
      G = Integer.valueOf(hexVal.substring(3, 5), 16).intValue();
      B = Integer.valueOf(hexVal.substring(5, 7), 16).intValue();
      if (R <= 0) {
        R = 1;
      }
    }
    loc.setX(loc.getX() + Math.random() * (xOffset / 2.0F - -(xOffset / 2.0F)));
    loc.setY(loc.getY() + Math.random() * (yOffset / 2.0F - -(yOffset / 2.0F)));
    loc.setZ(loc.getZ() + Math.random() * (zOffset / 2.0F - -(zOffset / 2.0F)));
    
    ParticleEffect.REDSTONE.display(R, G, B, 0.004F, 0, loc, 257.0D);
  }
  
  public static void displayParticleVector(Location loc, ParticleEffect type, float xTrans, float yTrans, float zTrans)
  {
    if (type == ParticleEffect.FIREWORKS_SPARK) {
      ParticleEffect.FIREWORKS_SPARK.display(xTrans, yTrans, zTrans, 0.09F, 0, loc, 257.0D);
    } else if ((type == ParticleEffect.SMOKE_LARGE) || (type == ParticleEffect.SMOKE_NORMAL)) {
      ParticleEffect.SMOKE_LARGE.display(xTrans, yTrans, zTrans, 0.04F, 0, loc, 257.0D);
    } else if ((type == ParticleEffect.SMOKE_NORMAL) || (type == ParticleEffect.SMOKE_LARGE)) {
      ParticleEffect.SMOKE_NORMAL.display(xTrans, yTrans, zTrans, 0.04F, 0, loc, 257.0D);
    } else if (type == ParticleEffect.ENCHANTMENT_TABLE) {
      ParticleEffect.ENCHANTMENT_TABLE.display(xTrans, yTrans, zTrans, 0.5F, 0, loc, 257.0D);
    } else if (type == ParticleEffect.PORTAL) {
      ParticleEffect.PORTAL.display(xTrans, yTrans, zTrans, 0.5F, 0, loc, 257.0D);
    } else if (type == ParticleEffect.FLAME) {
      ParticleEffect.FLAME.display(xTrans, yTrans, zTrans, 0.04F, 0, loc, 257.0D);
    } else if (type == ParticleEffect.CLOUD) {
      ParticleEffect.CLOUD.display(xTrans, yTrans, zTrans, 0.04F, 0, loc, 257.0D);
    } else if (type == ParticleEffect.SNOW_SHOVEL) {
      ParticleEffect.SNOW_SHOVEL.display(xTrans, yTrans, zTrans, 0.2F, 0, loc, 257.0D);
    } else {
      ParticleEffect.REDSTONE.display(0.0F, 0.0F, 0.0F, 0.004F, 0, loc, 257.0D);
    }
  }
  
  public static void dropItems(Block block, Collection<ItemStack> items)
  {
    for (ItemStack item : items) {
      block.getWorld().dropItem(block.getLocation(), item);
    }
  }
  
  public static String getAbility(String string)
  {
    for (String st : AbilityModuleManager.abilities) {
      if (st.equalsIgnoreCase(string)) {
        return st;
      }
    }
    return null;
  }
  
  public static ChatColor getAbilityColor(String ability)
  {
    if (AbilityModuleManager.chiabilities.contains(ability)) {
      return ChiMethods.getChiColor();
    }
    if (AbilityModuleManager.airbendingabilities.contains(ability))
    {
      if (AbilityModuleManager.subabilities.contains(ability)) {
        return getSubBendingColor(Element.Air);
      }
      return AirMethods.getAirColor();
    }
    if (AbilityModuleManager.waterbendingabilities.contains(ability))
    {
      if (AbilityModuleManager.subabilities.contains(ability)) {
        return getSubBendingColor(Element.Water);
      }
      return WaterMethods.getWaterColor();
    }
    if (AbilityModuleManager.earthbendingabilities.contains(ability))
    {
      if (AbilityModuleManager.subabilities.contains(ability)) {
        return getSubBendingColor(Element.Earth);
      }
      return EarthMethods.getEarthColor();
    }
    if (AbilityModuleManager.firebendingabilities.contains(ability))
    {
      if (AbilityModuleManager.subabilities.contains(ability)) {
        return getSubBendingColor(Element.Fire);
      }
      return FireMethods.getFireColor();
    }
    if (AbilityModuleManager.SnowmanAbilities.contains(ability)) {
      return SnowMethods.getSnowColor();
    }
    if (AbilityModuleManager.SunshineAbilities.contains(ability)) {
        return SunshineMethods.getSunshineColor();
      }
    if (AbilityModuleManager.ScarecrowAbilities.contains(ability)) {
      return ScareMethods.getScareColor();
    }
    return getAvatarColor();
  }
  
  public static Element getAbilityElement(String ability)
  {
    if (AbilityModuleManager.airbendingabilities.contains(ability)) {
      return Element.Air;
    }
    if (AbilityModuleManager.earthbendingabilities.contains(ability)) {
      return Element.Earth;
    }
    if (AbilityModuleManager.firebendingabilities.contains(ability)) {
      return Element.Fire;
    }
    if (AbilityModuleManager.waterbendingabilities.contains(ability)) {
      return Element.Water;
    }
    if (AbilityModuleManager.chiabilities.contains(ability)) {
      return Element.Chi;
    }
    if (AbilityModuleManager.ScarecrowAbilities.contains(ability)) {
      return Element.Scarecrow;
    }
    if (AbilityModuleManager.SnowmanAbilities.contains(ability)) {
      return Element.Snowman;
    }
    if (AbilityModuleManager.SunshineAbilities.contains(ability)) {
        return Element.Sunshine;
      }
    return null;
  }
  
  public static SubElement getAbilitySubElement(String ability)
  {
    if (AbilityModuleManager.bloodabilities.contains(ability)) {
      return SubElement.Bloodbending;
    }
    if (AbilityModuleManager.iceabilities.contains(ability)) {
      return SubElement.Icebending;
    }
    if (AbilityModuleManager.plantabilities.contains(ability)) {
      return SubElement.Plantbending;
    }
    if (AbilityModuleManager.healingabilities.contains(ability)) {
      return SubElement.Healing;
    }
    if (AbilityModuleManager.sandabilities.contains(ability)) {
      return SubElement.Sandbending;
    }
    if (AbilityModuleManager.metalabilities.contains(ability)) {
      return SubElement.Metalbending;
    }
    if (AbilityModuleManager.lavaabilities.contains(ability)) {
      return SubElement.Lavabending;
    }
    if (AbilityModuleManager.lightningabilities.contains(ability)) {
      return SubElement.Lightning;
    }
    if (AbilityModuleManager.combustionabilities.contains(ability)) {
      return SubElement.Combustion;
    }
    if (AbilityModuleManager.spiritualprojectionabilities.contains(ability)) {
      return SubElement.SpiritualProjection;
    }
    if (AbilityModuleManager.flightabilities.contains(ability)) {
      return SubElement.Flight;
    }
    return null;
  }
  
  public static ChatColor getAvatarColor()
  {
    return ChatColor.valueOf(plugin.getConfig().getString("Properties.Chat.Colors.Avatar"));
  }
  
  public static BendingPlayer getBendingPlayer(String playerName)
  {
    OfflinePlayer player = Bukkit.getPlayer(playerName);
    if (player == null) {
      player = Bukkit.getOfflinePlayer(playerName);
    }
    return (BendingPlayer)BendingPlayer.getPlayers().get(player.getUniqueId());
  }
  
  public static List<Block> getBlocksAlongLine(Location ploc, Location tloc, World w) {
		List<Block> blocks = new ArrayList<Block>();

		//Next we will name each coordinate
		int x1 = ploc.getBlockX();
		int y1 = ploc.getBlockY();
		int z1 = ploc.getBlockZ();

		int x2 = tloc.getBlockX();
		int y2 = tloc.getBlockY();
		int z2 = tloc.getBlockZ();

		//Then we create the following integers
		int xMin, yMin, zMin;
		int xMax, yMax, zMax;
		int x, y, z;

		//Now we need to make sure xMin is always lower then xMax
		if (x1 > x2) { //If x1 is a higher number then x2
			xMin = x2;
			xMax = x1;
		} else {
			xMin = x1;
			xMax = x2;
		}
		//Same with Y
		if (y1 > y2) {
			yMin = y2;
			yMax = y1;
		} else {
			yMin = y1;
			yMax = y2;
		}

		//And Z
		if (z1 > z2) {
			zMin = z2;
			zMax = z1;
		} else {
			zMin = z1;
			zMax = z2;
		}

		//Now it's time for the loop
		for (x = xMin; x <= xMax; x++) {
			for (y = yMin; y <= yMax; y++) {
				for (z = zMin; z <= zMax; z++) {
					Block b = new Location(w, x, y, z).getBlock();
					blocks.add(b);
				}
			}
		}

		//And last but not least, we return with the list
		return blocks;
	}

  
  public static List<Block> getBlocksAroundPoint(Location location, double radius)
  {
    List<Block> blocks = new ArrayList();
    
    int xorg = location.getBlockX();
    int yorg = location.getBlockY();
    int zorg = location.getBlockZ();
    
    int r = (int)radius * 4;
    for (int x = xorg - r; x <= xorg + r; x++) {
      for (int y = yorg - r; y <= yorg + r; y++) {
        for (int z = zorg - r; z <= zorg + r; z++)
        {
          Block block = location.getWorld().getBlockAt(x, y, z);
          if (block.getLocation().distance(location) <= radius) {
            blocks.add(block);
          }
        }
      }
    }
    return blocks;
  }
  
  public static String getBoundAbility(Player player)
  {
    BendingPlayer bPlayer = getBendingPlayer(player.getName());
    if (bPlayer == null) {
      return null;
    }
    int slot = player.getInventory().getHeldItemSlot() + 1;
    return (String)bPlayer.getAbilities().get(Integer.valueOf(slot));
  }
  
  public static BlockFace getCardinalDirection(Vector vector)
  {
    BlockFace[] faces = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };
    
    Vector w = new Vector(-1, 0, 0);
    Vector n = new Vector(0, 0, -1);
    Vector s = n.clone().multiply(-1);
    Vector e = w.clone().multiply(-1);
    Vector ne = n.clone().add(e.clone()).normalize();
    Vector se = s.clone().add(e.clone()).normalize();
    Vector nw = n.clone().add(w.clone()).normalize();
    Vector sw = s.clone().add(w.clone()).normalize();
    
    Vector[] vectors = { n, ne, e, se, s, sw, w, nw };
    
    double comp = 0.0D;
    int besti = 0;
    for (int i = 0; i < vectors.length; i++)
    {
      double dot = vector.dot(vectors[i]);
      if (dot > comp)
      {
        comp = dot;
        besti = i;
      }
    }
    return faces[besti];
  }
  
  public static List<Location> getCircle(Location loc, int radius, int height, boolean hollow, boolean sphere, int plusY)
  {
    List<Location> circleblocks = new ArrayList();
    int cx = loc.getBlockX();
    int cy = loc.getBlockY();
    int cz = loc.getBlockZ();
    for (int x = cx - radius; x <= cx + radius; x++) {
      for (int z = cz - radius; z <= cz + radius; z++) {
        for (int y = sphere ? cy - radius : cy; y < (sphere ? cy + radius : cy + height); y++)
        {
          double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
          if ((dist < radius * radius) && ((!hollow) || (dist >= (radius - 1) * (radius - 1))))
          {
            Location l = new Location(loc.getWorld(), x, y + plusY, z);
            circleblocks.add(l);
          }
        }
      }
    }
    return circleblocks;
  }
  
  public static ChatColor getComboColor(String combo)
  {
    for (String ability : ComboManager.comboAbilityList.keySet())
    {
      ComboManager.ComboAbility comboability = (ComboManager.ComboAbility)ComboManager.comboAbilityList.get(ability);
      if (comboability.getName().equalsIgnoreCase(combo))
      {
        if (!ComboManager.descriptions.containsKey(comboability.getName())) {
          return ChatColor.STRIKETHROUGH;
        }
        if ((comboability.getComboType() instanceof ComboAbilityModule))
        {
          ComboAbilityModule module = (ComboAbilityModule)comboability.getComboType();
          if (module.getSubElement() != null)
          {
            if ((module.getSubElement() == SubElement.Bloodbending) || (module.getSubElement() == SubElement.Icebending) || (module.getSubElement() == SubElement.Plantbending) || (module.getSubElement() == SubElement.Healing)) {
              return WaterMethods.getWaterSubColor();
            }
            if ((module.getSubElement() == SubElement.Lightning) || (module.getSubElement() == SubElement.Combustion)) {
              return FireMethods.getFireSubColor();
            }
            if ((module.getSubElement() == SubElement.Sandbending) || (module.getSubElement() == SubElement.Metalbending) || (module.getSubElement() == SubElement.Lavabending)) {
              return EarthMethods.getEarthSubColor();
            }
            if ((module.getSubElement() == SubElement.Flight) || (module.getSubElement() == SubElement.SpiritualProjection)) {
              return AirMethods.getAirSubColor();
            }
          }
          if (module.getElement().equalsIgnoreCase(Element.Water.toString())) {
            return WaterMethods.getWaterColor();
          }
          if (module.getElement().equalsIgnoreCase(Element.Earth.toString())) {
            return EarthMethods.getEarthColor();
          }
          if (module.getElement().equalsIgnoreCase(Element.Fire.toString())) {
            return FireMethods.getFireColor();
          }
          if (module.getElement().equalsIgnoreCase(Element.Air.toString())) {
            return AirMethods.getAirColor();
          }
          if (module.getElement().equalsIgnoreCase(Element.Chi.toString())) {
            return ChiMethods.getChiColor();
          }
          return getAvatarColor();
        }
        if ((combo.equalsIgnoreCase("IceBullet")) || (combo.equalsIgnoreCase("IceWave"))) {
          return WaterMethods.getWaterSubColor();
        }
        if (comboability.getComboType().equals(WaterCombo.class)) {
          return WaterMethods.getWaterColor();
        }
        if (comboability.getComboType().equals(FireCombo.class)) {
          return FireMethods.getFireColor();
        }
        if (comboability.getComboType().equals(AirCombo.class)) {
          return AirMethods.getAirColor();
        }
        Element element = null;
        for (ComboManager.AbilityInformation abilityinfo : comboability.getAbilities())
        {
          Element currElement = getAbilityElement(abilityinfo.getAbilityName());
          if (currElement == null) {
            return getAvatarColor();
          }
          if (element == null) {
            element = currElement;
          }
          if (getAbilitySubElement(abilityinfo.getAbilityName()) != null)
          {
            SubElement sub = getAbilitySubElement(abilityinfo.getAbilityName());
            if ((sub == SubElement.Bloodbending) || (sub == SubElement.Icebending) || (sub == SubElement.Plantbending) || (sub == SubElement.Healing)) {
              return WaterMethods.getWaterSubColor();
            }
            if ((sub == SubElement.Lightning) || (sub == SubElement.Combustion)) {
              return FireMethods.getFireSubColor();
            }
            if ((sub == SubElement.Sandbending) || (sub == SubElement.Metalbending) || (sub == SubElement.Lavabending)) {
              return EarthMethods.getEarthSubColor();
            }
            if ((sub == SubElement.Flight) || (sub == SubElement.SpiritualProjection)) {
              return AirMethods.getAirSubColor();
            }
          }
        }
        if (element == Element.Air) {
          return AirMethods.getAirColor();
        }
        if (element == Element.Earth) {
          return EarthMethods.getEarthColor();
        }
        if (element == Element.Fire) {
          return FireMethods.getFireColor();
        }
        if (element == Element.Water) {
          return WaterMethods.getWaterColor();
        }
        if (element == Element.Chi) {
          return ChiMethods.getChiColor();
        }
        return getAvatarColor();
      }
    }
    return getAvatarColor();
  }
  
  public static String getCurrentDate()
  {
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    return dateFormat.format(date);
  }
  
  public static Vector getDirection(Location location, Location destination)
  {
    double x1 = destination.getX();
    double y1 = destination.getY();
    double z1 = destination.getZ();
    
    double x0 = location.getX();
    double y0 = location.getY();
    double z0 = location.getZ();
    
    return new Vector(x1 - x0, y1 - y0, z1 - z0);
  }
  
  public static double getDistanceFromLine(Vector line, Location pointonline, Location point)
  {
    Vector AP = new Vector();
    
    double Ax = pointonline.getX();
    double Ay = pointonline.getY();
    double Az = pointonline.getZ();
    
    double Px = point.getX();
    double Py = point.getY();
    double Pz = point.getZ();
    
    AP.setX(Px - Ax);
    AP.setY(Py - Ay);
    AP.setZ(Pz - Az);
    
    return AP.crossProduct(line).length() / line.length();
  }
  
  public static Collection<ItemStack> getDrops(Block block, Material type, byte data, ItemStack breakitem)
  {
    BlockState tempstate = block.getState();
    block.setType(type);
    block.setData(data);
    Collection<ItemStack> item = block.getDrops();
    tempstate.update(true);
    return item;
  }
  
  public static List<Entity> getEntitiesAroundPoint(Location location, double radius)
  {
    List<Entity> entities = location.getWorld().getEntities();
    List<Entity> list = location.getWorld().getEntities();
    for (Entity entity : entities) {
      if (entity.getWorld() != location.getWorld()) {
        list.remove(entity);
      } else if (entity.getLocation().distance(location) > radius) {
        list.remove(entity);
      }
    }
    return list;
  }
  
  public static long getGlobalCooldown()
  {
    return plugin.getConfig().getLong("Properties.GlobalCooldown");
  }
  
  public static int getIntCardinalDirection(Vector vector)
  {
    BlockFace face = getCardinalDirection(vector);
    switch (face)
    {
    case EAST_NORTH_EAST: 
      return 7;
    case SELF: 
      return 6;
    case EAST_SOUTH_EAST: 
      return 3;
    case NORTH_NORTH_WEST: 
      return 0;
    case DOWN: 
      return 1;
    case NORTH_NORTH_EAST: 
      return 2;
    case EAST: 
      return 5;
    case NORTH_WEST: 
      return 8;
    }
    return 4;
  }
  
  public static Plugin getItems()
  {
    if (hasItems()) {
      return Bukkit.getServer().getPluginManager().getPlugin("ProjectKorraItems");
    }
    return null;
  }
  
  public static String getLastUsedAbility(Player player, boolean checkCombos)
  {
    List<ComboManager.AbilityInformation> lastUsedAbility = ComboManager.getRecentlyUsedAbilities(player, 1);
    if (!lastUsedAbility.isEmpty())
    {
      if ((ComboManager.checkForValidCombo(player) != null) && (checkCombos)) {
        return ComboManager.checkForValidCombo(player).getName();
      }
      return ((ComboManager.AbilityInformation)lastUsedAbility.get(0)).getAbilityName();
    }
    return null;
  }
  
  public static Location getLeftSide(Location location, double distance)
  {
    float angle = location.getYaw() / 60.0F;
    return location.clone().add(new Vector(Math.cos(angle), 0.0D, Math.sin(angle)).normalize().multiply(distance));
  }
  
  public static int getMaxPresets(Player player)
  {
    if (player.isOp()) {
      return 500;
    }
    int cap = 0;
    for (int i = 0; i <= 500; i++) {
      if (player.hasPermission("bending.command.presets.create." + i)) {
        cap = i;
      }
    }
    return cap;
  }
  
  public static Vector getOrthogonalVector(Vector axis, double degrees, double length)
  {
    Vector ortho = new Vector(axis.getY(), -axis.getX(), 0.0D);
    ortho = ortho.normalize();
    ortho = ortho.multiply(length);
    
    return rotateVectorAroundVector(axis, ortho, degrees);
  }
  
  public static Collection<Player> getPlayersAroundPoint(Location location, double distance)
  {
    Collection<Player> players = new HashSet();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if ((player.getLocation().getWorld().equals(location.getWorld())) && 
        (player.getLocation().distance(location) <= distance)) {
        players.add(player);
      }
    }
    return players;
  }
  
  public static Location getPointOnLine(Location origin, Location target, double distance)
  {
    return origin.clone().add(getDirection(origin, target).normalize().multiply(distance));
  }
  
  public static Location getRightSide(Location location, double distance)
  {
    float angle = location.getYaw() / 60.0F;
    return location.clone().subtract(new Vector(Math.cos(angle), 0.0D, Math.sin(angle)).normalize().multiply(distance));
  }
  
  public static Plugin getRPG()
  {
    if (hasRPG()) {
      return Bukkit.getServer().getPluginManager().getPlugin("ProjectKorraRPG");
    }
    return null;
  }
  
  public static ChatColor getSubBendingColor(Element element)
  {
    switch (element)
    {
    case Fire: 
      return ChatColor.valueOf(plugin.getConfig().getString("Properties.Chat.Colors.FireSub"));
    case Air: 
      return ChatColor.valueOf(plugin.getConfig().getString("Properties.Chat.Colors.AirSub"));
    case Chi: 
      return ChatColor.valueOf(plugin.getConfig().getString("Properties.Chat.Colors.WaterSub"));
    case Earth: 
      return ChatColor.valueOf(plugin.getConfig().getString("Properties.Chat.Colors.EarthSub"));
    }
    return getAvatarColor();
  }
  
  public static SubElement getSubElementByString(String sub)
  {
    return SubElement.getType(sub);
  }
  
  public static Entity getTargetedEntity(Player player, double range, List<Entity> avoid)
  {
    double longestr = range + 1.0D;
    Entity target = null;
    Location origin = player.getEyeLocation();
    Vector direction = player.getEyeLocation().getDirection().normalize();
    for (Entity entity : origin.getWorld().getEntities()) {
      if (!avoid.contains(entity)) {
        if ((entity.getLocation().distance(origin) < longestr) && (getDistanceFromLine(direction, origin, entity.getLocation()) < 2.0D) && ((entity instanceof LivingEntity)) && (entity.getEntityId() != player.getEntityId()) && (entity.getLocation().distance(origin.clone().add(direction)) < entity.getLocation().distance(origin.clone().add(direction.clone().multiply(-1)))))
        {
          target = entity;
          longestr = entity.getLocation().distance(origin);
        }
      }
    }
    if (target != null)
    {
      List<Block> blocklist = new ArrayList();
      blocklist = getBlocksAlongLine(player.getLocation(), target.getLocation(), player.getWorld());
      for (Block isAir : blocklist) {
        if (isObstructed(origin, target.getLocation()))
        {
          target = null;
          break;
        }
      }
    }
    return target;
  }
  
  public static Location getTargetedLocation(Player player, double originselectrange, Integer... nonOpaque2) {
		Location origin = player.getEyeLocation();
		Vector direction = origin.getDirection();

		HashSet<Byte> trans = new HashSet<Byte>();
		trans.add((byte) 0);

		if (nonOpaque2 == null) {
			trans = null;
		} else {
			for (int i : nonOpaque2) {
				trans.add((byte) i);
			}
		}

		Block block = player.getTargetBlock(trans, (int) originselectrange + 1);
		double distance = block.getLocation().distance(origin) - 1.5;
		Location location = origin.add(direction.multiply(distance));

		return location;
}
  
  public static Location getTargetedLocation(Player player, int range)
  {
    return getTargetedLocation(player, range, new Integer[] { Integer.valueOf(0) });
  }
  
  public static Block getTopBlock(Location loc, int range)
  {
    return getTopBlock(loc, range, range);
  }
  
  public static Block getTopBlock(Location loc, int positiveY, int negativeY)
  {
    Block blockHolder = loc.getBlock();
    int y = 0;
    do
    {
      y++;
      Block tempBlock = loc.clone().add(0.0D, y, 0.0D).getBlock();
      if (tempBlock.getType() == Material.AIR) {
        return blockHolder;
      }
      blockHolder = tempBlock;
      if (blockHolder.getType() == Material.AIR) {
        break;
      }
    } while (Math.abs(y) < Math.abs(positiveY));
    while ((blockHolder.getType() == Material.AIR) && (Math.abs(y) < Math.abs(negativeY)))
    {
      y--;
      blockHolder = loc.clone().add(0.0D, y, 0.0D).getBlock();
      if (blockHolder.getType() != Material.AIR) {
        return blockHolder;
      }
    }
    return null;
  }
  
  public static boolean hasItems()
  {
    return Bukkit.getServer().getPluginManager().getPlugin("ProjectKorraItems") != null;
  }
  
  public static boolean hasPermission(Player player, String ability)
  {
    return (player.hasPermission("bending.ability." + ability)) && (canBind(player.getName(), ability));
  }
  
  public static boolean hasRPG()
  {
    return Bukkit.getServer().getPluginManager().getPlugin("ProjectKorraRPG") != null;
  }
  
  public static boolean isAbilityInstalled(String name, String author)
  {
    String ability = getAbility(name);
    return (ability != null) && (((String)AbilityModuleManager.authors.get(name)).equalsIgnoreCase(author));
  }
  
  public static boolean isAdjacentToThreeOrMoreSources(Block block)
  {
    if (TempBlock.isTempBlock(block)) {
      return false;
    }
    int sources = 0;
    byte full = 0;
    BlockFace[] faces = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH };
    BlockFace[] arrayOfBlockFace1;
    int j = (arrayOfBlockFace1 = faces).length;
    for (int i = 0; i < j; i++)
    {
      BlockFace face = arrayOfBlockFace1[i];
      Block blocki = block.getRelative(face);
      if (((blocki.getType() == Material.LAVA) || (blocki.getType() == Material.STATIONARY_LAVA)) && (blocki.getData() == full) && (EarthPassive.canPhysicsChange(blocki))) {
        sources++;
      }
      if (((blocki.getType() == Material.WATER) || (blocki.getType() == Material.STATIONARY_WATER)) && (blocki.getData() == full) && (WaterManipulation.canPhysicsChange(blocki))) {
        sources++;
      }
      if (!FreezeMelt.frozenblocks.containsKey(blocki)) {
        blocki.getType();
      }
    }
    return sources >= 2;
  }
  
  public static boolean isBender(String player, Element element)
  {
    BendingPlayer bPlayer = getBendingPlayer(player);
    return (bPlayer != null) && (bPlayer.hasElement(element));
  }
  
  public static boolean isDisabledStockAbility(String string)
  {
    for (String st : AbilityModuleManager.disabledStockAbilities) {
      if (string.equalsIgnoreCase(st)) {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isHarmlessAbility(String ability)
  {
    return AbilityModuleManager.harmlessabilities.contains(ability);
  }
  
  public static boolean isImportEnabled()
  {
    return plugin.getConfig().getBoolean("Properties.ImportEnabled");
  }
  
  public static boolean isInteractable(Block block)
  {
    return Arrays.asList(interactable).contains(block.getType());
  }
  
  public static boolean isObstructed(Location location1, Location location2)
  {
    Vector loc1 = location1.toVector();
    Vector loc2 = location2.toVector();
    
    Vector direction = loc2.subtract(loc1);
    direction.normalize();
    
    double max = location1.distance(location2);
    for (double i = 0.0D; i <= max; i += 1.0D)
    {
      Location loc = location1.clone().add(direction.clone().multiply(i));
      Material type = loc.getBlock().getType();
      if (type != Material.AIR) {
        if ((!Arrays.asList(new HashSet[] { EarthMethods.getTransparentEarthbending() }).contains(Integer.valueOf(type.getId()))) && (!WaterMethods.isWater(loc.getBlock()))) {
          return true;
        }
      }
    }
    return false;
  }
  

	/**
	 * isRegionProtectedFromBuild is one of the most server intensive methods in
	 * the plugin. It uses a blockCache that keeps track of recent blocks that
	 * may have already been checked. Abilities like TremorSense call this
	 * ability 5 times per tick even though it only needs to check a single
	 * block, instead of doing all 5 of those checks this method will now look
	 * in the map first.
	 */
	public static boolean isRegionProtectedFromBuild(Player player, String ability, Location loc) {
		if (!blockProtectionCache.containsKey(player.getName()))
			blockProtectionCache.put(player.getName(), new ConcurrentHashMap<Block, BlockCacheElement>());

		ConcurrentHashMap<Block, BlockCacheElement> blockMap = blockProtectionCache.get(player.getName());
		Block block = loc.getBlock();
		if (blockMap.containsKey(block)) {
			BlockCacheElement elem = blockMap.get(block);

			// both abilities must be equal to each other to use the cache
			if ((ability == null && elem.getAbility() == null) || (ability != null && elem.getAbility() != null && elem.getAbility().equals(ability))) {
				return elem.isAllowed();
			}
		}

		boolean value = isRegionProtectedFromBuildPostCache(player, ability, loc);
		blockMap.put(block, new BlockCacheElement(player, block, ability, value, System.currentTimeMillis()));
		return value;
	}

	public static boolean isRegionProtectedFromBuildPostCache(Player player, String ability, Location loc) {
		boolean allowharmless = plugin.getConfig().getBoolean("Properties.RegionProtection.AllowHarmlessAbilities");
		boolean respectWorldGuard = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectWorldGuard");
		boolean respectPreciousStones = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectPreciousStones");
		boolean respectFactions = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectFactions");
		boolean respectTowny = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectTowny");
		boolean respectGriefPrevention = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectGriefPrevention");
		boolean respectLWC = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectLWC");

		List<String> ignite = AbilityModuleManager.igniteabilities;
		List<String> explode = AbilityModuleManager.explodeabilities;

		if (ability == null && allowharmless)
			return false;
		if (isHarmlessAbility(ability) && allowharmless)
			return false;

		PluginManager pm = Bukkit.getPluginManager();

		Plugin wgp = pm.getPlugin("WorldGuard");
		Plugin psp = pm.getPlugin("PreciousStones");
		Plugin fcp = pm.getPlugin("Factions");
		Plugin twnp = pm.getPlugin("Towny");
		Plugin gpp = pm.getPlugin("GriefPrevention");
		Plugin massivecore = pm.getPlugin("MassiveCore");
		Plugin lwc = pm.getPlugin("LWC");

		for (Location location : new Location[] { loc, player.getLocation() }) {
			World world = location.getWorld();

			if (lwc != null && respectLWC) {
				LWCPlugin lwcp = (LWCPlugin) lwc;
				LWC lwc2 = lwcp.getLWC();
				Protection protection = lwc2.getProtectionCache().getProtection(location.getBlock());
				if (protection != null) {
					if (!lwc2.canAccessProtection(player, protection)) {
						return true;
					}
				}
			}
			if (wgp != null && respectWorldGuard && !player.hasPermission("worldguard.region.bypass." + world.getName())) {
				WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
				if (!player.isOnline())
					return true;

				if (ignite.contains(ability)) {
					if (!wg.hasPermission(player, "worldguard.override.lighter")) {
						if (wg.getGlobalStateManager().get(world).blockLighter)
							return true;
					}
				}
				if (explode.contains(ability)) {
					if (wg.getGlobalStateManager().get(location.getWorld()).blockTNTExplosions)
						return true;
					if (!wg.getRegionContainer().createQuery().testBuild(location, player, DefaultFlag.TNT))
						return true;
				}

				if (!wg.canBuild(player, location.getBlock())) {
					return true;
				}
			}

			if (psp != null && respectPreciousStones) {
				PreciousStones ps = (PreciousStones) psp;

				if (ignite.contains(ability)) {
					if (ps.getForceFieldManager().hasSourceField(location, FieldFlag.PREVENT_FIRE))
						return true;
				}
				if (explode.contains(ability)) {
					if (ps.getForceFieldManager().hasSourceField(location, FieldFlag.PREVENT_EXPLOSIONS))
						return true;
				}

				//				if (ps.getForceFieldManager().hasSourceField(location,
				//						FieldFlag.PREVENT_PLACE))
				//					return true;

				if (!PreciousStones.API().canBreak(player, location)) {
					return true;
				}
			}

			if (fcp != null && massivecore != null && respectFactions) {
				return !EngineMain.canPlayerBuildAt(player, PS.valueOf(loc.getBlock()), false);
			}

			if (twnp != null && respectTowny) {
				Towny twn = (Towny) twnp;

				WorldCoord worldCoord;

				try {
					TownyWorld tWorld = TownyUniverse.getDataSource().getWorld(world.getName());
					worldCoord = new WorldCoord(tWorld.getName(), Coord.parseCoord(location));

					boolean bBuild = PlayerCacheUtil.getCachePermission(player, location, 3, (byte) 0, ActionType.BUILD);

					if (ignite.contains(ability)) {

					}

					if (explode.contains(ability)) {

					}

					if (!bBuild) {
						PlayerCache cache = twn.getCache(player);
						TownBlockStatus status = cache.getStatus();

						if (((status == TownBlockStatus.ENEMY) && TownyWarConfig.isAllowingAttacks())) {
							try {
								TownyWar.callAttackCellEvent(twn, player, location.getBlock(), worldCoord);
							}
							catch (Exception e) {
								TownyMessaging.sendErrorMsg(player, e.getMessage());
							}
							return true;
						} else if (status == TownBlockStatus.WARZONE) {

						} else {
							return true;
						}

						if ((cache.hasBlockErrMsg()))
							TownyMessaging.sendErrorMsg(player, cache.getBlockErrMsg());
					}
				}
				catch (Exception e1) {
					TownyMessaging.sendErrorMsg(player, TownySettings.getLangString("msg_err_not_configured"));
				}
			}

			if (gpp != null && respectGriefPrevention) {
				Material type = player.getWorld().getBlockAt(location).getType();
				if (type == null)
					type = Material.AIR;
				String reason = GriefPrevention.instance.allowBuild(player, location); // WORKING with WorldGuard 6.0 BETA 4

				Claim claim = GriefPrevention.instance.dataStore.getClaimAt(loc, true, null);
				if (ignite.contains(ability)) {

				}

				if (explode.contains(ability)) {

				}

				if (reason != null && claim.siegeData != null)
					return true;
			}
		}
		return false;
}
  
  public static boolean isSolid(Block block)
  {
    return !Arrays.asList(nonOpaque).contains(Integer.valueOf(block.getTypeId()));
  }
  
  public static boolean isSubAbility(String ability)
  {
    return AbilityModuleManager.subabilities.contains(ability);
  }
  
  public static boolean isUndead(Entity entity)
  {
    return (entity != null) && ((entity.getType() == EntityType.ZOMBIE) || (entity.getType() == EntityType.BLAZE) || (entity.getType() == EntityType.GIANT) || (entity.getType() == EntityType.IRON_GOLEM) || (entity.getType() == EntityType.MAGMA_CUBE) || (entity.getType() == EntityType.PIG_ZOMBIE) || (entity.getType() == EntityType.SKELETON) || (entity.getType() == EntityType.SLIME) || (entity.getType() == EntityType.SNOWMAN) || (entity.getType() == EntityType.ZOMBIE));
  }
  
  public static boolean isWeapon(Material mat)
  {
    return (mat != null) && ((mat == Material.WOOD_AXE) || (mat == Material.WOOD_PICKAXE) || (mat == Material.WOOD_SPADE) || (mat == Material.WOOD_SWORD) || (mat == Material.STONE_AXE) || (mat == Material.STONE_PICKAXE) || (mat == Material.STONE_SPADE) || (mat == Material.STONE_SWORD) || (mat == Material.IRON_AXE) || (mat == Material.IRON_PICKAXE) || (mat == Material.IRON_SWORD) || (mat == Material.IRON_SPADE) || (mat == Material.DIAMOND_AXE) || (mat == Material.DIAMOND_PICKAXE) || (mat == Material.DIAMOND_SWORD) || (mat == Material.DIAMOND_SPADE));
  }
  
  public static void playAvatarSound(Location loc)
  {
    loc.getWorld().playSound(loc, Sound.BLOCK_ANVIL_LAND, 1.0F, 10.0F);
  }
  
  public static void reloadPlugin()
  {
    ProjectKorra.log.info("Reloading ProjectKorra and configuration");
    BendingReloadEvent event = new BendingReloadEvent();
    Bukkit.getServer().getPluginManager().callEvent(event);
    if (DBConnection.isOpen) {
      DBConnection.sql.close();
    }
    stopBending();
    ConfigManager.defaultConfig.reload();
    ConfigManager.deathMsgConfig.reload();
    BendingManager.getInstance().reloadVariables();
    new AbilityModuleManager(plugin);
    new ComboManager();
    new MultiAbilityModuleManager();
    DBConnection.host = plugin.getConfig().getString("Storage.MySQL.host");
    DBConnection.port = plugin.getConfig().getInt("Storage.MySQL.port");
    DBConnection.pass = plugin.getConfig().getString("Storage.MySQL.pass");
    DBConnection.db = plugin.getConfig().getString("Storage.MySQL.db");
    DBConnection.user = plugin.getConfig().getString("Storage.MySQL.user");
    DBConnection.init();
    if (!DBConnection.isOpen())
    {
      ProjectKorra.log.severe("Unable to enable ProjectKorra due to the database not being open");
      stopPlugin();
    }
    for (Player player : Bukkit.getOnlinePlayers()) {
      createBendingPlayer(player.getUniqueId(), player.getName());
    }
    ProjectKorra.log.info("Reload complete");
  }
  
  public static void removeBlock(Block block)
  {
    if (isAdjacentToThreeOrMoreSources(block))
    {
      block.setType(Material.WATER);
      block.setData((byte)0);
    }
    else
    {
      block.setType(Material.AIR);
    }
  }
  
  public static void removeUnusableAbilities(String player)
  {
    BendingPlayer bPlayer = getBendingPlayer(player);
    HashMap<Integer, String> slots = bPlayer.getAbilities();
    HashMap<Integer, String> finalabilities = new HashMap();
    for (Iterator localIterator = slots.keySet().iterator(); localIterator.hasNext();)
    {
      int i = ((Integer)localIterator.next()).intValue();
      if (canBend(player, (String)slots.get(Integer.valueOf(i)))) {
        finalabilities.put(Integer.valueOf(i), (String)slots.get(Integer.valueOf(i)));
      }
    }
    bPlayer.setAbilities(finalabilities);
  }
  
  public static Vector rotateVectorAroundVector(Vector axis, Vector rotator, double degrees)
  {
    double angle = Math.toRadians(degrees);
    Vector rotation = axis.clone();
    Vector rotate = rotator.clone();
    rotation = rotation.normalize();
    
    Vector thirdaxis = rotation.crossProduct(rotate).normalize().multiply(rotate.length());
    
    return rotate.multiply(Math.cos(angle)).add(thirdaxis.multiply(Math.sin(angle)));
  }
  
  public static Vector rotateXZ(Vector vec, double theta)
  {
    Vector vec2 = vec.clone();
    double x = vec2.getX();
    double z = vec2.getZ();
    vec2.setX(x * Math.cos(Math.toRadians(theta)) - z * Math.sin(Math.toRadians(theta)));
    vec2.setZ(x * Math.sin(Math.toRadians(theta)) + z * Math.cos(Math.toRadians(theta)));
    return vec2;
  }
  
  public static void runDebug()
  {
    File debugFile = new File(plugin.getDataFolder(), "debug.txt");
    if (debugFile.exists()) {
      debugFile.delete();
    }
    writeToDebug("ProjectKorra Debug: Paste this on http://pastie.org and put it in your bug report thread.");
    writeToDebug("====================");
    writeToDebug("");
    writeToDebug("");
    writeToDebug("Date Created: " + getCurrentDate());
    writeToDebug("Bukkit Version: " + Bukkit.getServer().getVersion());
    writeToDebug("");
    writeToDebug("ProjectKorra (Core) Information");
    writeToDebug("====================");
    writeToDebug("Version: " + plugin.getDescription().getVersion());
    writeToDebug("Author: " + plugin.getDescription().getAuthors());
    if (hasRPG())
    {
      writeToDebug("");
      writeToDebug("ProjectKorra (RPG) Information");
      writeToDebug("====================");
      writeToDebug("Version: " + getRPG().getDescription().getVersion());
      writeToDebug("Author: " + getRPG().getDescription().getAuthors());
    }
    if (hasItems())
    {
      writeToDebug("");
      writeToDebug("ProjectKorra (Items) Information");
      writeToDebug("====================");
      writeToDebug("Version: " + getItems().getDescription().getVersion());
      writeToDebug("Author: " + getItems().getDescription().getAuthors());
    }
    writeToDebug("");
    writeToDebug("Ability Information");
    writeToDebug("====================");
    ArrayList<String> stockAbils = new ArrayList();
    ArrayList<String> unofficialAbils = new ArrayList();
    for (String ability : AbilityModuleManager.abilities) {
      if (StockAbility.isStockAbility(ability)) {
        stockAbils.add(ability);
      } else {
        unofficialAbils.add(ability);
      }
    }
    if (!stockAbils.isEmpty())
    {
      Collections.sort(stockAbils);
      for (String ability : stockAbils) {
        writeToDebug(ability + " - STOCK");
      }
    }
    if (!unofficialAbils.isEmpty())
    {
      Collections.sort(unofficialAbils);
      for (String ability : unofficialAbils) {
        writeToDebug(ability + " - UNOFFICAL");
      }
    }
    writeToDebug("");
    writeToDebug("Supported Plugins");
    writeToDebug("====================");
    
    boolean respectWorldGuard = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectWorldGuard");
    boolean respectPreciousStones = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectPreciousStones");
    boolean respectFactions = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectFactions");
    boolean respectTowny = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectTowny");
    boolean respectGriefPrevention = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectGriefPrevention");
    boolean respectLWC = plugin.getConfig().getBoolean("Properties.RegionProtection.RespectLWC");
    PluginManager pm = Bukkit.getPluginManager();
    
    Plugin wgp = pm.getPlugin("WorldGuard");
    Plugin psp = pm.getPlugin("PreciousStones");
    Plugin fcp = pm.getPlugin("Factions");
    Plugin twnp = pm.getPlugin("Towny");
    Plugin gpp = pm.getPlugin("GriefPrevention");
    Plugin massivecore = pm.getPlugin("MassiveCore");
    Plugin lwc = pm.getPlugin("LWC");
    if ((wgp != null) && (respectWorldGuard)) {
      writeToDebug("WorldGuard v" + wgp.getDescription().getVersion());
    }
    if ((psp != null) && (respectPreciousStones)) {
      writeToDebug("PreciousStones v" + psp.getDescription().getVersion());
    }
    if ((fcp != null) && (respectFactions)) {
      writeToDebug("Factions v" + fcp.getDescription().getVersion());
    }
    if ((massivecore != null) && (respectFactions)) {
      writeToDebug("MassiveCore v" + massivecore.getDescription().getVersion());
    }
    if ((twnp != null) && (respectTowny)) {
      writeToDebug("Towny v" + twnp.getDescription().getVersion());
    }
    if ((gpp != null) && (respectGriefPrevention)) {
      writeToDebug("GriefPrevention v" + gpp.getDescription().getVersion());
    }
    if ((lwc != null) && (respectLWC)) {
      writeToDebug("LWC v" + lwc.getDescription().getVersion());
    }
    writeToDebug("");
    writeToDebug("Plugins Hooking Into ProjectKorra (Core)");
    writeToDebug("====================");
    Plugin[] arrayOfPlugin;
    int j = (arrayOfPlugin = Bukkit.getPluginManager().getPlugins()).length;
    for (int i = 0; i < j; i++)
    {
      Plugin plugin = arrayOfPlugin[i];
      if ((plugin.getDescription().getDepend() != null) && (plugin.getDescription().getDepend().contains("ProjectKorra"))) {
        writeToDebug(plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion());
      }
    }
    writeToDebug("");
    writeToDebug("Collection Sizes");
    writeToDebug("====================");
    ClassLoader loader = ProjectKorra.class.getClassLoader();
    try
    {
      for (ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
        if (info.getName().startsWith("com.projectkorra."))
        {
          Object clazz = info.load();
          Field[] arrayOfField;
          int m = (arrayOfField = ((Class)clazz).getDeclaredFields()).length;
          for (int k = 0; k < m; k++)
          {
            Field field = arrayOfField[k];
            String simpleName = ((Class)clazz).getSimpleName();
            field.setAccessible(true);
            try
            {
              Object obj = field.get(null);
              if ((obj instanceof Collection)) {
                writeToDebug(simpleName + ": " + field.getName() + " size=" + ((Collection)obj).size());
              } else if ((obj instanceof Map)) {
                writeToDebug(simpleName + ": " + field.getName() + " size=" + ((Map)obj).size());
              }
            }
            catch (Exception localException) {}
          }
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void saveAbility(BendingPlayer bPlayer, int slot, String ability)
  {
    if (bPlayer == null) {
      return;
    }
    String uuid = bPlayer.getUUIDString();
    if (MultiAbilityManager.playerAbilities.containsKey(Bukkit.getPlayer(bPlayer.getUUID()))) {
      return;
    }
    HashMap<Integer, String> abilities = bPlayer.getAbilities();
    
    DBConnection.sql.modifyQuery("UPDATE pk_players SET slot" + slot + " = '" + (abilities.get(Integer.valueOf(slot)) == null ? null : (String)abilities.get(Integer.valueOf(slot))) + "' WHERE uuid = '" + uuid + "'");
  }
  
  public static void saveElements(BendingPlayer bPlayer)
  {
    if (bPlayer == null) {
      return;
    }
    String uuid = bPlayer.getUUIDString();
    
    StringBuilder elements = new StringBuilder();
    if (bPlayer.hasElement(Element.Air)) {
      elements.append("a");
    }
    if (bPlayer.hasElement(Element.Water)) {
      elements.append("w");
    }
    if (bPlayer.hasElement(Element.Earth)) {
      elements.append("e");
    }
    if (bPlayer.hasElement(Element.Fire)) {
      elements.append("f");
    }
    if (bPlayer.hasElement(Element.Chi)) {
      elements.append("c");
    }
    if (bPlayer.hasElement(Element.Scarecrow)) {
      elements.append("sc");
    }
    if (bPlayer.hasElement(Element.Snowman)) {
      elements.append("sn");
    }
    if (bPlayer.hasElement(Element.Sunshine)) {
        elements.append("su");
    }
    DBConnection.sql.modifyQuery("UPDATE pk_players SET element = '" + elements + "' WHERE uuid = '" + uuid + "'");
  }
  
  public static void savePermaRemoved(BendingPlayer bPlayer)
  {
    if (bPlayer == null) {
      return;
    }
    String uuid = bPlayer.getUUIDString();
    boolean permaRemoved = bPlayer.isPermaRemoved();
    DBConnection.sql.modifyQuery("UPDATE pk_players SET permaremoved = '" + (permaRemoved ? "true" : "false") + "' WHERE uuid = '" + uuid + "'");
  }
  
  public static void setVelocity(Entity entity, Vector velocity)
  {
    if ((entity instanceof TNTPrimed))
    {
      if (plugin.getConfig().getBoolean("Properties.BendingAffectFallingSand.TNT")) {
        entity.setVelocity(velocity.multiply(plugin.getConfig().getDouble("Properties.BendingAffectFallingSand.TNTStrengthMultiplier")));
      }
      return;
    }
    if ((entity instanceof FallingBlock))
    {
      if (plugin.getConfig().getBoolean("Properties.BendingAffectFallingSand.Normal")) {
        entity.setVelocity(velocity.multiply(plugin.getConfig().getDouble("Properties.BendingAffectFallingSand.NormalStrengthMultiplier")));
      }
      return;
    }
    entity.setVelocity(velocity);
  }
  
  public static FallingBlock spawnFallingBlock(Location loc, int type)
  {
    return spawnFallingBlock(loc, type, (byte)0);
  }
  
  public static FallingBlock spawnFallingBlock(Location loc, int type, byte data)
  {
    return loc.getWorld().spawnFallingBlock(loc, type, data);
  }
  
  public static FallingBlock spawnFallingBlock(Location loc, Material type)
  {
    return spawnFallingBlock(loc, type, (byte)0);
  }
  
  public static FallingBlock spawnFallingBlock(Location loc, Material type, byte data)
  {
    return loc.getWorld().spawnFallingBlock(loc, type, data);
  }
  
  public static void startCacheCleaner(final double period) {
		new BukkitRunnable() {
			public void run() {
				for (ConcurrentHashMap<Block, BlockCacheElement> map : blockProtectionCache.values()) {
					for (Iterator<Block> i = map.keySet().iterator(); i.hasNext();) {
						Block key = i.next();
						BlockCacheElement value = map.get(key);

						if (System.currentTimeMillis() - value.getTime() > period) {
							map.remove(key);
						}
					}
				}
			}
		}.runTaskTimer(ProjectKorra.plugin, 0, (long) (period / 20));
}
  
  public static void stopBending()
  {
    List<AbilityModule> abilities = AbilityModuleManager.ability;
    for (AbilityModule ab : abilities) {
      ab.stop();
    }
    HashMap<String, ComboManager.ComboAbility> combos = ComboManager.comboAbilityList;
    for (String combo : combos.keySet())
    {
      ComboManager.ComboAbility c = (ComboManager.ComboAbility)combos.get(combo);
      if ((c.getComboType() instanceof ComboAbilityModule)) {
        ((ComboAbilityModule)c.getComboType()).stop();
      }
    }
    AirMethods.stopBending();
    EarthMethods.stopBending();
    WaterMethods.stopBending();
    FireMethods.stopBending();
    ChiMethods.stopBending();
    
    Flight.removeAll();
    TempBlock.removeAll();
    MultiAbilityManager.removeAll();
    if (!invincible.isEmpty()) {
      invincible.clear();
    }
  }
  
  public static void stopPlugin()
  {
    plugin.getServer().getPluginManager().disablePlugin(plugin);
  }
  
  public static void writeToDebug(String message)
  {
    try
    {
      File dataFolder = plugin.getDataFolder();
      if (!dataFolder.exists()) {
        dataFolder.mkdir();
      }
      File saveTo = new File(plugin.getDataFolder(), "debug.txt");
      if (!saveTo.exists()) {
        saveTo.createNewFile();
      }
      FileWriter fw = new FileWriter(saveTo, true);
      PrintWriter pw = new PrintWriter(fw);
      pw.println(message);
      pw.flush();
      pw.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public ComboAbilityModule getCombo(String name)
  {
    for (ComboAbilityModule c : ComboModuleManager.combo) {
      if (name.equalsIgnoreCase(c.getName())) {
        return c;
      }
    }
    return null;
  }
  
  public static class BlockCacheElement
  {
    private Player player;
    private Block block;
    private String ability;
    private boolean allowed;
    private long time;
    
    public BlockCacheElement(Player player, Block block, String ability, boolean allowed, long time)
    {
      this.player = player;
      this.block = block;
      this.ability = ability;
      this.allowed = allowed;
      this.time = time;
    }
    
    public String getAbility()
    {
      return this.ability;
    }
    
    public Block getBlock()
    {
      return this.block;
    }
    
    public Player getPlayer()
    {
      return this.player;
    }
    
    public long getTime()
    {
      return this.time;
    }
    
    public boolean isAllowed()
    {
      return this.allowed;
    }
    
    public void setAbility(String ability)
    {
      this.ability = ability;
    }
    
    public void setAllowed(boolean allowed)
    {
      this.allowed = allowed;
    }
    
    public void setBlock(Block block)
    {
      this.block = block;
    }
    
    public void setPlayer(Player player)
    {
      this.player = player;
    }
    
    public void setTime(long time)
    {
      this.time = time;
    }
  }
  
  public static ChatColor getElementColor(Element element)
  {
    switch (element)
    {
    case Air: 
      return AirMethods.getAirColor();
    case Fire: 
      return FireMethods.getFireColor();
    case Earth: 
      return EarthMethods.getEarthColor();
    case Water: 
      return WaterMethods.getWaterColor();
    case Chi: 
      return ChiMethods.getChiColor();
    case Snowman: 
      return SnowMethods.getSnowColor();
    case Scarecrow: 
      return ScareMethods.getScareColor();
	case Sunshine:
      return SunshineMethods.getSunshineColor();
	default:
		break;
    }
    return null;
  }
}
