package svenhjol.charmony.tweaks.client.features.chiseled_bookshelves_show_book_on_hover;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import svenhjol.charmony.core.base.Setup;

import java.util.Optional;
import java.util.OptionalInt;

public  class Handlers extends Setup<ChiseledBookshelvesShowBookOnHover> {
    public Handlers(ChiseledBookshelvesShowBookOnHover feature) {
        super(feature);
    }

    public BlockHitResult lookingAtBlock(Player player) {
        var cameraPosVec = player.getEyePosition(1.0f);
        var rotationVec = player.getViewVector(1.0f);
        var vec3d = cameraPosVec.add(rotationVec.x * 6, rotationVec.y * 6, rotationVec.z * 6);
        var raycast = player.level().clip(new ClipContext(cameraPosVec, vec3d, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        var pos = raycast.getBlockPos();
        return new BlockHitResult(raycast.getLocation(), raycast.getDirection(), pos, true);
    }

    public ItemStack getItem(ChiseledBookShelfBlockEntity bookshelf, int slot) {
        if (bookshelf == null) {
            return ItemStack.EMPTY;
        }
        return bookshelf.getItem(slot);
    }

    public void hudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        feature().registers.hudRenderer.render(guiGraphics, deltaTracker);
    }

    // API has been removed in snapshots for 1.21.6
    //    public void hudRender(LayeredDrawerWrapper drawers) {
    //        drawers.attachLayerAfter(
    //            IdentifiedLayer.MISC_OVERLAYS,
    //            Charmony.id("chiseled_bookshelf"),
    //            ((guiGraphics, deltaTracker)
    //                -> feature().registers.hudRenderer.render(guiGraphics, deltaTracker)));
    //    }

    public void clientTick(Minecraft minecraft) {
        var player = minecraft.player;
        if (player == null) return;
        feature().registers.hudRenderer.tick(player);
    }

    /**
     * Copypasta from ChiseledBookShelfBlock so we can get the slot positions.
     */
    public OptionalInt getHitSlot(BlockHitResult blockHitResult, BlockState blockState) {
        return getRelativeHitCoordinatesForBlockFace(blockHitResult, blockState.getValue(HorizontalDirectionalBlock.FACING)).map(vec2 -> {
            int i = vec2.y >= 0.5f ? 0 : 1;
            int j = getSection(vec2.x);
            return OptionalInt.of(j + i * 3);
        }).orElseGet(OptionalInt::empty);
    }

    /**
     * Copypasta from ChiseledBookShelfBlock so we can get the slot positions.
     */
    private Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult blockHitResult, Direction direction) {
        Direction direction2 = blockHitResult.getDirection();
        if (direction != direction2) {
            return Optional.empty();
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos().relative(direction2);
            Vec3 vec3 = blockHitResult.getLocation().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            double d = vec3.x();
            double e = vec3.y();
            double f = vec3.z();

            return switch (direction2) {
                case NORTH -> Optional.of(new Vec2((float)(1.0 - d), (float)e));
                case SOUTH -> Optional.of(new Vec2((float)d, (float)e));
                case WEST -> Optional.of(new Vec2((float)f, (float)e));
                case EAST -> Optional.of(new Vec2((float)(1.0 - f), (float)e));
                case DOWN, UP -> Optional.empty();
            };
        }
    }

    /**
     * Copypasta from ChiseledBookShelfBlock so we can get the slot positions.
     */
    private int getSection(float f) {
        if (f < 0.375f) {
            return 0;
        } else {
            return f < 0.6875f ? 1 : 2;
        }
    }
}
