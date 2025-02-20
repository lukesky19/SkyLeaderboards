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
package com.github.lukesky19.skyleaderboards.configuration.record;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;

@ConfigSerializable
public record Data(HashMap<Integer, Head> heads, HashMap<Integer, Sign> signs, HashMap<Integer, NPC> npcs) {
    @ConfigSerializable
    public record Head(Location location, @Nullable String placeholder) {}
    @ConfigSerializable
    public record Sign(Location location, Lines lines) {}
    @ConfigSerializable
    public record NPC(Location location, @Nullable String placeholder) {}

    @ConfigSerializable
    public record Location(
            @Nullable String world,
            double x,
            double y,
            double z) {}

    @ConfigSerializable
    public record Lines(
            @Nullable String one,
            @Nullable String two,
            @Nullable String three,
            @Nullable String four) {}
}
