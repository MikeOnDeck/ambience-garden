package svenhjol.charmony.tweaks.common.mixins.suspicious_block_creating;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charmony.tweaks.common.features.suspicious_block_creating.SuspiciousBlockCreating;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {
    @Inject(
        method = "triggerEvent",
        at = @At("HEAD")
    )
    private void hookTriggerEvent(BlockState state, Level level, BlockPos pos, int signal, int j, CallbackInfoReturnable<Boolean> cir) {
        SuspiciousBlockCreating.feature().handlers.checkAndConvert(level, pos, state);
    }
}
