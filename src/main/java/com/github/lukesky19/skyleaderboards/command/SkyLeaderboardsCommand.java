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
import org.jetbrains.annotations.NotNull;

/**
 * This class creates the SkyLeaderboards command.
 */
public class SkyLeaderboardsCommand {
    private final @NotNull SkyLeaderboards skyLeaderboards;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull HeadManager headManager;
    private final @NotNull NPCManager npcManager;
    private final @NotNull SignManager signManager;
    private final @NotNull HoloManager holoManager;

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
            @NotNull SkyLeaderboards skyLeaderboards,
            @NotNull LocaleManager localeManager,
            @NotNull HeadManager headManager,
            @NotNull NPCManager npcManager,
            @NotNull SignManager signManager,
            @NotNull HoloManager holoManager) {
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
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skyleaderboards")
                .requires(ctx -> ctx.getSender().hasPermission("skyleaderboards.command.skyleaderboards"));

        builder.then(Commands.literal("reload")
                .requires(ctx -> ctx.getSender().hasPermission("skyleaderboards.command.skyleaderboards.reload"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    CommandSender sender = ctx.getSource().getSender();

                    skyLeaderboards.reload();

                    if(sender instanceof Player) {
                        sender.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.reload()));
                    } else {
                        skyLeaderboards.getComponentLogger().info(AdventureUtil.serialize(locale.reload()));
                    }

                    return 1;
                }));

        builder.then(Commands.literal("update")
                .requires(ctx -> ctx.getSender().hasPermission("skyleaderboards.command.skyleaderboards.update"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    CommandSender sender = ctx.getSource().getSender();

                    headManager.update();
                    npcManager.update();
                    signManager.update();
                    holoManager.update();

                    if(sender instanceof Player) {
                        sender.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.update()));
                    } else {
                        skyLeaderboards.getComponentLogger().info(AdventureUtil.serialize(locale.update()));
                    }

                    return 1;
                }));

        return builder.build();
    }
}