package com.mikeondeck.ambiencegarden.client.debug;

import com.mikeondeck.ambiencegarden.AmbientSoundsMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public final class AmbientDebugBootstrap {
    private static final Identifier DEBUG_OVERLAY_ID = Identifier.fromNamespaceAndPath(
        AmbientSoundsMod.ID,
        "ambient_debug_overlay"
    );

    private AmbientDebugBootstrap() {}

    public static void register() {
        AmbientDebugKeys.register();

        // addLast draws with the subtitle pass, after the rest of the HUD has been
        // recorded and still before GuiRenderer flushes into the main framebuffer.
        HudElementRegistry.addLast(DEBUG_OVERLAY_ID, AmbientDebugOverlay::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (AmbientDebugKeys.toggleOverlay.consumeClick()) {
                AmbientDebugOverlay.toggle();
            }
            AmbientDebugOverlayVerifier.endClientTick();
            OutdoorExposureVerifier.endClientTick();
        });

        if (OutdoorExposureVerifier.enabled()) {
            OutdoorExposureVerifier.hook();
        } else if (AmbientDebugOverlayVerifier.enabled()) {
            AmbientDebugOverlayVerifier.hook();
        }
    }
}
