package charmony.tweaks.client.features.shulker_boxes_show_contents;

import charmony.api.events.HoverOverItemTooltipCallback;
import charmony.api.events.RenderTooltipComponentCallback;
import charmony.core.base.Setup;

public class Registers extends Setup<ShulkerBoxesShowContents> {
    public Registers(ShulkerBoxesShowContents feature) {
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
