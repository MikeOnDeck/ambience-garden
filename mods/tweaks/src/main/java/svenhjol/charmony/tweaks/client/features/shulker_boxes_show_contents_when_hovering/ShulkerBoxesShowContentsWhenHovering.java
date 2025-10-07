package svenhjol.charmony.tweaks.client.features.shulker_boxes_show_contents_when_hovering;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = "Shulker boxes show their content when hovering over the inventory item.")
public final class ShulkerBoxesShowContentsWhenHovering extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public ShulkerBoxesShowContentsWhenHovering(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }
}
