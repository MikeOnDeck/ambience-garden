package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.TaskHolder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

public abstract class Type implements EventListener, TaskHolder {
    private Task task;

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public Optional<Task> getTask() {
        return Optional.ofNullable(task);
    }

    public abstract String getId();

    public abstract Component getName();

    public Optional<Requirement> makeRequirement(RegistryAccess registryAccess, Definition definition, double multiplier, RandomSource random) {
        return Optional.empty();
    }

    public boolean hasRequirements() {
        return !getRequirements().isEmpty();
    }

    public List<Requirement> getRequirements() {
        return getTask().map(Task::getRequirements).orElseThrow();
    }
}
