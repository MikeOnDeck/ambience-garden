package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.aspects.Collect;
import charmony.villager_tasks.common.features.villager_tasks.aspects.Rewards;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskModifier;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskStatus;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
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
import java.util.UUID;

public class Task implements EventListener, Satisfiable {
    private final String titleKey;

    public final UUID id;
    public final ResourceLocation definitionId;
    public final List<Aspect> aspects = new ArrayList<>();
    public final UUID villager;
    public final long seed;
    public final double multiplier;
    public final int expiry;
    public final int level;
    public final boolean epic;

    public final Collect collect;
    public final Rewards rewards;

    private TaskStatus status;
    private int duration = 0;

    public static final Codec<Task> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("id").forGetter(task -> task.id),
        TaskStatus.CODEC.fieldOf("status").forGetter(task -> task.status),
        ResourceLocation.CODEC.fieldOf("definitionId").forGetter(task -> task.definitionId),
        UUIDUtil.CODEC.fieldOf("villager").forGetter(task -> task.villager),
        Codec.STRING.fieldOf("titleKey").forGetter(task -> task.titleKey),
        Codec.BOOL.fieldOf("epic").forGetter(task -> task.epic),
        Codec.LONG.fieldOf("seed").forGetter(task -> task.seed),
        Codec.DOUBLE.fieldOf("multiplier").forGetter(task -> task.multiplier),
        Codec.INT.fieldOf("level").forGetter(task -> task.level),
        Codec.INT.fieldOf("expiry").forGetter(task -> task.expiry),
        Codec.INT.fieldOf("duration").forGetter(task -> task.duration),
        Collect.CODEC.fieldOf("collect").forGetter(task -> task.collect),
        Rewards.CODEC.fieldOf("rewards").forGetter(task -> task.rewards)
    ).apply(instance, Task::new));

    private Task(UUID id, TaskStatus status, ResourceLocation definitionId, UUID villager, String titleKey, boolean epic, long seed, double multiplier, int level, int expiry, int duration,
         Collect collect,
         Rewards reward
    ) {
        this.id = id;
        this.status = status;
        this.definitionId = definitionId;
        this.villager = villager;
        this.epic = epic;
        this.titleKey = titleKey;
        this.seed = seed;
        this.multiplier = multiplier;
        this.expiry = expiry;
        this.level = level;
        this.duration = duration;

        this.collect = collect;
        this.rewards = reward;
    }

    public static Task create(ServerPlayer player, Definition definition, UUID uuid, TaskModifier modifier, long seed) {
        var id = UUID.randomUUID();
        var random = RandomSource.create(seed);
        var expiry = definition.expiry;
        var level = definition.level;
        var titleKey = definition.title;

        // The modifier and player luck affect the task multiplier.
        var multiplier = Math.max(definition.multiplier, modifier.getMultiplier(random)) + (player.getLuck() * 1.0d);

        var serverLevel = player.level();
        var registryAccess = serverLevel.registryAccess();

        // Finally create the task instance.
        return new Task(id, TaskStatus.NotStarted, definition.id, uuid, titleKey, modifier.isEpic(), seed, multiplier, level, expiry, 0,
            Collect.make(registryAccess, definition, multiplier, random),
            Rewards.make(registryAccess, definition, multiplier, random)
        );
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
        return epic;
    }
}
