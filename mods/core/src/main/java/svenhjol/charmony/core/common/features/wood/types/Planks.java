package svenhjol.charmony.core.common.features.wood.types;

import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;
import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.blocks.CustomPlanksBlock;

import java.util.function.Supplier;

public class Planks extends CustomWoodType {
    public final Supplier<CustomPlanksBlock> block;
    public final Supplier<CustomPlanksBlock.PlanksBlockItem> item;

    public Planks(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var id = material.getSerializedName() + "_planks";

        block = commonRegistry.block(id, key -> new CustomPlanksBlock(key, material));
        item = commonRegistry.item(id, key -> new CustomPlanksBlock.PlanksBlockItem(key, block));

        commonRegistry.ignite(block); // Planks can set on fire.
        woodRegistry.addItemToCreativeTab(item, material, CustomWood.PLANKS);
    }
}
