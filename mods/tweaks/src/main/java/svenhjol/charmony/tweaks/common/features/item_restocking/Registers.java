package svenhjol.charmony.tweaks.common.features.item_restocking;

import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.api.events.PlayerTickCallback;

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
