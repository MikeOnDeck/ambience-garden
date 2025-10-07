package svenhjol.charmony.core.common.features.advancements;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.advancements.CriteriaTriggers;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<Advancements> {
    public final ActionPerformed actionPerformed;

    public Registers(Advancements feature) {
        super(feature);
        actionPerformed = CriteriaTriggers.register("charmony_action_performed", new ActionPerformed());
    }

    @Override
    public Runnable boot() {
        return () -> ServerPlayConnectionEvents.JOIN.register(feature().handlers::playerJoin);
    }
}
