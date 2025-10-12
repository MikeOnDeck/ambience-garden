package charmony.tweaks.client.features.repair_cost_unlimited;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public class RepairCostUnlimited extends SidedFeature {
    public RepairCostUnlimited(Mod mod) {
        super(mod);
    }

    public static RepairCostUnlimited feature() {
        return Mod.getSidedFeature(RepairCostUnlimited.class);
    }
}
