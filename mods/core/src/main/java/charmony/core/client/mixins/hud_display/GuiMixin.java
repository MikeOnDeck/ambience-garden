package charmony.core.client.mixins.hud_display;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import charmony.api.events.HudDisplayCallback;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(
        method = "render",
        at = @At("TAIL")
    )
    private void hookRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        HudDisplayCallback.EVENT.invoker().interact(guiGraphics, deltaTracker);
    }
}
