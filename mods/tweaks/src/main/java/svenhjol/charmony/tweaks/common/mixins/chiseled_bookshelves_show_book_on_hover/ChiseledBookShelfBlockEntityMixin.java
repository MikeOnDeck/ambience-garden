package svenhjol.charmony.tweaks.common.mixins.chiseled_bookshelves_show_book_on_hover;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.client.features.chiseled_bookshelves_show_book_on_hover.ChiseledBookshelvesShowBookOnHover;

import javax.annotation.Nullable;

@Mixin(ChiseledBookShelfBlockEntity.class)
public class ChiseledBookShelfBlockEntityMixin extends BlockEntity {
    public ChiseledBookShelfBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        var logger = ChiseledBookshelvesShowBookOnHover.feature().log().getLogger();
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(this.problemPath(), logger)) {
            var valueOutput = TagValueOutput.createWithContext(scopedCollector, provider);
            saveAdditional(valueOutput);
            return valueOutput.buildResult();
        }
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(getBlockPos());
        }
    }
}
