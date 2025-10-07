package svenhjol.charmony.tweaks.client.features.repair_cost_unlimited;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public class RepairCostUnlimited extends SidedFeature {
    public RepairCostUnlimited(Mod mod) {
        super(mod);
    }

    public static RepairCostUnlimited feature() {
        return Mod.getSidedFeature(RepairCostUnlimited.class);
    }
}
