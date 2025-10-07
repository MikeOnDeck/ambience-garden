package svenhjol.charmony.core.common.mixins.anvil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.api.events.AnvilEvents;
import svenhjol.charmony.core.common.features.core.Core;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuOnTakeMixin extends ItemCombinerMenu {
    public AnvilMenuOnTakeMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess access,
                                ItemCombinerMenuSlotDefinition definition) {
        super(menuType, i, inventory, access, definition);
    }

    @Inject(
        method = "onTake",
        at = @At("HEAD")
    )
    private void hookOnTake(Player player, ItemStack taken, CallbackInfo ci) {
        AnvilEvents.ON_TAKE.invoke(player, inputSlots.getItem(0).copy(), inputSlots.getItem(1).copy(), taken);
    }

    @WrapOperation(
        method = "onTake",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private void hookSetItemOnTake(Container inputSlots, int slot, ItemStack stack, Operation<Void> original) {
        if (Core.feature().customAnvilOnTakeBehavior() && stack == ItemStack.EMPTY) {
            var inSlot = inputSlots.getItem(slot);
            inSlot.shrink(1);
            inputSlots.setItem(slot, inSlot);
            return;
        }
        original.call(inputSlots, slot, stack);
    }
}
