package charmony.villager_tasks.common.features.villager_tasks.aspects;

import charmony.core.helpers.UuidHelper;
import charmony.villager_tasks.common.features.villager_tasks.Aspect;
import charmony.villager_tasks.common.features.villager_tasks.Helpers;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import charmony.villager_tasks.common.features.villager_tasks.requirements.TreasureItem;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Treasure extends Aspect implements Satisfiable {
    public static final String ID = "treasure";

    private final List<TreasureItem> items;

    public static final Codec<Treasure> CODEC = TreasureItem.CODEC.listOf().fieldOf("items")
        .xmap(Treasure::new, treasure -> treasure.items).codec();

    public static final Treasure EMPTY = new Treasure(List.of());

    public Treasure(List<TreasureItem> items) {
        this.items = items;
    }

    @SuppressWarnings("unchecked")
    public static Treasure make(Task.AspectBuilder builder) {
        var map = builder.definition().treasure;
        if (map.isEmpty()) return EMPTY;

        var random = builder.random();
        var multiplier = builder.modifier().negativeMultiplier();

        var items = (List<Map<String, Object>>)map.getOrDefault("items", List.of());
        if (items.isEmpty()) {
            throw new IllegalStateException("Aspect requires at least one item.");
        }

        var count = Math.min(items.size(), Helpers.getCountFromMap(map, multiplier, random));
        var criteria = new ArrayList<TreasureItem>();

        for (var i = 0; i < items.size(); i++) {
            try {
                var uniqueId = UuidHelper.fromRandom(random);
                var itemMap = items.get(i);
                var itemId = (String) itemMap.get("item");
                var lootTable = (String) itemMap.get("loot_table");
                var chance = (double) itemMap.getOrDefault("chance", 1.0d);
                var itemStack = Helpers.createTreasureItemStack(builder.registryAccess(), itemId, uniqueId, random);

                if (itemStack.isEmpty()) {
                    throw new IllegalStateException("Item " + itemId + " could not be parsed");
                }

                criteria.add(new TreasureItem(itemStack, uniqueId, lootTable, chance, false));
            } catch (Exception e) {
                log().warn(e.getMessage() + " at index " + i);
            }
        }

        if (criteria.isEmpty()) {
            return EMPTY;
        }

        Util.shuffle(criteria, random);
        var list = criteria.subList(0, Math.min(count, criteria.size()));
        return new Treasure(list);
    }

    @Override
    public Treasure copy() {
        return new Treasure(items.stream().map(TreasureItem::copy).toList());
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Component getName() {
        return Resources.TREASURE_ASPECT;
    }

    @Override
    public boolean isEmpty() {
        return items().isEmpty();
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        return items().stream().mapToInt(TreasureItem::remaining).sum();
    }

    @Override
    public int total() {
        return items().stream().mapToInt(TreasureItem::total).sum();
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
    public void onItemPickup(Task task, Player player, ItemStack itemStack) {
        items().forEach(i -> i.onItemPickup(task, player, itemStack));
    }

    @Override
    public Optional<ItemStack> onLootTablePopulate(Task task, Player player, ResourceLocation lootTableId, RandomSource random) {
        if (player == null) {
            return Optional.empty();
        }

        for (var item : items()) {
            var stack = item.onLootTablePopulate(task, player, lootTableId, random);
            if (stack.isPresent()) {
                return stack;
            }
        }
        return Optional.empty();
    }

    public List<TreasureItem> items() {
        return items;
    }
}
