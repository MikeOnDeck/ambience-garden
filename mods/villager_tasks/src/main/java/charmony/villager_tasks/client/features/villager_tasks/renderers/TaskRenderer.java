package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.villager_tasks.common.features.villager_tasks.Task;

public class TaskRenderer {
    public final CollectRenderer collect;
    public final RewardsRenderer rewards;

    public TaskRenderer(Task task) {
        this.collect = new CollectRenderer(task);
        this.rewards = new RewardsRenderer(task);
    }
}
