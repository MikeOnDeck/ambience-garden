package charmony.tweaks.client.features.item_frame_hiding;

import charmony.core.base.Mod;
import charmony.tweaks.common.features.item_frame_hiding.ItemFrameHiding;
import charmony.tweaks.common.features.item_frame_hiding.Registers;

public class Common {
    public final Registers registers;

    public Common() {
        var common = Mod.getSidedFeature(ItemFrameHiding.class);
        registers = common.registers;
    }
}
