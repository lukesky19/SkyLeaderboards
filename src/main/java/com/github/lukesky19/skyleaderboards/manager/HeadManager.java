package com.github.lukesky19.skyleaderboards.manager;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import com.github.lukesky19.skyleaderboards.configuration.loader.DataManager;
import com.github.lukesky19.skyleaderboards.configuration.loader.LocaleManager;
import com.github.lukesky19.skyleaderboards.configuration.record.Data;
import com.github.lukesky19.skyleaderboards.configuration.record.Locale;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.format.PlaceholderAPIUtil;
import com.github.lukesky19.skylib.player.PlayerUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This classes manages the updating of Player Heads.
 */
public class HeadManager {
    private final SkyLeaderboards skyLeaderboards;
    private final LocaleManager localeManager;
    private final DataManager dataManager;

    /**
     * Constructor
     * @param skyLeaderboards The Plugin's Instance
     * @param localeManager A LocaleManager instance
     * @param dataManager A DataManager instance
     */
    public HeadManager(SkyLeaderboards skyLeaderboards, LocaleManager localeManager, DataManager dataManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.localeManager = localeManager;
        this.dataManager = dataManager;
    }

    /**
     * Updates player skulls based on the plugin's configuration.
     */
    public void update() {
        // If no players are online, there is no reason to update anything.
        if(skyLeaderboards.getServer().getOnlinePlayers().isEmpty()) return;

        // Get the plugin's configuration
        Data data = dataManager.getData();
        // Get the first player online just in-case a Placeholder requires a player to parse them.
        Player firstPlayer = skyLeaderboards.getServer().getOnlinePlayers().stream().toList().getFirst();
        // Get the ComponentLogger from the plugin.
        ComponentLogger logger = skyLeaderboards.getComponentLogger();
        // Get the plugin's locale config.
        Locale locale = localeManager.getLocale();

        // Loop through configured heads to update
        data.heads().forEach((key, headData) -> {
            // Create the placeholder list for the world error message
            List<TagResolver.Single> worldErrorPlaceholders = List.of(
                    Placeholder.parsed("type", "heads"),
                    Placeholder.parsed("id", String.valueOf(key)));

            // Get the World configured and log an error message if it is null
            if(headData.location().world() == null) {
                logger.error(FormatUtil.format(locale.invalidWorld(), worldErrorPlaceholders));
                return;
            }

            World world = skyLeaderboards.getServer().getWorld(headData.location().world());
            if(world == null) {
                logger.error(FormatUtil.format(locale.invalidWorld(), worldErrorPlaceholders));
                return;
            }

            // Create the placeholder list for the block error message
            List<TagResolver.Single> blockErrorPlaceholders = List.of(
                    Placeholder.parsed("type", "player head"),
                    Placeholder.parsed("world", headData.location().world()),
                    Placeholder.parsed("x", String.valueOf(headData.location().x())),
                    Placeholder.parsed("y", String.valueOf(headData.location().y())),
                    Placeholder.parsed("z", String.valueOf(headData.location().z())));

            // Build the Location object for the head data config.
            Location loc = new Location(world, headData.location().x(), headData.location().y(), headData.location().z());

            // If the BlockState at the Location is a Skull, attempt to update it
            // Otherwise we log a warning to the console.
            if (world.getBlockState(loc) instanceof Skull skull) {
                // Parse the placeholder for the given head data config.
                String playerName = PlaceholderAPIUtil.parsePlaceholders(firstPlayer, headData.placeholder());

                // If the placeholder parsed successfully, attempt to update the Skull.
                if (!playerName.isEmpty() && !playerName.equals(headData.placeholder())) {
                    // Get the OfflinePlayer from the player name produced from the placeholder.
                    OfflinePlayer skullPlayer = skyLeaderboards.getServer().getOfflinePlayer(playerName);
                    // Get the OfflinePlayer's UUID
                    UUID uuid = skullPlayer.getUniqueId();
                    // Get the PlayerProfile from the cache.
                    PlayerProfile playerProfile = PlayerUtil.getCachedPlayerProfile(uuid);

                    // If the PlayerProfile in the cache does not exist, attempt to add it to the cache.
                    // If it does, we just update the Skull with the PlayerProfile.
                    if(playerProfile == null) {
                        // If the player is online and connected, the profile is already complete and can be added to the cache.
                        // Otherwise, we attempt to complete the profile then cache it.
                        // We finally then update the Skull with the PlayerProfile.
                        if(skullPlayer.isOnline() && skullPlayer.isConnected() && skullPlayer.getPlayerProfile().isComplete()) {
                            // Cache the player profile
                            PlayerProfile updatedProfile = skullPlayer.getPlayerProfile();
                            PlayerUtil.cachePlayerProfile(uuid, skullPlayer.getPlayerProfile());

                            // Update the skull
                            skull.setPlayerProfile(updatedProfile);
                            skull.update(true);
                        } else {
                            CompletableFuture<PlayerProfile> future = skullPlayer.getPlayerProfile().update();

                            future.thenAccept(updatedProfile -> {
                                // Cache the player profile
                                PlayerUtil.cachePlayerProfile(uuid, updatedProfile);

                                // Update the skull sync because the API is not thread safe.
                                skyLeaderboards.getServer().getScheduler().runTask(skyLeaderboards, () -> {
                                    skull.setPlayerProfile(updatedProfile);
                                    skull.update(true);
                                });
                            });
                        }
                    } else {
                        // Update the skull
                        skull.setPlayerProfile(playerProfile);
                        skull.update(true);
                    }
                }
            } else {
                logger.error(FormatUtil.format(locale.invalidBlock(), blockErrorPlaceholders));
            }
        });
    }
}
