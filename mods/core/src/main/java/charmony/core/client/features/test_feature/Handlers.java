package charmony.core.client.features.test_feature;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.DyeColor;
import charmony.core.base.Setup;
import charmony.core.client.features.tint_background.TintedGuiGraphics;
import charmony.core.helpers.ColorHelper;

public class Handlers extends Setup<TestFeature> {
    public Handlers(TestFeature feature) {
        super(feature);
    }

    /**
     * Use the reference to the last clicked block color to render a tinted background.
     */
    public boolean tryRenderBackground(GuiGraphics guiGraphics, int width, int height, int imageWidth, int imageHeight) {
        if (feature().purpleShulkerBoxes()) {
            var x = (width - imageWidth) / 2;
            var y = (height - imageHeight) / 2;
            var bgColor = ColorHelper.getBackgroundColor(DyeColor.PURPLE);
            ((TintedGuiGraphics) guiGraphics).tint(bgColor).blit(RenderPipelines.GUI_TEXTURED, ShulkerBoxScreen.CONTAINER_TEXTURE, x, y, 0.0f, 0.0f, imageWidth, imageHeight, 256, 256);
            return true;
        }
        return false;
    }
}
