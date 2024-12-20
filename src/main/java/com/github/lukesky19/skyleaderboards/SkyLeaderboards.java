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
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SkyLeaderboards extends JavaPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private DataManager dataManager;
    private TaskManager taskManager;

    @Override
    public void onEnable() {
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

    @Override
    public void onDisable() {
        taskManager.stopUpdateTask();
    }

    public void reload() {
        this.settingsManager.reload();
        this.localeManager.reload();
        this.dataManager.reload();
    }
}
