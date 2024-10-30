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
package com.github.lukesky19.skymodels;

import com.github.lukesky19.skymodels.command.SkyLeaderboardsCommand;
import com.github.lukesky19.skymodels.configuration.loader.DataLoader;
import com.github.lukesky19.skymodels.configuration.loader.LocaleLoader;
import com.github.lukesky19.skymodels.configuration.loader.SettingsLoader;
import com.github.lukesky19.skymodels.util.ConfigurationUtility;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SkyLeaderboards extends JavaPlugin {
    private SettingsLoader settingsLoader;
    private LocaleLoader localeLoader;
    private DataLoader dataLoader;
    private DataManager dataManager;

    Boolean pluginState = true;

    public void setPluginState(Boolean pluginState) {
        this.pluginState = pluginState;
    }

    public Boolean isPluginEnabled() {
        return this.pluginState;
    }

    @Override
    public void onEnable() {
        ConfigurationUtility configurationUtility = new ConfigurationUtility();

        this.settingsLoader = new SettingsLoader(this, configurationUtility);
        this.localeLoader = new LocaleLoader(this, configurationUtility, this.settingsLoader);
        this.dataLoader = new DataLoader(this, configurationUtility);
        this.dataManager = new DataManager(this, dataLoader);
        SkyLeaderboardsCommand skyLeaderboardsCommand = new SkyLeaderboardsCommand(this, localeLoader, dataManager);

        Objects.requireNonNull(Bukkit.getPluginCommand("skyleaderboards")).setExecutor(skyLeaderboardsCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("slb")).setExecutor(skyLeaderboardsCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("skyleaderboards")).setTabCompleter(skyLeaderboardsCommand);
        Objects.requireNonNull(Bukkit.getPluginCommand("slb")).setTabCompleter(skyLeaderboardsCommand);

        reload();
        dataManager.startUpdateTask();
    }

    @Override
    public void onDisable() {
        dataManager.stopUpdateTask();
    }

    public void reload() {
        pluginState = true;
        this.settingsLoader.reload();
        this.localeLoader.reload();
        this.dataLoader.reload();
    }
}
