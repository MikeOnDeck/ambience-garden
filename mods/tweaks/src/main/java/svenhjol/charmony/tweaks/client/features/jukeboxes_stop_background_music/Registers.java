package svenhjol.charmony.tweaks.client.features.jukeboxes_stop_background_music;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.api.events.PlaySoundCallback;

public class Registers extends Setup<JukeboxesStopBackgroundMusic> {
    public Registers(JukeboxesStopBackgroundMusic feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            UseBlockCallback.EVENT.register(feature().handlers::useBlock);
            PlaySoundCallback.EVENT.register(feature().handlers::playSound);
            ClientTickEvents.END_CLIENT_TICK.register(feature().handlers::clientTick);
        };
    }
}
