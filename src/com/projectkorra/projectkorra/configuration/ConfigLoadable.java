/*
 * Decompiled with CFR 0_118.
 */
package com.projectkorra.projectkorra.configuration;

import com.projectkorra.projectkorra.configuration.Config;
import com.projectkorra.projectkorra.configuration.ConfigManager;

public interface ConfigLoadable {
    public static final Config config = ConfigManager.defaultConfig;

    public void reloadVariables();
}

