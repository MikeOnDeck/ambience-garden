package svenhjol.charmony.core.common.features.wood.types;

import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;
import svenhjol.charmony.core.common.features.wood.blocks.CustomWoodenButtonBlock;

import java.util.function.Supplier;

public class Button extends CustomWoodType {
    public final Supplier<CustomWoodenButtonBlock> block;
    public final Supplier<CustomWoodenButtonBlock.WoodenButtonBlockItem> item;

    public Button(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var id = material.getSerializedName() + "_button";

        block = commonRegistry.block(id, key -> new CustomWoodenButtonBlock(key, material));
        item = commonRegistry.item(id, key -> new CustomWoodenButtonBlock.WoodenButtonBlockItem(key, block));

        woodRegistry.addItemToCreativeTab(item, material, CustomWood.BUTTON);
    }
}
