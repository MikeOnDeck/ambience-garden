package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.TaskHolder;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class Behavior implements EventListener, TaskHolder {
    private Task task;

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public Task getTask() {
        if (task == null) {
            throw new IllegalStateException("Task has not been set.");
        }
        return task;
    }

    public abstract String getId();

    public abstract Component getName();

    public boolean hasRequirements() {
        return !getRequirements().isEmpty();
    }

    public List<Requirement> getRequirements() {
        return getTask().getRequirements();
    }
}
