package charmony.tweaks.client.features.chiseled_bookshelves_show_book;

import charmony.api.events.HudDisplayCallback;
import charmony.core.base.Setup;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Registers extends Setup<ChiseledBookshelvesShowBook> {
    public final HudRenderer hudRenderer;

    public Registers(ChiseledBookshelvesShowBook feature) {
        super(feature);
        this.hudRenderer = new HudRenderer();
    }

    @Override
    public Runnable boot() {
        return () -> {
            ClientTickEvents.END_CLIENT_TICK.register(feature().handlers::clientTick);
            HudDisplayCallback.EVENT.register(feature().handlers::hudRender);

            // API has been removed in snapshots for 1.21.6
            // HudLayerRegistrationCallback.EVENT.register(feature().handlers::hudRender);
        };
    }
}
