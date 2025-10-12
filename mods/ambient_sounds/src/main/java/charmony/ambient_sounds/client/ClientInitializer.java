package charmony.ambient_sounds.client;

import net.fabricmc.api.ClientModInitializer;
import charmony.ambient_sounds.AmbientSoundsMod;
import charmony.ambient_sounds.client.features.biomes.Biomes;
import charmony.ambient_sounds.client.features.environment.Environment;
import charmony.ambient_sounds.client.features.settings.Settings;
import charmony.api.core.Side;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Init charmony first.
        charmony.core.client.ClientInitializer.init();

        // Bootstrap and run the mod.
        var ambientSounds = AmbientSoundsMod.instance();
        ambientSounds.addSidedFeature(Settings.class);
        ambientSounds.addSidedFeature(Biomes.class);
        ambientSounds.addSidedFeature(Environment.class);
        ambientSounds.run(Side.Client);
    }
}
