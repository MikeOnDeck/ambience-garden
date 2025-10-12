package charmony.tasks.common;

import charmony.api.core.Side;
import charmony.tasks.TasksMod;
import net.fabricmc.api.ModInitializer;

public class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Init charmony first.
        charmony.core.common.CommonInitializer.init();

        // Bootstrap and run the common features.
        var travelJournal = TasksMod.instance();
//        travelJournal.addSidedFeature(TravelJournal.class);
        travelJournal.run(Side.Common);
    }
}
