package svenhjol.charmony.core.client.mixins.render_screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.api.events.RenderScreenCallback;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    /**
     * Fires the {@link RenderScreenCallback} event.
     * This is called on every vanilla drawForeground tick and allows custom gui rendering.
     */
    @Inject(
        method = "renderLabels",
        at = @At("HEAD")
    )
    private void hookRender(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        RenderScreenCallback.EVENT.invoker().interact((AbstractContainerScreen<?>)(Object)this, guiGraphics, mouseX, mouseY);
    }
}
