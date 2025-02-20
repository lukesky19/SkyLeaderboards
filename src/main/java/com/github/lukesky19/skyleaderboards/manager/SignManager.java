package com.github.lukesky19.skyleaderboards.manager;

import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import com.github.lukesky19.skyleaderboards.configuration.loader.DataManager;
import com.github.lukesky19.skyleaderboards.configuration.loader.LocaleManager;
import com.github.lukesky19.skyleaderboards.configuration.record.Data;
import com.github.lukesky19.skyleaderboards.configuration.record.Locale;
import com.github.lukesky19.skylib.format.FormatUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This classes manages the updating of Signs.
 */
public class SignManager {
    private final SkyLeaderboards skyLeaderboards;
    private final LocaleManager localeManager;
    private final DataManager dataManager;

    /**
     * Constructor
     * @param skyLeaderboards The Plugin's Instance
     * @param localeManager A LocaleManager instance
     * @param dataManager A DataLoader instance
     */
    public SignManager(SkyLeaderboards skyLeaderboards, LocaleManager localeManager, DataManager dataManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.localeManager = localeManager;
        this.dataManager = dataManager;
    }

    /**
     * Updates signs based on the plugin's configuration.
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

        // Loop through configured signs to update
        data.signs().forEach((key, signData) -> {
            // Create the placeholder list for the world error message
            List<TagResolver.Single> worldErrorPlaceholders = List.of(
                    Placeholder.parsed("type", "npcs"),
                    Placeholder.parsed("id", String.valueOf(key)));

            // Get the World configured and log an error message if it is null
            if(signData.location().world() == null) {
                logger.error(FormatUtil.format(locale.invalidWorld(), worldErrorPlaceholders));
                return;
            }

            World world = skyLeaderboards.getServer().getWorld(signData.location().world());
            if(world == null) {
                logger.error(FormatUtil.format(locale.invalidWorld(), worldErrorPlaceholders));
                return;
            }

            // Create the placeholder list for the block error message
            List<TagResolver.Single> blockErrorPlaceholders = List.of(
                    Placeholder.parsed("type", "sign"),
                    Placeholder.parsed("world", signData.location().world()),
                    Placeholder.parsed("x", String.valueOf(signData.location().x())),
                    Placeholder.parsed("y", String.valueOf(signData.location().y())),
                    Placeholder.parsed("z", String.valueOf(signData.location().z())));

            // Build the Location object for the sign data config.
            Location loc = new Location(world, signData.location().x(), signData.location().y(), signData.location().z());

            // If the BlockState at the Location is a Sign, attempt to update it
            // Otherwise we log a warning to the console.
            if(world.getBlockState(loc) instanceof Sign sign) {
                // Get the front of the sign
                SignSide frontSide = sign.getSide(Side.FRONT);
                SignSide backSide = sign.getSide(Side.BACK);

                // Update each line of the sign (front and back) based on the sign data config.
                if (signData.lines().one() != null) {
                    frontSide.line(0, FormatUtil.format(firstPlayer, signData.lines().one()));
                    backSide.line(0, FormatUtil.format(firstPlayer, signData.lines().one()));
                }
                if (signData.lines().two() != null) {
                    frontSide.line(1, FormatUtil.format(firstPlayer, signData.lines().two()));
                    backSide.line(1, FormatUtil.format(firstPlayer, signData.lines().two()));
                }
                if (signData.lines().three() != null) {
                    frontSide.line(2, FormatUtil.format(firstPlayer, signData.lines().three()));
                    backSide.line(2, FormatUtil.format(firstPlayer, signData.lines().three()));
                }
                if (signData.lines().four() != null) {
                    frontSide.line(3, FormatUtil.format(firstPlayer, signData.lines().four()));
                    backSide.line(3, FormatUtil.format(firstPlayer, signData.lines().four()));
                }

                // Update the block state
                sign.update(true);
            } else {
                logger.error(FormatUtil.format(locale.invalidBlock(), blockErrorPlaceholders));
            }
        });
    }
}
