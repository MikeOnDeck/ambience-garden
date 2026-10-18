package com.mikeondeck.ambiencegarden.client;

import net.fabricmc.api.ClientModInitializer;
import com.mikeondeck.ambiencegarden.AmbientSoundsMod;
import com.mikeondeck.ambiencegarden.client.features.biomes.Biomes;
import com.mikeondeck.ambiencegarden.client.features.environment.Environment;
import com.mikeondeck.ambiencegarden.client.features.settings.Settings;
import com.mikeondeck.ambiencegarden.client.gui.SoundOptionsHook;
import com.mikeondeck.ambiencegarden.client.sound.AmbientSoundEvents;
import com.mikeondeck.ambiencegarden.api.core.Side;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Init charmony first.
        com.mikeondeck.ambiencegarden.core.client.ClientInitializer.init();

        // Register sound events before features construct them.
        AmbientSoundEvents.registerAll();

        // Bootstrap and run the mod.
        var ambientSounds = AmbientSoundsMod.instance();
        ambientSounds.addSidedFeature(Settings.class);
        ambientSounds.addSidedFeature(Biomes.class);
        ambientSounds.addSidedFeature(Environment.class);
        ambientSounds.run(Side.Client);

        SoundOptionsHook.register();
        AmbientStartup.hook();
    }
}
