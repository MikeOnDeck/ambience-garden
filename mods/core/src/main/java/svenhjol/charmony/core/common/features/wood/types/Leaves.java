package svenhjol.charmony.core.common.features.wood.types;

import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;
import svenhjol.charmony.core.common.features.wood.blocks.CustomLeavesBlock;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Leaves extends CustomWoodType {
    public final Supplier<CustomLeavesBlock> block;
    public final Supplier<CustomLeavesBlock.LeavesBlockItem> item;

    public Leaves(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var id = material.getSerializedName() + "_leaves";

        block = commonRegistry.block(id, key -> new CustomLeavesBlock(key, material.leafParticleChance(), material));
        item = commonRegistry.item(id, key -> new CustomLeavesBlock.LeavesBlockItem(key, block));

        commonRegistry.ignite(block); // Leaves can set on fire.
        woodRegistry.addItemToCreativeTab(item, material, CustomWood.LEAVES);
    }
}
