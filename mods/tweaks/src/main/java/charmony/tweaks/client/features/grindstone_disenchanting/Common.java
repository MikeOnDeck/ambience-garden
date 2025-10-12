package charmony.tweaks.client.features.grindstone_disenchanting;

import charmony.core.base.Mod;
import charmony.tweaks.common.features.grindstone_disenchanting.GrindstoneDisenchanting;
import charmony.tweaks.common.features.grindstone_disenchanting.Handlers;

public class Common {
    public final Handlers handlers;

    public Common() {
        var common = Mod.getSidedFeature(GrindstoneDisenchanting.class);
        handlers = common.handlers;
    }
}
