package charmony.tweaks.client.mixins.piglin_pointing;

import charmony.core.base.Environment;
import charmony.tweaks.client.features.piglin_pointing.PiglinPointing;
import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiglinModel.class)
public class PiglinModelMixin {
    @Inject(
        method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PiglinRenderState;)V",
        at = @At("TAIL")
    )
    private void hookSetupAnim(PiglinRenderState state, CallbackInfo ci) {
        if (!Environment.usesCharmonyServer()) return;
        var handlers = PiglinPointing.feature().handlers;

        if (handlers.piglin instanceof Piglin mob) {
            var model = (PiglinModel)(Object)(this);
            handlers.animate(model, state, mob);

            // We're done with the piglin mob reference, release it.
            handlers.piglin = null;
        }
    }
}
