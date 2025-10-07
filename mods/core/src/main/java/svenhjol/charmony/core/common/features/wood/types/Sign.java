package svenhjol.charmony.core.common.features.wood.types;

import net.minecraft.world.level.block.entity.BlockEntityType;
import svenhjol.charmony.core.base.Registerable;
import svenhjol.charmony.core.common.features.wood.CustomWood;
import svenhjol.charmony.core.common.features.wood.CustomWoodType;
import svenhjol.charmony.core.common.features.wood.WoodMaterial;
import svenhjol.charmony.core.common.features.wood.WoodRegistry;
import svenhjol.charmony.core.common.features.wood.blocks.CustomStandingSignBlock;
import svenhjol.charmony.core.common.features.wood.blocks.CustomWallSignBlock;
import svenhjol.charmony.core.common.features.wood.items.CustomSignItem;

import java.util.List;
import java.util.function.Supplier;

public class Sign extends CustomWoodType {
    public final Supplier<CustomStandingSignBlock> standingBlock;
    public final Supplier<CustomWallSignBlock> wallBlock;
    public final Supplier<CustomSignItem> item;

    public Sign(WoodRegistry woodRegistry, WoodMaterial material) {
        super(woodRegistry, material);

        var commonRegistry = woodRegistry.commonRegistry();
        var signId = material.getSerializedName() + "_sign";
        var wallSignId = material.getSerializedName() + "_wall_sign";

        standingBlock = commonRegistry.block(signId, key -> new CustomStandingSignBlock(key, material));
        wallBlock = woodRegistry.wallSignBlock(wallSignId, material, standingBlock);
        item = commonRegistry.item(signId, key -> {
            var item = new CustomSignItem(key, material, standingBlock, wallBlock);
            wallBlock.get().item = item;
            return item;
        });

        // Associate with the sign block entity.
        new Registerable<Void>(commonRegistry.feature(), () -> {
            commonRegistry.blocksForBlockEntity(() -> BlockEntityType.SIGN, List.of(standingBlock, wallBlock));
            return null;
        });

        woodRegistry.addItemToCreativeTab(item, material, CustomWood.SIGN);
    }
}
