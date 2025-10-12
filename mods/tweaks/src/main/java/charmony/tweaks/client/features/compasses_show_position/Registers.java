package charmony.tweaks.client.features.compasses_show_position;

import charmony.api.events.HudDisplayCallback;
import charmony.api.tweaks.CompassOverlayItem;
import charmony.api.tweaks.CompassOverlayProvider;
import charmony.core.Api;
import charmony.core.base.Setup;

import java.util.ArrayList;
import java.util.List;

public class Registers extends Setup<CompassesShowPosition> {
    public final List<CompassOverlayItem> overlayItems = new ArrayList<>();

    public Registers(CompassesShowPosition feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            Api.consume(CompassOverlayProvider.class,
                provider -> overlayItems.addAll(provider.getCompassOverlayItems()));

            HudDisplayCallback.EVENT.register(feature().handlers::hudRender);
        };
    }
}
