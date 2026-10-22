package com.mikeondeck.ambiencegarden.core.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Shared upward-sky scan used by {@link WorldHelper#isOutside} and {@link OutdoorDiagnostics}.
 */
public record OutdoorExposure(
    boolean consideredOutside,
    BlockPos feetPosition,
    String feetBlockId,
    String roofDecision,
    boolean underWater,
    boolean canSeeSkyAtFeet,
    boolean canSeeSkyFromBelowWaterAtFeet,
    boolean canSeeSkyAboveScan,
    int scanBlocks
) {
    private static final int SCAN_BLOCKS = 24;
    private static final int SCAN_START = 1;
    private static final VoxelShape UPWARD_COLUMN = Shapes.box(0.4375, 0.0, 0.4375, 0.5625, 1.0, 0.5625);

    public static OutdoorExposure analyze(Player player) {
        var level = player.level();
        var feet = player.blockPosition();
        var feetState = level.getBlockState(feet);
        String feetId = blockId(level, feetState.getBlock());

        boolean underWater = player.isUnderWater();
        boolean canSeeSkyFeet = level.canSeeSky(feet);
        boolean canSeeSkyWater = level.canSeeSkyFromBelowWater(feet);
        boolean canSeeSkyAbove = level.canSeeSky(feet.above(SCAN_BLOCKS));

        if (underWater) {
            return snapshot(
                false,
                feet,
                feetId,
                "under water",
                underWater,
                canSeeSkyFeet,
                canSeeSkyWater,
                canSeeSkyAbove
            );
        }

        if (canSeeSkyFeet) {
            return snapshot(
                true,
                feet,
                feetId,
                "open sky",
                underWater,
                canSeeSkyFeet,
                canSeeSkyWater,
                canSeeSkyAbove
            );
        }

        if (canSeeSkyWater) {
            return snapshot(
                true,
                feet,
                feetId,
                "open sky",
                underWater,
                canSeeSkyFeet,
                canSeeSkyWater,
                canSeeSkyAbove
            );
        }

        for (int i = SCAN_START; i < SCAN_START + SCAN_BLOCKS; i++) {
            var check = feet.above(i);
            var state = level.getBlockState(check);

            if (level.isEmptyBlock(check)) {
                continue;
            }

            if (isRoofExempt(state)) {
                continue;
            }

            if (blocksUpwardView(level, check, state)) {
                return snapshot(
                    false,
                    feet,
                    feetId,
                    blockId(level, state.getBlock()) + " at y+" + i,
                    underWater,
                    canSeeSkyFeet,
                    canSeeSkyWater,
                    canSeeSkyAbove
                );
            }

            if (level.canSeeSky(check) || level.canSeeSkyFromBelowWater(check)) {
                return snapshot(
                    true,
                    feet,
                    feetId,
                    "open sky",
                    underWater,
                    canSeeSkyFeet,
                    canSeeSkyWater,
                    canSeeSkyAbove
                );
            }
        }

        boolean outside = canSeeSkyAbove;
        return snapshot(
            outside,
            feet,
            feetId,
            outside ? "open sky" : "blocked above scan",
            underWater,
            canSeeSkyFeet,
            canSeeSkyWater,
            canSeeSkyAbove
        );
    }

    private static OutdoorExposure snapshot(
        boolean outside,
        BlockPos feet,
        String feetId,
        String roof,
        boolean underWater,
        boolean canSeeSkyFeet,
        boolean canSeeSkyWater,
        boolean canSeeSkyAbove
    ) {
        return new OutdoorExposure(
            outside,
            feet,
            feetId,
            roof,
            underWater,
            canSeeSkyFeet,
            canSeeSkyWater,
            canSeeSkyAbove,
            SCAN_BLOCKS
        );
    }

    static boolean isRoofExempt(BlockState state) {
        var block = state.getBlock();
        return state.is(Blocks.GLASS)
            || (block instanceof RotatedPillarBlock && state.is(BlockTags.LOGS))
            || block instanceof LeavesBlock
            || block instanceof HugeMushroomBlock
            || block instanceof StemBlock;
    }

    static boolean blocksUpwardView(Level level, BlockPos pos, BlockState state) {
        if (state.canOcclude()) {
            return true;
        }
        VoxelShape collision = state.getCollisionShape(level, pos);
        if (collision.isEmpty()) {
            return false;
        }
        return Shapes.joinIsNotEmpty(collision, UPWARD_COLUMN, BooleanOp.AND);
    }

    private static String blockId(Level level, Block block) {
        var key = level.registryAccess().lookupOrThrow(Registries.BLOCK).getKey(block);
        return key != null ? key.toString() : "unknown";
    }
}
