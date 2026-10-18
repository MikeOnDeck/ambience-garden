package com.mikeondeck.ambiencegarden.client;

import com.mikeondeck.ambiencegarden.client.features.biomes.Biomes;
import com.mikeondeck.ambiencegarden.client.features.environment.Environment;
import com.mikeondeck.ambiencegarden.client.gui.AmbienceGardenSettingsScreen;
import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundEvents;
import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundIds;
import com.mikeondeck.ambiencegarden.core.base.Log;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.options.SoundOptionsScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.IdentityHashMap;

/**
 * Logs that ambient sounds were registered and that sounds.json resolved them.
 * With -Dambience_garden.verifySounds=true, also opens the settings screen,
 * plays one sound, and exits. Used by the headless client check.
 */
public final class AmbientStartup {
    private static final Log LOG = new Log(AmbientSoundIds.NAMESPACE, "Startup");

    private AmbientStartup() {}

    public static boolean verifyMode() {
        return Boolean.getBoolean("ambience_garden.verifySounds")
            || "true".equalsIgnoreCase(System.getenv("AMBIENCE_GARDEN_VERIFY_SOUNDS"));
    }

    public static void hook() {
        ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvents.EndTick() {
            private int ticks;
            private int reportedTick = -1;

            @Override
            public void onEndTick(Minecraft client) {
                ticks++;
                if (reportedTick >= 0) {
                    if (verifyMode() && ticks == reportedTick + 2) {
                        LOG.info("Settings screen class: " + client.screen.getClass().getName());
                        client.stop();
                    }
                    return;
                }
                if (!soundManagerReady(client)) {
                    if (ticks == 200) {
                        LOG.warn("Sound manager was not ready after 200 ticks; reporting anyway");
                    } else if (ticks < 200) {
                        return;
                    }
                }
                reportedTick = ticks;
                LOG.info("Ambient sound features ready (biomes enabled=" + Biomes.feature().enabled()
                    + ", environment enabled=" + Environment.feature().enabled() + ")");
                reportResolvedSounds(client);
                if (verifyMode()) {
                    playProbe(client);
                    var parent = client.screen;
                    client.setScreen(new SoundOptionsScreen(parent, client.options));
                    LOG.info("Music & Sounds has Ambience Garden button: " + containsAmbienceButton(client.screen));
                    client.setScreen(new AmbienceGardenSettingsScreen(parent));
                    LOG.info("Opened Ambience Garden settings screen (" + client.screen.children().size() + " widgets)");
                }
            }
        });
    }

    private static boolean containsAmbienceButton(GuiEventListener listener) {
        return containsAmbienceButton(listener, Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    private static boolean containsAmbienceButton(GuiEventListener listener, java.util.Set<GuiEventListener> seen) {
        if (listener == null || !seen.add(listener)) return false;
        if (listener instanceof AbstractWidget widget && widget.getMessage().getString().contains("Ambience Garden")) {
            return true;
        }
        if (listener instanceof ContainerEventHandler container) {
            for (GuiEventListener child : container.children()) {
                if (containsAmbienceButton(child, seen)) return true;
            }
        }
        return false;
    }

    private static boolean soundManagerReady(Minecraft client) {
        var click = client.getSoundManager().getSoundEvent(Identifier.parse("minecraft:ui.button.click"));
        return click != null && click.getWeight() > 0;
    }

    private static void reportResolvedSounds(Minecraft client) {
        int resolved = 0;
        int missing = 0;
        var manager = client.getSoundManager();
        for (var event : AmbientSoundEvents.registered()) {
            var weighed = manager.getSoundEvent(event.location());
            if (weighed != null && weighed.getWeight() > 0) {
                resolved++;
            } else {
                missing++;
                LOG.warn("Sound event has no audio in sounds.json: " + event.location()
                    + (weighed == null ? " (not loaded)" : " (empty)"));
            }
        }
        int total = AmbientSoundEvents.registered().size();
        LOG.info("Resolved " + resolved + "/" + total + " ambient sound events to playable audio");
        if (missing > 0) {
            LOG.warn(missing + " ambient sound events did not resolve");
        }
    }

    private static void playProbe(Minecraft client) {
        var event = AmbientSoundEvents.get("biome.forest.day");
        try {
            var result = client.getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F, 1.0F));
            LOG.info("Probe played " + event.location() + " with result " + result);
        } catch (RuntimeException e) {
            LOG.warn("Probe playback failed for " + event.location() + ": " + e.getMessage());
        }
    }
}
