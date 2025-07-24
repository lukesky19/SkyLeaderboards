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
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class manages the updating of Citizen's NPC skins.
 */
public class NPCManager {
    private final SkyLeaderboards skyLeaderboards;
    private final DataManager dataManager;

    /**
     * Constructor
     * @param skyLeaderboards The Plugin's Instance
     * @param dataManager A DataLoader instance
     */
    public NPCManager(@NotNull SkyLeaderboards skyLeaderboards, @NotNull DataManager dataManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.dataManager = dataManager;
    }

    /**
     * Updates Citizen's NPC skins based on the plugin's configuration.
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

        // Loop through configured npcs to update
        data.npcs().forEach((key, npcData) -> {
            // Get the World configured and log an error message if it is null
            if(npcData.location().world() == null) {
                logger.error(AdventureUtil.serialize("The world name for for " + key + " under npcs is invalid."));
                return;
            }

            World world = skyLeaderboards.getServer().getWorld(npcData.location().world());
            if(world == null) {
                logger.error(AdventureUtil.serialize("No world found for world name " + npcData.location().world() + " for " + key + " under npcs."));
                return;
            }

            // Build the Location object for the npc data config.
            Location loc = new Location(world, npcData.location().x(), npcData.location().y(), npcData.location().z());

            // Attempt to get the NPC at the configured location from the npc data config.
            NPC npc = null;
            for(Entity entity : loc.getNearbyEntities(1, 1, 1)) {
                if(CitizensAPI.getNPCRegistry().isNPC(entity)) {
                    npc = CitizensAPI.getNPCRegistry().getNPC(entity);
                }
            }

            // If the NPC and npc placeholder is not null, attempt to update the skin
            if(npc != null && npcData.placeholder() != null) {
                // Parse the placeholder for the given npc data config.
                String skinPlayerName = PlaceholderAPIUtil.parsePlaceholders(firstPlayer, npcData.placeholder());

                // If the placeholder parsed successfully, attempt to update the npc's skin.
                if(!skinPlayerName.isEmpty() && !skinPlayerName.equals(npcData.placeholder())) {
                    // Update the NPC's skin
                    npc.getOrAddTrait(SkinTrait.class).setSkinName(skinPlayerName);
                }
            } else {
                logger.error(AdventureUtil.serialize("There was no NPC found at " +
                        npcData.location().world() +
                        " at x: " + npcData.location().x() +
                        " y: " + npcData.location().y() +
                        " z: " + npcData.location().z() +
                        "."));
            }
        });
    }
}
