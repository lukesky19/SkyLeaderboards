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

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import com.github.lukesky19.skyleaderboards.configuration.manager.DataManager;
import com.github.lukesky19.skyleaderboards.configuration.record.Data;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.version.VersionUtil;
import com.github.lukesky19.skylib.paper.api.placeholderapi.PlaceholderAPIUtil;
import com.github.lukesky19.skylib.paper.api.player.PlayerUtil;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This classes manages the updating of Player Heads.
 */
public class HeadManager {
    private final @NonNull SkyLeaderboards skyLeaderboards;
    private final @NonNull DataManager dataManager;

    /**
     * Constructor
     * @param skyLeaderboards The Plugin's Instance
     * @param dataManager A DataManager instance
     */
    public HeadManager(@NonNull SkyLeaderboards skyLeaderboards, @NonNull DataManager dataManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.dataManager = dataManager;
    }

    /**
     * Updates player skulls based on the plugin's configuration.
     */
    public void update() {
        // If no players are online, there is no reason to update anything.
        if(skyLeaderboards.getServer().getOnlinePlayers().isEmpty()) return;

        // Get the plugin's configuration
        Data data = dataManager.getConfiguration();
        if(data == null) return;
        // Get the first player online just in-case a Placeholder requires a player to parse them.
        Player firstPlayer = skyLeaderboards.getServer().getOnlinePlayers().stream().toList().getFirst();
        // Get the ComponentLogger from the plugin.
        ComponentLogger logger = skyLeaderboards.getComponentLogger();

        // Loop through configured heads to update
        data.heads().forEach((key, headData) -> {
            // Get the World configured and log an error message if it is null
            if(headData.location().world() == null) {
                logger.error(AdventureUtility.plain("The world name for for " + key + " under heads is invalid."));
                return;
            }

            World world = skyLeaderboards.getServer().getWorld(headData.location().world());
            if(world == null) {
                logger.error(AdventureUtility.plain("No world found for world name " + headData.location().world() + " for " + key + " under heads."));
                return;
            }

            // Build the Location object for the head data config.
            Location loc = new Location(world, headData.location().x(), headData.location().y(), headData.location().z());

            // Ignore unloaded chunks
            if(!loc.isChunkLoaded()) return;

            // If the BlockState at the Location is a Skull, attempt to update it
            // Otherwise we log a warning to the console.
            if (world.getBlockState(loc) instanceof Skull skull) {
                if(headData.placeholder() == null) return;

                // Parse the placeholder for the given head data config.
                String playerName = PlaceholderAPIUtil.parsePlaceholders(firstPlayer, headData.placeholder());

                // If the placeholder parsed successfully, attempt to update the Skull.
                if (!playerName.isEmpty() && !playerName.equals(headData.placeholder())) {
                    // Get the OfflinePlayer from the player name produced from the placeholder.
                    OfflinePlayer skullPlayer = skyLeaderboards.getServer().getOfflinePlayer(playerName);
                    // Get the OfflinePlayer's UUID
                    UUID uuid = skullPlayer.getUniqueId();
                    // Get the PlayerProfile
                    CompletableFuture<PlayerProfile> future = PlayerUtil.getOrCreatePlayerProfile(uuid);

                    future.thenAccept(playerProfile -> {
                        if(VersionUtil.isLegacy()) {
                            if(VersionUtil.getMajorVersion() >= 21 && VersionUtil.getMinorVersion() >= 9) {
                                // Done sync because the API is not thread safe
                                skyLeaderboards.getServer().getScheduler().runTask(skyLeaderboards, () -> {
                                    ResolvableProfile resolvableProfile = ResolvableProfile.resolvableProfile(playerProfile);
                                    skull.setProfile(resolvableProfile);
                                    skull.update(true);
                                });
                            } else {
                                // Done sync because the API is not thread safe
                                skyLeaderboards.getServer().getScheduler().runTask(skyLeaderboards, () -> {
                                    // The deprecated usage here is to versions < 1.21.9
                                    skull.setPlayerProfile(playerProfile);
                                    skull.update(true);
                                });
                            }
                        } else {
                            // Done sync because the API is not thread safe
                            skyLeaderboards.getServer().getScheduler().runTask(skyLeaderboards, () -> {
                                ResolvableProfile resolvableProfile = ResolvableProfile.resolvableProfile(playerProfile);
                                skull.setProfile(resolvableProfile);
                                skull.update(true);
                            });
                        }
                    });
                }
            } else {
                logger.error(AdventureUtility.deserialize("The block in world " +
                        headData.location().world() +
                        " at x: " + headData.location().x() +
                        " y: " + headData.location().y() +
                        " z: " + headData.location().z() +
                        " is not a player head."));
            }
        });
    }
}