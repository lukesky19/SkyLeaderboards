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
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.Nullable;
import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the plugin's configuration for displaying leaderboards.
 */
public class DataManager extends SimpleConfigManager<Data> {
    /**
     * Constructor
     * @param skyLeaderboards A {@link SkyLeaderboards} instance.
     */
    public DataManager(@NonNull SkyLeaderboards skyLeaderboards) {
        super(skyLeaderboards, Path.of(skyLeaderboards.getDataFolder() + File.separator + "data.yml"), Data.class);
    }

    @Override
    public void saveDefaultConfiguration() {
        plugin.saveResource("settings.yml", false);
    }

    @Override
    public @Nullable Data migrateConfiguration(@NonNull Data data) {
        if(data.version() == 0) {
            return new Data(
                    1,
                    data.heads(),
                    data.signs(),
                    data.npcs(),
                    data.holos());
        }

        return data;
    }

    @Override
    public boolean validateConfiguration(@Nullable Data configuration) {
        return configuration != null;
    }
}