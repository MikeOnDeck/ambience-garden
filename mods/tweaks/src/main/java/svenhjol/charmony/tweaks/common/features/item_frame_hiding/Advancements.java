package svenhjol.charmony.tweaks.common.features.item_frame_hiding;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<ItemFrameHiding> {
    public Advancements(ItemFrameHiding feature) {
        super(feature);
    }

    public void hiddenItemFrame(ServerPlayer player) {
        AdvancementHelper.trigger("hidden_item_frame", player);
    }
}
