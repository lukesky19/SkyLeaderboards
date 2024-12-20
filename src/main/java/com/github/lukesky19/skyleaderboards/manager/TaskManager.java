package com.github.lukesky19.skyleaderboards.manager;

import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {
    private final SkyLeaderboards skyLeaderboards;
    private final HeadManager headManager;
    private final NPCManager npcManager;
    private final SignManager signManager;
    private BukkitTask task;

    public TaskManager(SkyLeaderboards skyLeaderboards, HeadManager headManager, NPCManager npcManager, SignManager signManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.headManager = headManager;
        this.npcManager = npcManager;
        this.signManager = signManager;
    }

    public void startUpdateTask() {
        task = skyLeaderboards.getServer().getScheduler().runTaskTimer(skyLeaderboards, () -> {
            headManager.update();
            npcManager.update();
            signManager.update();
        }, 1L, 20L * 300);
    }

    public void stopUpdateTask() {
        if(!task.isCancelled()) {
            task.cancel();
        }
    }
}
