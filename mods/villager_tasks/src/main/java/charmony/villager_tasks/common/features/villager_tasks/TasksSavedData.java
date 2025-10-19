package charmony.villager_tasks.common.features.villager_tasks;

import charmony.villager_tasks.VillagerTasksMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TasksSavedData extends SavedData {
    private List<Tasks> tasks = new ArrayList<>();

    public static final Codec<TasksSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Tasks.CODEC.listOf().fieldOf("tasks").forGetter(data -> data.tasks)
    ).apply(instance, TasksSavedData::new));

    public static final SavedDataType<TasksSavedData> TYPE = new SavedDataType<>(
        VillagerTasksMod.ID,
        TasksSavedData::new,
        CODEC,
        null
    );

    public TasksSavedData() {
        setDirty();
    }

    private TasksSavedData(List<Tasks> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public Tasks getTasks(Player player) {
        var uuid = player.getUUID();
        var name = player.getScoreboardName();
        var existing = getTasksByUUID(uuid);
        return existing.orElseGet(() -> new Tasks(uuid, name, List.of()));
    }

    public void updateTasks(Tasks updated) {
        var existing = getTasksByUUID(updated.uuid());

        if (!(tasks instanceof ArrayList<Tasks>)) {
            // Stupid hack.
            tasks = new ArrayList<>(tasks);
        }

        existing.ifPresent(tasks::remove);
        tasks.add(updated);
        setDirty();
    }

    /**
     * Helper to get the saved data for the server.
     *
     * @param server Server instance.
     * @return Saved data.
     */
    public static TasksSavedData getServerState(MinecraftServer server) {
        var level = server.getLevel(Level.OVERWORLD);
        if (level == null) {
            throw new RuntimeException("Level not available");
        }
        var storage = level.getDataStorage();
        var state = storage.computeIfAbsent(TYPE);
        state.setDirty();
        return state;
    }

    public Optional<Tasks> getTasksByUUID(UUID uuid) {
        return tasks.stream().filter(t -> t.uuid().equals(uuid)).findFirst();
    }
}
