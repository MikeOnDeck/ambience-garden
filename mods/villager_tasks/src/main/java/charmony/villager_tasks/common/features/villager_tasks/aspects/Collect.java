package charmony.villager_tasks.common.features.villager_tasks.aspects;

import charmony.villager_tasks.common.features.villager_tasks.Aspect;
import charmony.villager_tasks.common.features.villager_tasks.Definition;
import charmony.villager_tasks.common.features.villager_tasks.Helpers;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import charmony.villager_tasks.common.features.villager_tasks.requirements.CollectItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class Collect extends Aspect implements Satisfiable {
    public static final String ID = "collect";

    private final List<CollectItem> items;

//    public static final Codec<Collect> CODEC = CollectItem.CODEC.listOf().fieldOf("items").xmap(
//        Collect::new,
//        collect -> collect.items
//    ).codec();

    public static final Codec<Collect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        CollectItem.CODEC.listOf().fieldOf("items").forGetter(collect -> collect.items)
    ).apply(instance, Collect::new));

    public static final Collect EMPTY = new Collect(List.of());

    public Collect(List<CollectItem> items) {
        this.items = items;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getName() {
        return Resources.COLLECT_ASPECT;
    }

    public static Collect make(RegistryAccess registryAccess, Definition definition, double multiplier, RandomSource random) {
        var map = definition.collect;
        if (map.isEmpty()) {
            return EMPTY;
        }

        // Resolve items from map.
        var items = (List<Map<String, Object>>)map.getOrDefault("items", List.of());
        if (items.isEmpty()) {
            throw new IllegalStateException("Collect requires at least one item to collect.");
        }

        var count = Math.min(items.size(), Helpers.getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<CollectItem>();

        for (var i = 0; i < items.size(); i++) {
            try {
                var itemMap = items.get(i);
                var itemId = (String) itemMap.get("item");
                var itemWeight = (double) itemMap.getOrDefault("weight", 1.0d);
                var itemStack = new ItemStack(Helpers.resolveItem(registryAccess, itemId, random));
                var itemCount = Helpers.getCountFromMap(itemMap, multiplier, random);

                criteria.add(new CollectItem(itemStack, itemCount, (int)itemWeight));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to parse collect item at index " + i, e);
            }
        }

        var collectItems = Helpers.getRandomlyByWeight(criteria, count, random);
        return new Collect(collectItems);
    }

    public int total() {
        // Get the cumulative total of all items.
        return items.stream().mapToInt(CollectItem::total).sum();
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        var player = getPlayer().orElse(null);
        if (player == null) return total();
        var fullRemainder = total();

        // Iterate over the player's inventory and decrement the remainder for each matching item found.
        for (var req : items) {
            var remainder = req.total();

            if (remainder > 0) {
                // Make safe copy of the player's inventory.
                List<ItemStack> inventory = new ArrayList<>();
                for (var stack : player.getInventory().getNonEquipmentItems()) {
                    inventory.add(stack.copy());
                }

                for (var invItem : inventory) {
                    if (invItem.is(req.stack().getItem()) && !invItem.isDamaged()) {
                        var decrement = Math.min(remainder, invItem.getCount());
                        remainder -= decrement;
                        invItem.shrink(decrement);
                    }
                }
            }

            fullRemainder -= remainder;
        }

        return Math.max(0, fullRemainder);
    }

    @Override
    public void onComplete(ServerPlayer player) {
        for (var req : items) {
            var remainder = req.total();

            if (remainder > 0) {
                for (var invItem : player.getInventory().getNonEquipmentItems()) {
                    if (remainder <= 0) break;

                    if (invItem.is(req.stack().getItem()) && !invItem.isDamaged()) {
                        var decrement = Math.min(remainder, invItem.getCount());
                        remainder -= decrement;

                        if (!player.getAbilities().instabuild) {
                            invItem.shrink(decrement);
                        }
                    }
                }
            }
        }
    }
}
