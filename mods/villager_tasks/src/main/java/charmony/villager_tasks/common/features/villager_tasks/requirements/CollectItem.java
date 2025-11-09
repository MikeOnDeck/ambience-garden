package charmony.villager_tasks.common.features.villager_tasks.requirements;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.HasWeight;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.PlayerHolder;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.RemovesStacksOnCompletion;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CollectItem implements Satisfiable, PlayerHolder, HasWeight, RemovesStacksOnCompletion {
    private final ItemStack stack;
    private final int total;
    private final int weight;
    @Nullable private Player player;

    public static final Codec<CollectItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.CODEC.fieldOf("stack").forGetter(self -> self.stack),
        Codec.INT.fieldOf("total").forGetter(self -> self.total),
        Codec.INT.fieldOf("weight").forGetter(self -> self.weight)
    ).apply(instance, CollectItem::new));

    public CollectItem(ItemStack stack, int total, int weight) {
        this.stack = stack;
        this.total = total;
        this.weight = weight;
    }

    public CollectItem copy() {
        return new CollectItem(stack.copy(), total, weight);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Optional<Player> getPlayer() {
        return Optional.ofNullable(this.player);
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
                // Don't seek enchanted items if the required item isn't enchanted.
                if (!stack().isEnchanted() && invItem.isEnchanted()) continue;

                // Don't seek damaged items.
                if (invItem.isDamaged()) continue;

                if (invItem.is(stack().getItem())) {
                    // Must match enchantments if the required item is enchanted.
                    if (stack().isEnchanted() && !invItem.getEnchantments().equals(stack().getEnchantments())) continue;

                    var decrement = Math.min(remainder, invItem.getCount());
                    remainder -= decrement;
                    invItem.shrink(decrement);
                }
            }
        }

        return Math.max(0, remainder);
    }

    @Override
    public int total() {
        return total;
    }

    @Override
    public ItemStack stack() {
        return stack;
    }

    @Override
    public int weight() {
        return weight;
    }
}
