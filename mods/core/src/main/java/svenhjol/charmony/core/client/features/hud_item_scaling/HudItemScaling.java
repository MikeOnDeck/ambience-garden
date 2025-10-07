package svenhjol.charmony.core.client.features.hud_item_scaling;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

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
