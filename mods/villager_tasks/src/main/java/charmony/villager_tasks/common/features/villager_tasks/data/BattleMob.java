package charmony.villager_tasks.common.features.villager_tasks.data;

import charmony.core.base.Log;
import charmony.core.helpers.MobHelper;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BattleMob implements Satisfiable {
    public static final String BATTLE_TAG = "charmony_battle";

    private final BattleMobData data;
    private final UUID uniqueId;
    private final Identifier dimension;
    private final int total;
    private int defeated;
    private boolean spawned;
    private Optional<BlockPos> pos;

    public static final Codec<BattleMob> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BattleMobData.CODEC.fieldOf("data").forGetter(self -> self.data),
        UUIDUtil.CODEC.fieldOf("unique_id").forGetter(self -> self.uniqueId),
        Identifier.CODEC.fieldOf("dimension").forGetter(self -> self.dimension),
        BlockPos.CODEC.lenientOptionalFieldOf("pos").forGetter(self -> self.pos),
        Codec.INT.fieldOf("total").forGetter(self -> self.total),
        Codec.INT.fieldOf("defeated").forGetter(self -> self.defeated),
        Codec.BOOL.fieldOf("spawned").forGetter(self -> self.spawned)
    ).apply(instance, BattleMob::new));

    public BattleMob(BattleMobData data, UUID uniqueId, Identifier dimension, Optional<BlockPos> pos, int total, int defeated, boolean spawned) {
        this.dimension = dimension;
        this.pos = pos;
        this.data = data;
        this.uniqueId = uniqueId;
        this.total = total;
        this.defeated = defeated;
        this.spawned = spawned;
    }

    public BattleMob copy() {
        return new BattleMob(data.copy(), uniqueId, dimension, pos, total, defeated, spawned);
    }

    @Override
    public boolean isSatisfied() {
        return remaining() == 0;
    }

    @Override
    public int remaining() {
        return Math.max(0, total() - getDefeated());
    }

    @Override
    public int total() {
        return total;
    }

    public int getDefeated() {
        return defeated;
    }

    public void addDefeated() {
        this.defeated++;
    }

    public Identifier mob() {
        return data.mob();
    }

    public ResourceKey<EntityType<?>> mobKey() {
        return ResourceKey.create(Registries.ENTITY_TYPE, mob());
    }

    public Optional<GlobalPos> globalPos() {
        return pos.map(pos -> GlobalPos.of(ResourceKey.create(Registries.DIMENSION, dimension), pos));
    }

    public BattleMobData data() {
        return data;
    }

    public List<Effect> effects() {
        return data.effects();
    }

    @SuppressWarnings("unchecked")
    public void onTick(Task task, RegistryAccess registryAccess, ServerPlayer player) {
        var level = player.level();

        var pos = this.pos.orElse(null);
        if (pos == null) return;

        var playerDim = player.level().dimension().identifier();
        var playerPos = player.blockPosition();
        var xzPos = new BlockPos(playerPos.getX(), 0, playerPos.getZ());

        var dist = pos.distManhattan(xzPos);
        var playerInRange = playerDim.equals(this.dimension) && dist <= 32;

        if (playerInRange && !spawned) {
            var entityRegistry = registryAccess.lookup(Registries.ENTITY_TYPE).orElseThrow();
            spawned = true;

            // Spawn all mobs in a radius around the player.
            var successfullySpawned = 0;

            for (int i = 0; i < total(); i++) {
                var entityType = entityRegistry.getOptional(mobKey()).orElse(null);
                if (entityType == null) continue;

                var spawnReason = EntitySpawnReason.TRIGGERED;
                var spawnPos = findRandomSpawnPos(entityType, level, pos);

                try {
                    if (spawnPos.isPresent()) {
                        var health = data().health();
                        var result = MobHelper.spawn((EntityType<? extends Mob>) entityType, level, spawnPos.get(), spawnReason, (mob) -> {
                            mob.addTag(BATTLE_TAG + "_" + uniqueId.toString());
                            mob.setTarget(player);
                            mob.setPersistenceRequired();
                            mob.setAggressive(true);
                            mob.setHealth(health);

                            for (var effect : data().effects()) {
                                mob.addEffect(effect.mobEffectInstance(registryAccess));
                            }
                        });
                        if (result) {
                            log().debug("Spawned mob " + mob() + " at " + spawnPos.get());
                            successfullySpawned++;
                        }
                    }
                } catch (Exception e) {
                    log().warn("Error spawning mob: " + e.getMessage());
                }
            }

            if (successfullySpawned == 0) {
                log().warn("No mobs spawned, abandoning task");
                VillagerTasks.feature().handlers.abandonTask(player, task);
            }
        }
    }

    public void onStart(Task task, ServerPlayer player) {
        if (this.pos.isPresent()) return;
        if (!task.isStarting()) return; // Just in case.

        var level = player.level();
        var playerPos = player.blockPosition();
        var pos = task.battle.spawn().getSpawnPosition(level, playerPos, task.random());

        this.pos = Optional.of(pos);
    }

    public boolean onEntityKilled(RegistryAccess registryAccess, LivingEntity entity) {
        var entityRegistry = registryAccess.lookup(Registries.ENTITY_TYPE).orElseThrow();

        var isValidMob = entityRegistry.getOptional(mobKey())
            .map(type -> type.equals(entity.getType()))
            .orElse(false);

        var tags = entity.getTags();
        var hasBattleTag = tags.contains(BATTLE_TAG + "_" + uniqueId.toString());

        if (isValidMob && hasBattleTag && !isSatisfied()) {
            addDefeated();
            return true;
        }

        return false;
    }

    private Optional<BlockPos> findRandomSpawnPos(EntityType<?> entity, ServerLevel level, BlockPos pos) {
        var random = RandomSource.create();
        for (int i = 0; i < 20; i++) {
            var x = pos.getX() + random.nextInt(24) - 8;
            var z = pos.getZ() + random.nextInt(24) - 8;
            var y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            var p = new BlockPos(x, y, z).below();
            var s = level.getBlockState(p);
            if (s.isValidSpawn(level, p, entity)) {
                return Optional.of(p.above());
            }
        }
        return Optional.empty();
    }

    private Log log() {
        return VillagerTasks.feature().log();
    }
}
