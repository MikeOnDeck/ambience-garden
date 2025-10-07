package svenhjol.charmony.core.common.mixins.entity_killed_drop_callback;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.api.events.EntityKilledDropCallback;

@SuppressWarnings({"ConstantConditions", "UnreachableCode"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    /**
     * Fires the {@link EntityKilledDropCallback} event.
     */
    @Inject(
        method = "dropAllDeathLoot",
        at = @At("TAIL")
    )
    private void hookDrop(ServerLevel level, DamageSource source, CallbackInfo ci) {
        var entity = (LivingEntity)(Object)this;
        
        EntityKilledDropCallback.EVENT.invoker().interact(entity, source);
    }
}