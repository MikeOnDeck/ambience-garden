package charmony.villager_tasks.common.features.villager_tasks.data;

import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.aspects.Battle;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BattleMob implements Satisfiable {

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

    public UUID uniqueId() {
        return uniqueId;
    }

    public List<Effect> effects() {
        return data.effects();
    }

    public void onTick(Task task, ServerPlayer player) {
        var pos = this.pos.orElse(null);
        if (pos == null) return;

        var playerDim = player.level().dimension().identifier();
        var playerPos = player.blockPosition();
        var xzPos = new BlockPos(playerPos.getX(), 0, playerPos.getZ());

        var dist = pos.distManhattan(xzPos);
        var playerInRange = playerDim.equals(this.dimension) && dist <= Battle.TRIGGER_DISTANCE;

        if (playerInRange && !spawned) {
            spawned = true;
            task.battle.spawnMobs(this, task, player, pos);
        }
    }

    public void onStart(Task task, ServerPlayer player) {
        if (this.pos.isPresent()) return;
        if (!task.isStarting()) return; // Just in case.

        var level = player.level();
        var playerPos = player.blockPosition();
        var rand = RandomSource.create(level.getGameTime());
        var pos = task.battle.spawn().getSpawnPosition(level, playerPos, rand);

        this.pos = Optional.of(pos);
    }

    public boolean onEntityKilled(RegistryAccess registryAccess, LivingEntity entity) {
        var entityRegistry = registryAccess.lookup(Registries.ENTITY_TYPE).orElseThrow();

        var isValidMob = entityRegistry.getOptional(mobKey())
            .map(type -> type.equals(entity.getType()))
            .orElse(false);

        var tags = entity.getTags();
        var hasBattleTag = tags.contains(Battle.BATTLE_TAG + "_" + uniqueId.toString());

        if (isValidMob && hasBattleTag && !isSatisfied()) {
            addDefeated();
            return true;
        }

        return false;
    }
}
