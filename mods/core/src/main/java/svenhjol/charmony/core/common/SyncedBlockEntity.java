package svenhjol.charmony.core.common;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class SyncedBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            TagValueOutput tagValueOutput = TagValueOutput.createWithContext(scopedCollector, provider);
            saveAdditional(tagValueOutput);
            return tagValueOutput.buildResult();
        }
    }

    public void setChanged() {
        super.setChanged();
        this.syncToClient();
    }

    private void syncToClient() {
        Level level = this.getLevel();
        if (level != null && !level.isClientSide()) {
            syncBlockEntityToClient((ServerLevel)level, this.getBlockPos());
        }
    }

    public static void syncBlockEntityToClient(ServerLevel level, BlockPos pos) {
        level.getChunkSource().blockChanged(pos);
    }
}
