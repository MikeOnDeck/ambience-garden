package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.enums.TaskModifier;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskStatus;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.UUID;

public class Task {
    private final TaskType type;
    private final ResourceLocation definitionId;
    private final UUID villager;
    private final boolean epic;
    private final long seed;
    private final double multiplier;
    private final int expiry;
    private final int level;

    private TaskStatus status;
    private int duration = 0;

    public static final Codec<Task> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        TaskType.CODEC.fieldOf("type").forGetter(task -> task.type),
        TaskStatus.CODEC.fieldOf("status").forGetter(task -> task.status),
        ResourceLocation.CODEC.fieldOf("definitionId").forGetter(task -> task.definitionId),
        UUIDUtil.CODEC.fieldOf("villager").forGetter(task -> task.villager),
        Codec.BOOL.fieldOf("epic").forGetter(task -> task.epic),
        Codec.LONG.fieldOf("seed").forGetter(task -> task.seed),
        Codec.DOUBLE.fieldOf("multiplier").forGetter(task -> task.multiplier),
        Codec.INT.fieldOf("level").forGetter(task -> task.level),
        Codec.INT.fieldOf("expiry").forGetter(task -> task.expiry),
        Codec.INT.fieldOf("duration").forGetter(task -> task.duration)
    ).apply(instance, Task::new));

    private Task(TaskType type, ResourceLocation definitionId, UUID villager, boolean epic, long seed, double multiplier, int level, int expiry) {
        this(type, TaskStatus.NotStarted, definitionId, villager, epic, seed, multiplier, expiry, level, 0);
    }

    private Task(TaskType type, TaskStatus status, ResourceLocation definitionId, UUID villager, boolean epic, long seed, double multiplier, int level, int expiry, int duration) {
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
    }

    public static Task create(ServerPlayer player, Definition definition, UUID uuid, TaskModifier modifier, long seed) {
        var random = RandomSource.create(seed);
        var expiry = definition.expiry;
        var level = definition.level;

        // The modifier and player luck affect the task multiplier.
        var multiplier = Math.max(definition.multiplier, modifier.getMultiplier(random)) + (player.getLuck() * 1.0d);
        var type = TaskType.fromString(definition.types.get(random.nextInt(definition.types.size())));

        return new Task(type, definition.id, uuid, modifier.isEpic(), seed, multiplier, level, expiry);
    }
}
