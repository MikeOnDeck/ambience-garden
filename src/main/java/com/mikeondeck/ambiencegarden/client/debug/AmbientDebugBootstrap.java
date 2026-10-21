package com.mikeondeck.ambiencegarden.client.debug;

import com.mikeondeck.ambiencegarden.AmbientSoundsMod;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public final class AmbientDebugBootstrap {
    private static final Identifier DEBUG_OVERLAY_ID = Identifier.fromNamespaceAndPath(
        AmbientSoundsMod.ID,
        "ambient_debug_overlay"
    );

    private AmbientDebugBootstrap() {}

    public static void register() {
        AmbientDebugKeys.register();

        HudElementRegistry.attachElementAfter(
            VanillaHudElements.MISC_OVERLAYS,
            DEBUG_OVERLAY_ID,
            AmbientDebugOverlay::render
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (AmbientDebugKeys.toggleOverlay.consumeClick()) {
                AmbientDebugOverlay.toggle();
            }
            AmbientDebugOverlayVerifier.endClientTick();
        });

        if (AmbientDebugOverlayVerifier.enabled()) {
            AmbientDebugOverlayVerifier.hook();
        }
    }
}
