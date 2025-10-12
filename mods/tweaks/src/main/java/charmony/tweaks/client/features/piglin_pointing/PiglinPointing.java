package charmony.tweaks.client.features.piglin_pointing;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public final class PiglinPointing extends SidedFeature {
    public final Handlers handlers;
    public final Supplier<Common> common;

    public PiglinPointing(Mod mod) {
        super(mod);

        common = Common::new;
        handlers = new Handlers(this);
    }

    public static PiglinPointing feature() {
        return Mod.getSidedFeature(PiglinPointing.class);
    }
}
