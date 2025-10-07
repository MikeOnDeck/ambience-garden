package svenhjol.charmony.tweaks.common.features.pigs_find_mushrooms;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.sounds.SoundEvent;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.CommonRegistry;

import java.util.function.Supplier;

public class Registers extends Setup<PigsFindMushrooms> {
    public final Supplier<SoundEvent> sniffingSound;

    public Registers(PigsFindMushrooms feature) {
        super(feature);

        var registry = CommonRegistry.forFeature(feature);
        sniffingSound = registry.sound("pig_sniffing");
    }

    @Override
    public Runnable boot() {
        return () -> {
            ServerEntityEvents.ENTITY_LOAD.register(feature().handlers::entityJoin);
        };
    }
}
