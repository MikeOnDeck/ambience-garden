package charmony.villager_tasks.common.features.villager_tasks;

import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, canBeDisabledInConfig = false)
public final class VillagerTasks extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public VillagerTasks(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        registers = new Registers(this);
    }

    public static VillagerTasks feature() {
        return Mod.getSidedFeature(VillagerTasks.class);
    }
}
