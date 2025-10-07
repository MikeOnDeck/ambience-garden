package svenhjol.charmony.core.common.features.wood;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, canBeDisabled = false, description = """
    Allows registration of custom wood types.""")
public final class Wood extends SidedFeature {
    public Wood(Mod mod) {
        super(mod);
    }
}
