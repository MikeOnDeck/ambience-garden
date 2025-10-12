package charmony.tasks.client;

import charmony.api.core.Side;
import charmony.tasks.TasksMod;
import net.fabricmc.api.ClientModInitializer;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Init charmony first.
        charmony.core.client.ClientInitializer.init();

        // Bootstrap and run the mod.
        var travelJournal = TasksMod.instance();
//        travelJournal.addSidedFeature(TravelJournal.class);
        travelJournal.run(Side.Client);
    }
}
