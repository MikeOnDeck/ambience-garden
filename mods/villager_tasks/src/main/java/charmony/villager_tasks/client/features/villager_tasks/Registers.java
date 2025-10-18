package charmony.villager_tasks.client.features.villager_tasks;

import charmony.api.events.SetupScreenCallback;
import charmony.core.base.Setup;
import charmony.core.client.ClientRegistry;
import charmony.villager_tasks.common.features.villager_tasks.Networking.S2CTasks;

public class Registers extends Setup<VillagerTasks> {
    public Registers(VillagerTasks feature) {
        super(feature);

        var registry = ClientRegistry.forFeature(feature);

        registry.packetReceiver(S2CTasks.TYPE, feature.handlers::handleTasks);
    }

    @Override
    public Runnable boot() {
        return () -> {
            SetupScreenCallback.EVENT.register(feature().handlers::setupScreen);
        };
    }
}
