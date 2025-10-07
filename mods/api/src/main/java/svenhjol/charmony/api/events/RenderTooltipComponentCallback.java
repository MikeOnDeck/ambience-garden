package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Custom Fabric event that is fired when rendering tooltip components in a tooltip.
 * Use this to add extra information to a tooltip e.g. shulker box contents.
 */
@SuppressWarnings("unused")
public interface RenderTooltipComponentCallback {
    Event<RenderTooltipComponentCallback> EVENT = EventFactory.createArrayBacked(RenderTooltipComponentCallback.class,
        listeners -> stack -> {
            for (RenderTooltipComponentCallback listener : listeners) {
                var result = listener.run(stack);

                // Return the first handler that satisfies the itemstack hover.
                if (result.isPresent()) {
                    return result;
                }
            }
            return Optional.empty();
        });

    Optional<TooltipComponent> run(ItemStack stack);
}
