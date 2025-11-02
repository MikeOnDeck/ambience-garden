package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.requirements.CollectItem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

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
        if (collect.isEmpty()) {
            return Pair.of(0, 0);
        }

        var boxMargin = 3;

        for (var i = 0; i < collect.items().size(); i++) {
            var item = collect.items().get(i);
            var box = renderItemBox(guiGraphics, item, x + xx, y + yy, mouseX, mouseY, task.isStarted());

            var width = box.getFirst();
            var height = box.getSecond();

            xx += width + boxMargin;
            if (xx >= maxWidth) {
                // Move to next row
                xx = 0;
                yy += height + boxMargin;
            }
        }

        return Pair.of(xx, yy);
    }

    public Pair<Integer, Integer> renderItemBox(GuiGraphics guiGraphics, CollectItem collectItem, int x, int y, int mouseX, int mouseY, boolean showProgress) {
        var textColor = textColor(collectItem);
        var fillColor = fillColor(collectItem);
        var text = showProgress ? (collectItem.total() - collectItem.remaining()) + "/" + collectItem.total() : "" + collectItem.total();
        var box = renderRequirementBox(guiGraphics, Component.literal(text), x, y, fillColor);
        var width = box.getFirst();
        var height = box.getSecond();

        // Item x and y
        var ix = x + 2;
        var iy = y + 1;

        renderItemStack(guiGraphics, collectItem.stack(), List.of(), ix, iy, mouseX, mouseY);

        // Text x and y
        var tx = ix + 19;
        var ty = iy + 5;

        guiGraphics.drawString(font, text, tx, ty, textColor.getArgbColor());

        // Tooltip on item box hover
        var itemTooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), collectItem.stack());
        var tooltip = new ArrayList<Component>();
        tooltip.add(collectItem.isSatisfied() ? Resources.YOU_COLLECTED : Resources.YOU_COLLECT);
        tooltip.add(Component.translatable("gui.charmony.villager_tasks.name_and_number", itemTooltip.getFirst(), collectItem.total()));

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }

        return box;
    }
}
