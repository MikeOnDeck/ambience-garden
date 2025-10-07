package svenhjol.charmony.tweaks.client.features.shulker_box_transferring;

import svenhjol.charmony.tweaks.common.features.shulker_box_transferring.Handlers;
import svenhjol.charmony.tweaks.common.features.shulker_box_transferring.ShulkerBoxTransferring;

public class Common {
    public final Handlers handlers;

    public Common() {
        var feature = ShulkerBoxTransferring.feature();
        handlers = feature.handlers;
    }
}
