package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.base.Setup;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskModifier;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskQuery;
import charmony.villager_tasks.common.features.villager_tasks.requirements.TreasureLootFunction;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class Handlers extends Setup<VillagerTasks> {
    public static final int TEMP_RECENT_TASK_DURATION_TICKS = 150;
    public static final int TEMP_EPIC_TASK_LOYALTY = 2;
    public static final String DEFINITIONS_DIR = "villager_tasks";
    public static final Map<Player, Tasks> PLAYER_TASKS = new HashMap<>();
    public static final Map<Player, Tasks> RECENT_TASKS = new HashMap<>();
    public static final Map<Player, AvailableTasks> AVAILABLE_TASKS = new HashMap<>();
    public static final Map<Player, Long> LAST_REQUESTED_TASK_SYNC = new HashMap<>();
    public static final Map<Player, UUID> LAST_VILLAGER_INTERACTION = new HashMap<>();

    public final Map<ResourceLocation, Definition> definitions = new HashMap<>();

    public Handlers(VillagerTasks feature) {
        super(feature);
    }

    public void entityJoin(Entity entity, ServerLevel level) {
        if (entity instanceof ServerPlayer player) {
            var state = PersistentData.getServerState(level.getServer());
            var tasks = state.getTasks(player);

            // Update this player's tasks.
            setActiveTasks(player, tasks);

            // Sync tasks to the client.
            syncActiveTasks(player);
        }
    }

    public void playerTick(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        // Tick all tasks for the player.
        var tasks = PLAYER_TASKS.getOrDefault(player, Tasks.EMPTY);
        tasks.tasks().forEach(task -> task.onTick(task, serverPlayer));

        // Remove recent tasks after a certain time.
        refreshRecentTasks(serverPlayer);
    }

    /**
     * Handle player interaction with a merchant entity (villager, wandering trader).
     */
    public InteractionResult useEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        if (entity instanceof AbstractVillager villager && player instanceof ServerPlayer serverPlayer) {
            setLastVillagerInteraction(serverPlayer, villager.getUUID());
            syncVillagerInteraction(serverPlayer);
            syncActiveTasks(serverPlayer);
        }

        return InteractionResult.PASS;
    }

    public InteractionResult itemPickup(Player player, ItemEntity itemEntity) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        var tasks = PLAYER_TASKS.getOrDefault(serverPlayer, Tasks.EMPTY);
        for (var task : tasks.tasks()) {
            task.onItemPickup(task, serverPlayer, itemEntity.getItem());
        }

        return InteractionResult.PASS;
    }

    public void lootTableModify(ResourceKey<LootTable> lootTable, LootTable.Builder builder, LootTableSource source, HolderLookup.Provider provider) {
        if (!source.isBuiltin()) return;

        // Add custom loot functions for aspects that modify loot tables.
        var pool = LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(Items.AIR)
                .setWeight(1)
                .apply(() -> new TreasureLootFunction(lootTable)));

        builder.pool(pool.build());
    }

    public void afterEntityDeath(LivingEntity entity, DamageSource damageSource) {
        if (entity.level() instanceof ServerLevel && damageSource.getEntity() instanceof ServerPlayer player) {
            var tasks = PLAYER_TASKS.getOrDefault(player, Tasks.EMPTY);
            for (var task : tasks.tasks()) {
                var result = task.onEntityKilled(task, entity, damageSource);
                if (result) return; // Stop processing if the event was handled.
            }
        }
    }

    public void handleReceiveQueryTask(Player player, Networking.C2SQueryTask payload) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        var query = payload.query();
        var id = payload.id();
        var playerName = player.getName().getString();

        switch (query) {
            case TaskQuery.Accept -> {
                var potentialTasks = AVAILABLE_TASKS.getOrDefault(player, AvailableTasks.EMPTY);
                var task = potentialTasks.tasks.getTaskById(id).orElse(null);
                if (task == null) {
                    log().warn("Task not found in available tasks: " + id);
                    return;
                }

                log().info("Player " + playerName + " accepted task: " + id);
                startTask(serverPlayer, task.copy()); // We copy the task to avoid mutating the available tasks.
            }

            case TaskQuery.Abandon -> {
                var tasks = PLAYER_TASKS.getOrDefault(player, Tasks.EMPTY);
                var task = tasks.getTaskById(id).orElse(null);
                if (task == null) {
                    log().warn("Task not found in available tasks: " + id);
                    return;
                }

                log().info("Player " + playerName + " abandoned task: " + id);
                abandonTask(serverPlayer, task);
            }

            case TaskQuery.Complete -> {
                var tasks = PLAYER_TASKS.getOrDefault(player, Tasks.EMPTY);
                var task = tasks.getTaskById(id).orElse(null);
                if (task == null) {
                    log().warn("Task not found in active tasks: " + id);
                    return;
                }

                if (!task.isSatisfied()) {
                    log().warn("Player " + playerName + " attempted to complete unsatisfied task: " + id);
                    return;
                }

                log().info("Player " + playerName + " completed task: " + id);
                completeTask(serverPlayer, task);
            }
        }
    }

    public void handleReceiveRequestActiveTasks(Player player, Networking.C2SRequestActiveTasks payload) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (shouldThrottleClientRequest(serverPlayer)) return;
        syncActiveTasks(serverPlayer);
    }

    public void setActiveTasks(Player player, Tasks tasks) {
        PLAYER_TASKS.put(player, tasks);
    }

    public Optional<Tasks> getActiveTasks(Player player) {
        return Optional.ofNullable(PLAYER_TASKS.get(player));
    }

    public void syncActiveTasks(ServerPlayer player) {
        getActiveTasks(player).ifPresent(
            tasks -> Networking.S2CSendActiveTasks.send(player, tasks));
    }

    public void syncVillagerInteraction(ServerPlayer player) {
        getLastVillagerInteraction(player).ifPresent(
            villager -> Networking.S2CSendVillagerInteraction.send(player, villager));
    }

    public void syncEverything(ServerPlayer player) {
        syncActiveTasks(player);
        syncVillagerInteraction(player);
        syncAvailableTasks(player);
    }

    /**
     * Called by villger interaction mixins for the villager and wandering trader.
     */
    public void interactWithVillager(ServerPlayer player, AbstractVillager villager) {
        setLastVillagerInteraction(player, villager.getUUID());
        syncAvailableTasks(player);
    }

    public void setLastVillagerInteraction(ServerPlayer player, UUID villager) {
        LAST_VILLAGER_INTERACTION.put(player, villager);
    }

    public Optional<UUID> getLastVillagerInteraction(ServerPlayer player) {
        return Optional.ofNullable(LAST_VILLAGER_INTERACTION.get(player));
    }

    /**
     * Generate a list of tasks based on a deterministic seed using the villager's UUID and current minecraft day.
     * Cache tasks in the AVAILABLE_TASKS map for faster lookup.
     */
    public void syncAvailableTasks(ServerPlayer player) {
        var uuid = getLastVillagerInteraction(player).orElse(null);
        if (uuid == null) return;

        TaskModifier taskModifier;
        var taskOwner = Helpers.getNearbyTaskOwner(player, uuid).orElse(null);
        if (taskOwner instanceof Villager villager) {
            // Reputation and loyalty matter when generating a task modifier.
            var reputation = villager.getPlayerReputation(player);
            var loyalty = getLoyalty(player, uuid);

            if (reputation >= 0 && loyalty >= TEMP_EPIC_TASK_LOYALTY) {
                taskModifier = TaskModifier.Epic;
            } else {
                taskModifier = TaskModifier.fromReputation(reputation);
            }
        } else {
            taskModifier = TaskModifier.Normal;
        }

        var level = player.level();
        var seed = getTaskSeed(level, uuid, taskModifier);
        var availableTasks = AVAILABLE_TASKS.computeIfAbsent(player, p -> AvailableTasks.EMPTY);

        // Regenerate tasks if the seed no longer matches.
        if (availableTasks.seed != seed) {
            var random = RandomSource.create(seed);
            var defs = new ArrayList<>(definitions.values());
            Util.shuffle(defs, random);

            // Get top valid definitions.
            var valid = defs.stream()
                .filter(def -> taskOwner != null && def.appliesTo(level.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE), taskOwner))
                .limit(5)
                .toList();

            // Generate tasks from definitions
            var taskList = new ArrayList<Task>();
            for (var def : valid) {
                try {
                    var task = Task.create(player, def, uuid, taskModifier, seed);
                    taskList.add(task);
                } catch (Exception e) {
                    log().error("Failed to create task from definition " + def.id + ": " + e.getMessage());
                }
            }

            var name = taskOwner != null ? taskOwner.getDisplayName().getString() : "??";
            var tasks = new Tasks(uuid, name, taskList);
            availableTasks = new AvailableTasks(seed, tasks);
            AVAILABLE_TASKS.put(player, availableTasks);
        }

        var filtered = filterRecentTasks(player, availableTasks.tasks);
        Networking.S2CSendAvailableTasks.send(player, filtered);
    }

    public int getLoyalty(ServerPlayer player, UUID villager) {
        var state = PersistentData.getServerState(player.level().getServer());
        return state.getLoyalty(player).getLoyalty(villager);
    }

    public void resetLoyalty(ServerPlayer player, UUID villager) {
        var level = player.level();
        var state = PersistentData.getServerState(level.getServer());
        state.updateLoyalty(state.getLoyalty(player).resetLoyalty(villager));
    }

    public void tryIncreaseLoyalty(ServerPlayer player, Task task) {
        var uuid = getLastVillagerInteraction(player).orElse(Helpers.emptyUuid());
        if (!task.belongsTo(uuid)) {
            log().debug("Not increasing loyalty since the task does not belong to the last interacted villager.");
            return;
        }

        var level = player.level();
        var state = PersistentData.getServerState(level.getServer());
        var loyalty = state.getLoyalty(player);
        state.updateLoyalty(loyalty.addLoyalty(uuid));
        level.playSound(null, player.blockPosition(), SoundEvents.VILLAGER_YES, player.getSoundSource(), 1.0f, 1.0f);
        log().debug("Increasing loyalty for villager " + uuid + " for player " + player.getName().getString());
    }

    public void addToRecentTasks(ServerPlayer player, Task task) {
        var recent = RECENT_TASKS.computeIfAbsent(player, p -> Tasks.EMPTY);
        var updated = recent.addTask(task.copyWithTime(player.level().getGameTime()));
        RECENT_TASKS.put(player, updated);
    }

    public Tasks filterRecentTasks(ServerPlayer player, Tasks tasks) {
        var updated = new ArrayList<>(tasks.tasks());
        var active = PLAYER_TASKS.getOrDefault(player, Tasks.EMPTY);
        var recent = RECENT_TASKS.getOrDefault(player, Tasks.EMPTY);

        for (var availableTask : tasks.tasks()) {
            if (active.getTaskById(availableTask.id).isPresent()) continue;
            var matching = recent.ofDefinition(availableTask.definitionId);

            for (var toRemove : matching) {
                updated.removeIf(t -> t.definitionId == toRemove.definitionId);
            }
        }

        return new Tasks(tasks.uuid(), tasks.name(), updated);
    }

    public void refreshRecentTasks(ServerPlayer serverPlayer) {
        var gameTime = serverPlayer.level().getGameTime();
        var recent = RECENT_TASKS.getOrDefault(serverPlayer, Tasks.EMPTY);
        var recentCount = recent.tasks().size();

        var updated = recent.removeOlderThan(gameTime - TEMP_RECENT_TASK_DURATION_TICKS); // TODO: needs config
        var updatedCount = updated.tasks().size();

        RECENT_TASKS.put(serverPlayer, updated);

        if (updatedCount != recentCount) {
            // Allows villagers to offer refreshed tasks dynamically as the player is looking at available tasks screen.
            syncAvailableTasks(serverPlayer);
        }
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

    public void startTask(ServerPlayer player, Task task) {
        var tasks = PLAYER_TASKS.getOrDefault(player, Tasks.EMPTY);
        var serverLevel = player.level();
        var state = PersistentData.getServerState(serverLevel.getServer());
        var playerName = player.getName().getString();

        if (tasks.getTaskById(task.id).isPresent()) {
            // Don't start the same task twice.
            return;
        }

        if (tasks.tasks().size() >= 5) {
            // TODO: probably need to notify the player that they can't accept more tasks?
            log().error("Player " + playerName + " has reached the maximum number of active tasks.");
            return;
        }

        log().info("Starting task for player " + playerName + ": " + task.getDefinitionId());
        task.onStart(task, player);
        tasks = tasks.addTask(task);

        if (task.isEpic()) {
            // Accepting an epic task resets loyalty with the task-giver.
            resetLoyalty(player, task.villager);
        }

        addToRecentTasks(player, task);
        state.updateTasks(tasks);
        PLAYER_TASKS.put(player, tasks);
        playSound(player, feature().registers.taskAccept);
        syncEverything(player);
    }

    public void abandonTask(ServerPlayer player, Task task) {
        var tasks = PLAYER_TASKS.getOrDefault(player, Tasks.EMPTY);
        var serverLevel = player.level();
        var state = PersistentData.getServerState(serverLevel.getServer());
        var playerName = player.getName().getString();

        if (tasks.getTaskById(task.id).isEmpty()) {
            return;
        }

        log().info("Abandoning task for player " + playerName + ": " + task.id);
        task.onAbandon(task, player);
        tasks = tasks.removeTask(task);

        state.updateTasks(tasks);
        PLAYER_TASKS.put(player, tasks);
        playSound(player, feature().registers.taskAbandon);
        syncEverything(player);
    }

    public void completeTask(ServerPlayer player, Task task) {
        var tasks = PLAYER_TASKS.getOrDefault(player, Tasks.EMPTY);
        var serverLevel = player.level();
        var state = PersistentData.getServerState(serverLevel.getServer());
        var playerName = player.getName().getString();

        if (tasks.getTaskById(task.id).isEmpty()) {
            log().error("Task not found for player " + playerName + ": " + task.id);
            return;
        }

        log().info("Completing task for player " + playerName + ": " + task.id);
        task.onComplete(task, player);
        tasks = tasks.removeTask(task);

        tryIncreaseLoyalty(player, task);
        addToRecentTasks(player, task);
        state.updateTasks(tasks);
        PLAYER_TASKS.put(player, tasks);
        playSound(player, task.isEpic() ? feature().registers.epicTaskCompleteSound : feature().registers.taskCompleteSound);
        syncEverything(player);
    }

    public long getTaskSeed(ServerLevel level, UUID merchant, TaskModifier modifier) {
        // Get the current minecraft day.
        var day = level.getDayTime() / 24000L;

        // Create a unique seed based on the level seed, the day and the villager's UUID.
        return (level.getSeed() / 4) + modifier.reputation() + day * 31 + merchant.hashCode() * 17L;
    }

    public boolean shouldThrottleClientRequest(ServerPlayer player) {
        var lastRequested = LAST_REQUESTED_TASK_SYNC.getOrDefault(player, 0L);
        var gameTime = player.level().getGameTime();

        if (gameTime - lastRequested < 20) {
            // Throttle requests to once per second.
            return true;
        }

        LAST_REQUESTED_TASK_SYNC.put(player, gameTime);
        return false;
    }

    public void playSound(ServerPlayer player, Supplier<SoundEvent> soundEvent) {
        player.level().playSound(null, player.blockPosition(), soundEvent.get(), player.getSoundSource(), 1.0f, 1.0f);
    }

    public record AvailableTasks(long seed, Tasks tasks) {
        public static AvailableTasks EMPTY = new AvailableTasks(0L, Tasks.EMPTY);
    }
}
