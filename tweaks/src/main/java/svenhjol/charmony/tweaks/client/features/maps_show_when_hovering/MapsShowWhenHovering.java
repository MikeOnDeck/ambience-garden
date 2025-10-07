package svenhjol.charmony.tweaks.client.features.maps_show_when_hovering;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = "Maps show their content when hovering over the inventory item.")
public final class MapsShowWhenHovering extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public MapsShowWhenHovering(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }
}
