package charmony.villager_tasks.client.features.villager_tasks;

import charmony.api.core.FeatureDefinition;
import charmony.api.core.Side;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public final class VillagerTasks extends SidedFeature {
    public final Supplier<Common> common;
    public final Registers registers;
    public final Handlers handlers;

    public VillagerTasks(Mod mod) {
        super(mod);
        common = Common::new;
        handlers = new Handlers(this);
        registers = new Registers(this);
    }

    public static VillagerTasks feature() {
        return Mod.getSidedFeature(VillagerTasks.class);
    }
}
