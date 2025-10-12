package charmony.core.client.features.hud_item_scaling;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.world.item.ItemStack;
import charmony.core.base.Setup;

public class Handlers extends Setup<HudItemScaling> {
    private ItemStack stack = null;

    public Handlers(HudItemScaling feature) {
        super(feature);
    }

    public void setRendering(ItemStack stack) {
        this.stack = stack;
    }

    public void setGuiItemRenderState(GuiItemRenderState state) {
        if (stack != null) {
            var minecraft = Minecraft.getInstance();
            var width = minecraft.getWindow().getGuiScaledWidth();
            var height = minecraft.getWindow().getGuiScaledHeight();

            HudItemScaling.feature().registers.getHudRenderers().forEach(
                hud -> hud.scaleItem(stack, state, minecraft, width, height));
            stack = null;
        }
    }
}
