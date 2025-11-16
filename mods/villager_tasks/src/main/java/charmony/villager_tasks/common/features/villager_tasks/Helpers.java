package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.helpers.TagHelper;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.HasWeight;
import charmony.villager_tasks.common.features.villager_tasks.requirements.TreasureItem;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.function.Consumer;

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

    @SuppressWarnings("unchecked")
    public static void parseStandardItemsEntry(RegistryAccess registryAccess, List<Map<String, Object>> items,
                                               double multiplier, RandomSource random, Consumer<ParsedItem> consumer) {
        for (var i = 0; i < items.size(); i++) {
            try {
                var itemMap = items.get(i);
                var itemId = (String) itemMap.get("item");
                var itemWeight = (double) itemMap.getOrDefault("weight", 1.0d);
                var itemStack = new ItemStack(Helpers.resolveItem(registryAccess, itemId, random));
                var itemCount = Helpers.getCountFromMap(itemMap, multiplier, random);

                var enchantments = (List<Map<String, Object>>) itemMap.get("enchantments");
                if (enchantments != null) {
                    Helpers.applyEnchantments(registryAccess, itemStack, enchantments, random);
                }

                if (itemStack.isEmpty()) {
                    throw new IllegalStateException("Item " + itemId + " could not be parsed");
                }

                consumer.accept(new ParsedItem(itemStack, itemCount, (int)itemWeight));
            } catch (Exception e) {
                VillagerTasks.feature().log().warn(e.getMessage() + " at index " + i);
            }
        }
    }

    public static void applyEnchantments(RegistryAccess registryAccess, ItemStack stack,
                                         List<Map<String, Object>> enchantments, RandomSource random) {
        var registry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

        for (var enchantmentMap : enchantments) {
            Holder<Enchantment> ench;
            var name = (String) enchantmentMap.get("enchantment");
            if (!name.equals("random")) {
                var key = ResourceKey.create(Registries.ENCHANTMENT, Identifier.parse(name));
                ench = registry.get(key).orElse(null);
            } else {
                ench = registry.getRandom(random).orElse(null);
            }

            if (ench == null) continue;

            var level = (int) (double) enchantmentMap.getOrDefault("level", 1.0d);
            if (ench.value().canEnchant(stack)) {
                stack.enchant(ench, Math.min(level, ench.value().getMaxLevel()));
            }
        }
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
            var tagKey = TagKey.create(Registries.ITEM, Identifier.parse(itemId.substring(1)));
            var values = TagHelper.getValues(itemRegistry, tagKey);
            if (values.isEmpty()) {
                throw new IllegalStateException("Could not get values for tag: " + itemId);
            }

            Util.shuffle(values, random);
            item = values.getFirst().value();
        } else {
            item = itemRegistry.getValue(Identifier.parse(itemId));
        }

        return item;
    }

    public static ItemStack createTreasureItemStack(RegistryAccess registryAccess, String itemId, UUID uniqueId, RandomSource random) {
        var stack = new ItemStack(resolveItem(registryAccess, itemId, random));
        var registry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

        var enchantment = registry.getRandom(random).orElseThrow();
        stack.enchant(enchantment, 1);

        // TODO: generate a random name.
        var component = Component.literal("The Treasure");

        var tag = new CompoundTag();
        tag.putString(TreasureItem.TREASURE_TAG, uniqueId.toString());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.set(DataComponents.CUSTOM_NAME, component);
        stack.set(DataComponents.RARITY, Rarity.RARE);

        return stack;
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

    public record ParsedItem(ItemStack stack, int count, int weight) {
    }
}
