package svenhjol.charmony.tweaks.common.features.crop_feather_falling;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<CropFeatherFalling> {
    public Advancements(CropFeatherFalling feature) {
        super(feature);
    }

    public void preventedCropDamage(ServerPlayer player) {
        AdvancementHelper.trigger("prevented_crop_damage", player);
    }
}
