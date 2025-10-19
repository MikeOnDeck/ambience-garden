package charmony.villager_tasks.common.features.villager_tasks.behaviors;

import charmony.villager_tasks.common.features.villager_tasks.Behavior;
import charmony.villager_tasks.common.features.villager_tasks.Definition;
import charmony.villager_tasks.common.features.villager_tasks.Requirement;
import charmony.villager_tasks.common.features.villager_tasks.requirements.CollectCriteria;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
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
    @Override
    public void onComplete(ServerPlayer player) {
        getTask().getRequirements().forEach(
            req -> req.collectItems().forEach(
                criteria -> criteria.onComplete(player)));
    }

    public static Optional<Requirement> makeRequirement(RegistryAccess registryAccess, Definition definition, double multiplier, RandomSource random) {
        var map = definition.collect;
        if (map.isEmpty()) return Optional.empty();

        // Get the item registry; we need it to resolve item IDs.
        var itemRegistry = registryAccess.lookupOrThrow(Registries.ITEM);

        // Resolve items from map.
        var items = (List<Map<String, Object>>)map.getOrDefault("items", List.of());
        if (items.isEmpty()) {
            throw new IllegalStateException("Collect behavior requires at least one item to collect.");
        }

        var count = Math.min(items.size(), getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<CollectCriteria>();

        for (var i = 0; i < items.size(); i++) {
            try {
                var itemMap = items.get(i);
                var itemId = (String) itemMap.get("item");
                var itemWeight = (double) itemMap.getOrDefault("weight", 1.0d);
                var itemStack = new ItemStack(itemRegistry.get(ResourceLocation.parse(itemId)).orElseThrow());
                var itemCount = getCountFromMap(itemMap, multiplier, random);

                criteria.add(new CollectCriteria(itemStack, itemCount, (int)itemWeight));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to parse collect item at index " + i, e);
            }
        }

        // Order criteria by weight.
        criteria.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));

        // LLM: Take items randomly up to count.
        var selectedCriteria = new ArrayList<CollectCriteria>();
        var totalWeight = criteria.stream().mapToInt(CollectCriteria::getWeight).sum();
        for (var i = 0; i < count; i++) {
            var r = random.nextInt(totalWeight);
            var cumulative = 0;

            for (var crit : criteria) {
                cumulative += crit.getWeight();
                if (r < cumulative) {
                    selectedCriteria.add(crit);
                    totalWeight -= crit.getWeight();
                    criteria.remove(crit);
                    break;
                }
            }
        }

        var requirement = new Requirement(selectedCriteria);
        return Optional.of(requirement);
    }

    private static int getCountFromMap(Map<String, Object> map, double multiplier, RandomSource random) {
        int count;

        var min = (double)map.getOrDefault("min", 1.0d);
        var max = (double)map.getOrDefault("max", 1.0d);
        var exactly = map.getOrDefault("exactly", null);

        if (exactly != null) {
            count = (int)((double)exactly * multiplier);
        } else {
            var mmin = (int)(min * multiplier);
            var mmax = (int)(max * multiplier);
            count = random.nextIntBetweenInclusive(mmin, mmax);
        }

        return count;
    }
}
