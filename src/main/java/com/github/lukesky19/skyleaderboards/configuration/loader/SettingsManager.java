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
import com.github.lukesky19.skylib.config.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;

import javax.annotation.CheckForNull;
import java.io.File;
import java.nio.file.Path;

public class SettingsManager {
    final SkyLeaderboards skyLeaderboards;
    Settings settingsConfig;

    public SettingsManager(SkyLeaderboards skyLeaderboards) {
        this.skyLeaderboards = skyLeaderboards;
    }

    @CheckForNull
    public Settings getSettingsConfig() {
        return settingsConfig;
    }

    public void reload() {
        settingsConfig = null;

        Path path = Path.of(skyLeaderboards.getDataFolder() + File.separator + "settings.yml");
        if(!path.toFile().exists()) {
            skyLeaderboards.saveResource("settings.yml", false);
        }

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            settingsConfig = loader.load().get(Settings.class);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }
}
