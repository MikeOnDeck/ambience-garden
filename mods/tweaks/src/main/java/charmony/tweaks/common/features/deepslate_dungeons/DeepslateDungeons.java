package charmony.tweaks.common.features.deepslate_dungeons;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Dungeons in the deepslate layer will be constructed of deepslate bricks and cobbled deepslate.""")
public final class DeepslateDungeons extends SidedFeature {
    public final Handlers handlers;

    public DeepslateDungeons(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
    }

    public static DeepslateDungeons feature() {
        return Mod.getSidedFeature(DeepslateDungeons.class);
    }
}