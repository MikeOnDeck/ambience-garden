package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.common.features.villager_tasks.enums.TaskModifier;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.Villager;

import java.util.UUID;

public class Task {
    private final TaskType type;
    private ResourceLocation definition;
    private UUID villager = UUID.randomUUID();
    private boolean epic = false;
    private long seed = -1;
    private double multiplier = 1.0d;
    private int expiry = 0;
    private int duration = 0;

    public static final Codec<Task> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        TaskType.CODEC.fieldOf("type").forGetter(task -> task.type),
        ResourceLocation.CODEC.fieldOf("definition").forGetter(task -> task.definition),
        UUIDUtil.CODEC.fieldOf("villager").forGetter(task -> task.villager),
        Codec.BOOL.fieldOf("epic").forGetter(task -> task.epic),
        Codec.LONG.fieldOf("seed").forGetter(task -> task.seed),
        Codec.DOUBLE.fieldOf("multiplier").forGetter(task -> task.multiplier),
        Codec.INT.fieldOf("expiry").forGetter(task -> task.expiry),
        Codec.INT.fieldOf("duration").forGetter(task -> task.duration)
    ).apply(instance, Task::new));

    private Task(TaskType type, ResourceLocation definition, UUID villager, boolean epic, long seed, double multiplier, int expiry) {
        this(type, definition, villager, epic, seed, multiplier, expiry, 0);
    }

    private Task(TaskType type, ResourceLocation definition, UUID villager, boolean epic, long seed, double multiplier, int expiry, int duration) {
        this.type = type;
    }

    public static Task create(ServerPlayer player, Villager villager, TaskModifier modifier, long seed) {
        var level = player.level();
        var random = RandomSource.create(seed);
        var definition = VillagerTasks.feature().handlers.definition(level, villager, random).orElseThrow();
        return create(player, definition, villager.getUUID(), modifier, seed);
    }

    public static Task create(ServerPlayer player, Definition definition, UUID uuid, TaskModifier modifier, long seed) {
        var random = RandomSource.create(seed);
        var expiry = definition.expiry;
        var type = TaskType.valueOf(definition.types.get(random.nextInt(definition.types.size())));
        var multiplier = Math.max(definition.multiplier, modifier.getMultiplier(random)) + (player.getLuck() * 1.0d);
        return new Task(type, definition.id, uuid, modifier.isEpic(), seed, multiplier, expiry);
    }
}
