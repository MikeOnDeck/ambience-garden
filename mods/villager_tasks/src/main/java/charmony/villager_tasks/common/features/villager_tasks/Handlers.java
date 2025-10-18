package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.base.Setup;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Handlers extends Setup<VillagerTasks> {
    private static final Map<Player, Tasks> TASKS = new HashMap<>();

    public static final String DEFINITIONS_DIR = "villager_tasks";
    public final Map<ResourceLocation, Definition> definitions = new HashMap<>();

    public Handlers(VillagerTasks feature) {
        super(feature);
    }

    public void entityJoin(Entity entity, ServerLevel level) {
        if (entity instanceof ServerPlayer player) {
            var state = TasksSavedData.getServerState(level.getServer());
            var tasks = state.getTasks(player);

            // Update this player's tasks.
            setTasks(player, tasks);

            // Sync tasks to the client.
            syncTasks(player);
        }
    }

    public void setTasks(Player player, Tasks tasks) {
        TASKS.put(player, tasks);
    }

    public void syncTasks(ServerPlayer player) {
        var tasks = TASKS.get(player);
        if (tasks != null) {
            Networking.S2CSendActiveTasks.send(player, tasks);
        }
    }

    public void generateVillagerTasks(AbstractVillager villager) {

    }

    /**
     * Get a random definition that applies to the given villager.
     */
    public Optional<Definition> definition(ServerLevel level, Villager villager, RandomSource random) {
        var entityRegistry = level.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE);

        var valid = new ArrayList<Definition>();

        for (var def : definitions.values()) {
            if (def.appliesTo(entityRegistry, villager)) {
                valid.add(def);
            }
        }

        if (valid.isEmpty()) {
            return Optional.empty();
        }

        Util.shuffle(valid, random);
        return Optional.of(valid.getFirst());
    }

    /**
     * Get a specific definition by its ID.
     */
    public Definition definition(ResourceLocation id) {
        var definition = definitions.get(id);

        if (definition == null) {
            throw new RuntimeException("Definition not found: " + id);
        }

        return definition;
    }

    /**
     * Clear and reload all villager task definitions from disk.
     */
    public void loadDefinitions(MinecraftServer server) {
        definitions.clear();

        var manager = server.getResourceManager();
        var files = manager.listResources(DEFINITIONS_DIR, file -> file.getPath().endsWith(".json"));

        for (var file : files.entrySet()) {
            var id = file.getKey();
            var path = id.getPath();
            var resource = file.getValue();

            Definition def;

            try {
                def = Definition.fromJson(id, manager, resource);
            } catch (Exception e) {
                log().error("Failed to load villager task definition from " + id + ": " + e.getMessage());
                continue;
            }

            definitions.put(id, def);
            log().debug("Loaded villager task definition: " + path);
        }
    }
}
