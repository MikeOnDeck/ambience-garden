package svenhjol.charmony.tweaks.client.mixins.totem_emergency_swap;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.client.features.totem_emergency_swap.TotemEmergencySwap;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    /**
     * Prevents held keys from being interrupted when the inventory screen is initialized.
     * Check if the totem is currently being swapped, and if it is, suppress the keybind releasing.
     * Without this pressing the totem keybind will stop any held keys such as walking.
     */
    @WrapMethod(
        method = "releaseAll"
    )
    private static void hookReleaseAll(Operation<Void> original) {
        if (TotemEmergencySwap.feature().handlers.preventKeybindReleasing()) {
            return;
        }
        original.call();
    }
}
