package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.aspects.Collect;
import charmony.villager_tasks.common.features.villager_tasks.aspects.Rewards;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskModifier;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskStatus;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task implements EventListener, Satisfiable {
    public final List<Aspect> aspects = new ArrayList<>();

    public final UUID id;
    public final UUID villager;
    public final ResourceLocation definitionId;
    public final TaskModifier modifier;
    public final String titleKey;
    public final long seed;
    public final int level;
    public final int expiry;

    public final Collect collect;
    public final Rewards rewards;

    private TaskStatus status;
    private int duration = 0;

    public static final Codec<Task> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("id").forGetter(task -> task.id),
        UUIDUtil.CODEC.fieldOf("villager").forGetter(task -> task.villager),
        ResourceLocation.CODEC.fieldOf("definitionId").forGetter(task -> task.definitionId),
        TaskStatus.CODEC.fieldOf("status").forGetter(task -> task.status),
        TaskModifier.CODEC.fieldOf("modifier").forGetter(task -> task.modifier),
        Codec.STRING.fieldOf("titleKey").forGetter(task -> task.titleKey),
        Codec.LONG.fieldOf("seed").forGetter(task -> task.seed),
        Codec.INT.fieldOf("level").forGetter(task -> task.level),
        Codec.INT.fieldOf("expiry").forGetter(task -> task.expiry),
        Codec.INT.fieldOf("duration").forGetter(task -> task.duration),
        Collect.CODEC.fieldOf("collect").forGetter(task -> task.collect),
        Rewards.CODEC.fieldOf("rewards").forGetter(task -> task.rewards)
    ).apply(instance, Task::new));

    public static final Task EMPTY = new Task(
        UUID.randomUUID(), UUID.randomUUID(), ResourceLocation.parse("minecraft:empty"), TaskStatus.Unspecified, TaskModifier.Unspecified, "", 0L, 0, 0, 0,
        Collect.EMPTY, Rewards.EMPTY
    );

    private Task(UUID id, UUID villager, ResourceLocation definitionId, TaskStatus status, TaskModifier modifier, String titleKey, long seed, int level, int expiry, int duration,
                 Collect collect, Rewards reward
    ) {
        this.id = id;
        this.status = status;
        this.definitionId = definitionId;
        this.modifier = modifier;
        this.villager = villager;
        this.titleKey = titleKey;
        this.seed = seed;
        this.expiry = expiry;
        this.level = level;
        this.duration = duration;

        this.collect = collect;
        this.rewards = reward;
    }

    public static Task create(ServerPlayer player, Definition definition, UUID uuid, TaskModifier modifier, long seed) {
        Task task;

        var id = UUID.randomUUID();
        var random = RandomSource.create(seed);
        var expiry = definition.expiry;
        var level = definition.level;
        var titleKey = definition.title;

        // Initialise the aspect builder that will be passed to each aspect during task creation.
        var builder = new AspectBuilder(player, definition, modifier, random);

        try {
            // Create the task with its aspects.
            task = new Task(id, uuid, definition.id, TaskStatus.NotStarted, modifier, titleKey, seed, level, expiry, 0,
                Collect.make(builder),
                Rewards.make(builder)
            );
        } catch (Exception e) {
            VillagerTasks.feature().log().error("Failed to create task for definition: " + definition.id, e);
            task = EMPTY;
        }

        return task;
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        var remaining = 0;
        remaining += getRequirements().stream().anyMatch(req -> !req.isSatisfied()) ? 1 : 0;
        return remaining;
    }

    @Override
    public void onStart(ServerPlayer player) {
        this.aspects.forEach(b -> b.onStart(player));
    }

    @Override
    public void onStarted(ServerPlayer player) {
        this.aspects.forEach(b -> b.onStarted(player));
    }

    @Override
    public void onTick(ServerPlayer player) {
        this.aspects.forEach(b -> b.onTick(player));
    }

    @Override
    public void onAbandon(ServerPlayer player) {
        this.aspects.forEach(b -> b.onAbandon(player));
    }

    @Override
    public void onComplete(ServerPlayer player) {
        this.aspects.forEach(b -> b.onComplete(player));
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

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public ResourceLocation getDefinitionId() {
        return definitionId;
    }

    public Component getTitle() {
        if (titleKey.contains(".villager_tasks.")) {
            // If it's a translatable key (containing the keyword villager_tasks) then translate it.
            return Component.translatable(titleKey);
        } else if (!titleKey.isEmpty()) {
            // Just output it literally.
            return Component.literal(titleKey);
        } else {
            return Resources.MISSINGNO;
        }
    }

    public List<? extends Satisfiable> getRequirements() {
        return List.of(collect);
    }

    public boolean isEpic() {
        return modifier.isEpic();
    }

    public record AspectBuilder(ServerPlayer player, Definition definition, TaskModifier modifier, RandomSource random) {
        public RegistryAccess registryAccess() {
            return player.level().registryAccess();
        }
    }
}
