package svenhjol.charmony.tweaks.common.mixins.piglin_pointing;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.tweaks.common.features.piglin_pointing.PiglinPointing;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
    @ModifyReturnValue(
        method = "isLovedItem",
        at = @At("RETURN")
    )
    private static boolean hookIsLovedItem(boolean original, @Local(argsOnly = true) ItemStack stack) {
        if (PiglinPointing.feature().handlers.isBarteringItem(stack)) {
            return true;
        }
        return original;
    }

    @WrapMethod(
        method = "wantsToPickup"
    )
    private static boolean hookWantsToPickup(Piglin piglin, ItemStack stack, Operation<Boolean> original) {
        if (PiglinPointing.feature().handlers.wantsToPickup(piglin, stack)) {
            return true;
        }
        return original.call(piglin, stack);
    }

    @WrapOperation(
        method = "pickUpItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isLovedItem(Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    private static boolean hookCheckLovedItem(ItemStack stack, Operation<Boolean> original, @Local(argsOnly = true) Piglin piglin) {
        if (PiglinPointing.feature().handlers.tryToPickup(piglin, stack)) {
            return true;
        }
        return original.call(stack);
    }

    @WrapOperation(
        method = "stopHoldingOffHandItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;isBarterCurrency(Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    private static boolean hookCheckBarterCurrency(ItemStack stack, Operation<Boolean> original, @Local(argsOnly = true) Piglin piglin) {
        if (PiglinPointing.feature().enabled()) {
            PiglinPointing.feature().handlers.checkBlockAndFindStructure(piglin, stack);
            return false;
        }
        return original.call(stack);
    }

    @Inject(
        method = "updateActivity",
        at = @At("HEAD")
    )
    private static void hookUpdatePointing(Piglin piglin, CallbackInfo ci) {
        PiglinPointing.feature().handlers.setPointing(piglin);
    }
}
