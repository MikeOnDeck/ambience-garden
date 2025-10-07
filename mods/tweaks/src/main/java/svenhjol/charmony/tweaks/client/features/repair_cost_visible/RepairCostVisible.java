package svenhjol.charmony.tweaks.client.features.repair_cost_visible;

import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = "An item's repair cost is shown in its tooltip when viewing on the anvil.")
public final class RepairCostVisible extends SidedFeature {
    public Handlers handlers;

    public RepairCostVisible(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
    }
}
