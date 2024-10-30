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
package com.github.lukesky19.skymodels;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.lukesky19.skymodels.configuration.loader.DataLoader;
import com.github.lukesky19.skymodels.configuration.record.Data;
import com.github.lukesky19.skymodels.util.FormatUtil;
import com.github.lukesky19.skymodels.util.PlaceholderAPIUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Objects;

public class DataManager {
    private final SkyLeaderboards skyLeaderboards;
    private final DataLoader dataLoader;
    private BukkitTask task;

    public DataManager(SkyLeaderboards skyLeaderboards, DataLoader dataLoader) {
        this.skyLeaderboards = skyLeaderboards;
        this.dataLoader = dataLoader;
    }

    public void update() {
        if(!skyLeaderboards.isPluginEnabled()) return;
        if(skyLeaderboards.getServer().getOnlinePlayers().isEmpty()) return;
        Player player = skyLeaderboards.getServer().getOnlinePlayers().stream().toList().getFirst();

        Data data = dataLoader.getData();

        for(Map.Entry<Integer, Data.Head> headEntry : data.heads().entrySet()) {
            Data.Head headData = headEntry.getValue();
            World world = skyLeaderboards.getServer().getWorld(headData.location().world());
            Location loc = new Location(world, headData.location().x(), headData.location().y(), headData.location().z());

            BlockState blockState = Objects.requireNonNull(world).getBlockState(loc);
            if(blockState instanceof Skull skull) {
                String playerName = PlaceholderAPIUtil.parsePlaceholders(player, headData.placeholder());
                Player skullPlayer = skyLeaderboards.getServer().getPlayer(playerName);

                if(skullPlayer != null) {
                    PlayerProfile playerProfile = skullPlayer.getPlayerProfile();
                    skull.setPlayerProfile(playerProfile);
                    skull.update(true);
                }
            }
        }

        for(Map.Entry<Integer, Data.Sign> signEntry : data.signs().entrySet()) {
            Data.Sign signData = signEntry.getValue();
            World world = skyLeaderboards.getServer().getWorld(signData.location().world());
            Location loc = new Location(world, signData.location().x(), signData.location().y(), signData.location().z());

            BlockState blockState = Objects.requireNonNull(world).getBlockState(loc);
            if(blockState instanceof Sign sign) {
                String playerName = PlaceholderAPIUtil.parsePlaceholders(player, signData.placeholder());
                Player signPlayer = skyLeaderboards.getServer().getPlayer(playerName);
                if(signPlayer != null) {
                    SignSide signSide = sign.getSide(Side.FRONT);
                    signSide.line(0, FormatUtil.format(player, signData.lines().one()));
                    signSide.line(1, FormatUtil.format(player, signData.lines().two()));
                    signSide.line(2, FormatUtil.format(player, signData.lines().three()));
                    signSide.line(3, FormatUtil.format(player, signData.lines().four()));
                    sign.update(true);
                }
            }
        }

        for(Map.Entry<Integer, Data.NPC> npcEntry : data.npcs().entrySet()) {
            Data.NPC npcData = npcEntry.getValue();
            World world = skyLeaderboards.getServer().getWorld(npcData.location().world());
            Location loc = new Location(world, npcData.location().x(), npcData.location().y(), npcData.location().z());
            NPC npc = null;

            for(Entity entity : loc.getNearbyEntities(1, 1, 1)) {
                if(CitizensAPI.getNPCRegistry().isNPC(entity)) {
                    npc = CitizensAPI.getNPCRegistry().getNPC(entity);
                }
            }

            if(npc != null) {
                String skinPlayerName = PlaceholderAPIUtil.parsePlaceholders(player, npcData.placeholder());
                Player skinPlayer = skyLeaderboards.getServer().getPlayer(skinPlayerName);
                if(skinPlayer != null) {
                    npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(skinPlayer);
                }
            }
        }
    }

    public void startUpdateTask() {
        task = skyLeaderboards.getServer().getScheduler().runTaskTimer(skyLeaderboards, this::update, 1L, 20L * 300);
    }

    public void stopUpdateTask() {
        if(!task.isCancelled()) {
            task.cancel();
        }
    }
}
