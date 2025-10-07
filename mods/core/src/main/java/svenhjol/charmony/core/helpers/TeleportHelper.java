package svenhjol.charmony.core.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Log;

import javax.annotation.Nullable;

public final class TeleportHelper {
    private static final Log LOGGER = new Log(Charmony.ID, "TeleportHelper");

    /**
     * @see ServerPlayer#teleport
     */
    public static void changeDimension(ServerPlayer serverPlayer, ServerLevel newDimension, Vec3 pos) {
        serverPlayer.isChangingDimension = true;
        var connection = serverPlayer.connection;
        var currentDimension = serverPlayer.level();
        var levelData = newDimension.getLevelData();

        connection.send(new ClientboundRespawnPacket(serverPlayer.createCommonSpawnInfo(newDimension), (byte)3));
        connection.send(new ClientboundChangeDifficultyPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));

        var playerList = currentDimension.getServer().getPlayerList();
        playerList.sendPlayerPermissionLevel(serverPlayer);
        currentDimension.removePlayerImmediately(serverPlayer, Entity.RemovalReason.CHANGED_DIMENSION);
        serverPlayer.unsetRemoved();

        var yRot = serverPlayer.getYRot();
        var xRot = serverPlayer.getXRot();

        serverPlayer.setServerLevel(newDimension);
        connection.teleport(pos.x(), pos.y(), pos.z(), yRot, xRot);
        connection.resetPosition();

        newDimension.addDuringTeleport(serverPlayer);

        connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));
        connection.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.getAbilities()));

        playerList.sendLevelInfo(serverPlayer, newDimension);
        playerList.sendAllPlayerInfo(serverPlayer);
        playerList.sendActivePlayerEffects(serverPlayer);

        serverPlayer.lastSentExp = -1;
        serverPlayer.lastSentHealth = -1.0f;
        serverPlayer.lastSentFood = -1;
    }

    @Nullable
    public static BlockPos getSurfacePos(Level level, BlockPos pos, int startAtHeight) {
        int surface = 0;

        for (int y = startAtHeight; y >= 0; --y) {
            BlockPos n = new BlockPos(pos.getX(), y, pos.getZ());
            if (level.isEmptyBlock(n)
                && !level.isEmptyBlock(n.below())
                && !level.getBlockState(n.below()).is(Blocks.LAVA)) {
                surface = y;
                break;
            }
        }

        if (surface == 0) {
            LOGGER.warn("Failed to find a surface value to spawn the player");
            return null;
        }

        return new BlockPos(pos.getX(), surface, pos.getZ());
    }
}
