package svenhjol.charmony.core.common.mixins.grindstone;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.api.events.GrindstoneEvents;

@Mixin(targets = {"net/minecraft/world/inventory/GrindstoneMenu$4"})
public class GrindstoneMenuOutputMixin extends Slot {
    public GrindstoneMenuOutputMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPickup(Player player) {
        var instance = GrindstoneEvents.instance(player);
        if (instance == null) {
            return super.mayPickup(player);
        }

        var result = GrindstoneEvents.CAN_TAKE.invoke(instance, player);

        if (result == InteractionResult.SUCCESS) {
            return true;
        } else if (result == InteractionResult.FAIL) {
            return false;
        }

        return super.mayPickup(player);
    }

    @WrapMethod(
        method = "onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"
    )
    private void hookOnTake(Player player, ItemStack stack, Operation<Void> original) {
        var instance = GrindstoneEvents.instance(player);
        if (instance != null && GrindstoneEvents.ON_TAKE.invoke(instance, player, stack)) {
            return;
        }
        original.call(player, stack);
    }
}
