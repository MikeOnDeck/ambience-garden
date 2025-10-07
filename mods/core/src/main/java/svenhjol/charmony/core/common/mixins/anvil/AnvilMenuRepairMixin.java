package svenhjol.charmony.core.common.mixins.anvil;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.api.events.AnvilEvents;

@SuppressWarnings("UnreachableCode")
@Mixin(AnvilMenu.class)
public abstract class AnvilMenuRepairMixin extends ItemCombinerMenu {
    public AnvilMenuRepairMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess access,
                                ItemCombinerMenuSlotDefinition definition) {
        super(menuType, i, inventory, access, definition);
    }

    /**
     * Fires the {@link svenhjol.charmony.api.events.AnvilEvents.RepairEvent} event.
     * Allows intervention when checking if the anvil item can be repaired with another item via the CheckAnvilRepairEvent event.
     * For example, elytra cannot normally be repaired with leather, but using the player.world we can check if insomnia is disabled
     * and therefore allow this repair check.  The reason we don't hook into ElytraItem's canRepair method directly is because
     * there is no world reference.
     */
    @ModifyExpressionValue(
        method = "createResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;isValidRepairItem(Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    private boolean hookUpdateResultCanRepair(boolean original,
                                              @Local(ordinal = 0) ItemStack leftStack,
                                              @Local(ordinal = 1) ItemStack leftItem,
                                              @Local(ordinal = 2) ItemStack rightStack
    ) {
        boolean result = AnvilEvents.REPAIR.invoke((AnvilMenu) (Object) this, this.player, leftStack, rightStack);
        return result || original;
    }
}
