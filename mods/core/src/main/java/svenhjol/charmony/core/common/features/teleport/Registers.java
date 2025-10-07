package svenhjol.charmony.core.common.features.teleport;

import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.api.events.PlayerTickCallback;

public class Registers extends Setup<Teleport> {
    public Registers(Teleport feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            PlayerTickCallback.EVENT.register(feature().handlers::playerTick);
        };
    }
}
