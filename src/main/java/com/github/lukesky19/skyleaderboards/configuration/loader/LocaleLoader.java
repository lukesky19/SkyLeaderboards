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
import com.github.lukesky19.skyleaderboards.util.ConfigurationUtility;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;

public class LocaleLoader {
    final SkyLeaderboards skyLeaderboards;
    final SettingsLoader settingsLoader;
    final ConfigurationUtility configurationUtility;
    Locale locale;
    private final Locale defaultLocale = new Locale(
            "<aqua><bold>SkyLeaderboards</bold></aqua><gray> ▪ </gray>",
            "<aqua>The plugin has been reloaded.</aqua>",
            "<red>The plugin failed to reload due to a config error.</red>",
            "<red>Force updating signs, heads, and NPCs.</red>",
            "<red>You do not have permission for this command.<red>",
            "<red>Unknown argument.</red>");

    public LocaleLoader(SkyLeaderboards skyLeaderboards, ConfigurationUtility configurationUtility, SettingsLoader settingsLoader) {
        this.skyLeaderboards = skyLeaderboards;
        this.configurationUtility = configurationUtility;
        this.settingsLoader = settingsLoader;
    }

    public Locale getLocale() {
        return locale;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void reload() {
        locale = null;
        ComponentLogger logger = skyLeaderboards.getComponentLogger();

        if(!skyLeaderboards.isPluginEnabled()) {
            logger.error(MiniMessage.miniMessage().deserialize("<red>The locale config cannot be loaded due to a previous plugin error.</red>"));
            logger.error(MiniMessage.miniMessage().deserialize("<red>Please check your server's console.</red>"));
            return;
        }

        copyDefaultLocales();

        String localeString = settingsLoader.getSettingsConfig().locale();
        Path path = Path.of(skyLeaderboards.getDataFolder() + File.separator + "locale" + File.separator + (localeString + ".yml"));

        YamlConfigurationLoader loader = configurationUtility.getYamlConfigurationLoader(path);
        try {
            locale = loader.load().get(Locale.class);
        } catch (ConfigurateException e) {
            skyLeaderboards.setPluginState(false);
            throw new RuntimeException(e);
        }
    }

    private void copyDefaultLocales() {
        Path path = Path.of(skyLeaderboards.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if (!path.toFile().exists()) {
            skyLeaderboards.saveResource("locale/en_US.yml", false);
        }
    }
}
