package com.github.lukesky19.skyleaderboards.manager;

import com.github.lukesky19.skyleaderboards.SkyLeaderboards;
import com.github.lukesky19.skyleaderboards.configuration.loader.DataManager;
import com.github.lukesky19.skyleaderboards.configuration.loader.LocaleManager;
import com.github.lukesky19.skyleaderboards.configuration.record.Data;
import com.github.lukesky19.skyleaderboards.configuration.record.Locale;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.format.PlaceholderAPIUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class manages the updating of Citizen's NPC skins.
 */
public class NPCManager {
    private final SkyLeaderboards skyLeaderboards;
    private final LocaleManager localeManager;
    private final DataManager dataManager;

    /**
     * Constructor
     * @param skyLeaderboards The Plugin's Instance
     * @param localeManager A LocaleManager instance
     * @param dataManager A DataLoader instance
     */
    public NPCManager(SkyLeaderboards skyLeaderboards, LocaleManager localeManager, DataManager dataManager) {
        this.skyLeaderboards = skyLeaderboards;
        this.localeManager = localeManager;
        this.dataManager = dataManager;
    }

    /**
     * Updates Citizen's NPC skins based on the plugin's configuration.
     */
    public void update() {
        // If no players are online, there is no reason to update anything.
        if(skyLeaderboards.getServer().getOnlinePlayers().isEmpty()) return;

        // Get the plugin's configuration
        final Data data = dataManager.getData();
        // Get the first player online just in-case a Placeholder requires a player to parse them.
        final Player firstPlayer = skyLeaderboards.getServer().getOnlinePlayers().stream().toList().getFirst();
        // Get the ComponentLogger from the plugin.
        final ComponentLogger logger = skyLeaderboards.getComponentLogger();
        // Get the plugin's locale config.
        final Locale locale = localeManager.getLocale();

        // Loop through configured npcs to update
        data.npcs().forEach((key, npcData) -> {
            // Create the placeholder list for the world error message
            List<TagResolver.Single> worldErrorPlaceholders = List.of(
                    Placeholder.parsed("type", "npc"),
                    Placeholder.parsed("id", String.valueOf(key)));

            // Get the World configured and log an error message if it is null
            if(npcData.location().world() == null) {
                logger.error(FormatUtil.format(locale.invalidWorld(), worldErrorPlaceholders));
                return;
            }

            World world = skyLeaderboards.getServer().getWorld(npcData.location().world());
            if(world == null) {
                logger.error(FormatUtil.format(locale.invalidWorld(), worldErrorPlaceholders));
                return;
            }

            // Create the placeholder list for the npc error message
            List<TagResolver.Single> npcErrorPlaceholders = List.of(
                    Placeholder.parsed("type", "sign"),
                    Placeholder.parsed("world", npcData.location().world()),
                    Placeholder.parsed("x", String.valueOf(npcData.location().x())),
                    Placeholder.parsed("y", String.valueOf(npcData.location().y())),
                    Placeholder.parsed("z", String.valueOf(npcData.location().z())));

            // Build the Location object for the npc data config.
            Location loc = new Location(world, npcData.location().x(), npcData.location().y(), npcData.location().z());

            // Attempt to get the NPC at the configured location from the npc data config.
            NPC npc = null;
            for(Entity entity : loc.getNearbyEntities(1, 1, 1)) {
                if(CitizensAPI.getNPCRegistry().isNPC(entity)) {
                    npc = CitizensAPI.getNPCRegistry().getNPC(entity);
                }
            }

            // If the NPC is not null, attempt to update the skin
            if(npc != null) {
                // Parse the placeholder for the given npc data config.
                String skinPlayerName = PlaceholderAPIUtil.parsePlaceholders(firstPlayer, npcData.placeholder());

                // If the placeholder parsed successfully, attempt to update the npc's skin.
                if(!skinPlayerName.isEmpty() && !skinPlayerName.equals(npcData.placeholder())) {
                    // Update the NPC's skin
                    npc.getOrAddTrait(SkinTrait.class).setSkinName(skinPlayerName);
                }
            } else {
                logger.error(FormatUtil.format(locale.invalidNpc(), npcErrorPlaceholders));
            }
        });
    }
}
