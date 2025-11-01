package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.villager_tasks.client.features.villager_tasks.renderers.TaskRenderer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class AvailableTaskTooltip extends TaskTooltip {
    private final TaskRenderer renderer;

    public AvailableTaskTooltip(TaskRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void renderImage(Font font, int x, int y, int xx, int yy, GuiGraphics guiGraphics) {
        var calcWidth = 0;
        var calcHeight = 12;
        var xy = Pair.of(0, 0);
        startScaling(guiGraphics, x, y);

        // Collect items
        xy = renderer.collect.renderTaskHoverTooltip(guiGraphics, x, y + calcHeight);
        calcWidth = Math.max(calcWidth, xy.getFirst());
        calcHeight += xy.getSecond() + 5;

        // Rewards
        xy = renderer.rewards.renderTooltip(guiGraphics, x, y + calcHeight);
        calcWidth = Math.max(calcWidth, xy.getFirst());
        calcHeight += xy.getSecond();

        calcHeight -= 5; // Remove last padding
        recalculateDimensions(calcWidth, calcHeight);
        stopScaling(guiGraphics);
    }
}
