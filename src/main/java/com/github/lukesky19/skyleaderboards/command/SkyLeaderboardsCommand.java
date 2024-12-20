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

import com.github.lukesky19.skyleaderboards.DataManager;
import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import com.github.lukesky19.skyleaderboards.configuration.loader.LocaleLoader;
import com.github.lukesky19.skyleaderboards.configuration.record.Locale;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyLeaderboardsCommand {
    private final SkyLeaderboards skyLeaderboards;
    private final LocaleLoader localeLoader;
    private final DataManager dataManager;

    public SkyLeaderboardsCommand(SkyLeaderboards skyLeaderboards, LocaleLoader localeLoader, DataManager dataManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.localeLoader = localeLoader;
        this.dataManager = dataManager;
    }

    public LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skyleaderboards");

        builder.then(Commands.literal("reload")
                .executes(ctx -> {
                    Locale locale = localeLoader.getLocale();
                    CommandSender sender = ctx.getSource().getSender();

                    if(sender instanceof Player) {
                        if (sender.hasPermission("skyleaderboards.command.skyleaderboards")
                                && sender.hasPermission("skyleaderboards.command.skyleaderboards.reload")) {
                            skyLeaderboards.reload();

                            locale = localeLoader.getLocale();

                            sender.sendMessage(FormatUtil.format(locale.prefix() + locale.reload()));

                            return 1;
                        } else {
                            sender.sendMessage(FormatUtil.format(locale.prefix() + locale.noPermission()));

                            return 0;
                        }
                    } else {
                        skyLeaderboards.reload();

                        locale = localeLoader.getLocale();

                        skyLeaderboards.getComponentLogger().info(FormatUtil.format(locale.reload()));

                        return 1;
                    }
                }));

        builder.then(Commands.literal("update")
                .executes(ctx -> {
                    Locale locale = localeLoader.getLocale();
                    CommandSender sender = ctx.getSource().getSender();

                    if(sender instanceof Player) {
                        if (sender.hasPermission("skyleaderboards.command.skyleaderboards")
                                && sender.hasPermission("skyleaderboards.command.skyleaderboards.update")) {
                            dataManager.update();

                            sender.sendMessage(FormatUtil.format(locale.prefix() + locale.update()));

                            return 1;
                        } else {
                            sender.sendMessage(FormatUtil.format(locale.prefix() + locale.noPermission()));

                            return 0;
                        }
                    } else {
                        dataManager.update();

                        skyLeaderboards.getComponentLogger().info(FormatUtil.format(locale.update()));

                        return 1;
                    }
                }));

        return builder.build();
    }
}