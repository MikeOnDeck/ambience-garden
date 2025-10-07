package svenhjol.charmony.tweaks.common.features.shulker_box_transferring;

import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<ShulkerBoxTransferring> {
    public Advancements(ShulkerBoxTransferring feature) {
        super(feature);
    }

    public void transferredToShulkerBox(Player player) {
        AdvancementHelper.trigger("transferred_to_shulker_box", player);
    }
}
