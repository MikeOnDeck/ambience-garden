package charmony.core.client.features.wood;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, canBeDisabled = false)
public final class Wood extends SidedFeature {
    public final Registers registers;

    public Wood(Mod mod) {
        super(mod);
        registers = new Registers(this);
    }
}
