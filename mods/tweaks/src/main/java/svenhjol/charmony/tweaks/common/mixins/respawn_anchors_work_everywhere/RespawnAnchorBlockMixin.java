package svenhjol.charmony.tweaks.common.mixins.respawn_anchors_work_everywhere;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.tweaks.common.features.respawn_anchors_work_everywhere.RespawnAnchorsWorkEverywhere;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {
    @WrapMethod(
        method = "canSetSpawn"
    )
    private static boolean hookCanSetSpawn(Level level, Operation<Boolean> original) {
        if (Mod.getSidedFeature(RespawnAnchorsWorkEverywhere.class).enabled()) {
            return true;
        }
        return original.call(level);
    }
}
