package charmony.tweaks.common.features.repair_cost_unlimited;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = "Allows anvil repair of items with repair cost 39 or more.")
public final class RepairCostUnlimited extends SidedFeature {
    public RepairCostUnlimited(Mod mod) {
        super(mod);
    }

    public static RepairCostUnlimited feature() {
        return Mod.getSidedFeature(RepairCostUnlimited.class);
    }
}
