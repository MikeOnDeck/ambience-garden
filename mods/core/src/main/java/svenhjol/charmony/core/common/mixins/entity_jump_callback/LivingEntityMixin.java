package svenhjol.charmony.core.common.mixins.entity_jump_callback;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.api.events.EntityJumpCallback;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
        method = "jumpFromGround",
        at = @At("TAIL")
    )
    private void hookJumpFromGround(CallbackInfo ci) {
        EntityJumpCallback.EVENT.invoker().interact((LivingEntity)(Object)this);
    }
}
