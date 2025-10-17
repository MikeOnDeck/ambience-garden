package charmony.villager_tasks.client;

import charmony.api.core.Side;
import charmony.villager_tasks.VillagerTasksMod;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import net.fabricmc.api.ClientModInitializer;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Init charmony first.
        charmony.core.client.ClientInitializer.init();

        // Bootstrap and run the mod.
        var villagerTasks = VillagerTasksMod.instance();
        villagerTasks.addSidedFeature(VillagerTasks.class);
        villagerTasks.run(Side.Client);
    }
}
