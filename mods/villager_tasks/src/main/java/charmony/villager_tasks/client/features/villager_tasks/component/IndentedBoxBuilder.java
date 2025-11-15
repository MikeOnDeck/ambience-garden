package charmony.villager_tasks.client.features.villager_tasks.component;

import charmony.api.core.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;

public class IndentedBoxBuilder {
    private static final Color INDENT_COLOR = new Color(0xffffff);
    private static final Color OUTDENT_COLOR = new Color(0x000000);

    private Color color;
    private double opacity = 1.0d;
    private boolean overlay = false;
    private int width = 0;
    private int height = 0;

    public IndentedBoxBuilder withColor(Color color) {
        this.color = color;
        return this;
    }

    public IndentedBoxBuilder withOpacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    public IndentedBoxBuilder withOverlay(boolean overlay) {
        this.overlay = overlay;
        return this;
    }

    public IndentedBoxBuilder withDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        var bgColor = ARGB.color((int)(255.0d * opacity), color.getArgbColor());

        var x1 = x + width;
        var y1 = y + height;

        guiGraphics.hLine(x, x1 - 1, y, OUTDENT_COLOR.getArgbColor());
        guiGraphics.vLine(x1, y, y1 + 1, INDENT_COLOR.getArgbColor());
        guiGraphics.hLine(x + 1, x1, y1, INDENT_COLOR.getArgbColor());
        guiGraphics.vLine(x, y, y1, OUTDENT_COLOR.getArgbColor());

        int bx, bx1, by, by1;
        if (overlay) {
            bx = x;
            bx1 = x1 + 1;
            by = y;
            by1 = y1 + 1;
        } else {
            bx = x + 1;
            bx1 = x1;
            by = y + 1;
            by1 = y1;
        }

        guiGraphics.fill(RenderPipelines.GUI, bx, by, bx1, by1, bgColor);
    }
}
