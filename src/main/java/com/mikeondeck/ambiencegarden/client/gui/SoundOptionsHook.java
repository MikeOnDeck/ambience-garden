package com.mikeondeck.ambiencegarden.client.gui;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.network.chat.Component;

/**
 * Adds an Ambience Garden button to vanilla Options, Music and Sounds.
 * Uses the screen event rather than a mixin so the button sits in the existing
 * options list, which a controller can focus like any other vanilla control.
 */
public final class SoundOptionsHook {
    private SoundOptionsHook() {}

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof SoundOptionsScreen soundOptions) || soundOptions.list == null) {
                return;
            }
            var button = Button.builder(
                Component.translatable("gui.ambience_garden.settings.open"),
                pressed -> client.setScreen(new AmbienceGardenSettingsScreen(soundOptions))
            ).width(150).build();
            soundOptions.list.addSmall(button, null);
        });
    }
}
