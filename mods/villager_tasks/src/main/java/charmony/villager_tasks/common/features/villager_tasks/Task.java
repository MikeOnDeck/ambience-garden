package charmony.villager_tasks.common.features.villager_tasks;

import charmony.core.helpers.UuidHelper;
import charmony.villager_tasks.common.features.villager_tasks.aspects.Collect;
import charmony.villager_tasks.common.features.villager_tasks.aspects.Hunt;
import charmony.villager_tasks.common.features.villager_tasks.aspects.Rewards;
import charmony.villager_tasks.common.features.villager_tasks.aspects.Treasure;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskModifier;
import charmony.villager_tasks.common.features.villager_tasks.enums.TaskStatus;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.EventListener;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.UUID;

public class Task implements EventListener, Satisfiable {
    public final UUID id;
    public final UUID villager;
    public final ResourceLocation definitionId;
    public final TaskModifier modifier;
    public final String titleKey;
    public final long seed;
    public final long created;
    public final int level;

    public final Collect collect;
    public final Hunt hunt;
    public final Treasure treasure;
    public final Rewards rewards;

    private TaskStatus status;

    public static final Codec<Task> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        UUIDUtil.CODEC.fieldOf("id").forGetter(task -> task.id),
        UUIDUtil.CODEC.fieldOf("villager").forGetter(task -> task.villager),
        ResourceLocation.CODEC.fieldOf("definitionId").forGetter(task -> task.definitionId),
        TaskStatus.CODEC.fieldOf("status").forGetter(task -> task.status),
        TaskModifier.CODEC.fieldOf("modifier").forGetter(task -> task.modifier),
        Codec.STRING.fieldOf("titleKey").forGetter(task -> task.titleKey),
        Codec.LONG.fieldOf("seed").forGetter(task -> task.seed),
        Codec.LONG.fieldOf("created").forGetter(task -> task.created),
        Codec.INT.fieldOf("level").forGetter(task -> task.level),
        Collect.CODEC.fieldOf("collect").forGetter(task -> task.collect),
        Hunt.CODEC.fieldOf("hunt").forGetter(task -> task.hunt),
        Treasure.CODEC.fieldOf("treasure").forGetter(task -> task.treasure),
        Rewards.CODEC.fieldOf("rewards").forGetter(task -> task.rewards)
    ).apply(instance, Task::new));

    public static final Task EMPTY = new Task(
        UUID.randomUUID(), UUID.randomUUID(), ResourceLocation.parse("minecraft:empty"), TaskStatus.Unspecified, TaskModifier.Unspecified, "", 0L, 0L, 0,
        Collect.EMPTY, Hunt.EMPTY, Treasure.EMPTY, Rewards.EMPTY
    );

    private Task(UUID id, UUID villager, ResourceLocation definitionId, TaskStatus status, TaskModifier modifier, String titleKey, long seed, long created, int level,
                 Collect collect, Hunt hunt, Treasure treasure, Rewards reward
    ) {
        this.id = id;
        this.status = status;
        this.definitionId = definitionId;
        this.modifier = modifier;
        this.villager = villager;
        this.titleKey = titleKey;
        this.seed = seed;
        this.created = created;
        this.level = level;

        this.collect = collect;
        this.hunt = hunt;
        this.treasure = treasure;
        this.rewards = reward;
    }

    public Task copy() {
        return copyWithTime(created);
    }

    public Task copyWithTime(long created) {
        return new Task(
            id, villager, definitionId, status, modifier, titleKey, seed, created, level,
            collect.copy(), hunt.copy(), treasure.copy(), rewards.copy()
        );
    }

    public static Task create(ServerPlayer player, Definition definition, UUID uuid, TaskModifier modifier, long seed) {
        Task task;

        var random = RandomSource.create(seed);

        try {
            var id = UuidHelper.fromString(definition.id.toString() + seed);
            var created = player.level().getGameTime();
            var level = definition.level;
            var titleKey = definition.title;

            // Initialise the aspect builder that will be passed to each aspect during task creation.
            var builder = new AspectBuilder(player, definition, modifier, random);

            // Create the task with its aspects.
            task = new Task(id, uuid, definition.id, TaskStatus.NotStarted, modifier, titleKey, seed, created, level,
                Collect.make(builder),
                Hunt.make(builder),
                Treasure.make(builder),
                Rewards.make(builder)
            );
        } catch (Exception e) {
            VillagerTasks.feature().log().error("Failed to create task for definition: " + definition.id, e);
            task = EMPTY;
        }

        return task;
    }

    // Define all aspects here or they won't be ticked.
    public List<? extends Aspect> aspects() {
        return List.of(collect, hunt, treasure, rewards);
    }

    // Define the aspects that are also requirements for completing the task or they won't be calculated when checking completion.
    public List<? extends Satisfiable> requirements() {
        return List.of(collect, hunt, treasure);
    }

    public Rewards rewards() {
        return rewards;
    }

    public boolean belongsTo(UUID villager) {
        return this.villager.equals(villager);
    }

    @Override
    public boolean isSatisfied() {
        return isStarted() && remaining() == 0;
    }

    @Override
    public int remaining() {
        var remaining = 0;
        remaining += requirements().stream().map(Satisfiable::remaining).reduce(0, Integer::sum);
        return remaining;
    }

    @Override
    public int total() {
        return requirements().stream().map(Satisfiable::total).reduce(0, Integer::sum);
    }

    @Override
    public void onStart(Task task, ServerPlayer player) {
        setStatus(TaskStatus.Starting);
        aspects().forEach(b -> b.onStart(this, player));
        onStarted(this, player);
    }

    @Override
    public void onStarted(Task task, ServerPlayer player) {
        setStatus(TaskStatus.InProgress);
        aspects().forEach(b -> b.onStarted(this, player));
    }

    @Override
    public void onTick(Task task, Player player) {
        var aspects = aspects();

        for (var aspect : aspects) {
            aspect.onTick(task, player);
        }
    }

    @Override
    public void onAbandon(Task task, ServerPlayer player) {
        aspects().forEach(a -> a.onAbandon(this, player));
    }

    @Override
    public void onComplete(Task task, ServerPlayer player) {
        aspects().forEach(a -> a.onComplete(this, player));
    }

    @Override
    public boolean onEntityKilled(Task task, LivingEntity livingEntity, DamageSource damageSource) {
        for (var aspect : aspects()) {
            if (aspect.onEntityKilled(this, livingEntity, damageSource)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemPickup(Task task, Player player, ItemStack itemStack) {
        aspects().forEach(a -> a.onItemPickup(this, player, itemStack));
    }

    @Override
    public void onLootTableModify(Task task, ResourceKey<LootTable> key, LootTable.Builder builder, LootTableSource source, HolderLookup.Provider provider) {
        aspects().forEach(a -> a.onLootTableModify(this, key, builder, source, provider));
    }

    public boolean isNotStarted() {
        return status.equals(TaskStatus.NotStarted);
    }

    public boolean isStarting() {
        return status.equals(TaskStatus.Starting);
    }

    public boolean isStarted() {
        return status.equals(TaskStatus.InProgress);
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public ResourceLocation getDefinitionId() {
        return definitionId;
    }

    public Component getTitle() {
        if (titleKey.contains(".villager_tasks.")) {
            // If it's a translatable key (containing the keyword villager_tasks) then translate it.
            return Component.translatable(titleKey);
        } else if (!titleKey.isEmpty()) {
            // Just output it literally.
            return Component.literal(titleKey);
        } else {
            return Resources.MISSINGNO;
        }
    }

    public boolean isEpic() {
        return modifier.isEpic();
    }

    public record AspectBuilder(ServerPlayer player, Definition definition, TaskModifier modifier, RandomSource random) {
        public RegistryAccess registryAccess() {
            return player.level().registryAccess();
        }
    }
}
