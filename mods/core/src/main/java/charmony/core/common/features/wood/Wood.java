package charmony.core.common.features.wood;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, canBeDisabled = false, description = """
    Allows registration of custom wood types.""")
public final class Wood extends SidedFeature {
    public Wood(Mod mod) {
        super(mod);
    }
}
