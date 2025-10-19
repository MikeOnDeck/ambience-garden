package charmony.villager_tasks.common.features.villager_tasks.behaviors;

import charmony.villager_tasks.common.features.villager_tasks.*;
import charmony.villager_tasks.common.features.villager_tasks.requirements.CollectCriteria;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class Collect extends Behavior {
    public static final String ID = "collect";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getName() {
        return Resources.COLLECT_BEHAVIOR;
    }

    @Override
    public void onComplete(ServerPlayer player) {
        getCriteria().forEach(c -> c.onComplete(player));
    }

    @Override
    public Optional<Requirement> makeRequirement(RegistryAccess registryAccess, Definition definition, double multiplier, RandomSource random) {
        var map = definition.collect;
        if (map.isEmpty()) return Optional.empty();

        // Get the item registry; we need it to resolve item IDs.
        var itemRegistry = registryAccess.lookupOrThrow(Registries.ITEM);

        // Resolve items from map.
        var items = (List<Map<String, Object>>)map.getOrDefault("items", List.of());
        if (items.isEmpty()) {
            throw new IllegalStateException("Collect behavior requires at least one item to collect.");
        }

        var count = Math.min(items.size(), Helpers.getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<CollectCriteria>();

        for (var i = 0; i < items.size(); i++) {
            try {
                var itemMap = items.get(i);
                var itemId = (String) itemMap.get("item");
                var itemWeight = (double) itemMap.getOrDefault("weight", 1.0d);
                var itemStack = new ItemStack(itemRegistry.get(ResourceLocation.parse(itemId)).orElseThrow());
                var itemCount = Helpers.getCountFromMap(itemMap, multiplier, random);

                criteria.add(new CollectCriteria(itemStack, itemCount, (int)itemWeight));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to parse collect item at index " + i, e);
            }
        }

        var sortedCriteria = Helpers.getItemsRandomlyByWeight(criteria, count, random);
        var requirement = new Requirement(sortedCriteria);
        return Optional.of(requirement);
    }

    public List<CollectCriteria> getCriteria() {
        return getRequirements().stream()
            .flatMap(req -> req.collectCriteria().stream())
            .toList();
    }

}
