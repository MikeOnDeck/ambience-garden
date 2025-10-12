package charmony.tweaks.common.features.item_restocking;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = "Refills hotbar from your inventory.")
public final class ItemRestocking extends SidedFeature {
    public final Advancements advancements;
    public final Handlers handlers;
    public final Registers registers;

    public ItemRestocking(Mod mod) {
        super(mod);

        advancements = new Advancements(this);
        handlers = new Handlers(this);
        registers = new Registers(this);
    }

    public static ItemRestocking feature() {
        return Mod.getSidedFeature(ItemRestocking.class);
    }
}
