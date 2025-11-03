package charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Custom Fabric event that is fired when the player picks up an item.
 */
public interface ItemPickupCallback {
    Event<ItemPickupCallback> EVENT = EventFactory.createArrayBacked(ItemPickupCallback.class, listeners
        -> (player, itemEntity) -> {
            for (var listener : listeners) {
                var result = listener.interact(player, itemEntity);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
            return InteractionResult.PASS;
        });

    InteractionResult interact(Player player, ItemEntity itemEntity);
}
