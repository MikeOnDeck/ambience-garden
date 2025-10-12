package charmony.travel_journal.common;

import net.fabricmc.api.ModInitializer;
import charmony.api.core.Side;
import charmony.travel_journal.TravelJournalMod;
import charmony.travel_journal.common.features.travel_journal.TravelJournal;

public class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Init charmony first.
        charmony.core.common.CommonInitializer.init();

        // Bootstrap and run the common features.
        var travelJournal = TravelJournalMod.instance();
        travelJournal.addSidedFeature(TravelJournal.class);
        travelJournal.run(Side.Common);
    }
}
