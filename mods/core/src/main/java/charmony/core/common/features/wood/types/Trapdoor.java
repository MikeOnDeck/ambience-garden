package charmony.core.common.features.wood.types;

import charmony.core.common.features.wood.CustomWood;
import charmony.core.common.features.wood.CustomWoodType;
import charmony.core.common.features.wood.WoodMaterial;
import charmony.core.common.features.wood.WoodRegistry;
import charmony.core.common.features.wood.blocks.CustomWoodenTrapdoorBlock;

import java.util.function.Supplier;

public class Trapdoor extends CustomWoodType {
    public final Supplier<CustomWoodenTrapdoorBlock> block;
    public final Supplier<CustomWoodenTrapdoorBlock.WoodenTrapdoorBlockItem> item;

    public Trapdoor(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var id = material.getSerializedName() + "_trapdoor";

        block = commonRegistry.block(id, key -> new CustomWoodenTrapdoorBlock(key, material));
        item = commonRegistry.item(id, key -> new CustomWoodenTrapdoorBlock.WoodenTrapdoorBlockItem(key, block));

        woodRegistry.addItemToCreativeTab(item, material, CustomWood.TRAPDOOR);
    }
}
