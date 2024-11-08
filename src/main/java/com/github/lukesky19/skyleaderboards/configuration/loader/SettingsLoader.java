/*
    SkyLeaderboards handles parsing PlaceholderAPI placeholders on signs, for updating heads, and for updating NPC skins (Citizens).
    Copyright (C) 2024  lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skyleaderboards.configuration.loader;

import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import com.github.lukesky19.skyleaderboards.configuration.record.Settings;
import com.github.lukesky19.skyleaderboards.util.ConfigurationUtility;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class SettingsLoader {
    final SkyLeaderboards skyLeaderboards;
    final ConfigurationUtility configurationUtility;
    Settings settingsConfig;

    public SettingsLoader(SkyLeaderboards skyLeaderboards, ConfigurationUtility configurationUtility) {
        this.skyLeaderboards = skyLeaderboards;
        this.configurationUtility = configurationUtility;
    }

    public Settings getSettingsConfig() {
        return settingsConfig;
    }

    public void reload() {
        settingsConfig = null;
        Path path = Path.of(skyLeaderboards.getDataFolder() + File.separator + "settings.yml");
        if(!path.toFile().exists()) {
            skyLeaderboards.saveResource("settings.yml", false);
        }

        YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);
        try {
            settingsConfig = loader.load().get(Settings.class);
        } catch (ConfigurateException e) {
            skyLeaderboards.setPluginState(false);
            throw new RuntimeException(e);
        }
    }
}
