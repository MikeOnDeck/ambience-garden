package svenhjol.charmony.tweaks.common.features.totems_work_from_inventory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import svenhjol.charmony.api.tweaks.TotemType;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<TotemsWorkFromInventory> {
    public Handlers(TotemsWorkFromInventory feature) {
        super(feature);
    }

    public ItemStack tryUsingTotemOfUndying(LivingEntity entity) {
        if (entity instanceof Player player) {
            ItemStack found = null;
            for (var provider : feature().registers.inventoryCheckProviders) {
                var item = provider.findTotemFromInventory(player, TotemType.Undying);
                if (item.isPresent()) {
                    found = item.get();
                    break;
                }
            }

            if (found != null) {
                feature().advancements.usedTotemFromInventory(player);
                return found;
            }
        }

        return ItemStack.EMPTY;
    }
}
