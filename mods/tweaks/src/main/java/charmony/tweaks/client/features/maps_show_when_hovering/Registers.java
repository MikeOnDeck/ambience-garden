package charmony.tweaks.client.features.maps_show_when_hovering;

import charmony.core.base.Setup;
import charmony.api.events.RenderTooltipCallback;

public class Registers extends Setup<MapsShowWhenHovering> {
    public Registers(MapsShowWhenHovering feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> RenderTooltipCallback.EVENT.register(feature().handlers::renderMapTooltip);
    }
}
