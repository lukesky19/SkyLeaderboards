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
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This classes manages the updating of Signs.
 */
public class SignManager {
    private final @NotNull SkyLeaderboards skyLeaderboards;
    private final @NotNull DataManager dataManager;

    /**
     * Constructor
     * @param skyLeaderboards The Plugin's Instance
     * @param dataManager A DataLoader instance
     */
    public SignManager(@NotNull SkyLeaderboards skyLeaderboards, @NotNull DataManager dataManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.dataManager = dataManager;
    }

    /**
     * Updates signs based on the plugin's configuration.
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

        // Loop through configured signs to update
        data.signs().forEach((key, signData) -> {
            // Get the World configured and log an error message if it is null
            if(signData.location().world() == null) {
                logger.error(AdventureUtil.serialize("The world name for for " + key + " under signs is invalid."));
                return;
            }

            World world = skyLeaderboards.getServer().getWorld(signData.location().world());
            if(world == null) {
                logger.error(AdventureUtil.serialize("No world found for world name " + signData.location().world() + " for " + key + " under signs."));
                return;
            }

            // Build the Location object for the sign data config.
            Location loc = new Location(world, signData.location().x(), signData.location().y(), signData.location().z());

            // If the BlockState at the Location is a Sign, attempt to update it
            // Otherwise we log a warning to the console.
            if(world.getBlockState(loc) instanceof Sign sign) {
                // Get the front of the sign
                SignSide frontSide = sign.getSide(Side.FRONT);
                SignSide backSide = sign.getSide(Side.BACK);

                // Update each line of the sign (front and back) based on the sign data config.
                if (signData.lines().one() != null) {
                    frontSide.line(0, AdventureUtil.serialize(firstPlayer, signData.lines().one()));
                    backSide.line(0, AdventureUtil.serialize(firstPlayer, signData.lines().one()));
                }
                if (signData.lines().two() != null) {
                    frontSide.line(1, AdventureUtil.serialize(firstPlayer, signData.lines().two()));
                    backSide.line(1, AdventureUtil.serialize(firstPlayer, signData.lines().two()));
                }
                if (signData.lines().three() != null) {
                    frontSide.line(2, AdventureUtil.serialize(firstPlayer, signData.lines().three()));
                    backSide.line(2, AdventureUtil.serialize(firstPlayer, signData.lines().three()));
                }
                if (signData.lines().four() != null) {
                    frontSide.line(3, AdventureUtil.serialize(firstPlayer, signData.lines().four()));
                    backSide.line(3, AdventureUtil.serialize(firstPlayer, signData.lines().four()));
                }

                // Update the block state
                sign.update(true);
            } else {
                logger.error(AdventureUtil.serialize("The block in world " +
                        signData.location().world() +
                        " at x: " + signData.location().x() +
                        " y: " + signData.location().y() +
                        " z: " + signData.location().z() +
                        " is not a sign."));
            }
        });
    }
}
