package charmony.tweaks.common.features.wandering_trader_tiers;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    Adds a tier of trades to a Wandering Trader. The trader selects a tier at random when spawned.
    If this feature is disabled, mods that register tiers will be ignored.""")
public final class WanderingTraderTiers extends SidedFeature {
    public final Handlers handlers;
    public final Registers registers;

    public WanderingTraderTiers(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        registers = new Registers(this);
    }

    public static WanderingTraderTiers feature() {
        return Mod.getSidedFeature(WanderingTraderTiers.class);
    }
}
