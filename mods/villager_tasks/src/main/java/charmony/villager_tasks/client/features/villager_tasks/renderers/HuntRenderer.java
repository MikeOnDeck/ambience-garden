package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.core.client.MobSpriteRenderer;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class HuntRenderer extends BaseRenderer {
    public HuntRenderer(Task task) {
        super(task);
    }

    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;

        var mobs = task.hunt.mobs();
        var rows = mobs.size();

        if (!mobs.isEmpty()) {
            guiGraphics.drawString(font, Resources.HUNT_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
            calcHeight += 10;

            for (var i = 0; i < Math.min(3, mobs.size()); i++) {
                var mob = mobs.get(i);
                var spriteRenderer = new MobSpriteRenderer(mob.mob());
                calcWidth = Math.max(calcWidth, renderSpriteTooltip(guiGraphics, spriteRenderer, Component.literal("" + mob.total()), x, y + calcHeight + (i * 15)));
            }

            calcHeight += (rows * 15) + 15;
        }

        return Pair.of(calcWidth, calcHeight);
    }
}
