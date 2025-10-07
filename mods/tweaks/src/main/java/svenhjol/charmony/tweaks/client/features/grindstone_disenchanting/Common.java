package svenhjol.charmony.tweaks.client.features.grindstone_disenchanting;

import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.tweaks.common.features.grindstone_disenchanting.GrindstoneDisenchanting;
import svenhjol.charmony.tweaks.common.features.grindstone_disenchanting.Handlers;

public class Common {
    public final Handlers handlers;

    public Common() {
        var common = Mod.getSidedFeature(GrindstoneDisenchanting.class);
        handlers = common.handlers;
    }
}
