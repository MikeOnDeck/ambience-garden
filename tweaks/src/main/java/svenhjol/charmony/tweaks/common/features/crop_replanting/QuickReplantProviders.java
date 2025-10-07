package svenhjol.charmony.tweaks.common.features.crop_replanting;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import svenhjol.charmony.api.tweaks.QuickReplantProvider;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class QuickReplantProviders extends Setup<CropReplanting> implements QuickReplantProvider {
    public QuickReplantProviders(CropReplanting feature) {
        super(feature);
        Api.registerProvider(this);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public List<Supplier<BlockState>> getHarvestableBlocks() {
        List<Supplier<BlockState>> harvestables = new ArrayList<>(List.of(
            () -> Blocks.BEETROOTS.defaultBlockState().setValue(BeetrootBlock.AGE, 3),
            () -> Blocks.CARROTS.defaultBlockState().setValue(CarrotBlock.AGE, 7),
            () -> Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 3),
            () -> Blocks.POTATOES.defaultBlockState().setValue(PotatoBlock.AGE, 7),
            () -> Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7),
            () -> Blocks.PITCHER_CROP.defaultBlockState().setValue(PitcherCropBlock.AGE, 4),
            () -> Blocks.PITCHER_PLANT.defaultBlockState(),
            () -> Blocks.TORCHFLOWER.defaultBlockState()
        ));

        // Cocoa also has FACING property. We need to capture all states with AGE=2.
        Blocks.COCOA.getStateDefinition().getPossibleStates().stream()
            .filter(s -> s.getValue(CocoaBlock.AGE) == 2)
            .forEach(s -> harvestables.add(() -> s));

        return harvestables;
    }
}
