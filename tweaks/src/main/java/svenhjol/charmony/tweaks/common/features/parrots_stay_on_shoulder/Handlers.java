package svenhjol.charmony.tweaks.common.features.parrots_stay_on_shoulder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<ParrotsStayOnShoulder> {
    private static final long STAY_MOUNTED_TIME = 20L;

    public Handlers(ParrotsStayOnShoulder feature) {
        super(feature);
    }

    public boolean hasParrotOnAnyShoulder(Player player) {
        return hasParrotOnShoulder(player, Shoulder.Left) || hasParrotOnShoulder(player, Shoulder.Right);
    }

    public boolean hasParrotOnShoulder(Player player, Shoulder shoulder) {
        if (player.level().isClientSide()) {
            return false;
        }

        var serverPlayer = (ServerPlayer)player;
        return shoulder == Shoulder.Left && !serverPlayer.getShoulderEntityLeft().isEmpty()
            || shoulder == Shoulder.Right && !serverPlayer.getShoulderEntityRight().isEmpty();
    }

    public boolean shouldParrotStayMounted(Level level, long shoulderTime) {
        return shoulderTime + STAY_MOUNTED_TIME < level.getGameTime();
    }

    public void playerTick(Player player) {
        if (!player.level().isClientSide()
            && player.level().getGameTime() % 10 == 0
            && (player.isSecondaryUseActive() || player.isUnderWater())
        ) {
            var serverPlayer = (ServerPlayer)player;

            if (hasParrotOnShoulder(serverPlayer, Shoulder.Left)) {
                serverPlayer.respawnEntityOnShoulder(serverPlayer.getShoulderEntityLeft());
                serverPlayer.setShoulderEntityLeft(new CompoundTag());
            }

            if (hasParrotOnShoulder(serverPlayer, Shoulder.Right)) {
                serverPlayer.respawnEntityOnShoulder(serverPlayer.getShoulderEntityRight());
                serverPlayer.setShoulderEntityRight(new CompoundTag());
            }
        }
    }
}
