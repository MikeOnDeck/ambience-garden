package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.behaviors.CollectBehavior;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskModifier;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskStatus;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.PlayerHolder;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Task implements EventListener, Satisfiable, PlayerHolder {
    private final ResourceLocation definitionId;
    private final UUID villager;
    private final boolean epic;
    private final long seed;
    private final double multiplier;
    private final int expiry;
    private final int level;
    private final List<Requirement> requirements = new ArrayList<>();
    private final List<Behavior> behaviors = new ArrayList<>();

    private TaskStatus status;
    private int duration = 0;
    private ServerPlayer player;

    public static final Codec<Task> CODEC = RecordCodecBuilder.create(instance -> instance.group(
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

    private Task(TaskStatus status, ResourceLocation definitionId, List<Requirement> requirements, UUID villager, boolean epic, long seed, double multiplier, int level, int expiry, int duration) {
        this.status = status;
        this.definitionId = definitionId;
        this.villager = villager;
        this.epic = epic;
        this.seed = seed;
        this.multiplier = multiplier;
        this.expiry = expiry;
        this.level = level;
        this.duration = duration;

        // Setup behaviors and ensure all behaviors have a reference to this task.
        this.behaviors.addAll(loadBehaviors());
        this.behaviors.forEach(behavior -> behavior.setTask(this));

        // Setup requirements and ensure all requirements have a reference to this task.
        this.requirements.addAll(requirements);
        this.requirements.forEach(req -> req.setTask(this));
    }

    /**
     * Ensure all behaviors are added here.
     */
    public static List<Behavior> loadBehaviors() {
        return List.of(
            new CollectBehavior()
        );
    }

    public static Task create(ServerPlayer player, Definition definition, UUID uuid, TaskModifier modifier, long seed) {
        var random = RandomSource.create(seed);
        var expiry = definition.expiry;
        var level = definition.level;

        // The modifier and player luck affect the task multiplier.
        var multiplier = Math.max(definition.multiplier, modifier.getMultiplier(random)) + (player.getLuck() * 1.0d);

        List<Requirement> requirements = new ArrayList<>();
        var serverLevel = player.level();
        var registryAccess = serverLevel.registryAccess();

        // Pass the definition to each behavior to see if it needs to set up a task requirement.
        loadBehaviors().forEach(b -> b.makeRequirement(registryAccess, definition, multiplier, random).ifPresent(requirements::add));

        // Finally create the task instance.
        return new Task(TaskStatus.NotStarted, definition.id, requirements, uuid, modifier.isEpic(), seed, multiplier, level, expiry, 0);
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
        this.behaviors.forEach(b -> b.onStart(player));
    }

    @Override
    public void onStarted(ServerPlayer player) {
        this.behaviors.forEach(b -> b.onStarted(player));
    }

    @Override
    public void onTick(ServerPlayer player) {
        this.player = player;
        this.behaviors.forEach(b -> b.onTick(player));
    }

    @Override
    public void onAbandon(ServerPlayer player) {
        this.behaviors.forEach(b -> b.onAbandon(player));
    }

    @Override
    public void onComplete(ServerPlayer player) {
        this.behaviors.forEach(b -> b.onComplete(player));
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

    public List<Component> getActiveBehaviorNames() {
        return behaviors.stream()
            .filter(Behavior::hasRequirements)
            .map(Behavior::getName)
            .toList();
    }

    public List<String> getActiveBehaviorIds() {
        return behaviors.stream()
            .filter(Behavior::hasRequirements)
            .map(Behavior::getId)
            .toList();
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }
}
