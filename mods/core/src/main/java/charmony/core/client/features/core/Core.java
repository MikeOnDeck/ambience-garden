package charmony.core.client.features.core;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, canBeDisabled = false)
public final class Core extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public Core(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public static Core feature() {
        return Mod.getSidedFeature(Core.class);
    }
}
