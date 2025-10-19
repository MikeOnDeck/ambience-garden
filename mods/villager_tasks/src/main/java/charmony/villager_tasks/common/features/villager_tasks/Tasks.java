package charmony.villager_tasks.common.features.villager_tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.UUID;

public record Tasks(UUID uuid, String name, List<Task> tasks) {
    public static final String TASKS_TAG = "tasks";

    public static final Codec<Tasks> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("uuid").forGetter(Tasks::uuid),
        Codec.STRING.fieldOf("name").forGetter(Tasks::name),
        Task.CODEC.listOf().fieldOf("tasks").forGetter(Tasks::tasks)
    ).apply(instance, Tasks::new));

    public CompoundTag save() {
        var tag = new CompoundTag();
        tag.store(TASKS_TAG, Tasks.CODEC, this);
        return tag;
    }

    public static Tasks load(CompoundTag tag) {
        return tag.read(TASKS_TAG, Tasks.CODEC).orElseThrow();
    }

    public boolean isEmpty() {
        return tasks.isEmpty();
    }
}
