package svenhjol.charmony.tweaks.common.features.villager_attracting;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.api.events.PlayerTickCallback;

public class Registers extends Setup<VillagerAttracting> {
    public Registers(VillagerAttracting feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            ServerEntityEvents.ENTITY_LOAD.register(feature().handlers::entityJoin);
            PlayerTickCallback.EVENT.register(feature().handlers::playerTick);
        };
    }
}
