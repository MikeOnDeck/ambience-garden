package svenhjol.charmony.tweaks.client.features.maps_show_when_hovering;

import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.api.events.RenderTooltipCallback;

public class Registers extends Setup<MapsShowWhenHovering> {
    public Registers(MapsShowWhenHovering feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> RenderTooltipCallback.EVENT.register(feature().handlers::renderMapTooltip);
    }
}
