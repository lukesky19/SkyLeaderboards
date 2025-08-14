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
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the plugin's locale configuration.
 */
public class LocaleManager {
    private final @NotNull SkyLeaderboards skyLeaderboards;
    private final @NotNull SettingsManager settingsManager;
    private @Nullable Locale locale;
    private final @NotNull Locale DEFAULT_LOCALE = new Locale(
            "<aqua><bold>SkyLeaderboards</bold></aqua><gray> ▪ </gray>",
            "<aqua>The plugin has been reloaded.</aqua>",
            "<red>Force updating signs, heads, and NPCs.</red>");

    /**
     * Constructor
     * @param skyLeaderboards A {@link SkyLeaderboards} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public LocaleManager(@NotNull SkyLeaderboards skyLeaderboards, @NotNull SettingsManager settingsManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.settingsManager = settingsManager;
    }

    /**
     * Get the plugin's locale.
     * Will return the DEFAULT_LOCALE if the configured locale is null.
     * @return Locale object containing the plugin's messages.
     */
    public Locale getLocale() {
        if(locale != null) return locale;

        return DEFAULT_LOCALE;
    }

    /**
     * Reloads the plugin's locale configuration.
     */
    public void reload() {
        locale = null;
        final Settings settings = settingsManager.getSettings();
        final ComponentLogger logger = skyLeaderboards.getComponentLogger();

        if(settings == null) {
            logger.error(AdventureUtil.serialize("<red>The plugin's locale config cannot be loaded due to an error with your settings.yml config.</red>"));
            return;
        }

        copyDefaultLocales();

        if(settings.locale() == null) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>Please check your server's console.</red>"));
            return;
        }

        Path path = Path.of(skyLeaderboards.getDataFolder() + File.separator + "locale" + File.separator + (settings.locale() + ".yml"));

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            locale = loader.load().get(Locale.class);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        checkLocale();
    }

    /**
     * Copies the default locale files bundled with the plugin.
     */
    private void copyDefaultLocales() {
        Path path = Path.of(skyLeaderboards.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if (!path.toFile().exists()) {
            skyLeaderboards.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    /**
     * Checks the locale for any missing messages.
     */
    private void checkLocale() {
        if(locale == null) return;

        if(locale.prefix() == null
                || locale.reload() == null
                || locale.update() == null)
            locale = null;
    }
}
