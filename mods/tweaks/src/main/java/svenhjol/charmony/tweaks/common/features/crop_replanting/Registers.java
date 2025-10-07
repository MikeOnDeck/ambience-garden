package svenhjol.charmony.tweaks.common.features.crop_replanting;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import svenhjol.charmony.api.tweaks.AutoPickupItemProvider;
import svenhjol.charmony.api.tweaks.QuickReplantProvider;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Registers extends Setup<CropReplanting> {
    public final List<BlockState> replantable = new ArrayList<>();
    public final List<Function<Player, Boolean>> autoPickup = new ArrayList<>();
    public final List<Block> notReplantable = List.of(
        Blocks.TORCHFLOWER,
        Blocks.PITCHER_CROP,
        Blocks.PITCHER_PLANT
    );

    public Registers(CropReplanting feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            // Listen to quick replant providers.
            Api.consume(QuickReplantProvider.class,
                provider -> provider.getHarvestableBlocks().forEach(
                    supplier -> replantable.add(supplier.get())));

            // Listen to auto pickup providers.
            Api.consume(AutoPickupItemProvider.class,
                provider -> autoPickup.add(provider.getAutoPickupItemChecks()));

            UseBlockCallback.EVENT.register(feature().handlers::useBlock);
        };
    }
}
