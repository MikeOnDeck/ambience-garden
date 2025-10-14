package charmony.villager_tasks.common;

import charmony.api.core.Side;
import charmony.villager_tasks.VillagerTasksMod;
import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;
import net.fabricmc.api.ModInitializer;

public class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Init charmony first.
        charmony.core.common.CommonInitializer.init();

        // Bootstrap and run the common features.
        var villagerTasks = VillagerTasksMod.instance();
        villagerTasks.addSidedFeature(VillagerTasks.class);
        villagerTasks.run(Side.Common);
    }
}
