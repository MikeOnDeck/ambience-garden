package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.TaskHolder;

public abstract class Behavior implements EventListener, TaskHolder {
    private Task task;

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public Task getTask() {
        if (task == null) {;
            throw new IllegalStateException("Task has not been set.");
        }
        return task;
    }
}
