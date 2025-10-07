package svenhjol.charmony.tweaks.common.features.grindstone_disenchanting;

import net.minecraft.server.level.ServerPlayer;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<GrindstoneDisenchanting> {
    public Advancements(GrindstoneDisenchanting feature) {
        super(feature);
    }

    public void extractedEnchantment(ServerPlayer player) {
        AdvancementHelper.trigger("extracted_enchantment", player);
    }
}
