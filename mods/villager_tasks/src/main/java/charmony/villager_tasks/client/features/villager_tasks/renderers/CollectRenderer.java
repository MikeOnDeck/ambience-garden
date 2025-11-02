package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class CollectRenderer extends BaseRenderer {
    public CollectRenderer(Task task) {
        super(task);
    }

    /**
     * Render tooltip when hovering over the task name in a task row.
     */
    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;

        var items = task.collect.items();
        var rows = items.size();

        if (!items.isEmpty()) {
            guiGraphics.drawString(font, Resources.COLLECT_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
            calcHeight += 10;

            for (var i = 0; i < Math.min(3, items.size()); i++) {
                var item = items.get(i);
                calcWidth = Math.max(calcWidth, renderItemTooltip(guiGraphics, item.stack(), Component.literal("" + item.total()), x, y + calcHeight + (i * 15)));
            }

            calcHeight += (rows * 15) + 15;
        }

        return Pair.of(calcWidth, calcHeight);
    }

    public Pair<Integer, Integer> renderPanel(GuiGraphics guiGraphics, int x, int y, int xx, int yy, int maxWidth, int mouseX, int mouseY) {
        var collect = task.collect;
        if (!collect.isEmpty()) {
            var boxMargin = 3;

            for (var i = 0; i < collect.items().size(); i++) {
                var item = collect.items().get(i);
                var stack = item.stack();
                var box = renderItemBox(guiGraphics, item, stack, Resources.YOU_COLLECT, x + xx, y + yy, mouseX, mouseY, task.isStarted());

                var width = box.getFirst();
                var height = box.getSecond();

                xx += width + boxMargin;
                if (xx >= maxWidth) {
                    // Move to next row
                    xx = 0;
                    yy += height + boxMargin;
                }
            }
        }

        return Pair.of(xx, yy);
    }
}
