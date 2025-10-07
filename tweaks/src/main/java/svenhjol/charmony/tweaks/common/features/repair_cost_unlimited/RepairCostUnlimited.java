package svenhjol.charmony.tweaks.common.features.repair_cost_unlimited;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = "Allows anvil repair of items with repair cost 39 or more.")
public final class RepairCostUnlimited extends SidedFeature {
    public RepairCostUnlimited(Mod mod) {
        super(mod);
    }

    public static RepairCostUnlimited feature() {
        return Mod.getSidedFeature(RepairCostUnlimited.class);
    }
}
