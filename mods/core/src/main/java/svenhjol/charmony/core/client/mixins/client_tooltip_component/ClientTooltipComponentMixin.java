package svenhjol.charmony.core.client.mixins.client_tooltip_component;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentMixin {
    /**
     * Vanilla hardcodes create() so that only ClientBundleTooltips can be returned.
     * Any other instance that implements ClientTooltipComponent will throw an Exception.
     * This mixin allows any valid ClientTooltipComponent to be returned.
     */
    @WrapMethod(
        method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;"
    )
    private static ClientTooltipComponent hookCreate(TooltipComponent tooltipComponent, Operation<ClientTooltipComponent> original) {
        if (tooltipComponent instanceof ClientTooltipComponent) {
            return (ClientTooltipComponent)tooltipComponent;
        }
        return original.call(tooltipComponent);
    }
}
