package svenhjol.charmony.tweaks.common.features.totems_work_from_inventory;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.AdvancementHelper;

public class Advancements extends Setup<TotemsWorkFromInventory> {
    public Advancements(TotemsWorkFromInventory feature) {
        super(feature);
    }

    public void usedTotemFromInventory(Player player) {
        var mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        var offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        if (!mainHand.is(Items.TOTEM_OF_UNDYING) && !offHand.is(Items.TOTEM_OF_UNDYING)) {
            AdvancementHelper.trigger("used_totem_from_inventory", player);
        }
    }
}
