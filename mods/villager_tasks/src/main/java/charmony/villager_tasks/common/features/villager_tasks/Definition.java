package charmony.villager_tasks.common.features.villager_tasks;

import com.google.gson.Gson;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"FieldMayBeFinal", "unused"})
public class Definition {
    private ResourceManager manager; // TODO: check why this is needed

    // These are loaded from the JSON definition.
    public Identifier id;
    public String title = "";
    public int level = 0;
    public int expiry = 0;
    public String villager = ""; // Don't reference this directly; use appliesTo().
    public Map<String, Object> collect = new HashMap<>();
    public Map<String, Object> treasure = new HashMap<>();
    public Map<String, Object> hunt = new HashMap<>();
    public Map<String, Object> battle = new HashMap<>();
    public Map<String, Object> rewards = new HashMap<>();
    public Map<String, Object> penalties = new HashMap<>();
    public Map<String, Object> effects = new HashMap<>();

    private @Nullable ResourceKey<EntityType<?>> villagerKey = null;
    private @Nullable TagKey<EntityType<?>> villagerTag = null;

    public static Definition fromJson(Identifier id, ResourceManager manager, Resource resource) throws IOException {
        BufferedReader reader;

        reader = resource.openAsReader();
        var def = new Gson().fromJson(reader, Definition.class);

        def.id = id;
        def.manager = manager;

        // Convert villager to a tag or resource key for lookup later.
        if (def.villager.startsWith("#")) {
            def.villagerTag = TagKey.create(Registries.ENTITY_TYPE, Identifier.parse(def.villager.substring(1)));
        } else if (!def.villager.isEmpty()) {
            def.villagerKey = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.parse(def.villager));
        }

        return def;
    }

    /**
     * True if this definition applies to the given villager.
     */
    public boolean appliesTo(Registry<EntityType<?>> entityRegistry, AbstractVillager abstractVillager) {
        if (this.villager.isEmpty()) {
            return true; // Allows all villagers if undefined.
        }

        int tradingLevel;

        if (abstractVillager instanceof Villager v) {
            tradingLevel = v.getVillagerData().level();
        } else {
            tradingLevel = 0;
        }

        if (tradingLevel > 0 && tradingLevel < this.level) {
            return false;
        }

        if (villagerTag != null) {
            return abstractVillager.getType().is(villagerTag);
        } else {
            return entityRegistry.getOptional(villagerKey)
                .map(type -> abstractVillager.getType().equals(type))
                .orElse(false);
        }
    }

    public Optional<Component> getTitle() {
        if (title.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Component.translatable(title));
    }
}
