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
package com.github.lukesky19.skyleaderboards.command;

import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import com.github.lukesky19.skyleaderboards.configuration.manager.LocaleManager;
import com.github.lukesky19.skyleaderboards.configuration.record.Locale;
import com.github.lukesky19.skyleaderboards.manager.HeadManager;
import com.github.lukesky19.skyleaderboards.manager.HoloManager;
import com.github.lukesky19.skyleaderboards.manager.NPCManager;
import com.github.lukesky19.skyleaderboards.manager.SignManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * This class creates the SkyLeaderboards command.
 */
public class SkyLeaderboardsCommand {
    private final @NonNull SkyLeaderboards skyLeaderboards;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull HeadManager headManager;
    private final @NonNull NPCManager npcManager;
    private final @NonNull SignManager signManager;
    private final @NonNull HoloManager holoManager;

    /**
     * Constructor
     * @param skyLeaderboards A {@link SkyLeaderboards} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param headManager A {@link HeadManager} instance.
     * @param npcManager A {@link NPCManager} instance.
     * @param signManager A {@link SignManager} instance.
     * @param holoManager A {@link HoloManager} instance.
     */
    public SkyLeaderboardsCommand(
            @NonNull SkyLeaderboards skyLeaderboards,
            @NonNull LocaleManager localeManager,
            @NonNull HeadManager headManager,
            @NonNull NPCManager npcManager,
            @NonNull SignManager signManager,
            @NonNull HoloManager holoManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.localeManager = localeManager;
        this.headManager = headManager;
        this.npcManager = npcManager;
        this.signManager = signManager;
        this.holoManager = holoManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the skyleaderboards command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skyleaderboards")
                .requires(ctx -> ctx.getSender().hasPermission("skyleaderboards.command.skyleaderboards"));

        builder.then(Commands.literal("reload")
                .requires(ctx -> ctx.getSender().hasPermission("skyleaderboards.command.skyleaderboards.reload"))
                .executes(ctx -> {
                    Locale locale = localeManager.getConfiguration();
                    CommandSender sender = ctx.getSource().getSender();

                    skyLeaderboards.reload();

                    if(sender instanceof Player) {
                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.reload()));
                    } else {
                        sender.sendMessage(AdventureUtil.deserialize(locale.reload()));
                    }

                    return 1;
                }));

        builder.then(Commands.literal("update")
                .requires(ctx -> ctx.getSender().hasPermission("skyleaderboards.command.skyleaderboards.update"))
                .executes(ctx -> {
                    Locale locale = localeManager.getConfiguration();
                    CommandSender sender = ctx.getSource().getSender();

                    headManager.update();
                    npcManager.update();
                    signManager.update();
                    holoManager.update();

                    if(sender instanceof Player) {
                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.update()));
                    } else {
                        sender.sendMessage(AdventureUtil.deserialize(locale.update()));
                    }

                    return 1;
                }));

        return builder.build();
    }
}