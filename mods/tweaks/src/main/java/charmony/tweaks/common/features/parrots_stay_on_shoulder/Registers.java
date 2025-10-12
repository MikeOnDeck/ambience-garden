package charmony.tweaks.common.features.parrots_stay_on_shoulder;

import charmony.core.base.Setup;
import charmony.api.events.PlayerTickCallback;

public class Registers extends Setup<ParrotsStayOnShoulder> {
    public Registers(ParrotsStayOnShoulder feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            PlayerTickCallback.EVENT.register(feature().handlers::playerTick);
        };
    }
}
