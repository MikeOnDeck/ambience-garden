package charmony.travel_journal.client;

import net.fabricmc.api.ClientModInitializer;
import charmony.api.core.Side;
import charmony.travel_journal.TravelJournalMod;
import charmony.travel_journal.client.features.travel_journal.TravelJournal;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Init charmony first.
        charmony.core.client.ClientInitializer.init();

        // Bootstrap and run the mod.
        var travelJournal = TravelJournalMod.instance();
        travelJournal.addSidedFeature(TravelJournal.class);
        travelJournal.run(Side.Client);
    }
}
