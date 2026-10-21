package com.mikeondeck.ambiencegarden.core.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshot of inputs used for surface / outdoor ambient sound eligibility.
 */
public record OutdoorDiagnostics(
    boolean consideredOutside,
    boolean underWater,
    boolean canSeeSkyAtFeet,
    boolean canSeeSkyFromBelowWaterAtFeet,
    boolean canSeeSkyAboveScan,
    int skyLight,
    int blockLight,
    int blockY,
    int seaLevel,
    boolean belowSeaLevel,
    float distanceFromGround,
    int cullHeightSetting,
    int scanBlocks,
    String blockingSummary
) {
    public static OutdoorDiagnostics analyze(Player player, int cullHeightSetting) {
        var level = player.level();
        var pos = player.blockPosition();
        int sky = level.getBrightness(LightLayer.SKY, pos);
        int block = level.getBrightness(LightLayer.BLOCK, pos);
        boolean underWater = player.isUnderWater();
        boolean canSeeSkyFeet = level.canSeeSky(pos);
        boolean canSeeSkyWater = level.canSeeSkyFromBelowWater(pos);

        int blocks = 24;
        int start = 1;
        String blocking = "none";
        boolean canSeeSkyAboveScan = level.canSeeSky(pos.above(blocks));

        if (!underWater && !canSeeSkyFeet && !canSeeSkyWater) {
            for (int i = start; i < start + blocks; i++) {
                var check = new BlockPos(pos.getX(), pos.getY() + i, pos.getZ());
                var state = level.getBlockState(check);
                var blockType = state.getBlock();

                if (level.isEmptyBlock(check)) {
                    continue;
                }

                if (state.is(Blocks.GLASS)
                    || (blockType instanceof RotatedPillarBlock && state.is(BlockTags.LOGS))
                    || blockType instanceof LeavesBlock
                    || blockType instanceof HugeMushroomBlock
                    || blockType instanceof StemBlock
                ) {
                    continue;
                }

                if (level.canSeeSky(check) || level.canSeeSkyFromBelowWater(check)) {
                    blocking = "open at y+" + i;
                    break;
                }

                if (state.canOcclude()) {
                    blocking = state.getBlock().getName().getString() + " at y+" + i;
                    break;
                }
            }
        }

        boolean outside = WorldHelper.isOutside(player);
        float distance = cullHeightSetting > 0 ? WorldHelper.distanceFromGround(player, cullHeightSetting) : 0f;

        return new OutdoorDiagnostics(
            outside,
            underWater,
            canSeeSkyFeet,
            canSeeSkyWater,
            canSeeSkyAboveScan,
            sky,
            block,
            pos.getY(),
            level.getSeaLevel(),
            WorldHelper.isBelowSeaLevel(player),
            distance,
            cullHeightSetting,
            blocks,
            blocking
        );
    }

    public List<String> toOverlayLines() {
        var lines = new ArrayList<String>();
        lines.add("outdoor=" + consideredOutside + " (isOutside)");
        lines.add("underWater=" + underWater);
        lines.add("skyLight=" + skyLight + " blockLight=" + blockLight);
        lines.add("canSeeSky feet=" + canSeeSkyAtFeet + " belowWater=" + canSeeSkyFromBelowWaterAtFeet);
        lines.add("canSeeSky y+" + scanBlocks + "=" + canSeeSkyAboveScan + " roof=" + blockingSummary);
        lines.add("y=" + blockY + " sea=" + seaLevel + " belowSea=" + belowSeaLevel);
        if (cullHeightSetting > 0) {
            lines.add("distAboveGround=" + String.format("%.1f", distanceFromGround) + " / cull=" + cullHeightSetting);
        } else {
            lines.add("cullHeight=0 (disabled)");
        }
        return lines;
    }
}
