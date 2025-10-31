package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.helpers.TagHelper;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.HasWeight;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.*;

public final class Helpers {
    /**
     * Get a count from a map with min, max, exactly values and apply a multiplier.
     * This is a commonly used pattern in villager task definitions.
     */
    public static int getCountFromMap(Map<String, Object> map, double multiplier, RandomSource random) {
        int count;

        var min = (double)map.getOrDefault("min", 1.0d);
        var max = (double)map.getOrDefault("max", 1.0d);
        var exactly = map.getOrDefault("exactly", null);

        if (exactly != null) {
            count = (int) Math.round((double)exactly * multiplier);
        } else {
            var imin = (int) Math.max(1, Math.round(min * multiplier));
            var imax = (int) Math.max(1, Math.round(max * multiplier));
            count = random.nextIntBetweenInclusive(imin, imax);
        }

        return count;
    }

    public static <T extends HasWeight> List<T> getRandomlyByWeight(List<T> list, int count, RandomSource random) {
        var selected = new ArrayList<T>();

        list.sort((a, b) -> Integer.compare(b.weight(), a.weight()));
        var totalWeight = list.stream().mapToInt(T::weight).sum();

        for (var i = 0; i < count; i++) {
            var r = random.nextInt(totalWeight);
            var cumulative = 0;

            for (var entry : list) {
                cumulative += entry.weight();
                if (r < cumulative) {
                    selected.add(entry);
                    totalWeight -= entry.weight();
                    list.remove(entry);
                    break;
                }
            }
        }

        return selected;
    }

    /**
     * Resolves an item ID string to a registered item instance and returns it.
     * If the ID string starts with a # then the ID is first resolved to a tag.
     * All tag values are loaded and one is selected at random to be returned.
     *
     * @param registryAccess Used to lookup the item registry.
     * @param itemId ID in string format, e.g. "minecraft:wheat", "#minecraft:piglin_loved".
     * @param random Random source to use when selecting an item from a tag.
     * @return Resolved item instance.
     */
    public static Item resolveItem(RegistryAccess registryAccess, String itemId, RandomSource random) {
        // Get the item registry; we need it to resolve item IDs.
        var itemRegistry = registryAccess.lookupOrThrow(Registries.ITEM);

        Item item;

        // If itemId starts with a # then it's a tag; resolve all elements.
        if (itemId.startsWith("#")) {
            var tagKey = TagKey.create(Registries.ITEM, ResourceLocation.parse(itemId.substring(1)));
            var values = TagHelper.getValues(itemRegistry, tagKey);
            if (values.isEmpty()) {
                throw new IllegalStateException("Could not get values for item tag");
            }

            Util.shuffle(values, random);
            item = values.getFirst();
        } else {
            item = itemRegistry.getValue(ResourceLocation.parse(itemId));
        }

        return item;
    }

    public static Optional<AbstractVillager> getNearbyTaskOwner(Player player, UUID uuid) {
        var nearby = getNearbyVillagers(player);
        return nearby.stream().filter(e -> e.getUUID().equals(uuid)).findFirst();
    }

    public static List<AbstractVillager> getNearbyVillagers(Player player) {
        return player.level().getEntitiesOfClass(AbstractVillager.class, new AABB(player.blockPosition()).inflate(8.0d));
    }

    public static Optional<AbstractVillager> getNearbyRewardGiver(Player player) {
        var nearby = getNearbyVillagers(player);
        return nearby.stream().findFirst();
    }

    public static void throwItemsAtPlayer(AbstractVillager villager, Player player, List<ItemStack> items) {
        for (ItemStack stack : items) {
            BehaviorUtils.throwItem(villager, stack, player.position());
        }
    }

    public static UUID emptyUuid() {
        return UUID.randomUUID(); // TODO: make actually empty
    }
}
