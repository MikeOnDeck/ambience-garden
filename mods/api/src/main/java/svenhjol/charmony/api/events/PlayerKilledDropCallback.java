package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

/**
 * Custom Fabric event that is fired when the player dies and drops inventory.
 */
@SuppressWarnings("unused")
public interface PlayerKilledDropCallback {
    Event<PlayerKilledDropCallback> EVENT = EventFactory.createArrayBacked(PlayerKilledDropCallback.class, listeners
        -> (player, inventory) -> {
            for (var listener : listeners) {
                var result = listener.interact(player, inventory);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
            return InteractionResult.PASS;
        });

    InteractionResult interact(Player player, Inventory inventory);

}
