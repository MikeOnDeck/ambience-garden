package svenhjol.charmony.core.common.mixins.player_killed_drop_callback;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.api.events.PlayerKilledDropCallback;

@SuppressWarnings("UnreachableCode")
@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow @Final
    Inventory inventory;

    /**
     * Fires the {@link svenhjol.charmony.api.events.PlayerKilledDropCallback} event.
     */
    @WrapWithCondition(
        method = "dropEquipment",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"
        )
    )
    private boolean hookDropInventory(Inventory instance) {
        InteractionResult result = PlayerKilledDropCallback.EVENT.invoker().interact((Player) (Object) this, this.inventory);
        return result != InteractionResult.SUCCESS;
    }
}
