package svenhjol.charmony.tweaks.client.features.item_frame_hiding;

import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.tweaks.common.features.item_frame_hiding.ItemFrameHiding;
import svenhjol.charmony.tweaks.common.features.item_frame_hiding.Registers;

public class Common {
    public final Registers registers;

    public Common() {
        var common = Mod.getSidedFeature(ItemFrameHiding.class);
        registers = common.registers;
    }
}
