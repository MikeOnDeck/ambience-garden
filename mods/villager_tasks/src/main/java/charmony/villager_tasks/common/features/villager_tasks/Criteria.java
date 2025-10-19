package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.TaskHolder;

import java.util.Optional;

public abstract class Criteria implements EventListener, Satisfiable, TaskHolder {
    private Task task;

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public Optional<Task> getTask() {
        return Optional.ofNullable(task);
    }
}
