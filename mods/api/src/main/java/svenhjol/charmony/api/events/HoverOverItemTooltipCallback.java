package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@SuppressWarnings("unused")
public interface HoverOverItemTooltipCallback {
    Event<HoverOverItemTooltipCallback> EVENT = EventFactory.createArrayBacked(HoverOverItemTooltipCallback.class,
        listeners -> (stack, lines, tooltipFlag) -> {
            for (HoverOverItemTooltipCallback listener : listeners) {
                listener.interact(stack, lines, tooltipFlag);
            }
        });

    void interact(ItemStack stack, List<Component> lines, TooltipFlag tooltipFlag);
}
