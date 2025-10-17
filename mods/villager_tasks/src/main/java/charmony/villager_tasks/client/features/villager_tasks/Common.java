package charmony.villager_tasks.client.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.Handlers;
import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;

public class Common {
    public final VillagerTasks feature;
    public final Handlers handlers;

    public Common() {
        feature = VillagerTasks.feature();
        handlers = feature.handlers;
    }
}
