package svenhjol.charmony.core.common.mixins.dispenser_behavior;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.core.common.CommonRegistry;
import svenhjol.charmony.core.common.dispenser.CompositeDispenseItemBehavior;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @ModifyReturnValue(
        method = "getDispenseMethod",
        at = @At("RETURN")
    )
    private DispenseItemBehavior hookGetDispenseMethod(DispenseItemBehavior original, Level level, ItemStack stack) {
        if (CommonRegistry.CONDITIONAL_DISPENSER_BEHAVIORS.containsKey(stack.getItem())) {
            return new CompositeDispenseItemBehavior(original);
        }
        return original;
    }
}
