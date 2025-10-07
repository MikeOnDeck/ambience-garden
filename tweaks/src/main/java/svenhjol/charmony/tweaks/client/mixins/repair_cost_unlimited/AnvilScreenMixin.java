package svenhjol.charmony.tweaks.client.mixins.repair_cost_unlimited;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.tweaks.client.features.repair_cost_unlimited.RepairCostUnlimited;
import svenhjol.charmony.tweaks.common.mixins.repair_cost_unlimited.AnvilMenuMixin;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    /**
     * A player in creative mode does not see the "Too Expensive" message.
     * Remove the message by always returning true for this check.
     * This targets the *display* of the message on the client side.
     * @see AnvilMenuMixin for the server side.
     */
    @WrapOperation(
        method = "renderLabels",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;hasInfiniteMaterials()Z"
        )
    )
    private boolean hookRenderLabelsCheckAbilities(LocalPlayer player, Operation<Boolean> original) {
        if (Environment.usesCharmonyServer() && RepairCostUnlimited.feature().enabled()) {
            return true;
        }
        return original.call(player);
    }
}
