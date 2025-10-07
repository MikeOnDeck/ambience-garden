package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

public class CustomWallSignBlock extends WallSignBlock {
    public CustomWallSignBlock(ResourceKey<Block> key, WoodMaterial material, Block standingSign) {
        super(material.woodType(), material.blockProperties()
            .sound(material.woodType().soundType())
            .forceSolidOn()
            .ignitedByLava()
            .instrument(NoteBlockInstrument.BASS)
            .noCollision()
            .overrideLootTable(standingSign.getLootTable())
            .overrideDescription(standingSign.getDescriptionId())
            .strength(1.0f)
            .setId(key));
    }
}
