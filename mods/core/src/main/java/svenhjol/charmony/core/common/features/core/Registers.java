package svenhjol.charmony.core.common.features.core;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.features.core.Networking.S2CUsingCharmonyServer;

public class Registers extends Setup<Core> {
    public Registers(Core feature) {
        super(feature);

        // Server-to-client packets
        PayloadTypeRegistry.playS2C().register(S2CUsingCharmonyServer.TYPE, S2CUsingCharmonyServer.CODEC);
    }

    @Override
    public Runnable boot() {
        return () -> ServerPlayConnectionEvents.JOIN.register(feature().handlers::playerJoin);
    }
}
