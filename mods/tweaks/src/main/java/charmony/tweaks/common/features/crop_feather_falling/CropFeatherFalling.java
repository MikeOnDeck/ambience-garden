package charmony.tweaks.common.features.crop_feather_falling;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

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
