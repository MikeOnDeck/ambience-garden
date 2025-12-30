package charmony.tweaks.common.features.shulker_boxes_show_contents;

import charmony.api.core.Configurable;
import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, description = "Shulker boxes show their contents.")
public final class ShulkerBoxesShowContents extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    @Configurable(name = "Show label when contents are same type",
        description = "If true, the contents and count of a shulker box will be shown when it contains items of the the same type.",
        requireRestart = false)
    private static boolean showSameItemLabel = true;

    public ShulkerBoxesShowContents(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        registers = new Registers(this);
    }

    public static ShulkerBoxesShowContents feature() {
        return Mod.getSidedFeature(ShulkerBoxesShowContents.class);
    }

    public boolean showSameItemLabel() {
        return showSameItemLabel;
    }
}
