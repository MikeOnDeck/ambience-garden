package charmony.rune_dictionary.common;

import charmony.api.core.Side;
import charmony.rune_dictionary.RuneDictionaryMod;
import charmony.rune_dictionary.common.features.rune_dictionary.RuneDictionary;
import net.fabricmc.api.ModInitializer;

public final class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Ensure charmony is launched first.
        charmony.core.common.CommonInitializer.init();

        // Prepare and run the mod.
        var mod = RuneDictionaryMod.instance();
        mod.addSidedFeature(RuneDictionary.class);
        mod.run(Side.Common);
    }
}
