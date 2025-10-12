package charmony.core.client.mixins.tint_background;

import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import charmony.core.client.features.tint_background.TintBackground;

@Mixin(GuiRenderState.class)
public class GuiRenderStateMixin {
    @Inject(
        method = "submitGuiElement",
        at = @At("TAIL")
    )
    private void hookSubmitGuiElement(GuiElementRenderState state, CallbackInfo ci) {
        TintBackground.feature().handlers.trySetState(state);
    }
}
