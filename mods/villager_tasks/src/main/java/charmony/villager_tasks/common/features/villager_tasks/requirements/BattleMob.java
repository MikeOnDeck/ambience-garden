package charmony.villager_tasks.common.features.villager_tasks.requirements;

import charmony.core.base.Log;
import charmony.core.helpers.MobHelper;
import charmony.core.helpers.WorldHelper;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BattleMob implements Satisfiable {
    public static final String BATTLE_TAG = "charmony_battle";

    private final ResourceLocation mob;
    private Optional<BlockPos> pos;
    private Optional<ItemStack> map;
    private final List<BattleMobEffect> effects;
    private final UUID uniqueId;
    private final int distance;
    private final int total;
    private int defeated;
    private boolean spawned;

    public static final Codec<BattleMob> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("mob").forGetter(self -> self.mob),
        BlockPos.CODEC.lenientOptionalFieldOf("pos").forGetter(self -> self.pos),
        ItemStack.CODEC.lenientOptionalFieldOf("map").forGetter(self -> self.map),
        BattleMobEffect.CODEC.listOf().fieldOf("effects").forGetter(self -> self.effects),
        UUIDUtil.CODEC.fieldOf("unique_id").forGetter(self -> self.uniqueId),
        Codec.INT.fieldOf("distance").forGetter(self -> self.distance),
        Codec.INT.fieldOf("total").forGetter(self -> self.total),
        Codec.INT.fieldOf("defeated").forGetter(self -> self.defeated),
        Codec.BOOL.fieldOf("spawned").forGetter(self -> self.spawned)
    ).apply(instance, BattleMob::new));

    public BattleMob(ResourceLocation mob, Optional<BlockPos> pos, Optional<ItemStack> map, List<BattleMobEffect> effects, UUID uniqueId, int distance, int total, int defeated, boolean spawned) {
        this.mob = mob;
        this.pos = pos;
        this.map = map;
        this.effects = effects;
        this.uniqueId = uniqueId;
        this.distance = distance;
        this.total = total;
        this.defeated = defeated;
        this.spawned = spawned;
    }

    public BattleMob copy() {
        return new BattleMob(mob, pos, map.map(ItemStack::copy), new ArrayList<>(effects), uniqueId, distance, total, defeated, spawned);
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

    public ResourceLocation mob() {
        return mob;
    }

    public ResourceKey<EntityType<?>> mobKey() {
        return ResourceKey.create(Registries.ENTITY_TYPE, mob());
    }

    public Optional<ItemStack> map() {
        return map;
    }

    @SuppressWarnings("unchecked")
    public void onTick(Task task, RegistryAccess registryAccess, ServerPlayer player) {
        var level = player.level();

        if (map.isPresent()) {
            var xmap = map.get();

            var savedData = MapItem.getSavedData(xmap, level);
            var mapId = xmap.get(DataComponents.MAP_ID);

            if (savedData != null && mapId != null) {
                var holding = savedData.getHoldingPlayer(player);
//                if (!savedData.carriedBy.contains(holding)) {
//                    savedData.carriedBy.add(holding);
//                }
                savedData.carriedByPlayers.put(player, holding);
                savedData.tickCarriedBy(player, xmap);
                savedData.addDecoration(MapDecorationTypes.PLAYER, player.level(), player.getPlainTextName(), player.getX(), player.getZ(), (double)player.getYRot(), (Component)null);
                savedData.setDirty();
                var packet = holding.nextUpdatePacket(mapId);
//                var holding = savedData.getHoldingPlayer(player);
//                var packet = holding.nextUpdatePacket(mapId);
                if (packet != null) {
                    player.connection.send(packet);
                }
            }
        }

        var entityRegistry = registryAccess.lookup(Registries.ENTITY_TYPE).orElseThrow();
        var effectRegistry = registryAccess.lookup(Registries.MOB_EFFECT).orElseThrow();

        var pos = this.pos.orElse(null);
        if (pos == null) return;

        var playerPos = player.blockPosition();
        var playerInRange = pos.distManhattan(playerPos) <= 32;

        if (playerInRange && !spawned) {
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
                        var result = MobHelper.spawn((EntityType<? extends Mob>) entityType, level, spawnPos.get(), spawnReason, (mob) -> {
                            mob.addTag(BATTLE_TAG + "_" + uniqueId.toString());
                            mob.setTarget(player);
                            mob.setPersistenceRequired();
                            mob.setAggressive(true);

                            for (var effect : effects) {
                                mob.addEffect(effect.mobEffectInstance(effectRegistry));
                            }
                        });
                        if (result) {
                            log().debug("Spawned mob " + mob + " at " + spawnPos.get());
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
        if (pos.isPresent()) return;
        if (!task.isStarting()) return; // Just in case.

        var level = player.level();
        var playerPos = player.blockPosition();
        var xpos = WorldHelper.addRandomOffset(level, playerPos, task.random(), distance / 2, distance);
        var pos = new BlockPos(xpos.getX(), level.getHeight(Heightmap.Types.WORLD_SURFACE, xpos.getX(), xpos.getZ()), xpos.getZ());

        var map = MapItem.create(level, pos.getX(), pos.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, pos, "+", MapDecorationTypes.RED_X);
        map.set(DataComponents.ITEM_NAME, Component.translatable(task.titleKey));

        this.pos = Optional.of(pos);
        this.map = Optional.of(map);
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
