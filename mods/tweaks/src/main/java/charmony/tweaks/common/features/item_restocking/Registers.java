package charmony.tweaks.common.features.item_restocking;

import charmony.core.base.Setup;
import charmony.api.events.PlayerTickCallback;

public class Registers extends Setup<ItemRestocking> {
    public Registers(ItemRestocking feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            PlayerTickCallback.EVENT.register(feature().handlers::playerTick);
        };
    }
}
