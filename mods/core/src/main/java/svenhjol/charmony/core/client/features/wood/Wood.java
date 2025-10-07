package svenhjol.charmony.core.client.features.wood;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, canBeDisabled = false)
public final class Wood extends SidedFeature {
    public final Registers registers;

    public Wood(Mod mod) {
        super(mod);
        registers = new Registers(this);
    }
}
