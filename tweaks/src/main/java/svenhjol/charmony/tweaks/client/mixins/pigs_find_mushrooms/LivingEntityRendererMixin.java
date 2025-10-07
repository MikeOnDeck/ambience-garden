package svenhjol.charmony.tweaks.client.mixins.pigs_find_mushrooms;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.tweaks.client.features.pigs_find_mushrooms.PigsFindMushrooms;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState> {
    /**
     * Set a reference to the currently rendering mob.
     */
    @Inject(
        method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
        at = @At("TAIL")
    )
    private void hookExtractRenderState(T livingEntity, S livingEntityRenderState, float f, CallbackInfo ci) {
        if (!Environment.usesCharmonyServer()) return;

        if (livingEntity instanceof Pig pig) {
            PigsFindMushrooms.feature().handlers.pig = pig;
        }
    }
}
