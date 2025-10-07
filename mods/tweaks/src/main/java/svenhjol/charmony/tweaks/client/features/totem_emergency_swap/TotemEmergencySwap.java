package svenhjol.charmony.tweaks.client.features.totem_emergency_swap;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    Adds a keybind to swap any totem in your inventory with your offhand.
    Note that this performs an off-screen drag and drop, so pressing the key will interrupt any continuous mouse operations.""")
public final class TotemEmergencySwap extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public TotemEmergencySwap(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public static TotemEmergencySwap feature() {
        return Mod.getSidedFeature(TotemEmergencySwap.class);
    }
}
