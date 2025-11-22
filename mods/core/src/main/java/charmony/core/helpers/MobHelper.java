package charmony.core.helpers;

import charmony.core.client.features.core.Core;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.BiConsumer;

public final class MobHelper {
    @SuppressWarnings("UnusedReturnValue")
    public static <T extends Mob> boolean spawn(EntityType<T> type, ServerLevel level, BlockPos pos, int tries, int range, int y, EntitySpawnReason reason, BiConsumer<T, BlockPos> beforeAddToLevel) {
        T mob = type.create(level, reason);

        if (mob != null) {
            if (mob instanceof Warden) {
                var m = SpawnUtil.trySpawnMob(type, reason, level, pos, tries, range, y, net.minecraft.util.SpawnUtil.Strategy.ON_TOP_OF_COLLIDER, false);
                if (m.isPresent()) {
                    var mm = m.get();
                    beforeAddToLevel.accept(mm, mm.blockPosition());
                    return true;
                } else {
                    Core.feature().log().debug("Failed to spawn Warden mob at position: " + pos);
                }
            } else {
                var p = findRandomSpawnPos(type, level, pos, tries, range);
                if (p.isPresent()) {
                    var pp = p.get();
                    beforeAddToLevel.accept(mob, pp);
                    mob.snapTo(new Vec3(pp.getX() + 0.5d, pp.getY() + 1.0d, pp.getZ() + 0.5d));
                    return level.addFreshEntity(mob);
                } else {
                    Core.feature().log().debug("Failed to find valid spawn position for mob at position: " + pos);
                }
            }
        }

        return false;
    }


    public static Optional<BlockPos> findRandomSpawnPos(EntityType<?> entity, ServerLevel level, BlockPos pos, int tries, int range) {
        var random = RandomSource.create();

        for (int i = 0; i < tries; i++) {
            var x = pos.getX() + random.nextInt(range) - random.nextInt(range);
            var z = pos.getZ() + random.nextInt(range) - random.nextInt(range);
            var y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            var atPos = new BlockPos(x, y, z);
            var belowPos = atPos.below();
            var belowState = level.getBlockState(belowPos);

            if (belowState.isValidSpawn(level, belowPos, entity)) {
                return Optional.of(atPos);
            }
        }

        for (int i = 0; i < tries; i++) {
            var x = pos.getX() + random.nextInt(range) - random.nextInt(range);
            var z = pos.getZ() + random.nextInt(range) - random.nextInt(range);
            var y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

            var atPos = new BlockPos(x, y, z);
            var atState = level.getBlockState(atPos);

            var belowPos = atPos.below();
            var belowState = level.getBlockState(belowPos);

            if (atState.isAir() && belowState.getFluidState().is(Fluids.WATER)) {
                return Optional.of(belowPos.above());
            }
        }

        return Optional.empty();
    }
}
