package charmony.core.client.features.hud_item_scaling;

import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Client, description = """
    Allows Charmony mods to scale items in the HUD.""")
public final class HudItemScaling extends SidedFeature {
    public final Handlers handlers;
    public final Registers registers;

    public HudItemScaling(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        registers = new Registers(this);
    }

    public static HudItemScaling feature() {
        return Mod.getSidedFeature(HudItemScaling.class);
    }
}
