package charmony.villager_tasks.client.features.villager_tasks.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;

public record BorderedBox(int alpha) {
    public void render(GuiGraphics guiGraphics, int x0, int x1, int y0, int y1, int color) {
        var lineColor = ARGB.color(Math.min(255, alpha * 3), color);
        var bgColor = ARGB.color(alpha(), color);

        guiGraphics.hLine(x0, x1, y0, lineColor);
        guiGraphics.vLine(x1, y0, y1, lineColor);
        guiGraphics.hLine(x0, x1, y1, lineColor);
        guiGraphics.vLine(x0, y0, y1, lineColor);
        guiGraphics.fill(RenderPipelines.GUI, x0, y0, x1, y1, bgColor);
    }
}
