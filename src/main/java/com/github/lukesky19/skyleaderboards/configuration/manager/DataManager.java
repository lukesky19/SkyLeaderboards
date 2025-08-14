/*
    SkyLeaderboards handles parsing PlaceholderAPI placeholders on signs, holograms, for updating heads, and for updating NPC skins (Citizens).
    Copyright (C) 2024 lukeskywlker19

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
package com.github.lukesky19.skyleaderboards.configuration.manager;

import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import com.github.lukesky19.skyleaderboards.configuration.record.Data;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the plugin's configuration for displaying leaderboards.
 */
public class DataManager {
    private final @NotNull SkyLeaderboards skyLeaderboards;
    private @Nullable Data data;

    /**
     * Constructor
     * @param skyLeaderboards A {@link SkyLeaderboards} instance.
     */
    public DataManager(@NotNull SkyLeaderboards skyLeaderboards) {
        this.skyLeaderboards = skyLeaderboards;
    }

    /**
     * Get the {@link Data} loaded for leaderboards.
     * @return The loaded {@link Data} or null.
     */
    public @Nullable Data getData() {
        return data;
    }

    /**
     * Reloads the plugin's data for leaderboards to display.
     */
    public void reload() {
        data = null;
        Path path = Path.of(skyLeaderboards.getDataFolder() + File.separator + "data.yml");
        if(!path.toFile().exists()) {
            skyLeaderboards.saveResource("data.yml", false);
        }

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            data = loader.load().get(Data.class);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }
}
