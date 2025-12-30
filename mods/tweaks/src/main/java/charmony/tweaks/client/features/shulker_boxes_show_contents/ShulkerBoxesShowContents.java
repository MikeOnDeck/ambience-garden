package charmony.tweaks.client.features.shulker_boxes_show_contents;

import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Client, description = "Shulker boxes show their content when hovering over the inventory item.")
public final class ShulkerBoxesShowContents extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public ShulkerBoxesShowContents(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }
}
