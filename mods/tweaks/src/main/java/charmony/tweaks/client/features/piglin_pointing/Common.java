package charmony.tweaks.client.features.piglin_pointing;

import charmony.core.base.Mod;
import charmony.tweaks.common.features.piglin_pointing.Handlers;
import charmony.tweaks.common.features.piglin_pointing.PiglinPointing;

public class Common {
    public final Handlers handlers;

    public Common() {
        var feature = Mod.getSidedFeature(PiglinPointing.class);
        handlers = feature.handlers;
    }
}
