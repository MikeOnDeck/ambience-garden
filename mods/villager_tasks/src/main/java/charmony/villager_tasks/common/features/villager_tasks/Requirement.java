package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.TaskHolder;
import charmony.villager_tasks.common.features.villager_tasks.requirements.CollectCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public class Requirement implements Satisfiable, TaskHolder {
    private final List<CollectCriteria> collectCriteria;
    private Task task;

    public static final Codec<Requirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        CollectCriteria.CODEC.listOf().fieldOf("collectCriteria").forGetter(req -> req.collectCriteria)
    ).apply(instance, Requirement::new));

    public Requirement(List<CollectCriteria> collectCriteria) {
        this.collectCriteria = collectCriteria;
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
        this.collectCriteria.forEach(ref -> ref.setTask(task));
    }

    @Override
    public Optional<Task> getTask() {
        return Optional.ofNullable(task);
    }

    public List<CollectCriteria> collectCriteria() {
        return collectCriteria;
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        var remaining = 0;
        remaining += collectCriteria.stream().anyMatch(ref -> !ref.isSatisfied()) ? 1 : 0;
        return remaining;
    }
}
