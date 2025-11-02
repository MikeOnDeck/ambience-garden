package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.core.client.MobSpriteRenderer;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.requirements.HuntMob;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

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

    public Pair<Integer, Integer> renderPanel(GuiGraphics guiGraphics, int x, int y, int xx, int yy, int maxWidth, int mouseX, int mouseY) {
        var hunt = task.hunt;
        if (hunt.isEmpty()) {
            return Pair.of(0, 0);
        }

        var boxMargin = 3;

        for (var i = 0; i < hunt.mobs().size(); i++) {
            var mob = hunt.mobs().get(i);
            var box = renderSpriteBox(guiGraphics, mob, x + xx, y + yy, mouseX, mouseY, task.isStarted());

            var width = box.getFirst();
            var height = box.getSecond();

            xx += width + boxMargin;
            if (xx + width > maxWidth) {
                // Move to next row
                xx = 0;
                yy += height + boxMargin;
            }
        }

        return Pair.of(xx, yy);
    }

    public Pair<Integer, Integer> renderSpriteBox(GuiGraphics guiGraphics, HuntMob huntMob, int x, int y, int mouseX, int mouseY, boolean showProgress) {
        var textColor = textColor(huntMob);
        var fillColor = fillColor(huntMob);
        var text = showProgress ? (huntMob.total() - huntMob.remaining()) + "/" + huntMob.total() : "" + huntMob.total();
        var box = renderRequirementBox(guiGraphics, Component.literal(text), x, y, fillColor);
        var width = box.getFirst();
        var height = box.getSecond();

        // Sprite x and y
        var sx = x + 2;
        var sy = y + 2;

        var spriteRenderer = new MobSpriteRenderer(huntMob.mob());
        spriteRenderer.render(guiGraphics, sx, sy);

        // Text x and y
        var tx = sx + 20;
        var ty = sy + 4;

        guiGraphics.drawString(font, text, tx, ty, textColor.getArgbColor());

        // Tooltip on sprite box hover
        var name = spriteRenderer.getName();
        var tooltip = new ArrayList<Component>();
        tooltip.add(huntMob.isSatisfied() ? Resources.YOU_HUNTED : Resources.YOU_HUNT);
        tooltip.add(Component.translatable("gui.charmony.villager_tasks.name_and_number", name, huntMob.total()));

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }

        return box;
    }
}
