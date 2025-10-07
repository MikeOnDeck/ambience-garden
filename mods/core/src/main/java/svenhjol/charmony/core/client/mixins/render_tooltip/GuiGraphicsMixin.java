package svenhjol.charmony.core.client.mixins.render_tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.api.events.RenderTooltipCallback;
import svenhjol.charmony.core.helpers.TooltipHelper;

import java.util.List;

@SuppressWarnings("UnreachableCode")
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    /**
     * Fires the {@link svenhjol.charmony.api.events.RenderTooltipCallback} event.
     * Modules can hook into the tooltip before it is rendered.
     */
    @Inject(
        method = "renderTooltip",
        at = @At("HEAD")
    )
    private void hookRenderOrderedTooltip(Font font, List<ClientTooltipComponent> lines, int x, int y, ClientTooltipPositioner clientTooltipPositioner, ResourceLocation resourceLocation, CallbackInfo ci) {
        var itemStack = TooltipHelper.getTooltipItemStack();
        RenderTooltipCallback.EVENT.invoker().interact((GuiGraphics)(Object)this, itemStack, lines, x, y);
        TooltipHelper.clearTooltipItemStack();
    }
}
