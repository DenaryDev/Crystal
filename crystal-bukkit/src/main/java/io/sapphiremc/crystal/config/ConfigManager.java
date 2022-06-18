/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.config;

import io.sapphiremc.crystal.CrystalPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@RequiredArgsConstructor
public class ConfigManager {

    private final CrystalPlugin plugin;

    public FileConfiguration getConfig(String fileName) {
        return getConfig(new File(plugin.getDataFolder(), fileName));
    }

    public FileConfiguration getConfig(File file) {
        String fileName = file.getName();

        FileConfiguration config = new YamlConfiguration();
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }

        try {
            config.load(file);
            return config;
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Cannot read configuration " + file.getPath(), ex);
        } catch (InvalidConfigurationException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Detected invalid configuration in file: " + file.getPath(), ex);
        }

        return null;
    }

    public void saveConfig(FileConfiguration config, String name) {
        try {
            config.save(new File(plugin.getDataFolder(), name));
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Could not save config " + name, ex);
        }
    }
}