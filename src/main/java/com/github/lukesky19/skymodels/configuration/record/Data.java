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
package com.github.lukesky19.skymodels.configuration.record;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;

@ConfigSerializable
public record Data(HashMap<Integer, Head> heads, HashMap<Integer, Sign> signs, HashMap<Integer, NPC> npcs) {
    @ConfigSerializable
    public record Head(Location location, String placeholder) {}
    @ConfigSerializable
    public record Sign(Location location, String placeholder, Lines lines) {}
    @ConfigSerializable
    public record NPC(Location location, String placeholder) {}

    @ConfigSerializable
    public record Location(
            String world,
            Double x,
            Double y,
            Double z) {}

    @ConfigSerializable
    public record Lines(
            String one,
            String two,
            String three,
            String four) {}
}
