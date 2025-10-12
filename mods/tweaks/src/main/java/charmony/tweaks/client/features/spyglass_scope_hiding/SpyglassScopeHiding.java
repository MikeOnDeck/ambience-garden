package charmony.tweaks.client.features.spyglass_scope_hiding;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = "Removes the border when zooming in with the spyglass.")
public final class SpyglassScopeHiding extends SidedFeature {
    public SpyglassScopeHiding(Mod mod) {
        super(mod);
    }
}
