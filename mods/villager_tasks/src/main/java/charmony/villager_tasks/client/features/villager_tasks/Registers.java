package charmony.villager_tasks.client.features.villager_tasks;

import charmony.api.events.SetupScreenCallback;
import charmony.core.base.Setup;
import charmony.core.client.ClientRegistry;
import charmony.villager_tasks.common.features.villager_tasks.Networking.S2CSendActiveTasks;
import charmony.villager_tasks.common.features.villager_tasks.Networking.S2CSendAvailableTasks;
import charmony.villager_tasks.common.features.villager_tasks.Networking.S2CSendMerchantInteraction;

public class Registers extends Setup<VillagerTasks> {
    public Registers(VillagerTasks feature) {
        super(feature);

        var registry = ClientRegistry.forFeature(feature);

        registry.packetReceiver(S2CSendActiveTasks.TYPE, feature.handlers::handleReceiveActiveTasks);
        registry.packetReceiver(S2CSendAvailableTasks.TYPE, feature.handlers::handleReceiveAvailableTasks);
        registry.packetReceiver(S2CSendMerchantInteraction.TYPE, feature.handlers::handleReceiveMerchantInteraction);
    }

    @Override
    public Runnable boot() {
        return () -> {
            SetupScreenCallback.EVENT.register(feature().handlers::setupScreen);
        };
    }
}
