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
package com.github.lukesky19.skymodels.configuration.loader;

import com.github.lukesky19.skymodels.SkyLeaderboards;
import com.github.lukesky19.skymodels.configuration.record.Data;
import com.github.lukesky19.skymodels.util.ConfigurationUtility;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class DataLoader {
    final SkyLeaderboards skyLeaderboards;
    final ConfigurationUtility configurationUtility;
    Data data;

    public DataLoader(SkyLeaderboards skyLeaderboards, ConfigurationUtility configurationUtility) {
        this.skyLeaderboards = skyLeaderboards;
        this.configurationUtility = configurationUtility;
    }

    public Data getData() {
        return data;
    }

    public void reload() {
        data = null;
        Path path = Path.of(skyLeaderboards.getDataFolder() + File.separator + "data.yml");
        if(!path.toFile().exists()) {
            skyLeaderboards.saveResource("data.yml", false);
        }

        YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);
        try {
            data = loader.load().get(Data.class);
        } catch (ConfigurateException e) {
            skyLeaderboards.setPluginState(false);
            throw new RuntimeException(e);
        }
    }
}
