package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskModifier;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Handlers extends Setup<VillagerTasks> {
    public static final String DEFINITIONS_DIR = "villager_tasks";

    public static final Map<Player, Tasks> PLAYER_TASKS = new HashMap<>();
    public static final Map<Player, Map<UUID, Map<Long, Tasks>>> AVAILABLE_TASKS = new HashMap<>();

    public final Map<ResourceLocation, Definition> definitions = new HashMap<>();

    public Handlers(VillagerTasks feature) {
        super(feature);
    }

    public void entityJoin(Entity entity, ServerLevel level) {
        if (entity instanceof ServerPlayer player) {
            var state = TasksSavedData.getServerState(level.getServer());
            var tasks = state.getTasks(player);

            // Update this player's tasks.
            setActiveTasks(player, tasks);

            // Sync tasks to the client.
            syncActiveTasks(player);
        }
    }

    public void setActiveTasks(Player player, Tasks tasks) {
        PLAYER_TASKS.put(player, tasks);
    }

    public void syncActiveTasks(ServerPlayer player) {
        var tasks = PLAYER_TASKS.get(player);
        if (tasks != null) {
            Networking.S2CSendActiveTasks.send(player, tasks);
        }
    }

    public void syncVillagerInteraction(ServerPlayer player, AbstractVillager villager) {
        Networking.S2CSendVillagerInteraction.send(player, villager.getUUID());
    }

    public void syncAvailableTasks(ServerPlayer player, Tasks tasks) {
        Networking.S2CSendAvailableTasks.send(player, tasks);
    }

    public void makeAvailableTasks(ServerPlayer player, AbstractVillager villager) {
        var level = player.level();
        var uuid = villager.getUUID();

        if (!AVAILABLE_TASKS.containsKey(player)) {
            AVAILABLE_TASKS.put(player, new HashMap<>());
        }

        var seed = getTaskSeed(level, uuid);
        var cachedTasksForPlayer = AVAILABLE_TASKS.get(player);

        if (cachedTasksForPlayer.containsKey(uuid)) {
            var cached = cachedTasksForPlayer.get(uuid);
            if (cached != null && cached.containsKey(seed)) {
                syncAvailableTasks(player, cached.get(seed));
                return;
            }
        } else {
            // Prep cache
            cachedTasksForPlayer.put(uuid, new HashMap<>());
        }
        
        var random = RandomSource.create(seed);
        var defs = new ArrayList<>(definitions.values());
        Util.shuffle(defs, random);

        // Get top three valid definitions.
        var valid = defs.stream()
            .filter(def -> def.appliesTo(level.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE), villager))
            .limit(3)
            .toList();

        // Generate tasks from definitions
        var taskList = new ArrayList<Task>();
        for (var def : valid) {
            try {
                taskList.add(Task.create(player, def, uuid, TaskModifier.Normal, seed));
            } catch (Exception e) {
                log().error("Failed to create task from definition " + def.id + ": " + e.getMessage());
            }
        }

        var tasks = new Tasks(uuid, villager.getDisplayName().getString(), taskList);
        cachedTasksForPlayer.get(uuid).put(seed, tasks);
        syncAvailableTasks(player, tasks);
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

    /**
     * Handle player interaction with a merchant entity (villager, wandering trader).
     */
    public InteractionResult handleUseEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        if (entity instanceof AbstractVillager villager && player instanceof ServerPlayer serverPlayer) {
            // We need to hold the UUID of the last interacted merchant on the client.
            syncVillagerInteraction(serverPlayer, villager);
            syncActiveTasks(serverPlayer);
        }

        return InteractionResult.PASS;
    }

    public void handleReceiveAcceptTask(Player player, Networking.C2SAcceptTask payload) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        var merchant = payload.merchant();
        var id = payload.definitionId();
        var playerName = player.getName().getString();

        if (!AVAILABLE_TASKS.containsKey(player)) {
            log().warn("No cached tasks for player " + playerName);
            return;
        }

        var cachedTasksForPlayer = AVAILABLE_TASKS.get(player);

        if (!cachedTasksForPlayer.containsKey(merchant)) {
            log().warn("No cached tasks for merchant " + merchant);
            return;
        }

        var availableTasks = cachedTasksForPlayer.get(merchant);
        var seed = getTaskSeed(serverPlayer.level(), merchant);

        if (!availableTasks.containsKey(seed)) {
            log().warn("No cached tasks for seed " + seed);
            return;
        }

        var tasks = availableTasks.get(seed);

        // Get the task definition from available tasks that matches the definition ID.
        var task = tasks.tasks().stream().filter(t -> t.getDefinitionId().equals(id)).findFirst().orElse(null);
        if (task == null) {
            log().warn("Task not found in available tasks: " + id);
            return;
        }

        log().info("Player " + playerName + " accepted task: " + id);
        startTask(serverPlayer, task);
    }

    public void startTask(ServerPlayer player, Task task) {
        var tasks = PLAYER_TASKS.get(player);
        var serverLevel = player.level();
        var state = TasksSavedData.getServerState(serverLevel.getServer());

        var playerName = player.getName().getString();

        if (tasks == null) {
            tasks = state.getTasks(player);
        }

        if (tasks.getTaskByDefinition(task.getDefinitionId()).isPresent()) {
            log().error("Player " + playerName + " already has task: " + task.getDefinitionId());
            return;
        }

        if (tasks.tasks().size() >= 3) {
            log().error("Player " + playerName + " has reached the maximum number of active tasks.");
            return;
        }

        log().info("Starting task for player " + playerName + ": " + task.getDefinitionId());
        tasks = tasks.addTask(task);

        // Update world save state.
        state.updateTasks(tasks);

        // Update server memory state.
        PLAYER_TASKS.put(player, tasks);

        // Sync to client.
        syncActiveTasks(player);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private long getTaskSeed(ServerLevel level, UUID merchant) {
        // Get the current minecraft day.
        var day = level.getDayTime() / 24000L;

        // Create a unique seed based on the level seed, the day and the villager's UUID.
        var seed = (level.getSeed() / 2) + day * 31 + merchant.hashCode() * 17L;

        return seed;
    }
}
