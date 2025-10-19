package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.behaviors.Collect;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskModifier;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskStatus;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskType;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.PlayerHolder;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Task implements EventListener, Satisfiable, PlayerHolder {
    private final TaskType type;
    private final ResourceLocation definitionId;
    private final UUID villager;
    private final boolean epic;
    private final long seed;
    private final double multiplier;
    private final int expiry;
    private final int level;
    private final List<Requirement> requirements = new ArrayList<>();
    private final Behavior behavior; // Not serialized but we load it on-demand

    private TaskStatus status;
    private int duration = 0;
    private ServerPlayer player;

    public static final Codec<Task> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        TaskType.CODEC.fieldOf("type").forGetter(task -> task.type),
        TaskStatus.CODEC.fieldOf("status").forGetter(task -> task.status),
        ResourceLocation.CODEC.fieldOf("definitionId").forGetter(task -> task.definitionId),
        Requirement.CODEC.listOf().fieldOf("requirements").forGetter(task -> task.requirements),
        UUIDUtil.CODEC.fieldOf("villager").forGetter(task -> task.villager),
        Codec.BOOL.fieldOf("epic").forGetter(task -> task.epic),
        Codec.LONG.fieldOf("seed").forGetter(task -> task.seed),
        Codec.DOUBLE.fieldOf("multiplier").forGetter(task -> task.multiplier),
        Codec.INT.fieldOf("level").forGetter(task -> task.level),
        Codec.INT.fieldOf("expiry").forGetter(task -> task.expiry),
        Codec.INT.fieldOf("duration").forGetter(task -> task.duration)
    ).apply(instance, Task::new));

    private Task(TaskType type, TaskStatus status, ResourceLocation definitionId, List<Requirement> requirements, UUID villager, boolean epic, long seed, double multiplier, int level, int expiry, int duration) {
        this.type = type;
        this.status = status;
        this.definitionId = definitionId;
        this.villager = villager;
        this.epic = epic;
        this.seed = seed;
        this.multiplier = multiplier;
        this.expiry = expiry;
        this.level = level;
        this.duration = duration;
        this.requirements.addAll(requirements);

        // Set up the task's behavior.
        this.behavior = this.type.getBehavior();
        this.behavior.setTask(this);

        // Ensure all requirements have a reference to this task.
        this.requirements.forEach(req -> req.setTask(this));
    }

    public static Task create(ServerPlayer player, Definition definition, UUID uuid, TaskModifier modifier, long seed) {
        var random = RandomSource.create(seed);
        var expiry = definition.expiry;
        var level = definition.level;

        // The modifier and player luck affect the task multiplier.
        var multiplier = Math.max(definition.multiplier, modifier.getMultiplier(random)) + (player.getLuck() * 1.0d);
        var type = TaskType.fromString(definition.types.get(random.nextInt(definition.types.size())));

        List<Requirement> requirements = new ArrayList<>();
        var serverLevel = player.level();
        var registryAccess = serverLevel.registryAccess();

        Collect.makeRequirement(registryAccess, definition, multiplier, random).ifPresent(requirements::add);

        return new Task(
            type,
            TaskStatus.NotStarted,
            definition.id,
            requirements,
            uuid,
            modifier.isEpic(),
            seed,
            multiplier,
            level,
            expiry,
            0
        );
    }

    @Override
    public Optional<ServerPlayer> getPlayer() {
        return Optional.ofNullable(player);
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        var remaining = 0;
        remaining += requirements.stream().anyMatch(req -> !req.isSatisfied()) ? 1 : 0;
        return remaining;
    }

    @Override
    public void onStart(ServerPlayer player) {
        behavior.onStart(player);
    }

    @Override
    public void onStarted(ServerPlayer player) {
        behavior.onStarted(player);
    }

    @Override
    public void onTick(ServerPlayer player) {
        this.player = player;
        behavior.onTick(player);
    }

    @Override
    public void onAbandon(ServerPlayer player) {
        behavior.onAbandon(player);
    }

    @Override
    public void onComplete(ServerPlayer player) {
        behavior.onComplete(player);
    }

    public boolean isStarting() {
        return status.equals(TaskStatus.Starting);
    }

    public boolean isStarted() {
        return status.equals(TaskStatus.InProgress);
    }

    public boolean isCompleted() {
        return status.equals(TaskStatus.Completed);
    }

    public boolean isAbandoned() {
        return status.equals(TaskStatus.Abandoned);
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }
}
