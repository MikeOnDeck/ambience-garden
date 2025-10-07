package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * This is a copy of Fabric's deprecated HudRenderCallback.
 * The non-deprecated API was removed in the 1.21.6 snapshots
 * and this seems to still work until a new API is ready.
 *
 * @see net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
 */
@SuppressWarnings({"unused", "deprecation"})
public interface HudDisplayCallback {
    Event<HudDisplayCallback> EVENT = EventFactory.createArrayBacked(HudDisplayCallback.class,
        listeners -> (guiGraphics, deltaTracker) -> {
            for (var listener : listeners) {
                listener.interact(guiGraphics, deltaTracker);
            }
        });

    void interact(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
}
