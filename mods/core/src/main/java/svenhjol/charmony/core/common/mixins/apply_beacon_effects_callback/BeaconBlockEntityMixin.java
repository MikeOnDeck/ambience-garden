package svenhjol.charmony.core.common.mixins.apply_beacon_effects_callback;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.api.events.ApplyBeaconEffectsCallback;

import javax.annotation.Nullable;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
    @Inject(
        method = "applyEffects",
        at = @At("HEAD")
    )
    private static void hookApplyEffects(Level level, BlockPos pos, int levels, @Nullable Holder<MobEffect> primaryEffect,
                                         @Nullable Holder<MobEffect> secondaryEffect, CallbackInfo ci) {
        ApplyBeaconEffectsCallback.EVENT.invoker().interact(level, pos, levels, primaryEffect, secondaryEffect);
    }
}
