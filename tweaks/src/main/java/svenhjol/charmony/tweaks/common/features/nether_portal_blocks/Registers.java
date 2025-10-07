package svenhjol.charmony.tweaks.common.features.nether_portal_blocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.PortalShape;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<NetherPortalBlocks> {
    public Registers(NetherPortalBlocks feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            PortalShape.FRAME = (blockState, blockView, blockPos)
                -> blockState.is(Tags.NETHER_PORTAL_BLOCKS) || blockState.is(Blocks.OBSIDIAN);
        };
    }
}
