package com.mikeondeck.ambiencegarden.core.helpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;

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
    String positionLine,
    String feetBlockLine,
    String roofLine
) {
    public static OutdoorDiagnostics analyze(Player player, int cullHeightSetting) {
        var level = player.level();
        var pos = player.blockPosition();
        int sky = level.getBrightness(LightLayer.SKY, pos);
        int block = level.getBrightness(LightLayer.BLOCK, pos);

        OutdoorExposure exposure = OutdoorExposure.analyze(player);
        float distance = cullHeightSetting > 0 ? WorldHelper.distanceFromGround(player, cullHeightSetting) : 0f;

        return new OutdoorDiagnostics(
            exposure.consideredOutside(),
            exposure.underWater(),
            exposure.canSeeSkyAtFeet(),
            exposure.canSeeSkyFromBelowWaterAtFeet(),
            exposure.canSeeSkyAboveScan(),
            sky,
            block,
            pos.getY(),
            level.getSeaLevel(),
            WorldHelper.isBelowSeaLevel(player),
            distance,
            cullHeightSetting,
            exposure.scanBlocks(),
            "pos=" + pos.getX() + " " + pos.getY() + " " + pos.getZ(),
            "feet=" + exposure.feetBlockId(),
            "roof=" + exposure.roofDecision()
        );
    }

    public List<String> toOverlayLines() {
        var lines = new ArrayList<String>();
        lines.add(positionLine);
        lines.add(feetBlockLine);
        lines.add("outdoor=" + consideredOutside + " (isOutside)");
        lines.add("underWater=" + underWater);
        lines.add("skyLight=" + skyLight + " blockLight=" + blockLight);
        lines.add("canSeeSky feet=" + canSeeSkyAtFeet + " skyThroughWater=" + canSeeSkyFromBelowWaterAtFeet);
        lines.add("canSeeSky y+" + scanBlocks + "=" + canSeeSkyAboveScan);
        lines.add(roofLine);
        lines.add("y=" + blockY + " sea=" + seaLevel + " belowSea=" + belowSeaLevel);
        if (cullHeightSetting > 0) {
            lines.add("distAboveGround=" + String.format("%.1f", distanceFromGround) + " / cull=" + cullHeightSetting);
        } else {
            lines.add("cullHeight=0 (disabled)");
        }
        return lines;
    }
}
