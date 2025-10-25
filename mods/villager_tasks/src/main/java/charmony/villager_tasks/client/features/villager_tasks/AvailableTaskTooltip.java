package charmony.villager_tasks.client.features.villager_tasks;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class AvailableTaskTooltip extends TaskTooltip {
    public AvailableTaskTooltip(Task task) {
        super(task);
    }

    @Override
    public void renderImage(Font font, int x, int y, int xx, int yy, GuiGraphics guiGraphics) {
        var calcWidth = 0;
        var calcHeight = 12;
        startScale(guiGraphics, x, y);

        if (true) {
            guiGraphics.drawString(font, Resources.COLLECT_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
            calcHeight += 10;

            var items = task.collect.items();

            // Collect items
            for (var i = 0; i < Math.min(3, items.size()); i++) {
                var item = items.get(i);
                calcWidth = Math.max(calcWidth, renderItem(guiGraphics, item.stack(), "" + item.total(), x, y + calcHeight + (i * 15)));
            }

            calcHeight += (items.size() * 15) + 10;
        }

        if (true) {
            guiGraphics.drawString(font, Resources.REWARD_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
            calcHeight += 10;

            var items = task.rewards.items;

            // Reward items
            for (var i = 0; i < Math.min(3, items.size()); i++) {
                var item = items.get(i);
                calcWidth = Math.max(calcWidth, renderItem(guiGraphics, item.stack(), "" + item.total(), x, y + calcHeight + (i * 15)));
            }

            calcHeight += (items.size() * 15) + 10;
        }

        calcHeight -= 5; // Remove last padding
        recalculateDimensions(calcWidth, calcHeight);
        stopScale(guiGraphics);
    }
}
