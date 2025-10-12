package charmony.tweaks.common.features.item_frame_hiding;

import net.minecraft.server.level.ServerPlayer;
import charmony.core.base.Setup;
import charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<ItemFrameHiding> {
    public Advancements(ItemFrameHiding feature) {
        super(feature);
    }

    public void hiddenItemFrame(ServerPlayer player) {
        AdvancementHelper.trigger("hidden_item_frame", player);
    }
}
