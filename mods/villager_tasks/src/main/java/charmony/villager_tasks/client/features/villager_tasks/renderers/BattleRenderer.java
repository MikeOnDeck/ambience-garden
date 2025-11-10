package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.core.client.MobSpriteRenderer;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class BattleRenderer extends BaseRenderer {
    public BattleRenderer(Task task) {
        super(task);
    }

    @Override
    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;
        var maxShown = 3;
        var rowHeight = 17;
        var margin = 11;

        var mobs = task.battle.mobs();
        var rows = Math.min(maxShown, mobs.size());
        var showEllipsis = mobs.size() > maxShown;

        if (!mobs.isEmpty()) {
            guiGraphics.drawString(font, Resources.BATTLE_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
            calcHeight += margin;

            for (var i = 0; i < rows; i++) {
                var mob = mobs.get(i);
                var spriteRenderer = new MobSpriteRenderer(mob.mob());
                calcWidth = Math.max(calcWidth, renderSpriteInTooltip(guiGraphics, spriteRenderer, Component.literal("" + mob.total()), x, y + calcHeight + (i * rowHeight)));
            }

            if (showEllipsis) {
                renderEllipsisInTooltip(guiGraphics, mobs.size() - maxShown, x, y + calcHeight + (rows * rowHeight));
                rows += 1;
            }

            calcHeight += (rows * rowHeight) + margin;
        }

        return Pair.of(calcWidth, calcHeight);
    }

    @Override
    public Pair<Integer, Integer> renderPanel(GuiGraphics guiGraphics, int x, int y, int xx, int yy, int maxWidth, int mouseX, int mouseY) {
        var battle = task.battle;

        if (!battle.isEmpty()) {
            var boxMargin = 3;

            for (var i = 0; i < battle.mobs().size(); i++) {
                var mob = battle.mobs().get(i);
                var spriteRenderer = new MobSpriteRenderer(mob.mob());

                var map = mob.map().orElse(null);
                if (map == null) continue;

                var box = renderSpriteAndMapBox(guiGraphics, mob, spriteRenderer, map, Resources.YOU_DEFEAT, x + xx, y + yy, mouseX, mouseY, task.isStarted());

                var width = box.getFirst();
                var height = box.getSecond();

                xx += width + boxMargin;
                if (xx + width > maxWidth) {
                    // Move to next row
                    xx = 0;
                    yy += height + boxMargin;
                }
            }
        }

        return Pair.of(xx, yy);
    }
}
