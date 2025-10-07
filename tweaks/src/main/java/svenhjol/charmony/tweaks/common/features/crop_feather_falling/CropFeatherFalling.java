package svenhjol.charmony.tweaks.common.features.crop_feather_falling;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Prevents crop trampling when wearing boots enchanted with Feather Falling.""")
public final class CropFeatherFalling extends SidedFeature {
    public final Handlers handlers;
    public final Advancements advancements;

    public CropFeatherFalling(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
    }

    public static CropFeatherFalling feature() {
        return Mod.getSidedFeature(CropFeatherFalling.class);
    }
}
