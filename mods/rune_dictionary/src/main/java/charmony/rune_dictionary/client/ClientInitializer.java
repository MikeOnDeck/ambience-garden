package charmony.rune_dictionary.client;

import charmony.api.core.Side;
import charmony.rune_dictionary.RuneDictionaryMod;
import charmony.rune_dictionary.client.features.RuneDictionary;
import net.fabricmc.api.ClientModInitializer;

public final class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ensure charmony is launched first.
        charmony.core.client.ClientInitializer.init();

        // Prepare and run the mod.
        var mod = RuneDictionaryMod.instance();
        mod.addSidedFeature(RuneDictionary.class);
        mod.run(Side.Client);
    }
}
