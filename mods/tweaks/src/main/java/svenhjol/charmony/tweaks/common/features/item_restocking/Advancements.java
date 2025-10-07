package svenhjol.charmony.tweaks.common.features.item_restocking;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<ItemRestocking> {
    public Advancements(ItemRestocking feature) {
        super(feature);
    }

    public void restockedCurrentItem(ServerPlayer player) {
        AdvancementHelper.trigger("restocked_current_item", player);
    }
}
