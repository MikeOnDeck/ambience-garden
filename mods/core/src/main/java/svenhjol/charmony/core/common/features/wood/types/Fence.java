package svenhjol.charmony.core.common.features.wood.types;

import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;
import svenhjol.charmony.core.common.features.wood.blocks.CustomFenceBlock;

import java.util.function.Supplier;

public class Fence extends CustomWoodType {
    public final Supplier<CustomFenceBlock> block;
    public final Supplier<CustomFenceBlock.FenceBlockItem> item;

    public Fence(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var id = material.getSerializedName() + "_fence";

        block = commonRegistry.block(id, key -> new CustomFenceBlock(key, material));
        item = commonRegistry.item(id, key -> new CustomFenceBlock.FenceBlockItem(key, block));

        commonRegistry.ignite(block); // Fences can set on fire.
        woodRegistry.addItemToCreativeTab(item, material, CustomWood.FENCE);
    }
}
