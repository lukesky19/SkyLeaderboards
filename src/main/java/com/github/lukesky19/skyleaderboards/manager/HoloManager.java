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
import com.github.lukesky19.skyleaderboards.configuration.manager.DataManager;
import com.github.lukesky19.skyleaderboards.configuration.record.Data;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.placeholderapi.PlaceholderAPIUtil;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class manages the updating of holograms.
 */
public class HoloManager {
    private final @NotNull SkyLeaderboards skyLeaderboards;
    private final @NotNull DataManager dataManager;

    /**
     * Constructor
     * @param skyLeaderboards A {@link SkyLeaderboards} instance.
     * @param dataManager A {@link DataManager} instance.
     */
    public HoloManager(
            @NotNull SkyLeaderboards skyLeaderboards,
            @NotNull DataManager dataManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.dataManager = dataManager;
    }

    /**
     * Updates holograms based on the plugin's configuration.
     */
    public void update() {
        // If no players are online, there is no reason to update anything.
        if(skyLeaderboards.getServer().getOnlinePlayers().isEmpty()) return;

        // Get the plugin's configuration
        Data data = dataManager.getData();
        if(data == null) return;
        // Get the first player online just in-case a Placeholder requires a player to parse them.
        Player firstPlayer = skyLeaderboards.getServer().getOnlinePlayers().stream().toList().getFirst();
        // Get the ComponentLogger from the plugin.
        ComponentLogger logger = skyLeaderboards.getComponentLogger();

        // Loop through configured holograms to update
        data.holos().forEach((key, holoData) -> {
            if(holoData.hologramId() != null) {
                Hologram hologram = DHAPI.getHologram(holoData.hologramId());
                if (hologram != null) {
                    HologramPage hologramPage = hologram.getPages().getFirst();
                    if(hologramPage != null) {
                        for(int i = 0; i <= holoData.lines().size() - 1; i++) {
                            String line = holoData.lines().get(i);
                            hologramPage.setLine(i, PlaceholderAPIUtil.parsePlaceholders(firstPlayer, line));
                        }
                    } else {
                        logger.error(AdventureUtil.serialize("No hologram page found for id " + holoData.hologramId() + " for key " + key));
                    }
                } else {
                    logger.error(AdventureUtil.serialize("No hologram found for id " + holoData.hologramId() + " for key " + key));
                }
            } else {
                logger.error(AdventureUtil.serialize("Unable to update hologram due to null id for key " + key));
            }
        });
    }
}
