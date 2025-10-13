package charmony.villager_tasks.common;

import charmony.api.core.Side;
import charmony.villager_tasks.VillagerTasksMod;
import net.fabricmc.api.ModInitializer;

public class CommonInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // Init charmony first.
        charmony.core.common.CommonInitializer.init();

        // Bootstrap and run the common features.
        var villagerTasks = VillagerTasksMod.instance();
        villagerTasks.run(Side.Common);
    }
}
