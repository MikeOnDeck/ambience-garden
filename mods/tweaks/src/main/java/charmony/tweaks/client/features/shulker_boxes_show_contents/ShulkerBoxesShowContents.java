package charmony.tweaks.client.features.shulker_boxes_show_contents;

import charmony.api.core.Configurable;
import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Client, description = "Shulker boxes show their contents.")
public final class ShulkerBoxesShowContents extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public ShulkerBoxesShowContents(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        registers = new Registers(this);
    }

    @Configurable(name = "Show contents when hovering",
        description = "If true, the contents of a shulker box will be shown when hovering over it.",
        requireRestart = false)
    private static boolean showContentsWhenHovering = true;

    public static ShulkerBoxesShowContents feature() {
        return Mod.getSidedFeature(ShulkerBoxesShowContents.class);
    }

    public boolean showContentsWhenHovering() {
        return showContentsWhenHovering;
    }
}
