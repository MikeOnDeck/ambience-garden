package charmony.tweaks.client.features.shulker_boxes_show_contents_when_hovering;

import charmony.core.base.Setup;
import charmony.api.events.HoverOverItemTooltipCallback;
import charmony.api.events.RenderTooltipComponentCallback;

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
