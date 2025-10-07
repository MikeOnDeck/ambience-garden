package svenhjol.charmony.tweaks.client.mixins.spyglass_scope_hiding;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.tweaks.client.features.spyglass_scope_hiding.SpyglassScopeHiding;

@Mixin(Gui.class)
public class GuiMixin {
    @WrapMethod(
        method = "renderSpyglassOverlay"
    )
    private void hookRenderCameraOverlays(GuiGraphics guiGraphics, float f, Operation<Void> original) {
        if (Mod.getSidedFeature(SpyglassScopeHiding.class).enabled()) {
            return;
        }
        original.call(guiGraphics, f);
    }
}
