package charmony.villager_tasks.common.features.villager_tasks.requirements;

import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.PlayerHolder;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TreasureItem implements Satisfiable, PlayerHolder {
    public static final String TREASURE_TAG = "charmony_treasure";

    private final ItemStack stack;
    private final String lootTable;
    private final UUID itemId;
    private final double chance;
    private boolean discovered;
    @Nullable private Player player;

    public static final Codec<TreasureItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.CODEC.fieldOf("stack").forGetter(self -> self.stack),
        UUIDUtil.CODEC.fieldOf("item_id").forGetter(self -> self.itemId),
        Codec.STRING.fieldOf("loot_table").forGetter(self -> self.lootTable),
        Codec.DOUBLE.fieldOf("chance").forGetter(self -> self.chance),
        Codec.BOOL.fieldOf("discovered").forGetter(self -> self.discovered)
    ).apply(instance, TreasureItem::new));

    public TreasureItem(ItemStack stack, UUID itemId, String lootTable, double chance, boolean discovered) {
        this.stack = stack;
        this.itemId = itemId;
        this.lootTable = lootTable;
        this.chance = chance;
        this.discovered = false;
    }

    public TreasureItem copy() {
        return new TreasureItem(stack.copy(), itemId, lootTable, chance, discovered);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        var player = getPlayer().orElse(null);
        if (player == null) return total();
        var remainder = total();

        if (remainder > 0) {
            // Make safe copy of the player's inventory.
            List<ItemStack> inventory = new ArrayList<>();
            for (var stack : player.getInventory().getNonEquipmentItems()) {
                inventory.add(stack.copy());
            }

            for (var invItem : inventory) {
                if (isTreasure(invItem)) {
                    remainder -= 1;
                }
            }
        }

        return Math.max(0, remainder);
    }

    @Override
    public int total() {
        return 1;
    }

    @Override
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(player);
    }

    public void onItemPickup(Task task, Player player, ItemStack itemStack) {
        if (!discovered && isTreasure(itemStack)) {
            VillagerTasks.feature().log().debug("Player has discovered treasure item");
            discovered = true;
        }
    }

    public void onLootTableModify(LootTable.Builder builder) {
        var enchantments = stack.getEnchantments();
        var customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var customName = stack.getDisplayName();

        var pool = LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .when(LootItemRandomChanceCondition.randomChance((float)chance))
            .add(LootItem.lootTableItem(stack.getItem())
                .apply(SetComponentsFunction.setComponent(DataComponents.ENCHANTMENTS, enchantments))
                .apply(SetComponentsFunction.setComponent(DataComponents.CUSTOM_DATA, customData))
                .apply(SetComponentsFunction.setComponent(DataComponents.CUSTOM_NAME, customName))
                .setWeight(1));

        builder.pool(pool.build());
    }

    public boolean isTreasure(ItemStack stack) {
        var custom = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var tag = custom.copyTag();

        if (tag.contains(TREASURE_TAG)) {
            var treasureTag = tag.getString(TREASURE_TAG).orElse("");
            return treasureTag.equals(this.itemId.toString());
        }

        return false;
    }
}
