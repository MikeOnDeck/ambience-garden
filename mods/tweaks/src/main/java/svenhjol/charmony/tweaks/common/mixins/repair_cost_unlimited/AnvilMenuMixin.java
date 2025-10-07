package svenhjol.charmony.tweaks.common.mixins.repair_cost_unlimited;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.tweaks.client.mixins.repair_cost_visible.AnvilScreenMixin;
import svenhjol.charmony.tweaks.common.features.repair_cost_unlimited.RepairCostUnlimited;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    /**
     * A player in creative mode can repair or enchant without "too expensive".
     * Remove the message by always returning true for this check.
     * This modifies the updated output slot on the server side.
     * @see AnvilScreenMixin for the client side.
     */
    @WrapOperation(
        method = "createResult",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;hasInfiniteMaterials()Z",
            ordinal = 1
        )
    )
    private boolean hookCreateResultCheckAbilities(Player player, Operation<Boolean> original) {
        if (RepairCostUnlimited.feature().enabled()) {
            return true;
        }
        return original.call(player);
    }
}
