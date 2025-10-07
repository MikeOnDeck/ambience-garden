package svenhjol.charmony.api.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.Screen;

/**
 * Custom Fabric event that is triggered when a new screen is initialized.
 * Use this to initialize variables and then use RenderScreenCallback to update them.
 * @see RenderScreenCallback
 */
@SuppressWarnings("unused")
public interface SetupScreenCallback {
    Event<SetupScreenCallback> EVENT = EventFactory.createArrayBacked(SetupScreenCallback.class, listeners -> screen -> {
        for (SetupScreenCallback listener : listeners) {
            listener.interact(screen);
        }
    });

    void interact(Screen screen);
}
