package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

public class CustomStandingSignBlock extends StandingSignBlock {
    protected final WoodMaterial material;

    public CustomStandingSignBlock(ResourceKey<Block> key, WoodMaterial material) {
        super(material.woodType(), material.blockProperties()
            .strength(1.0f)
            .noCollision()
            .setId(key));

        this.material = material;
    }
}
