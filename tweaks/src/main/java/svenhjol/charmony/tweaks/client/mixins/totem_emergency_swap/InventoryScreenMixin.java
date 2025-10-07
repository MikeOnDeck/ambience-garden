package svenhjol.charmony.tweaks.client.mixins.totem_emergency_swap;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.client.features.totem_emergency_swap.TotemEmergencySwap;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    /**
     * Prevents the inventory screen from being rendered.
     * Check if the totem is currently being swapped, and if it is, suppress the rendering.
     * Without this there will be a momentary popup of the inventory screen.
     */
    @WrapMethod(
        method = "render"
    )
    private void hookRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float ticks, Operation<Void> original) {
        if (TotemEmergencySwap.feature().handlers.preventInventoryRendering()) {
            return;
        }
        original.call(guiGraphics, mouseX, mouseY, ticks);
    }
}
