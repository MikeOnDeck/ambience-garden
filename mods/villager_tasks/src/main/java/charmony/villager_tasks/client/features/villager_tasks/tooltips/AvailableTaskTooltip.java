package charmony.villager_tasks.client.features.villager_tasks.tooltips;

import charmony.villager_tasks.client.features.villager_tasks.renderers.TaskRenderer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class AvailableTaskTooltip extends BaseTooltip {
    private final TaskRenderer taskRenderer;

    public AvailableTaskTooltip(TaskRenderer taskRenderer) {
        this.taskRenderer = taskRenderer;
    }

    @Override
    public void renderImage(Font font, int x, int y, int xx, int yy, GuiGraphics guiGraphics) {
        var calcWidth = 0;
        var calcHeight = 6;
        var xy = Pair.of(0, 0);
        startScaling(guiGraphics, x, y);

        for (var renderer : taskRenderer.aspectRenderers) {
            xy = renderer.renderTaskHoverTooltip(guiGraphics, x, y + calcHeight);
            calcWidth = Math.max(calcWidth, xy.getFirst());
            calcHeight += xy.getSecond();
        }

        calcHeight -= 5; // Remove last padding
        recalculateDimensions(calcWidth, calcHeight);
        stopScaling(guiGraphics);
    }
}
