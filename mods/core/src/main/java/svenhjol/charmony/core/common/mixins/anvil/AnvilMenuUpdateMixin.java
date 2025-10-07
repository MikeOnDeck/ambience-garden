package svenhjol.charmony.core.common.mixins.anvil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.api.events.AnvilEvents;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuUpdateMixin extends ItemCombinerMenu {
    @Shadow @Final private DataSlot cost;

    @Shadow private int repairItemCountCost;

    public AnvilMenuUpdateMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess access,
                                ItemCombinerMenuSlotDefinition definition) {
        super(menuType, i, inventory, access, definition);
    }

    @WrapOperation(
        method = "createResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
            ordinal = 1
        )
    )
    private boolean hookCreateResultCheckItemStack(ItemStack instance, Operation<Boolean> original,
                                                   @Local(ordinal = 0) ItemStack input,
                                                   @Local long baseCost,
                                                   @Local(ordinal = 2) ItemStack material) {
        var result = AnvilEvents.UPDATE.invoke(player, input, material, baseCost);
        if (result.isPresent()) {
            var recipe = result.get();
            resultSlots.setItem(0, recipe.output);
            cost.set(recipe.experienceCost);
            this.repairItemCountCost = recipe.materialCost;
            return false;
        }
        return original.call(instance);
    }
}
