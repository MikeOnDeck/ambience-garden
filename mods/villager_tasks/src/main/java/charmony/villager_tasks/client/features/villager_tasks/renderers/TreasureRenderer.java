package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.core.client.LootTableSpriteRenderer;
import charmony.villager_tasks.client.features.villager_tasks.component.AspectBoxBuilder;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class TreasureRenderer extends BaseRenderer {
    public TreasureRenderer(Task task) {
        super(task);
    }

    @Override
    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;
        var maxShown = 2;
        var rowHeight = 17;
        var margin = 11;

        var items = task.treasure.items();
        var rows = Math.min(maxShown, items.size());
        var showEllipsis = items.size() > maxShown;

        if (!items.isEmpty()) {
            guiGraphics.drawString(font, Resources.TREASURE_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
            calcHeight += margin;

            for (var i = 0; i < rows; i++) {
                var item = items.get(i);
                var spriteRenderer = new LootTableSpriteRenderer(item.lootTable());
                calcWidth = Math.max(calcWidth, renderItemAndSpriteInTooltip(guiGraphics, item.stack(), spriteRenderer,
                    Component.literal("" + item.total()), x, y + calcHeight + (i * rowHeight), true));
            }

            if (showEllipsis) {
                renderEllipsisInTooltip(guiGraphics, items.size() - maxShown, x, y + calcHeight + (rows * rowHeight));
                rows += 1;
            }

            calcHeight += (rows * rowHeight) + margin;
        }

        return Pair.of(calcWidth, calcHeight);
    }

    public Pair<Integer, Integer> renderPanel(GuiGraphics guiGraphics, int x, int y, int xx, int yy, int maxWidth, int mouseX, int mouseY) {
        var treasure = task.treasure;

        if (!treasure.isEmpty()) {
            var boxMargin = 3;

            for (var i = 0; i < treasure.items().size(); i++) {
                var item = treasure.items().get(i);
                var spriteRenderer = new LootTableSpriteRenderer(item.lootTable());
                var description = Component.translatable("gui.charmony.villager_tasks.in_loot", spriteRenderer.getName()).withStyle(ChatFormatting.GRAY);

                List<Component> tooltips = List.of(
                    Resources.YOU_MUST_DISCOVER,
                    nameAndTotal(itemNameFromTooltip(item.stack()), item.total()),
                    description
                );

                var box = new AspectBoxBuilder()
                    .withItemStack(item.stack())
                    .withSpriteRenderer(spriteRenderer)
                    .withTooltipText(tooltips)
                    .withRequirement(item);

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
