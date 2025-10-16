package charmony.rune_dictionary.common.features.rune_dictionary;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public record Knowledge(UUID uuid, String name, List<ResourceLocation> words) {
    public static final String UUID_TAG = "uuid";
    public static final String NAME_TAG = "name";
    public static final String WORDS_TAG = "words";

    /**
     * Codec represents the stored knowledge object and how to encode/decode it.
     */
    public static final Codec<Knowledge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("uuid").forGetter(Knowledge::uuid),
        Codec.STRING.fieldOf("name").forGetter(Knowledge::name),
        ResourceLocation.CODEC.listOf().fieldOf("words").forGetter(Knowledge::words)
    ).apply(instance, Knowledge::new));

    /**
     * Add the word to the knowledge and return the new knowledge record.
     *
     * @param word The word to add to the knowledge.
     * @return The new knowledge record.
     */
    public Knowledge learnWord(ResourceLocation word) {
        return learnWords(List.of(word));
    }

    /**
     * Add the words to the knowledge and return the new knowledge record.
     *
     * @param words The words to add to the knowledge.
     * @return The new knowledge record.
     */
    public Knowledge learnWords(List<ResourceLocation> words) {
        var updated = new ArrayList<>(words());
        for (var word : words) {
            if (word.toString().equals("minecraft:")) continue; // Don't store empty resource locations.
            if (!updated.contains(word)) {
                updated.add(word);
            }
        }
        return new Knowledge(uuid(), name(), updated);
    }

    /**
     * Save the knowledge record to a tag.
     * Typically this is used when saving the world data.
     *
     * @return The NBT representation of the knowledge.
     */
    public CompoundTag save() {
        var tag = new CompoundTag();
        var wordsList = new ListTag();
        for (var entry : words) {
            wordsList.add(StringTag.valueOf(entry.toString()));
        }
        tag.store(UUID_TAG, UUIDUtil.CODEC, uuid());
        tag.putString(NAME_TAG, name());
        tag.put(WORDS_TAG, wordsList);
        return tag;
    }

    /**
     * Load the knowledge record from a tag.
     * Typically this is used when loading the world data.
     *
     * @param tag NBT representation of the knowledge.
     * @return The knowledge record.
     */
    public static Knowledge load(CompoundTag tag) {
        var uuid = tag.read(UUID_TAG, UUIDUtil.CODEC).orElse(null);
        var name = tag.getString(NAME_TAG).orElse("");
        var list = tag.getListOrEmpty(WORDS_TAG);

        List<String> wordStrings = new ArrayList<>();
        for (var i = 0; i < list.size(); i++) {
            wordStrings.add(list.getStringOr(i, ""));
        }

        List<ResourceLocation> words = new ArrayList<>();

        for (var str : wordStrings) {
            if (str.isEmpty()) continue;
            words.add(ResourceLocation.tryParse(str));
        }

        return new Knowledge(uuid, name, words);
    }
}
