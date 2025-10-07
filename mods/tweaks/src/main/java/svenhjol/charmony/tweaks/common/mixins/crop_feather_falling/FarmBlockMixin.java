package svenhjol.charmony.tweaks.common.mixins.crop_feather_falling;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.common.features.crop_feather_falling.CropFeatherFalling;

@Mixin(FarmBlock.class)
public class FarmBlockMixin {
    @WrapMethod(
        method = "fallOn"
    )
    private void hookOnLandedUpon(Level level, BlockState state, BlockPos pos, Entity entity, double d, Operation<Void> original) {
        if (CropFeatherFalling.feature().handlers.shouldPreventCropDamage(entity)) {
            return;
        }
        original.call(level, state, pos, entity, d);
    }
}
