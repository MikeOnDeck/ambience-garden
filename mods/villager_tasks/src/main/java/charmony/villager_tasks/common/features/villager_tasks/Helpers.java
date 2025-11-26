package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.helpers.EnchantmentsHelper;
import charmony.core.helpers.TagHelper;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.HasWeight;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
                var map = items.get(i);

                var chance = (double) map.getOrDefault("chance", 1.0d);
                if (chance < random.nextDouble()) continue;

                var id = (String) map.get("item");
                var weight = (double) map.getOrDefault("weight", 1.0d);
                var stack = new ItemStack(Helpers.resolveItem(registryAccess, id, random));
                var count = Helpers.getCountFromMap(map, multiplier, random);

                var enchantments = (List<Map<String, Object>>) map.get("enchantments");
                if (enchantments != null) {
                    Helpers.applyEnchantments(registryAccess, stack, enchantments, random);
                }

                if (stack.isEmpty()) {
                    throw new IllegalStateException("Item " + id + " could not be parsed");
                }

                consumer.accept(new ParsedItem(stack, count, (int)weight));
            } catch (Exception e) {
                VillagerTasks.feature().log().warn(e.getMessage() + " at index " + i);
            }
        }
    }

    public static void parseStandardEffectsEntry(List<Map<String, Object>> effects, RandomSource random,
                                                 Consumer<ParsedEffect> consumer) {
        for (var i = 0; i < effects.size(); i++) {
            try {
                var map = effects.get(i);

                var chance = (double) map.getOrDefault("chance", 1.0d);
                if (chance < random.nextDouble()) continue;

                var idStr = (String) map.get("effect");
                var amplifier = (double) map.getOrDefault("amplifier", 0.0d);
                var duration = (double) map.getOrDefault("duration", 24000.0d);

                var id = Identifier.tryParse(idStr);
                if (id == null) {
                    throw new IllegalStateException("Invalid effect ID " + idStr);
                }

                consumer.accept(new ParsedEffect(id, (int)amplifier, (int)duration));
            } catch (Exception e) {
                VillagerTasks.feature().log().warn(e.getMessage() + " at index " + i);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void parseStandardEquipmentEntry(RegistryAccess registryAccess, List<Map<String, Object>> equipment, RandomSource random,
                                                   Consumer<ParsedRequipment> consumer) {
        for (var i = 0; i < equipment.size(); i++) {
            try {
                var map = equipment.get(i);

                var chance = (double) map.getOrDefault("chance", 1.0d);
                if (chance < random.nextDouble()) continue;

                var slotStr = (String) map.get("slot");
                var itemId = (String) map.get("item");

                var slot = EquipmentSlot.valueOf(slotStr.toUpperCase(Locale.ROOT));
                var stack = new ItemStack(Helpers.resolveItem(registryAccess, itemId, random));

                if (stack.isEmpty()) {
                    throw new IllegalStateException("Item " + itemId + " could not be parsed for equipment");
                }

                var enchantments = (List<Map<String, Object>>) map.get("enchantments");
                if (enchantments != null) {
                    applyEnchantments(registryAccess, stack, enchantments, random);
                }

                consumer.accept(new ParsedRequipment(slot, stack));
            } catch (Exception e) {
                VillagerTasks.feature().log().warn(e.getMessage() + " at index " + i);
            }
        }
    }

    public static void applyEnchantments(RegistryAccess registryAccess, ItemStack stack,
                                         List<Map<String, Object>> enchantments, RandomSource random) {
        var registry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

        for (var map : enchantments) {
            Holder<Enchantment> ench;

            var chance = (double) map.getOrDefault("chance", 1.0d);
            if (chance < random.nextDouble()) continue;

            var level = (int) (double) map.getOrDefault("level", 1.0d);
            var name = (String) map.get("enchantment");

            if (name.equals("random")) {
                ench = EnchantmentsHelper.getRandomEnchantment(registryAccess, stack, random).orElse(null);
            } else {
                var key = ResourceKey.create(Registries.ENCHANTMENT, Identifier.parse(name));
                ench = registry.get(key).orElse(null);
            }

            if (ench != null && ench.value().canEnchant(stack)) {
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

    public record ParsedItem(ItemStack stack, int count, int weight) {}

    public record ParsedEffect(Identifier effect, int amplifier, int duration) {}

    public record ParsedRequipment(EquipmentSlot slot, ItemStack stack) {}
}
