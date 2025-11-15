package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.core.client.MobSpriteRenderer;
import charmony.villager_tasks.client.features.villager_tasks.component.AspectBoxBuilder;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class HuntRenderer extends BaseRenderer {
    public HuntRenderer(Task task) {
        super(task);
    }

    @Override
    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;
        var maxShown = 3;
        var rowHeight = 17;
        var margin = 11;

        var mobs = task.hunt.mobs();
        var rows = Math.min(maxShown, mobs.size());
        var showEllipsis = mobs.size() > maxShown;

        if (!mobs.isEmpty()) {
            guiGraphics.drawString(font, Resources.HUNT_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
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
        var hunt = task.hunt;

        if (!hunt.isEmpty()) {
            var boxMargin = 3;

            for (var i = 0; i < hunt.mobs().size(); i++) {
                var mob = hunt.mobs().get(i);
                var spriteRenderer = new MobSpriteRenderer(mob.mob());

                List<Component> tooltips = List.of(
                    Resources.YOU_MUST_HUNT,
                    nameAndTotal(spriteRenderer.getName(), mob.total())
                );

                var box = new AspectBoxBuilder()
                    .withSpriteRenderer(spriteRenderer)
                    .withTooltipText(tooltips)
                    .withRequirement(mob);

                box.render(guiGraphics, font, x + xx, y + yy, mouseX, mouseY);
                var width = box.width();
                var height = box.height();

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
