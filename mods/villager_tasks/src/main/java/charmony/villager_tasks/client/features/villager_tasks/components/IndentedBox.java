package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

public record IndentedBox() {
    private static final Color INDENT_COLOR = new Color(0xFFFFFF);
    private static final Color OUTDENT_COLOR = new Color(0x000000);

    public void render(GuiGraphics guiGraphics, int x0, int x1, int y0, int y1, Color fillColor) {
        guiGraphics.hLine(x0, x1, y0, OUTDENT_COLOR.getArgbColor());
        guiGraphics.vLine(x1, y0, y1, INDENT_COLOR.getArgbColor());
        guiGraphics.hLine(x0, x1, y1, INDENT_COLOR.getArgbColor());
        guiGraphics.vLine(x0, y0, y1, OUTDENT_COLOR.getArgbColor());
        guiGraphics.fill(RenderPipelines.GUI, x0 + 1, y0 + 1, x1, y1, fillColor.getArgbColor());
    }
}
