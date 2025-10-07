package svenhjol.charmony.api.tweaks;

import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;

/**
 * Add the final blockstate for a crop-type block that can be harvested by the QuickReplant feature.
 */
@SuppressWarnings("unused")
public interface QuickReplantProvider {
    List<Supplier<BlockState>> getHarvestableBlocks();
}
