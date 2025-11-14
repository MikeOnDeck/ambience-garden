package charmony.rune_dictionary.common.features.rune_dictionary;

import charmony.api.rune_dictionary.RuneWordProvider;
import charmony.core.Api;
import charmony.core.base.Setup;
import charmony.rune_dictionary.common.features.rune_dictionary.Networking.C2SRequestDictionary;
import charmony.rune_dictionary.common.features.rune_dictionary.Networking.C2SRequestKnowledge;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;

@SuppressWarnings("unused")
public class Handlers extends Setup<RuneDictionary> {
    private static final Map<Identifier, String> DICTIONARY = new HashMap<>();
    private static final Map<Player, Knowledge> KNOWLEDGE = new HashMap<>();

    public Handlers(RuneDictionary feature) {
        super(feature);
    }

    /**
     * Update the entire dictionary.
     *
     * @param dictionary Dictionary map.
     */
    public void setDictionary(Map<Identifier, String> dictionary) {
        DICTIONARY.clear();
        DICTIONARY.putAll(dictionary);
    }

    /**
     * Update a knowledge map entry.
     *
     * @param player Player to update.
     * @param knowledge Knowledge to update.
     */
    public void setKnowledge(Player player, Knowledge knowledge) {
        KNOWLEDGE.put(player, knowledge);
    }

    /**
     * Add a registered object to the dictionary.
     *
     * @param word The word to add to the dictionary.
     */
    public void addDicationaryWord(Identifier word) {
        DICTIONARY.put(word, ""); // Empty string until the server is loaded and we generate it.
    }

    /**
     * Add a list of registered objects to the dictionary.
     *
     * @param words Words to add to the dictionary.
     */
    public void addDictionaryWords(List<Identifier> words) {
        for (var word : words) {
            addDicationaryWord(word);
        }
    }

    /**
     * Gets the rune word for the given registered object.
     * Returns empty optional if the word is not in the dictionary or is an empty string.
     *
     * @param word Registered object.
     * @return Rune word for the registered object, empty optional if not found.
     */
    public Optional<String> getRuneWord(Identifier word) {
        var runeWord = DICTIONARY.get(word);
        if (runeWord == null) return Optional.empty();
        return runeWord.isEmpty() ? Optional.empty() : Optional.of(runeWord);
    }

    /**
     * True if the player knows the word.
     *
     * @param player Player to check.
     * @param word Word to check
     * @return True if the player knows the word.
     */
    public boolean knowsWord(Player player, Identifier word) {
        return knownWords(player).contains(word);
    }

    /**
     * Get a list of all known words for the given player.
     *
     * @param player Player to check.
     * @return List of all known words (resourcelocations)
     */
    public List<Identifier> knownWords(Player player) {
        var knowledge = KNOWLEDGE.get(player);
        if (knowledge == null) return List.of();
        return knowledge.words();
    }

    /**
     * Add the word to the player's knowledge.
     *
     * @param player The player to add the word to.
     * @param word The word to add to the player's knowledge.
     */
    public void learnWord(ServerPlayer player, Identifier word) {
        learnWords(player, List.of(word));
    }

    /**
     * Add a list of words to the player's knowledge.
     *
     * @param player The player to add the words to.
     * @param words The words to add to the player's knowledge.
     */
    public void learnWords(ServerPlayer player, List<Identifier> words) {
        ServerLevel serverLevel = player.level();

        var knowledge = KNOWLEDGE.get(player);
        if (knowledge == null) return;

        // Learn the words.
        var updated = knowledge.learnWords(words);

        // Update this handler's cache.
        KNOWLEDGE.put(player, updated);

        // Update the server's saved state.
        var state = KnowledgeSavedData.getServerState(serverLevel.getServer());
        state.updateKnowledge(updated);

        // Send the updated knowledge to the client.
        Networking.S2CKnowledge.send(player, updated);

        // Debug message
        words.forEach(w -> feature().log().debug("Taught `" + w + "` to " + player.getScoreboardName()));
    }

    /**
     * Called when a player joins a world.
     *
     * @param entity The player joining the world.
     * @param level Level provides way to get a reference to the server.
     */
    public void entityJoin(Entity entity, Level level) {
        if (entity instanceof ServerPlayer player) {
            var serverLevel = (ServerLevel)level;
            var state = KnowledgeSavedData.getServerState(serverLevel.getServer());
            var knowledge = state.getKnowledge(player);

            // Send the full dictionary to the client.
            syncDictionary(player);

            // Update this player's knowledge.
            setKnowledge(player, knowledge);

            // Send the player's knowledge to the client.
            syncKnowledge(player);
        }
    }

    /**
     * Sends the player's knowledge to the client.
     *
     * @param player The player to send the knowledge to.
     */
    public void syncKnowledge(ServerPlayer player) {
        var knowledge = KNOWLEDGE.get(player);
        if (knowledge != null) {
            Networking.S2CKnowledge.send(player, knowledge);
        }
    }

    /**
     * Sends the full dictionary to the client.
     *
     * @param player The player to send the dictionary to.
     */
    public void syncDictionary(ServerPlayer player) {
        Networking.S2CDictionary.send(player, DICTIONARY);
    }

    /**
     * Called when a player sends a network request for their knowledge.
     *
     * @param player The player sending the request. Cast to ServerPlayer.
     * @param payload The request payload. Doesn't contain any data so it's ignored here.
     */
    public void handleRequestKnowledge(Player player, C2SRequestKnowledge payload) {
        if (player instanceof ServerPlayer serverPlayer) {
            syncKnowledge(serverPlayer);
        }
    }

    /**
     * Called when a player sends a network request for the dictionary.
     *
     * @param player The player sending the request. Cast to ServerPlayer.
     * @param payload The request payload. Doesn't contain any data so it's ignored here.
     */
    public void handleRequestDictionary(Player player, C2SRequestDictionary payload) {
        if (player instanceof ServerPlayer serverPlayer) {
            syncDictionary(serverPlayer);
        }
    }

    /**
     * Called when the server is starting.
     * Registries are frozen at this point; pass the registry access to all RuneWordProviders.
     *
     * @param server The server instance.
     */
    public void serverStarting(MinecraftServer server) {
        // Api consumer of rune provider classes.
        // We need the registry access quite late to be able to get access to all registered objects.
        var registryAccess = server.registryAccess();
        Api.consume(RuneWordProvider.class,
            provider -> feature().handlers.addDictionaryWords(provider.getRuneWords(registryAccess)));
    }

    /**
     * Called when the server is started.
     * Server levels exist at this point so we can get the world seed.
     *
     * @param server The server instance.
     */
    public void serverStarted(MinecraftServer server) {
        // Encode the populated dictionary with the world's seed.
        if (server.getLevel(Level.OVERWORLD) instanceof ServerLevel level) {
            encode(level.getSeed());
        }
    }

    /**
     * Encode the dictionary with the given seed.
     *
     * @param seed Seed to use as the random source.
     */
    private void encode(long seed) {
        var keys = new ArrayList<>(DICTIONARY.keySet());
        for (var key : keys) {
            var val = Helpers.generateRunes(key.toString(), seed, 5, 8);
            DICTIONARY.put(key, val);
        }

        // Check for duplicates and regenerate if needed.
        var seen = new HashSet<String>();
        for (var key : keys) {
            var val = DICTIONARY.get(key);
            while (seen.contains(val)) {
                // If duplicate found, regenerate with a different seed.
                val = Helpers.generateRunes(key.toString(), seed + seen.size(), 5, 8);
            }
            seen.add(val);
            DICTIONARY.put(key, val);
        }
    }
}
