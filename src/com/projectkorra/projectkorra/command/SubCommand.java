/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package com.projectkorra.projectkorra.command;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface SubCommand {
    public String getName();

    public String[] getAliases();

    public String getProperUse();

    public String getDescription();

    public void help(CommandSender var1, boolean var2);

    public void execute(CommandSender var1, List<String> var2);
}

