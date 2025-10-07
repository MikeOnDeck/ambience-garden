package svenhjol.charmony.tweaks.client.features.piglin_pointing;

import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.tweaks.common.features.piglin_pointing.Handlers;
import svenhjol.charmony.tweaks.common.features.piglin_pointing.PiglinPointing;

public class Common {
    public final Handlers handlers;

    public Common() {
        var feature = Mod.getSidedFeature(PiglinPointing.class);
        handlers = feature.handlers;
    }
}
