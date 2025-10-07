package svenhjol.charmony.tweaks.client.features.chiseled_bookshelves_show_book_on_hover;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import svenhjol.charmony.api.events.HudDisplayCallback;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<ChiseledBookshelvesShowBookOnHover> {
    public final HudRenderer hudRenderer;

    public Registers(ChiseledBookshelvesShowBookOnHover feature) {
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
