package charmony.tweaks.common.features.shulker_boxes_show_contents;

import charmony.api.core.Side;
import charmony.core.base.Setup;
import charmony.core.common.CommonRegistry;
import charmony.tweaks.common.features.shulker_boxes_show_contents.Networking.C2SRequestContents;
import charmony.tweaks.common.features.shulker_boxes_show_contents.Networking.S2CShowContents;

public class Registers extends Setup<ShulkerBoxesShowContents> {
    public Registers(ShulkerBoxesShowContents feature) {
        super(feature);

        var registry = CommonRegistry.forFeature(feature);
        registry.packetSender(Side.Common, S2CShowContents.TYPE, S2CShowContents.CODEC);
        registry.packetSender(Side.Client, C2SRequestContents.TYPE, C2SRequestContents.CODEC);
        registry.packetReceiver(C2SRequestContents.TYPE, feature.handlers::handleRequestContents);
    }
}
