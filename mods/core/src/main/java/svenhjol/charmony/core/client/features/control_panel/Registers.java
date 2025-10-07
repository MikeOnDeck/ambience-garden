package svenhjol.charmony.core.client.features.control_panel;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.components.ImageButton;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<ControlPanel> {
    public final ImageButton settingsButton;

    public Registers(ControlPanel feature) {
        super(feature);

        settingsButton = new ImageButton(0, 0, 20, 20,
            FeaturesScreen.SETTINGS_BUTTON, button -> feature().handlers.openSettingsScreen());
    }

    @Override
    public Runnable boot() {
        return () -> {
            ClientTickEvents.END_CLIENT_TICK.register(feature().handlers::clientTick);
        };
    }
}
