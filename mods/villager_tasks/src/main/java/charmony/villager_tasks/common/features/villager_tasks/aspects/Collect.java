package charmony.villager_tasks.common.features.villager_tasks.aspects;

import charmony.villager_tasks.common.features.villager_tasks.Aspect;
import charmony.villager_tasks.common.features.villager_tasks.Helpers;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import charmony.villager_tasks.common.features.villager_tasks.requirements.CollectItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Collect copy() {
        return new Collect(new ArrayList<>(items));
    }

    @SuppressWarnings("unchecked")
    public static Collect make(Task.AspectBuilder builder) {
        var map = builder.definition().collect;
        if (map.isEmpty()) return EMPTY;

        var random = builder.random();
        var multiplier = builder.modifier().negativeMultiplier();

        // Resolve items from map.
        var items = (List<Map<String, Object>>)map.getOrDefault("items", List.of());
        if (items.isEmpty()) {
            throw new IllegalStateException("Aspect requires at least one item.");
        }

        var count = Math.min(items.size(), Helpers.getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<CollectItem>();

        for (var i = 0; i < items.size(); i++) {
            try {
                var itemMap = items.get(i);
                var itemId = (String) itemMap.get("item");
                var itemWeight = (double) itemMap.getOrDefault("weight", 1.0d);
                var itemStack = new ItemStack(Helpers.resolveItem(builder.registryAccess(), itemId, random));
                var itemCount = Helpers.getCountFromMap(itemMap, multiplier, random);

                criteria.add(new CollectItem(itemStack, itemCount, (int)itemWeight));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to parse item at index " + i, e);
            }
        }

        var collectItems = Helpers.getRandomlyByWeight(criteria, count, random);
        return new Collect(collectItems);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getName() {
        return Resources.COLLECT_ASPECT;
    }

    @Override
    public boolean isEmpty() {
        return items().isEmpty();
    }

    @Override
    public void onTick(Task task, Player player) {
        super.onTick(task, player);

        // Pass player down to each item requirement on tick.
        for (var item : items()) {
            item.setPlayer(player);
        }
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        return items().stream().mapToInt(CollectItem::remaining).sum();
    }

    @Override
    public int total() {
        return items().stream().mapToInt(CollectItem::total).sum();
    }

    @Override
    public void onComplete(Task task, ServerPlayer player) {
        for (var req : items()) {
            var remainder = req.total();

            if (remainder > 0) {
                for (var invItem : player.getInventory().getNonEquipmentItems()) {
                    if (remainder <= 0) break;

                    // TODO: check enchantments.
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

    public List<CollectItem> items() {
        return items;
    }
}
