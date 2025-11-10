package charmony.core.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public final class MobHelper {
    @SuppressWarnings("UnusedReturnValue")
    public static <T extends Mob> boolean spawn(EntityType<T> type, ServerLevel level, BlockPos pos, EntitySpawnReason reason, Consumer<T> beforeAddToLevel) {
        boolean result = false;

        T mob = type.create(level, reason);

        if (mob != null) {
            beforeAddToLevel.accept(mob);
            mob.snapTo(new Vec3(pos.getX() + 0.5d, pos.getY() + 1.0d, pos.getZ() + 0.5d));
            result = level.addFreshEntity(mob);
        }

        return result;
    }
}
