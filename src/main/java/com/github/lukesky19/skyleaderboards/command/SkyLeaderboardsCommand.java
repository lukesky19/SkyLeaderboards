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
import com.github.lukesky19.skyleaderboards.util.FormatUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SkyLeaderboardsCommand implements CommandExecutor, TabCompleter {
    private final SkyLeaderboards skyLeaderboards;
    private final LocaleLoader localeLoader;
    private final DataManager dataManager;

    public SkyLeaderboardsCommand(SkyLeaderboards skyLeaderboards, LocaleLoader localeLoader, DataManager dataManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.localeLoader = localeLoader;
        this.dataManager = dataManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Locale locale;
        if (!skyLeaderboards.isPluginEnabled()) {
            locale = localeLoader.getDefaultLocale();
        } else {
            locale = localeLoader.getLocale();
        }

        if (sender instanceof Player player) {
            if (player.hasPermission("skyleaderboards.command.skyleaderboards")) {
                if (args.length == 1) {
                    switch (args[0]) {
                        case "reload" -> {
                            if (sender.hasPermission("skyleaderboards.command.skyleaderboards.reload")) {
                                skyLeaderboards.reload();
                                if (!skyLeaderboards.isPluginEnabled()) {
                                    locale = localeLoader.getDefaultLocale();
                                } else {
                                    locale = localeLoader.getLocale();
                                }

                                player.sendMessage(FormatUtil.format(player, locale.prefix() + locale.reload()));
                                return true;
                            }
                        }

                        case "update" -> {
                            if (sender.hasPermission("skyleaderboards.command.skyleaderboards.update")) {
                                player.sendMessage(FormatUtil.format(player, locale.prefix() + locale.update()));
                                dataManager.update();
                                return true;
                            }
                        }
                    }
                } else {
                    player.sendMessage(FormatUtil.format(player, locale.prefix() + locale.unknownArgument()));
                    return false;
                }
            } else {
                player.sendMessage(FormatUtil.format(player, locale.prefix() + locale.noPermission()));
                return false;
            }
        } else {
            ComponentLogger logger = skyLeaderboards.getComponentLogger();
            if (args.length == 1) {
                switch (args[0]) {
                    case "reload" -> {
                        skyLeaderboards.reload();
                        if (!skyLeaderboards.isPluginEnabled()) {
                            locale = localeLoader.getDefaultLocale();
                        } else {
                            locale = localeLoader.getLocale();
                        }

                        logger.info(FormatUtil.format(locale.reload()));
                        return true;
                    }

                    case "update" -> {
                        if(!skyLeaderboards.isPluginEnabled()) {
                            logger.error(MiniMessage.miniMessage().deserialize("<red>Cannot update signs, heads, and NPCs due to a config error.</red>"));
                            logger.error(MiniMessage.miniMessage().deserialize("<red>Please check your server's console.</red>"));
                            return false;
                        }

                        logger.info(FormatUtil.format(locale.update()));
                        dataManager.update();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> subCmds = new ArrayList<>();

        if(sender.hasPermission("skyleaderboards.command.skyleaderboards.reload")) subCmds.add("reload");
        if(sender.hasPermission("skyleaderboards.command.skyleaderboards.update")) subCmds.add("update");

        return subCmds;
    }
}
