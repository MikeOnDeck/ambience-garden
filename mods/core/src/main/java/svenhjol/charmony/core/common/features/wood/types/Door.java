package svenhjol.charmony.core.common.features.wood.types;

import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;
import svenhjol.charmony.core.common.features.wood.blocks.CustomWoodenDoorBlock;

import java.util.function.Supplier;

public class Door extends CustomWoodType {
    public final Supplier<CustomWoodenDoorBlock> block;
    public final Supplier<CustomWoodenDoorBlock.WoodenDoorBlockItem> item;

    public Door(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var id = material.getSerializedName() + "_door";

        block = commonRegistry.block(id, key -> new CustomWoodenDoorBlock(key, material));
        item = commonRegistry.item(id, key -> new CustomWoodenDoorBlock.WoodenDoorBlockItem(key, block));

        woodRegistry.addItemToCreativeTab(item, material, CustomWood.DOOR);
    }
}
