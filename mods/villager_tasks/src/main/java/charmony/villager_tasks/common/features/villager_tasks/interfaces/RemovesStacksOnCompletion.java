package charmony.villager_tasks.common.features.villager_tasks.interfaces;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Remove item stacks from the player's inventory upon task completion.
 */
public interface RemovesStacksOnCompletion extends Satisfiable {
    ItemStack stack();

    default void onComplete(ServerPlayer player) {
        var remainder = total();

        if (remainder > 0) {
            for (var invItem : player.getInventory().getNonEquipmentItems()) {
                // Don't seek enchanted items if the required item isn't enchanted.
                if (!stack().isEnchanted() && invItem.isEnchanted()) continue;

                // Don't seek damaged items.
                if (invItem.isDamaged()) continue;

                if (invItem.is(stack().getItem())) {
                    // Must match enchantments if the required item is enchanted.
                    if (stack().isEnchanted() && !invItem.getEnchantments().equals(stack().getEnchantments())) continue;

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
