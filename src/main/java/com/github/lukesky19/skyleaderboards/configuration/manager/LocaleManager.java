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
import com.github.lukesky19.skyleaderboards.configuration.record.Locale;
import com.github.lukesky19.skyleaderboards.configuration.record.Settings;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the plugin's locale configuration.
 */
public class LocaleManager extends SimpleConfigManager<Locale> {
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull Locale DEFAULT_LOCALE = new Locale(
            1,
            "<aqua><bold>SkyLeaderboards</bold></aqua><gray> ▪ </gray>",
            "<aqua>The plugin has been reloaded.</aqua>",
            "<red>Force updating signs, heads, and NPCs.</red>");

    /**
     * Constructor
     * @param skyLeaderboards A {@link SkyLeaderboards} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public LocaleManager(@NonNull SkyLeaderboards skyLeaderboards, @NonNull SettingsManager settingsManager) {
        super(skyLeaderboards, Locale.class);
        this.settingsManager = settingsManager;
    }

    /**
     * Gets the plugin's locale if not null or the default locale otherwise.
     * @return The plugin's locale if not null or the default locale otherwise.
     */
    @Override
    public @NonNull Locale getConfiguration() {
        if(configuration == null) return DEFAULT_LOCALE;
        return configuration;
    }

    @Override
    public void loadConfiguration() {
        Settings settings = settingsManager.getConfiguration();
        if(settings == null) {
            logger.error(AdventureUtility.plain("Failed to load plugin's locale due to plugin settings being null."));
            return;
        }
        if(settings.locale() == null) {
            logger.error(AdventureUtility.plain("Failed to load plugin's locale to use in settings.yml is null."));
            return;
        }

        String localeString = settings.locale();
        Path path = Path.of(plugin.getDirectoryFile() + File.separator + "locale" + File.separator + (localeString + ".yml"));
        setConfigurationPath(path);

        super.loadConfiguration();
    }

    @Override
    public void saveDefaultConfiguration() {
        Path path = Path.of(plugin.getDirectoryFile() + File.separator + "locale" + File.separator + "en_US.yml");
        if(!path.toFile().exists()) {
            plugin.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    /**
     * Migrate the locale.
     * @param locale The {@link Locale} to migrate.
     * @return The migrated {@link Locale} or null if migration failed.
     */
    @Override
    public @Nullable Locale migrateConfiguration(@NonNull Locale locale) {
        if(locale.version() == 0) {
            return new Locale(
                    1,
                    locale.prefix(),
                    locale.reload(),
                    locale.update());
        }

        return locale;
    }

    /**
     * Validates if the locale is missing any strings.
     */
    @Override
    public boolean validateConfiguration(@Nullable Locale configuration) {
        if(configuration == null) return false;

        if(configuration.prefix() == null
                || configuration.reload() == null
                || configuration.update() == null) {
            logger.error(AdventureUtility.plain("Your locale is missing one of the plugin's messages. The default locale will be used."));
            logger.info(AdventureUtility.plain("You can regenerate your locale file by deleting it or adding the missing messages to resolve the issue."));

            return false;
        }

        return true;
    }
}