package svenhjol.charmony.tweaks.common.features.totems_work_from_inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import svenhjol.charmony.api.tweaks.TotemInventoryCheckProvider;
import svenhjol.charmony.api.tweaks.TotemType;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;

import java.util.Optional;

public final class TotemInventoryProviders extends Setup<TotemsWorkFromInventory> implements TotemInventoryCheckProvider {
    public TotemInventoryProviders(TotemsWorkFromInventory feature) {
        super(feature);
        Api.registerProvider(this);
    }

    @Override
    public Optional<ItemStack> findTotemFromInventory(Player player, TotemType totemType) {
        if (totemType == TotemType.Undying) {
            var mainHand = player.getMainHandItem();
            if (mainHand.is(Items.TOTEM_OF_UNDYING)) {
                return Optional.of(mainHand);
            }

            var offHand = player.getOffhandItem();
            if (offHand.is(Items.TOTEM_OF_UNDYING)) {
                return Optional.of(offHand);
            }

            for (var item : player.getInventory().items) {
                if (item.is(Items.TOTEM_OF_UNDYING)) {
                    return Optional.of(item);
                }
            }
        }

        return Optional.empty();
    }
}
