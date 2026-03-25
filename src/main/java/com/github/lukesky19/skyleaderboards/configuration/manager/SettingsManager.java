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

import com.github.lukesky19.skyleaderboards.configuration.record.Settings;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import com.github.lukesky19.skylib.api.common.abstracts.config.SimpleConfigManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the plugin's settings.
 */
public class SettingsManager extends SimpleConfigManager<Settings> {
    /**
     * Constructor
     * @param plugin A {@link SkyPlugin}.
     */
    public SettingsManager(@NonNull SkyPlugin plugin) {
        super(plugin, Path.of(plugin.getDataFolder() + File.separator + "settings.yml"), Settings.class);
    }

    @Override
    public void saveBundledConfig() {
        plugin.saveResource("settings.yml", false);
    }

    @Override
    public @Nullable Settings migrateConfiguration(@NonNull Settings settings) {
        if(settings.version() == 0) {
            return new Settings(1, settings.locale());
        }

        return settings;
    }

    @Override
    public boolean validateConfiguration(@Nullable Settings configuration) {
        if(configuration == null) return false;

        if(configuration.locale() == null) {
            logger.error(AdventureUtil.deserialize("Your settings.yml is missing a defined locale."));
            logger.info(AdventureUtil.deserialize("You can regenerate your settings file by deleting it or defining the locale to use to resolve the issue."));

            return false;
        }

        return true;
    }
}