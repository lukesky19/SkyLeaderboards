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
package com.github.lukesky19.skyleaderboards.command;

import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import com.github.lukesky19.skyleaderboards.configuration.loader.LocaleManager;
import com.github.lukesky19.skyleaderboards.configuration.record.Locale;
import com.github.lukesky19.skyleaderboards.manager.HeadManager;
import com.github.lukesky19.skyleaderboards.manager.NPCManager;
import com.github.lukesky19.skyleaderboards.manager.SignManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyLeaderboardsCommand {
    private final SkyLeaderboards skyLeaderboards;
    private final LocaleManager localeManager;
    private final HeadManager headManager;
    private final NPCManager npcManager;
    private final SignManager signManager;

    public SkyLeaderboardsCommand(
            SkyLeaderboards skyLeaderboards,
            LocaleManager localeManager,
            HeadManager headManager,
            NPCManager npcManager,
            SignManager signManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.localeManager = localeManager;
        this.headManager = headManager;
        this.npcManager = npcManager;
        this.signManager = signManager;
    }

    public LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skyleaderboards");

        builder.then(Commands.literal("reload")
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    CommandSender sender = ctx.getSource().getSender();

                    if(sender instanceof Player) {
                        if (sender.hasPermission("skyleaderboards.command.skyleaderboards")
                                && sender.hasPermission("skyleaderboards.command.skyleaderboards.reload")) {
                            skyLeaderboards.reload();

                            locale = localeManager.getLocale();

                            sender.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.reload()));

                            return 1;
                        } else {
                            sender.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.noPermission()));

                            return 0;
                        }
                    } else {
                        skyLeaderboards.reload();

                        locale = localeManager.getLocale();

                        skyLeaderboards.getComponentLogger().info(AdventureUtil.serialize(locale.reload()));

                        return 1;
                    }
                }));

        builder.then(Commands.literal("update")
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    CommandSender sender = ctx.getSource().getSender();

                    if(sender instanceof Player) {
                        if (sender.hasPermission("skyleaderboards.command.skyleaderboards")
                                && sender.hasPermission("skyleaderboards.command.skyleaderboards.update")) {
                            headManager.update();
                            npcManager.update();
                            signManager.update();

                            sender.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.update()));

                            return 1;
                        } else {
                            sender.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.noPermission()));

                            return 0;
                        }
                    } else {
                        headManager.update();
                        npcManager.update();
                        signManager.update();

                        skyLeaderboards.getComponentLogger().info(AdventureUtil.serialize(locale.update()));

                        return 1;
                    }
                }));

        return builder.build();
    }
}