package svenhjol.charmony.core.client.mixins.wood;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.common.features.wood.blocks.CustomCeilingHangingSignBlock;
import svenhjol.charmony.core.common.features.wood.blocks.CustomWallHangingSignBlock;

@Mixin(HangingSignEditScreen.class)
public abstract class HangingSignEditScreenMixin extends AbstractSignEditScreen {
    @Mutable @Final @Shadow private ResourceLocation texture;
    @Unique private boolean hasCheckedForCustomGui;

    public HangingSignEditScreenMixin(SignBlockEntity signBlockEntity, boolean bl, boolean bl2) {
        super(signBlockEntity, bl, bl2);
    }

    @Inject(
        method = "renderSignBackground",
        at = @At("HEAD")
    )
    private void hookRenderSignBackground(GuiGraphics guiGraphics, CallbackInfo ci) {
        // Override the GUI texture location with the modded path.
        checkForCustomGui();
    }

    @Unique
    private void checkForCustomGui() {
        if (hasCheckedForCustomGui) {
            return;
        }
        hasCheckedForCustomGui = true;

        if (sign.getLevel() instanceof Level level) {
            var block = level.getBlockState(sign.getBlockPos()).getBlock();
            SidedFeature feature;

            if (block instanceof CustomCeilingHangingSignBlock cc) {
                feature = cc.feature();
            } else if (block instanceof CustomWallHangingSignBlock cw) {
                feature = cw.feature();
            } else {
                return;
            }

            this.texture = feature.registryId("textures/gui/hanging_signs/" + this.woodType.name() + ".png");
        }
    }
}
