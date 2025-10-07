package svenhjol.charmony.core.common.features.wood.types;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;
import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;
import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.blocks.CustomBarrelBlock;

import java.util.List;
import java.util.function.Supplier;

public class Barrel extends CustomWoodType {
    public final Supplier<CustomBarrelBlock> block;
    public final Supplier<CustomBarrelBlock.BarrelBlockItem> item;

    public Barrel(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var id = material.getSerializedName() + "_barrel";

        block = commonRegistry.block(id, key -> new CustomBarrelBlock(key, material));
        item = commonRegistry.item(id, key -> new CustomBarrelBlock.BarrelBlockItem(key, block));

        // Barrels can be used as furnace fuel.
        commonRegistry.fuel(item);

        // Associate the blocks with the block entity dynamically.
        commonRegistry.blocksForBlockEntity(() -> BlockEntityType.BARREL, List.of(block));

        // Add this barrel to the Fisherman's point of interest.
        commonRegistry.pointOfInterestBlockStates(
            () -> BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(PoiTypes.FISHERMAN),
            () -> block.get().getStateDefinition().getPossibleStates());

        // Add to creative menu.
        woodRegistry.addItemToCreativeTab(item, material, CustomWood.BARREL);
    }
}
