package charmony.villager_tasks.common.features.villager_tasks.requirements;

import charmony.villager_tasks.common.features.villager_tasks.Criteria;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.HasWeight;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CollectCriteria extends Criteria implements HasWeight {
    private final ItemStack stack;
    private final int total;
    private final int weight;

    public static final Codec<CollectCriteria> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.CODEC.fieldOf("stack").forGetter(self -> self.stack),
        Codec.INT.fieldOf("total").forGetter(self -> self.total),
        Codec.INT.fieldOf("weight").forGetter(self -> self.weight)
    ).apply(instance, CollectCriteria::new));

    public CollectCriteria(ItemStack stack, int total, int weight) {
        this.stack = stack;
        this.total = total;
        this.weight = weight;
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        var player = getTask().flatMap(Task::getPlayer).orElse(null);
        if (player == null) return total;

        var remainder = total;

        if (remainder > 0) {
            // Make safe copy of the player's inventory.
            List<ItemStack> inventory = new ArrayList<>();
            for (var stack : player.getInventory().getNonEquipmentItems()) {
                inventory.add(stack.copy());
            }

            for (var invItem : inventory) {
                if (invItem.is(stack.getItem()) && !invItem.isDamaged()) {
                    var decrement = Math.min(remainder, invItem.getCount());
                    remainder -= decrement;
                    invItem.shrink(decrement);
                }
            }
        }

        return Math.max(0, remainder);
    }

    @Override
    public void onComplete(ServerPlayer player) {
        var remainder = total;

        for (var invItem : player.getInventory().getNonEquipmentItems()) {
            if (remainder <= 0) continue;

            if (invItem.is(stack.getItem()) && !invItem.isDamaged()) {
                var decrement = Math.min(remainder, invItem.getCount());
                remainder -= decrement;

                if (!player.getAbilities().instabuild) {
                    invItem.shrink(decrement);
                }
            }
        }
    }

    @Override
    public int getWeight() {
        return weight;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getTotal() {
        return total;
    }
}
