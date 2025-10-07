package svenhjol.charmony.core.common.features.wood.blocks;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.SoundType;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;

public class CustomCeilingHangingSignBlock extends CeilingHangingSignBlock {
    protected final WoodMaterial material;
    protected final SidedFeature feature;

    public CustomCeilingHangingSignBlock(ResourceKey<Block> key, SidedFeature feature, WoodMaterial material) {
        super(material.woodType(), material.blockProperties()
            .noCollision()
            .sound(SoundType.HANGING_SIGN)
            .strength(1.0f)
            .setId(key));

        this.feature = feature;
        this.material = material;
    }

    public WoodMaterial material() {
        return material;
    }

    public SidedFeature feature() {
        return feature;
    }
}
