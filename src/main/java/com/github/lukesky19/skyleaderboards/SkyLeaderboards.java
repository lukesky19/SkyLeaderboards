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
package com.github.lukesky19.skyleaderboards;

import com.github.lukesky19.skyleaderboards.command.SkyLeaderboardsCommand;
import com.github.lukesky19.skyleaderboards.configuration.loader.DataManager;
import com.github.lukesky19.skyleaderboards.configuration.loader.LocaleManager;
import com.github.lukesky19.skyleaderboards.configuration.loader.SettingsManager;
import com.github.lukesky19.skyleaderboards.manager.HeadManager;
import com.github.lukesky19.skyleaderboards.manager.NPCManager;
import com.github.lukesky19.skyleaderboards.manager.SignManager;
import com.github.lukesky19.skyleaderboards.manager.TaskManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

// TODO Add missing javadocs
/**
 * The plugin's main class.
 */
public final class SkyLeaderboards extends JavaPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private DataManager dataManager;
    private TaskManager taskManager;

    /**
     * The method ran when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        if(!checkSkyLibVersion()) return;

        this.settingsManager = new SettingsManager(this);
        this.localeManager = new LocaleManager(this, this.settingsManager);
        this.dataManager = new DataManager(this);

        HeadManager headManager = new HeadManager(this, localeManager, dataManager);
        NPCManager npcManager = new NPCManager(this, localeManager, dataManager);
        SignManager signManager = new SignManager(this, localeManager, dataManager);
        taskManager = new TaskManager(this, headManager, npcManager, signManager);

        SkyLeaderboardsCommand skyLeaderboardsCommand = new SkyLeaderboardsCommand(this, localeManager, headManager, npcManager, signManager);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands ->
                commands.registrar().register(skyLeaderboardsCommand.createCommand(),
                        "Command to manage the skyleaderboards plugin.", List.of("skyleaderboard", "leaderboard", "sklb")));

        reload();

        taskManager.startUpdateTask();
    }

    /**
     * The method ran when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        if(taskManager != null) taskManager.stopUpdateTask();
    }

    /**
     * The method ran to reload the plugin.
     */
    public void reload() {
        this.settingsManager.reload();
        this.localeManager.reload();
        this.dataManager.reload();
    }

    /**
     * Checks if the Server has the proper SkyLib version.
     * @return true if it does, false if not.
     */
    private boolean checkSkyLibVersion() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        Plugin skyLib = pluginManager.getPlugin("SkyLib");
        if (skyLib != null) {
            String version = skyLib.getPluginMeta().getVersion();
            String[] splitVersion = version.split("\\.");
            int second = Integer.parseInt(splitVersion[1]);

            if(second >= 3) {
                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtil.serialize("SkyLib Version 1.3.0.0 or newer is required to run this plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }
}
