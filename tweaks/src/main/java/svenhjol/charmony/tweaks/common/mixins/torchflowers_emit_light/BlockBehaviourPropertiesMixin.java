package svenhjol.charmony.tweaks.common.mixins.torchflowers_emit_light;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.tweaks.common.features.torchflowers_emit_light.TorchflowersEmitLight;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviourPropertiesMixin {
    @ModifyReturnValue(
        method = "getLightEmission",
        at = @At("RETURN")
    )
    private int hookGetLightEmission(int original) {
        var feature = TorchflowersEmitLight.feature();
        if (feature.enabled()) {
            return feature.handlers
                .lightLevel((BlockBehaviour.BlockStateBase) (Object) this)
                .orElse(original);
        } else {
            return original;
        }
    }
}
