package svenhjol.charmony.tweaks.common.features.piglin_pointing;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Piglins turn and point in the rough direction of a nether fortress or bastion remnant.
    By default, give them a chiseled nether brick block for a fortress and a gilded blackstone block for a bastion.
    The item tags 'piglin_barters_for_bastions' and 'piglin_barters_for_fortresses' can be used to configure the blocks.""")
public final class PiglinPointing extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;

    public PiglinPointing(Mod mod) {
        super(mod);

        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
    }

    public static PiglinPointing feature() {
        return Mod.getSidedFeature(PiglinPointing.class);
    }
}
