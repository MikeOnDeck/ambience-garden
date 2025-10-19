package charmony.villager_tasks.common.features.villager_tasks.interfaces;

import charmony.villager_tasks.common.features.villager_tasks.Task;

import java.util.Optional;

public interface TaskHolder {
    void setTask(Task task);

    Optional<Task> getTask();
}
