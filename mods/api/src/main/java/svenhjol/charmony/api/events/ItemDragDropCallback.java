package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public interface ItemDragDropCallback {
    Event<ItemDragDropCallback> EVENT = EventFactory.createArrayBacked(ItemDragDropCallback.class, listeners ->
        (stackType, source, dest, slot, clickAction, player, slotAccess) -> {
            for (ItemDragDropCallback listener : listeners) {
                var result = listener.interact(stackType, source, dest, slot, clickAction, player, slotAccess);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }

            return InteractionResult.PASS;
        });

    InteractionResult interact(StackType stackType, ItemStack source, ItemStack dest, Slot slot, ClickAction clickAction, Player player, @Nullable SlotAccess slotAccess);

    enum StackType {
        STACKED_ON_OTHER,
        STACKED_ON_SELF
    }
}
