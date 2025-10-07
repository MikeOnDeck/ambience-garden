package svenhjol.charmony.core.client.mixins.render_tooltip_component;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.api.events.RenderTooltipComponentCallback;

import java.util.Optional;

@Mixin(Item.class)
public class ItemMixin {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ModifyReturnValue(
        method = "getTooltipImage",
        at = @At("RETURN")
    )
    private Optional<TooltipComponent> hookGetTooltipImage(Optional<TooltipComponent> original, @Local(argsOnly = true) ItemStack stack) {
        var tooltip = RenderTooltipComponentCallback.EVENT.invoker().run(stack);

        if (tooltip.isPresent()) {
            return tooltip;
        }
        return original;
    }
}
