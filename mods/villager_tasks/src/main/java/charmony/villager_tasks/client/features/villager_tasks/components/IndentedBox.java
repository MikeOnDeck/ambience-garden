package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;

@Deprecated
public record IndentedBox(int opacity, boolean overlay) {
    public static final Color INDENT_COLOR = new Color(0xffffff);
    public static final Color OUTDENT_COLOR = new Color(0x000000);

    public IndentedBox() {
        this(255, false);
    }

    public IndentedBox(int opacity) {
        this(opacity, true);
    }

    public void render(GuiGraphics guiGraphics, int x0, int x1, int y0, int y1, Color color) {
        var bgColor = ARGB.color(opacity(), color.getArgbColor());

        guiGraphics.hLine(x0, x1 - 1, y0, OUTDENT_COLOR.getArgbColor());
        guiGraphics.vLine(x1, y0, y1 + 1, INDENT_COLOR.getArgbColor());
        guiGraphics.hLine(x0 + 1, x1, y1, INDENT_COLOR.getArgbColor());
        guiGraphics.vLine(x0, y0, y1, OUTDENT_COLOR.getArgbColor());

        int bx0, bx1, by0, by1;
        if (overlay) {
            bx0 = x0;
            bx1 = x1 + 1;
            by0 = y0;
            by1 = y1 + 1;
        } else {
            bx0 = x0 + 1;
            bx1 = x1;
            by0 = y0 + 1;
            by1 = y1;
        }

        guiGraphics.fill(RenderPipelines.GUI, bx0, by0, bx1, by1, bgColor);
    }
}
