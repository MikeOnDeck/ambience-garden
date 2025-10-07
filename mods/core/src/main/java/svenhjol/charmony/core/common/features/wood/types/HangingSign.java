package svenhjol.charmony.core.common.features.wood.types;

import net.minecraft.world.level.block.entity.BlockEntityType;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;
import svenhjol.charmony.core.common.features.wood.blocks.CustomCeilingHangingSignBlock;
import svenhjol.charmony.core.common.features.wood.blocks.CustomWallHangingSignBlock;
import svenhjol.charmony.core.common.features.wood.items.CustomHangingSignItem;

import java.util.List;
import java.util.function.Supplier;

public class HangingSign extends CustomWoodType {
    public final Supplier<CustomCeilingHangingSignBlock> hangingBlock;
    public final Supplier<CustomWallHangingSignBlock> wallBlock;
    public final Supplier<CustomHangingSignItem> item;

    public HangingSign(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var feature = commonRegistry.feature();
        var hangingId = material.getSerializedName() + "_hanging_sign";
        var wallId = material.getSerializedName() + "_wall_hanging_sign";

        hangingBlock = commonRegistry.block(hangingId,
            key -> new CustomCeilingHangingSignBlock(key, feature, material));
        wallBlock = woodRegistry.wallHangingSignBlock(wallId, feature, material, hangingBlock);
        item = commonRegistry.item(hangingId,
            key -> new CustomHangingSignItem(key, material, hangingBlock, wallBlock));

        // Associate with the hanging sign block entity.
        new Registerable<Void>(commonRegistry.feature(), () -> {
            commonRegistry.blocksForBlockEntity(() -> BlockEntityType.HANGING_SIGN, List.of(hangingBlock, wallBlock));
            return null;
        });

        woodRegistry.addItemToCreativeTab(item, material, CustomWood.HANGING_SIGN);
    }
}
