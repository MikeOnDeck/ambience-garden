package svenhjol.charmony.tweaks.client.features.grindstone_disenchanting;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public final class GrindstoneDisenchanting extends SidedFeature {
    public final Handlers handlers;
    public final Supplier<Common> common;

    public GrindstoneDisenchanting(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        common = Common::new;
    }

    public static GrindstoneDisenchanting feature() {
        return Mod.getSidedFeature(GrindstoneDisenchanting.class);
    }
}
