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
package com.github.lukesky19.skyleaderboards.manager;

import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * This class manages the task that updates leaderboards every 5 minutes.
 */
public class TaskManager {
    private final @NonNull SkyLeaderboards skyLeaderboards;
    private final @NonNull HeadManager headManager;
    private final @NonNull NPCManager npcManager;
    private final @NonNull SignManager signManager;
    private final @NonNull HoloManager holoManager;
    private @Nullable BukkitTask task;

    /**
     * Constructor
     * @param skyLeaderboards A {@link SkyLeaderboards} instance.
     * @param headManager A {@link HeadManager} instance.
     * @param npcManager A {@link NPCManager} instance.
     * @param signManager A {@link SignManager} instance.
     * @param holoManager A {@link HoloManager} instance.
     */
    public TaskManager(
            @NonNull SkyLeaderboards skyLeaderboards,
            @NonNull HeadManager headManager,
            @NonNull NPCManager npcManager,
            @NonNull SignManager signManager,
            @NonNull HoloManager holoManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.headManager = headManager;
        this.npcManager = npcManager;
        this.signManager = signManager;
        this.holoManager = holoManager;
    }

    /**
     * Start the {@link BukkitTask} that updates leaderboards every 5 minutes.
     */
    public void startUpdateTask() {
        task = skyLeaderboards.getServer().getScheduler().runTaskTimer(skyLeaderboards, () -> {
            headManager.update();
            npcManager.update();
            signManager.update();
            holoManager.update();
        }, 1L, 20L * 300);
    }

    /**
     * Stops the {@link BukkitTask} that updates leaderboards every 5 minutes.
     */
    public void stopUpdateTask() {
        if(task == null) return;
        if(task.isCancelled()) return;

        task.cancel();
        task = null;
    }
}