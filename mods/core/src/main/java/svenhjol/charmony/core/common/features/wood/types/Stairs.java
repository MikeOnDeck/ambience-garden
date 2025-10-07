package svenhjol.charmony.core.common.features.wood.types;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.StairBlock;
import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;
import svenhjol.charmony.core.common.features.wood.CustomWood;

import java.util.function.Supplier;

public class Stairs extends CustomWoodType {
    public final Supplier<? extends StairBlock> block;
    public final Supplier<? extends BlockItem> item;

    public Stairs(WoodRegistry woodRegistry, WoodMaterial material, Supplier<Planks> planks) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var id = material.getSerializedName() + "_stairs";

        var stairs = woodRegistry.stairsBlockHelper(id, () -> material,
            () -> planks.get().block.get().defaultBlockState());

        // Stairs can set on fire.
        commonRegistry.ignite(stairs.getFirst());

        // References seem a bit broken here. Why are stairs this?
        block = stairs.getFirst();
        item = stairs.getSecond();

        woodRegistry.addItemToCreativeTab(item, material, CustomWood.STAIRS);
    }
}
