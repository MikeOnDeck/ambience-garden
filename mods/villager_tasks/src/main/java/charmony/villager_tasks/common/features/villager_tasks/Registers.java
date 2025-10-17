package charmony.villager_tasks.common.features.villager_tasks;

import charmony.api.core.Side;
import charmony.core.base.Setup;
import charmony.core.common.CommonRegistry;
import charmony.villager_tasks.common.features.villager_tasks.Networking.S2CTasks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class Registers extends Setup<VillagerTasks> {
    public Registers(VillagerTasks feature) {
        super(feature);
        var registry = CommonRegistry.forFeature(feature);

        // Packet registration.
        registry.packetSender(Side.Common, S2CTasks.TYPE, S2CTasks.CODEC);
    }

    @Override
    public Runnable boot() {
        return () -> {
            ServerWorldEvents.LOAD.register(((server, level) -> {
                // Load all definitions on world load. We need tags to already be resolved.
                feature().handlers.loadDefinitions(server);
            }));

            ServerEntityEvents.ENTITY_LOAD.register(feature().handlers::entityJoin);
        };
    }
}
