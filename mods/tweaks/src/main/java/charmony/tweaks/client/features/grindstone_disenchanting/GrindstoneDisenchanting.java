package charmony.tweaks.client.features.grindstone_disenchanting;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

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
