package charmony.villager_tasks.common.features.villager_tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.List;
import java.util.UUID;

public record Tasks(UUID uuid, String name, List<Task> tasks) {
    public static final String UUID_TAG = "uuid";
    public static final String NAME_TAG = "name";
    public static final String TASKS_TAG = "tasks";

    public static final Codec<Tasks> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("uuid").forGetter(Tasks::uuid),
        Codec.STRING.fieldOf("name").forGetter(Tasks::name),
        Task.CODEC.listOf().fieldOf("tasks").forGetter(Tasks::tasks)
    ).apply(instance, Tasks::new));
}
