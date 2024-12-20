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
import com.github.lukesky19.skyleaderboards.configuration.record.Locale;
import com.github.lukesky19.skyleaderboards.configuration.record.Settings;
import com.github.lukesky19.skylib.config.ConfigurationUtility;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.nio.file.Path;

public class LocaleManager {
    private final SkyLeaderboards skyLeaderboards;
    private final SettingsManager settingsManager;
    private Locale locale;
    private final Locale DEFAULT_LOCALE = new Locale(
            "<aqua><bold>SkyLeaderboards</bold></aqua><gray> ▪ </gray>",
            "<aqua>The plugin has been reloaded.</aqua>",
            "<red>The plugin failed to reload due to a config error.</red>",
            "<red>Force updating signs, heads, and NPCs.</red>",
            "<red>You do not have permission for this command.<red>",
            "<red>The world name for <yellow><id></yellow> under <yellow><type</yellow> is invalid.</red>",
            "<red>The block in <yellow><world></yellow> at <yellow><x> <y> <z></yellow> is not a <type>.</red>",
            "<red>There was no NPC found at <yellow><x> <y> <z></yellow></red>");

    public LocaleManager(SkyLeaderboards skyLeaderboards, SettingsManager settingsManager) {
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
        final Settings settings = settingsManager.getSettingsConfig();
        final ComponentLogger logger = skyLeaderboards.getComponentLogger();

        if(settings == null) {
            logger.error(FormatUtil.format("<red>The plugin's locale config cannot be loaded due to an error with your settings.yml config.</red>"));
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
                || locale.reloadError() == null
                || locale.update() == null
                || locale.noPermission() == null
                || locale.invalidWorld() == null
                || locale.invalidBlock() == null
                || locale.invalidNpc() == null)
            locale = null;
    }
}
