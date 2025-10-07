package svenhjol.charmony.tweaks.common.features.crop_replanting;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<CropReplanting> {
    public Advancements(CropReplanting feature) {
        super(feature);
    }

    public void replantedCrops(ServerPlayer player) {
        AdvancementHelper.trigger("replanted_crops", player);
    }
}
