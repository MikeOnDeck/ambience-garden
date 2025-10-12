package charmony.tweaks.client.features.maps_show_when_hovering;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

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
