package svenhjol.charmony.tweaks.common.mixins.item_restocking;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charmony.tweaks.common.features.item_restocking.ItemRestocking;

@Mixin(Animal.class)
public abstract class AnimalMixin {
    @Shadow public abstract boolean isFood(ItemStack var1);

    /**
     * Allows auto restock of an item fed to an animal.
     */
    @Inject(
        method = "mobInteract",
        at = @At("HEAD")
    )
    public void hookConsumeItemFromStack(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        var stack = player.getItemInHand(hand);
        if (this.isFood(stack)) {
            ItemRestocking.feature().handlers.addItemUsedStat(player, stack);
        }
    }
}
