package svenhjol.charmony.core.client.features.tint_background;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    Allows Charmony mods to modify the background color tint of game screens.""")
public final class TintBackground extends SidedFeature {
    public final Handlers handlers;

    public TintBackground(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
    }

    public static TintBackground feature() {
        return Mod.getSidedFeature(TintBackground.class);
    }
}
