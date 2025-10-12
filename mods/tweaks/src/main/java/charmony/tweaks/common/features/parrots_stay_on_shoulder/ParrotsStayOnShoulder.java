package charmony.tweaks.common.features.parrots_stay_on_shoulder;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Parrots stay on player's shoulder when jumping and falling. Crouch to make them dismount.""")
public final class ParrotsStayOnShoulder extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public ParrotsStayOnShoulder(Mod mod) {
        super(mod);

        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public static ParrotsStayOnShoulder feature() {
        return Mod.getSidedFeature(ParrotsStayOnShoulder.class);
    }
}
