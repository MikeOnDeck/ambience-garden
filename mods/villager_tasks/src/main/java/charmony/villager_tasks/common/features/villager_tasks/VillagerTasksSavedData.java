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

public class VillagerTasksSavedData extends SavedData {
    private List<Tasks> tasks = new ArrayList<>();
    private List<Loyalty> loyalties = new ArrayList<>();

    public static final Codec<VillagerTasksSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Tasks.CODEC.listOf().fieldOf("tasks").forGetter(data -> data.tasks),
        Loyalty.CODEC.listOf().fieldOf("loyalty").forGetter(data -> data.loyalties)
    ).apply(instance, VillagerTasksSavedData::new));

    public static final SavedDataType<VillagerTasksSavedData> TYPE = new SavedDataType<>(
        VillagerTasksMod.ID,
        VillagerTasksSavedData::new,
        CODEC,
        null
    );

    public VillagerTasksSavedData() {
        setDirty();
    }

    private VillagerTasksSavedData(List<Tasks> tasks, List<Loyalty> loyalties) {
        this.tasks = new ArrayList<>(tasks);
        this.loyalties = new ArrayList<>(loyalties);
    }

    public Tasks getTasks(Player player) {
        var uuid = player.getUUID();
        var name = player.getScoreboardName();
        var existing = getTasksByUUID(uuid);
        return existing.orElseGet(() -> new Tasks(uuid, name, List.of()));
    }

    public Optional<Tasks> getTasksByUUID(UUID uuid) {
        return tasks.stream().filter(t -> t.uuid().equals(uuid)).findFirst();
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

    public Loyalty getLoyalty(Player player) {
        var uuid = player.getUUID();
        var name = player.getScoreboardName();
        var existing = getLoyaltyByUUID(uuid);
        return existing.orElseGet(() -> new Loyalty(uuid, name, List.of()));
    }

    public Optional<Loyalty> getLoyaltyByUUID(UUID uuid) {
        return loyalties.stream().filter(l -> l.uuid().equals(uuid)).findFirst();
    }

    public void updateLoyalty(Loyalty updated) {
        var existing = getLoyaltyByUUID(updated.uuid());

        if (!(loyalties instanceof ArrayList<Loyalty>)) {
            // Stupid hack.
            loyalties = new ArrayList<>(loyalties);
        }

        existing.ifPresent(loyalties::remove);
        loyalties.add(updated);
        setDirty();
    }

    /**
     * Helper to get the saved data for the server.
     *
     * @param server Server instance.
     * @return Saved data.
     */
    public static VillagerTasksSavedData getServerState(MinecraftServer server) {
        var level = server.getLevel(Level.OVERWORLD);
        if (level == null) {
            throw new RuntimeException("Level not available");
        }
        var storage = level.getDataStorage();
        var state = storage.computeIfAbsent(TYPE);
        state.setDirty();
        return state;
    }
}
