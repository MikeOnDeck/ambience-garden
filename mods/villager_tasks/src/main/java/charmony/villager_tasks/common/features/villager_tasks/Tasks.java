package charmony.villager_tasks.common.features.villager_tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Tasks(UUID uuid, String name, List<Task> tasks) {
    public static final String UUID_TAG = "uuid";
    public static final String NAME_TAG = "name";
    public static final String TASKS_TAG = "tasks";

    public static final Codec<Tasks> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf(UUID_TAG).forGetter(Tasks::uuid),
        Codec.STRING.fieldOf(NAME_TAG).forGetter(Tasks::name),
        Task.CODEC.listOf().fieldOf(TASKS_TAG).forGetter(Tasks::tasks)
    ).apply(instance, Tasks::new));

    public CompoundTag save() {
        var tag = new CompoundTag();
        var tasksTag = new ListTag();

        for (var task : tasks()) {
            tasksTag.add(Task.CODEC.encodeStart(NbtOps.INSTANCE, task).getOrThrow());
        }

        tag.store(UUID_TAG, UUIDUtil.CODEC, uuid());
        tag.putString(NAME_TAG, name());
        tag.put(TASKS_TAG, tasksTag);
        return tag;
    }

    public static Tasks load(CompoundTag tag) {
        var uuid = tag.read(UUID_TAG, UUIDUtil.CODEC).orElse(null);
        var name = tag.getString(NAME_TAG).orElse("");
        var tasksTag = tag.getList(TASKS_TAG);

        var tasks = new ArrayList<>(tasksTag.stream().map(nbt ->
            Task.CODEC.parse(NbtOps.INSTANCE, nbt).result().orElse(null)
        ).filter(Objects::nonNull).toList());

        return new Tasks(uuid, name, tasks);
    }
}
