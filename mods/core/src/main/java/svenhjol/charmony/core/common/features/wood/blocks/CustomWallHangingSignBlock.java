package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

public class CustomWallHangingSignBlock extends WallHangingSignBlock {
    protected final SidedFeature feature;

    public CustomWallHangingSignBlock(ResourceKey<Block> key, SidedFeature feature, WoodMaterial material, Block hangingSign) {
        super(material.woodType(), material.blockProperties()
            .sound(material.woodType().soundType())
            .forceSolidOn()
            .ignitedByLava()
            .instrument(NoteBlockInstrument.BASS)
            .noCollision()
            .overrideLootTable(hangingSign.getLootTable())
            .overrideDescription(hangingSign.getDescriptionId())
            .strength(1.0f)
            .setId(key));

        this.feature = feature;
    }

    public SidedFeature feature() {
        return feature;
    }
}
