package charmony.tweaks.client.features.shulker_boxes_show_contents;

import charmony.api.events.HoverOverItemTooltipCallback;
import charmony.api.events.HudDisplayCallback;
import charmony.api.events.RenderTooltipComponentCallback;
import charmony.core.base.Setup;
import charmony.core.client.ClientRegistry;
import charmony.tweaks.common.features.shulker_boxes_show_contents.Networking.S2CShowContents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Registers extends Setup<ShulkerBoxesShowContents> {
    public final HudRenderer hudRenderer;

    public Registers(ShulkerBoxesShowContents feature) {
        super(feature);
        this.hudRenderer = new HudRenderer();

        var registry = ClientRegistry.forFeature(feature);
        registry.packetReceiver(S2CShowContents.TYPE, feature.handlers::handleShowContents);
    }

    @Override
    public Runnable boot() {
        return () -> {
            HoverOverItemTooltipCallback.EVENT.register(feature().handlers::removeLinesFromShulkerBox);
            RenderTooltipComponentCallback.EVENT.register(feature().handlers::addGridToShulkerBox);
            HudDisplayCallback.EVENT.register(feature().handlers::hudRender);
            ClientTickEvents.END_CLIENT_TICK.register(feature().handlers::clientTick);
        };
    }
}
