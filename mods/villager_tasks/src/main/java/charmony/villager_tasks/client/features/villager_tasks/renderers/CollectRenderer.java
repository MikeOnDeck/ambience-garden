package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.requirements.CollectItem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class CollectRenderer extends BaseRenderer {
    public CollectRenderer(Task task) {
        super(task);
    }

    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;

        guiGraphics.drawString(font, Resources.COLLECT_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
        calcHeight += 10;

        var items = task.collect.items();
        var rows = items.size();

        for (var i = 0; i < Math.min(3, items.size()); i++) {
            var item = items.get(i);
            calcWidth = Math.max(calcWidth, renderItemTooltip(guiGraphics, item.stack(), Component.literal("" + item.total()), x, y + calcHeight + (i * 15)));
        }

        calcHeight += (rows * 15) + 10;
        return Pair.of(calcWidth, calcHeight);
    }


    public Pair<Integer, Integer> renderCollectItemBox(CollectItem collectItem, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, boolean showProgress) {
        var textColor = textColor(collectItem);
        var fillColor = fillColor(collectItem);
        var text = showProgress ? (collectItem.total() - collectItem.remaining()) + "/" + collectItem.total() : "" + collectItem.total();
        var box = renderRequirementBox(guiGraphics, Component.literal(text), x, y, fillColor);

        // Item x and y
        var ix = x + 2;
        var iy = y + 1;

        List<Component> tooltips = new ArrayList<>();
        tooltips.add(collectItem.isSatisfied() ? Resources.YOU_COLLECTED : Resources.YOU_COLLECT);
        tooltips.add(Component.literal(text + ": " + collectItem.total()));
        renderItemStack(guiGraphics, collectItem.stack(), tooltips, ix, iy, mouseX, mouseY);

        // Text x and y
        var tx = ix + 18;
        var ty = iy + 5;

        guiGraphics.drawString(font, text, tx, ty, textColor.getArgbColor());
        return box;
    }

    public Color fillColor(CollectItem collectItem) {
        var satisfied = collectItem.isSatisfied();
        var none = collectItem.remaining() == collectItem.total();
        var some = collectItem.remaining() < collectItem.total() && !satisfied;

        if (satisfied) {
            return getCompleteColor();
        } else if (some) {
            return getProgressColor();
        } else if (none) {
            return getMissingColor();
        } else {
            return DEFAULT_FILL_COLOR;
        }
    }

    public Color textColor(CollectItem collectItem) {
        return DEFAULT_TEXT_COLOR;
    }
}
