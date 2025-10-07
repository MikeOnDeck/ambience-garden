package svenhjol.charmony.tweaks.client.features.totem_emergency_swap;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<TotemEmergencySwap> {
    public final KeyMapping swapTotemKey;

    public Registers(TotemEmergencySwap feature) {
        super(feature);
        this.swapTotemKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.charmony.swapTotem",
            GLFW.GLFW_KEY_Z,
            KeyMapping.Category.INVENTORY));
    }

    @Override
    public Runnable boot() {
        return () -> {
            ClientTickEvents.END_CLIENT_TICK.register(feature().handlers::clientTick);
        };
    }
}
