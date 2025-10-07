package svenhjol.charmony.tweaks.client.features.shulker_box_menu_colors;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<ShulkerBoxMenuColors> {
    public Registers(ShulkerBoxMenuColors feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            UseBlockCallback.EVENT.register(feature().handlers::useBlock);
        };
    }
}
