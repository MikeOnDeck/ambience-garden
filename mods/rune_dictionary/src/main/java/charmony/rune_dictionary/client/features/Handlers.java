package charmony.rune_dictionary.client.features;

import charmony.core.base.Setup;
import charmony.rune_dictionary.common.features.rune_dictionary.Networking.S2CDictionary;
import charmony.rune_dictionary.common.features.rune_dictionary.Networking.S2CKnowledge;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

@SuppressWarnings("unused")
public class Handlers extends Setup<RuneDictionary> {
    public Handlers(RuneDictionary feature) {
        super(feature);
    }

    /**
     * Update the static dictionary map in the common handlers.
     * On the integrated server (singleplayer) the static map is shared.
     * On the server-client model the server and client have a separate copy of the map.
     *
     * @param player Player to update.
     * @param payload Network packet payload.
     */
    public void handleDictionary(Player player, S2CDictionary payload) {
        feature().common.get().handlers.setDictionary(payload.dictionary());
    }

    /**
     * Update the static knowledge map in the common handlers.
     * On the integrated server (singleplayer) the static map is shared.
     * On the server-client model the server and client have a separate copy of the map.
     *
     * @param player Player to update.
     * @param payload Network packet payload.
     */
    public void handleKnowledge(Player player, S2CKnowledge payload) {
        feature().common.get().handlers.setKnowledge(player, payload.knowledge());
    }

    /**
     * Gets the rune word for the given registered object.
     * Returns empty optional if the word is not in the dictionary or is an empty string.
     *
     * @param word Registered object.
     * @return Rune word for the registered object, empty optional if not found.
     */
    public Optional<String> getRuneWord(Identifier word) {
        return feature().common.get().handlers.getRuneWord(word);
    }

    /**
     * True if the player knows the word.
     *
     * @param player Player to check.
     * @param word Word to check
     * @return True if the player knows the word.
     */
    public boolean knowsWord(Player player, Identifier word) {
        return feature().common.get().handlers.knowsWord(player, word);
    }
}
