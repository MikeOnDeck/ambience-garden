package charmony.tweaks.common.mixins.respawn_anchors_work_everywhere;

import charmony.core.base.Mod;
import charmony.tweaks.common.features.respawn_anchors_work_everywhere.RespawnAnchorsWorkEverywhere;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {
    @WrapMethod(
        method = "canSetSpawn"
    )
    private static boolean hookCanSetSpawn(ServerLevel serverLevel, BlockPos blockPos, Operation<Boolean> original) {
        if (Mod.getSidedFeature(RespawnAnchorsWorkEverywhere.class).enabled()) {
            return true;
        }
        return original.call(serverLevel, blockPos);
    }
}
