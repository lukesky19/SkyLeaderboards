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
package com.github.lukesky19.skyleaderboards.configuration.record;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * This record contains the data to update leaderboards with.
 * @param heads The {@link Map} of {@link Integer} to {@link Head} configuration for head-based leaderboards.
 * @param signs The {@link Map} of {@link Integer} to {@link Sign} configuration for sign-based leaderboards.
 * @param npcs The {@link Map} of {@link Integer} to {@link NPC} configuration for npc-based leaderboards.
 * @param holos The {@link Map} of {@link Integer} to {@link Holo} configuration for hologram-based leaderboards.
 */
@ConfigSerializable
public record Data(
        @NotNull Map<Integer, Head> heads,
        @NotNull Map<Integer, Sign> signs,
        @NotNull Map<Integer, NPC> npcs,
        @NotNull Map<Integer, Holo> holos) {
    /**
     * The configuration for a head leaderboard.
     * @param location The {@link Location} config for the head.
     * @param placeholder The placeholder that results in a player name to set the head's texture to.
     */
    @ConfigSerializable
    public record Head(@NotNull Location location, @Nullable String placeholder) {}

    /**
     * The configuration for a sign leaderboard.
     * @param location The {@link Location} config for the sign.
     * @param lines The {@link Lines} config for the sign.
     */
    @ConfigSerializable
    public record Sign(@NotNull Location location, @NotNull Lines lines) {}

    /**
     * The configuration for an NPC leaderboard.
     * @param location The {@link Location} config of the NPC.
     * @param placeholder The placeholder that results in a player name to set the NPC's skin to.
     */
    @ConfigSerializable
    public record NPC(@NotNull Location location, @Nullable String placeholder) {}

    /**
     * The configuration for a hologram leaderboard.
     * @param hologramId The name of the hologram.
     * @param lines The lines to set for the hologram.
     */
    @ConfigSerializable
    public record Holo(
            @Nullable String hologramId,
            @NotNull List<String> lines) {}

    /**
     * This record contains the data to create a {@link org.bukkit.Location}.
     * @param world The world name.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     */
    @ConfigSerializable
    public record Location(
            @Nullable String world,
            double x,
            double y,
            double z) {}

    /**
     * This record contains the data to populate a sign with.
     * @param one Line one's text.
     * @param two Line two's text.
     * @param three Line three's text.
     * @param four Line four's text.
     */
    @ConfigSerializable
    public record Lines(
            @Nullable String one,
            @Nullable String two,
            @Nullable String three,
            @Nullable String four) {}
}
