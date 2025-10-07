package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Custom Fabric event that fires whenever a tooltip render is called.
 * This can be used to render custom tooltips e.g. filled maps.
 */
@SuppressWarnings("unused")
public interface RenderTooltipCallback {
    Event<RenderTooltipCallback> EVENT = EventFactory.createArrayBacked(RenderTooltipCallback.class,
        listeners -> ((guiGraphics, stack, lines, x, y) -> {
            for (var listener : listeners) {
                listener.interact(guiGraphics, stack, lines, x, y);
            }
        }));

    void interact(GuiGraphics guiGraphics, ItemStack stack, List<ClientTooltipComponent> lines, int x, int y);
}
