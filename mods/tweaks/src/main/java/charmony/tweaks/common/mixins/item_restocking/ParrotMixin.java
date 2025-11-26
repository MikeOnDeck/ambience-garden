package charmony.tweaks.common.mixins.item_restocking;

import charmony.tweaks.common.features.item_restocking.ItemRestocking;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Parrot.class)
public class ParrotMixin {
    /**
     * Allows auto restock of an item fed to a parrot.
     */
    @Inject(
        method = "mobInteract",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/parrot/Parrot;usePlayerItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V",
            shift = At.Shift.BEFORE,
            ordinal = 0
        )
    )
    private void hookMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemRestocking.feature().handlers.addItemUsedStat(player, player.getItemInHand(hand));
    }
}
