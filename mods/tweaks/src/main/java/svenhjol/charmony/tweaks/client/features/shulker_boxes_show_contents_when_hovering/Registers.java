package svenhjol.charmony.tweaks.client.features.shulker_boxes_show_contents_when_hovering;

import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.api.events.HoverOverItemTooltipCallback;
import svenhjol.charmony.api.events.RenderTooltipComponentCallback;

public class Registers extends Setup<ShulkerBoxesShowContentsWhenHovering> {
    public Registers(ShulkerBoxesShowContentsWhenHovering feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            HoverOverItemTooltipCallback.EVENT.register(feature().handlers::removeLinesFromShulkerBox);
            RenderTooltipComponentCallback.EVENT.register(feature().handlers::addGridToShulkerBox);
        };
    }
}
