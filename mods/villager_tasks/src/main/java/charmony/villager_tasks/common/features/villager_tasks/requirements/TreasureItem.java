package charmony.villager_tasks.common.features.villager_tasks.requirements;

import charmony.core.base.Log;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.PlayerHolder;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.RemovesStacksOnCompletion;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TreasureItem implements Satisfiable, PlayerHolder, RemovesStacksOnCompletion {
    public static final String TREASURE_TAG = "charmony_treasure";

    private final ItemStack stack;
    private final String lootTable;
    private final UUID uniqueId;
    private final double chance;
    private boolean discovered;
    @Nullable private Player player;

    public static final Codec<TreasureItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.CODEC.fieldOf("stack").forGetter(self -> self.stack),
        UUIDUtil.CODEC.fieldOf("unique_id").forGetter(self -> self.uniqueId),
        Codec.STRING.fieldOf("loot_table").forGetter(self -> self.lootTable),
        Codec.DOUBLE.fieldOf("chance").forGetter(self -> self.chance),
        Codec.BOOL.fieldOf("discovered").forGetter(self -> self.discovered)
    ).apply(instance, TreasureItem::new));

    public TreasureItem(ItemStack stack, UUID uniqueId, String lootTable, double chance, boolean discovered) {
        this.stack = stack;
        this.uniqueId = uniqueId;
        this.lootTable = lootTable;
        this.chance = chance;
        this.discovered = discovered;
    }

    public TreasureItem copy() {
        return new TreasureItem(stack.copy(), uniqueId, lootTable, chance, discovered);
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

    @Override
    public ItemStack stack() {
        return stack;
    }

    public double chance() {
        return chance;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    public Identifier lootTable() {
        return Identifier.parse(lootTable);
    }

    public boolean isTreasure(ItemStack stack) {
        if (stack.isEmpty()) return false;

        // Check it's the same item type.
        if (!stack.is(stack().getItem())) {
            return false;
        }

        // Check enchantments are the same.
        if (!stack.getEnchantments().equals(stack().getEnchantments())) {
            return false;
        }

        // Check name is the same.
        if (!stack.getHoverName().equals(stack().getHoverName())) {
            return false;
        }

        // Check it has the correct unique ID for the task.
        var custom = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        var tag = custom.copyTag();

        if (tag.contains(TREASURE_TAG)) {
            var treasureTag = tag.getString(TREASURE_TAG).orElse("");
            return treasureTag.equals(uniqueId().toString());
        }

        return false;
    }

    public void onItemPickup(ItemStack itemStack) {
        if (!discovered && isTreasure(itemStack)) {
            log().debug("Player has discovered treasure item");
            discovered = true;
        }
    }

    public Optional<ItemStack> onLootTablePopulate(Task task, Identifier lootTableId, RandomSource randomSource) {
        if (!discovered && task.isStarted()) {
            if (lootTableId.equals(lootTable())) {
                if (randomSource.nextDouble() < chance()) {
                    log().debug("Providing treasure item from loot table {}", lootTableId);
                    discovered = true;
                    var treasureStack = stack().copy();
                    return Optional.of(treasureStack);
                } else {
                    log().debug("Treasure item not provided from loot table {} due to chance", lootTableId);
                }
            }
        }

        return Optional.empty();
    }

    private Log log() {
        return VillagerTasks.feature().log();
    }
}
