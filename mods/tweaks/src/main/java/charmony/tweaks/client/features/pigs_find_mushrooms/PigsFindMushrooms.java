package charmony.tweaks.client.features.pigs_find_mushrooms;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public final class PigsFindMushrooms extends SidedFeature {
    public final Handlers handlers;
    public final Supplier<Common> common;

    public PigsFindMushrooms(Mod mod) {
        super(mod);
        common = Common::new;
        handlers = new Handlers(this);
    }

    public static PigsFindMushrooms feature() {
        return Mod.getSidedFeature(PigsFindMushrooms.class);
    }
}
