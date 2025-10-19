package charmony.rune_dictionary.common.features.rune_dictionary;

import charmony.rune_dictionary.RuneDictionaryMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents the saved data for the knowledge.
 * Knowledge for every player is stored in the internal knowledge array.
 */
public class KnowledgeSavedData extends SavedData {
    private List<Knowledge> knowledge = new ArrayList<>();

    public static final Codec<KnowledgeSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Knowledge.CODEC.listOf().fieldOf("knowledge").forGetter(data -> data.knowledge)
    ).apply(instance, KnowledgeSavedData::new));

    public static final SavedDataType<KnowledgeSavedData> TYPE = new SavedDataType<>(
        RuneDictionaryMod.ID + "-knowledge",
        KnowledgeSavedData::new,
        CODEC,
        null
    );

    /**
     * Constructor loaded when the SavedDataType is created.
     */
    public KnowledgeSavedData() {
        setDirty();
    }

    /**
     * Constructor loaded when the data is loaded from disk.
     *
     * @param knowledge Knowledge to populate in the state.
     */
    private KnowledgeSavedData(List<Knowledge> knowledge) {
        this.knowledge = new ArrayList<>(knowledge);
    }

    /**
     * Updates an entry in the internal knowledge array by looking for an existing UUID.
     * If an entry doesn't exist, it is added.
     *
     * @param updated Knowledge to replace/update.
     */
    public void updateKnowledge(Knowledge updated) {
        var existing = getKnowledgeByUUID(updated.uuid());

        if (!(knowledge instanceof ArrayList<Knowledge>)) {
            // Stupid hack, look at how vanilla uses listOf() to make a mutable list.
            knowledge = new ArrayList<>(knowledge);
        }

        existing.ifPresent(knowledge::remove);
        knowledge.add(updated);
        setDirty();
    }

    /**
     * Gets knowledge record for a player. If it doesn't exist, one is created.
     *
     * @param player Player to get knowledge for.
     * @return The knowledge for the player.
     */
    public Knowledge getKnowledge(Player player) {
        var uuid = player.getUUID();
        var name = player.getScoreboardName();
        var existing = getKnowledgeByUUID(uuid);
        return existing.orElseGet(() -> new Knowledge(uuid, name, List.of()));
    }

    /**
     * Gets knowledge record for a player by their UUID.
     *
     * @param uuid UUID of the player.
     * @return The knowledge for the player, if it exists.
     */
    public Optional<Knowledge> getKnowledgeByUUID(UUID uuid) {
        return knowledge.stream().filter(k -> k.uuid().equals(uuid)).findFirst();
    }

    /**
     * Helper to get the knowledge data for the server.
     *
     * @param server Server instance.
     * @return Knowledge data.
     */
    public static KnowledgeSavedData getServerState(MinecraftServer server) {
        var level = server.getLevel(Level.OVERWORLD);
        if (level == null) {
            throw new RuntimeException("Level not available");
        }
        var storage = level.getDataStorage();
        var state = storage.computeIfAbsent(TYPE);
        state.setDirty();
        return state;
    }
}
