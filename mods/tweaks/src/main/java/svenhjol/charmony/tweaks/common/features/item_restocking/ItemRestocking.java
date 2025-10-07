package svenhjol.charmony.tweaks.common.features.item_restocking;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

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
