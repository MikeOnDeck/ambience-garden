package svenhjol.charmony.tweaks.client.features.spyglass_scope_hiding;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = "Removes the border when zooming in with the spyglass.")
public final class SpyglassScopeHiding extends SidedFeature {
    public SpyglassScopeHiding(Mod mod) {
        super(mod);
    }
}
