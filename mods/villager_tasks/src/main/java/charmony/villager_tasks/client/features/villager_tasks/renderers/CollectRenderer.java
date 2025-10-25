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

    public Pair<Integer, Integer> renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;

        guiGraphics.drawString(font, Resources.COLLECT_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
        calcHeight += 10;

        var items = task.collect.items();
        var rows = items.size();

        for (var i = 0; i < Math.min(3, items.size()); i++) {
            var item = items.get(i);
            calcWidth = Math.max(calcWidth, renderTooltipItem(guiGraphics, item.stack(), Component.literal("" + item.total()), x, y + calcHeight + (i * 15)));
        }

        calcHeight += (rows * 15) + 10;
        return Pair.of(calcWidth, calcHeight);
    }
}
