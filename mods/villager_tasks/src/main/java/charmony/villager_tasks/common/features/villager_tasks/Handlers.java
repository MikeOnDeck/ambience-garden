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
    public static final Map<Player, Tasks> TASKS = new HashMap<>();
    public static final String DEFINITIONS_DIR = "villager_tasks";
    public static final Map<UUID, Map<Long, Tasks>> CACHED_VILLAGER_TASKS = new WeakHashMap<>();

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
        TASKS.put(player, tasks);
    }

    public void syncActiveTasks(ServerPlayer player) {
        var tasks = TASKS.get(player);
        if (tasks != null) {
            Networking.S2CSendActiveTasks.send(player, tasks);
        }
    }

    public void syncAvailableTasks(ServerPlayer player, Tasks tasks) {
        Networking.S2CSendAvailableTasks.send(player, tasks);
    }

    public void makeAvailableTasks(ServerPlayer player, AbstractVillager villager) {
        var level = player.level();
        var uuid = villager.getUUID();

        // Get the current minecraft day.
        var day = level.getDayTime() / 24000L;

        // Create a unique seed based on the level seed, the day and the villager's UUID.
        var seed = (level.getSeed() / 2) + day * 31 + uuid.hashCode() * 17L;

        if (CACHED_VILLAGER_TASKS.containsKey(uuid)) {
            var cached = CACHED_VILLAGER_TASKS.get(uuid);
            if (cached != null && cached.containsKey(seed)) {
                syncAvailableTasks(player, cached.get(seed));
                return;
            }
        } else {
            // Prep cache
            CACHED_VILLAGER_TASKS.put(uuid, new HashMap<>());
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

        var tasks = new Tasks(uuid, "merchant", taskList);
        CACHED_VILLAGER_TASKS.get(uuid).put(seed, tasks);
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
            Networking.S2CSendMerchantInteraction.send(serverPlayer, villager.getUUID());
        }

        return InteractionResult.PASS;
    }
}
