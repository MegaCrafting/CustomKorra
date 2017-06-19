/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.configuration.file.FileConfiguration
 */
package com.projectkorra.projectkorra.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.command.BendingTabComplete;
import com.projectkorra.projectkorra.command.PKCommand;

public class Commands {
    private ProjectKorra plugin;
    public static Set<String> invincible = new HashSet<String>();
    public static boolean debugEnabled = false;
    public static boolean isToggledForAll = false;
    public static String[] airaliases = new String[]{"air", "a", "airbending", "airbender"};
    public static String[] chialiases = new String[]{"chi", "c", "chiblocking", "chiblocker"};
    public static String[] earthaliases = new String[]{"earth", "e", "earthbending", "earthbender"};
    public static String[] firealiases = new String[]{"fire", "f", "firebending", "firebender"};
    public static String[] wateraliases = new String[]{"water", "w", "waterbending", "waterbender"};
    public static String[] snowmanaliases = new String[]{"snowman", "sn", "snowbending", "snowbender"};
    public static String[] sunshinealiases = new String[]{"sunshine", "su", "sunshinebending", "sunshinebender"};
    public static String[] scarecrowaliases = new String[]{"scarecrow", "sc", "scarebending", "scarebender"};
    public static String[] elementaliases = new String[]{"air", "a", "airbending", "airbender", "chi", "c", "chiblocking", "chiblocker", "earth", "e", "earthbending", "earthbender", "fire", "f", "firebending", "firebender", "water", "w", "waterbending", "waterbender", "scarecrow", "sc", "scarebending", "scarebender", "snowman", "sn", "snowbending", "snowbender", "sunshine", "su", "sunshinebending", "sunshinebender"};
    public static String[] avataraliases = new String[]{"avatar", "av", "avy", "aang", "korra"};
    public static String[] aircomboaliases = new String[]{"aircombo", "ac", "aircombos", "airbendingcombos"};
    public static String[] chicomboaliases = new String[]{"chicombo", "cc", "chicombos", "chiblockingcombos", "chiblockercombos"};
    public static String[] earthcomboaliases = new String[]{"earthcombo", "ec", "earthcombos", "earthbendingcombos"};
    public static String[] firecomboaliases = new String[]{"firecombo", "fc", "firecombos", "firebendingcombos"};
    public static String[] watercomboaliases = new String[]{"watercombo", "wc", "watercombos", "waterbendingcombos"};
    public static String[] comboaliases = new String[]{"aircombo", "ac", "aircombos", "airbendingcombos", "chicombo", "cc", "chicombos", "chiblockingcombos", "chiblockercombos", "earthcombo", "ec", "earthcombos", "earthbendingcombos", "firecombo", "fc", "firecombos", "firebendingcombos", "watercombo", "wc", "watercombos", "waterbendingcombos"};
    public static String[] subelementaliases = new String[]{"flight", "fl", "spiritualprojection", "sp", "spiritual", "bloodbending", "bb", "healing", "heal", "icebending", "ice", "ib", "plantbending", "plant", "metalbending", "mb", "metal", "lavabending", "lb", "lava", "sandbending", "sb", "sand", "combustionbending", "combustion", "cb", "lightningbending", "lightning"};
    public static String[] flightaliases = new String[]{"flight", "fl"};
    public static String[] spiritualprojectionaliases = new String[]{"spiritualprojection", "sp", "spiritual"};
    public static String[] bloodaliases = new String[]{"bloodbending", "bb"};
    public static String[] healingaliases = new String[]{"healing", "heal"};
    public static String[] icealiases = new String[]{"icebending", "ice", "ib"};
    public static String[] plantaliases = new String[]{"plantbending", "plant"};
    public static String[] metalbendingaliases = new String[]{"metalbending", "mb", "metal"};
    public static String[] lavabendingaliases = new String[]{"lavabending", "lb", "lava"};
    public static String[] sandbendingaliases = new String[]{"sandbending", "sb", "sand"};
    public static String[] combustionaliases = new String[]{"combustionbending", "combustion", "cb"};
    public static String[] lightningaliases = new String[]{"lightningbending", "lightning"};
    public static String[] commandaliases = new String[]{"b", "pk", "bending", "mtla", "tla", "korra", "bend"};

    public Commands(ProjectKorra plugin) {
        this.plugin = plugin;
        debugEnabled = ProjectKorra.plugin.getConfig().getBoolean("debug");
        this.init();
    }

    private void init() {
        PluginCommand projectkorra = this.plugin.getCommand("projectkorra");
        new com.projectkorra.projectkorra.command.AddCommand();
        new com.projectkorra.projectkorra.command.BindCommand();
        new com.projectkorra.projectkorra.command.CheckCommand();
        new com.projectkorra.projectkorra.command.ChooseCommand();
        new com.projectkorra.projectkorra.command.ClearCommand();
        new com.projectkorra.projectkorra.command.DebugCommand();
        new com.projectkorra.projectkorra.command.DisplayCommand();
        new com.projectkorra.projectkorra.command.HelpCommand();
        new com.projectkorra.projectkorra.command.ImportCommand();
        new com.projectkorra.projectkorra.command.InvincibleCommand();
        new com.projectkorra.projectkorra.command.PermaremoveCommand();
        new com.projectkorra.projectkorra.command.PresetCommand();
        new com.projectkorra.projectkorra.command.ReloadCommand();
        new com.projectkorra.projectkorra.command.RemoveCommand();
        new com.projectkorra.projectkorra.command.ToggleCommand();
        new com.projectkorra.projectkorra.command.VersionCommand();
        new com.projectkorra.projectkorra.command.WhoCommand();
        CommandExecutor exe = new CommandExecutor(){

            public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
                int i = 0;
                while (i < args.length) {
                    args[i] = args[i];
                    ++i;
                }
                if (args.length == 0 && Arrays.asList(Commands.commandaliases).contains(label.toLowerCase())) {
                    s.sendMessage((Object)ChatColor.RED + "/bending help [Ability/Command] " + (Object)ChatColor.YELLOW + "Display help.");
                    s.sendMessage((Object)ChatColor.RED + "/bending choose [Element] " + (Object)ChatColor.YELLOW + "Choose an element.");
                    s.sendMessage((Object)ChatColor.RED + "/bending bind [Ability] # " + (Object)ChatColor.YELLOW + "Bind an ability.");
                    return true;
                }
                List<String> sendingArgs = Arrays.asList(args).subList(1, args.length);
                for (PKCommand command : PKCommand.instances.values()) {
                    if (!Arrays.asList(command.getAliases()).contains(args[0].toLowerCase())) continue;
                    command.execute(s, sendingArgs);
                    return true;
                }
                return true;
            }
        };
        projectkorra.setExecutor(exe);
        projectkorra.setTabCompleter((TabCompleter)new BendingTabComplete());
    }

}

