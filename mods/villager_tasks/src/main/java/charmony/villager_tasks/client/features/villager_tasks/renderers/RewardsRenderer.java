package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.client.features.villager_tasks.component.AspectBoxBuilder;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class RewardsRenderer extends BaseRenderer {
    public RewardsRenderer(Task task) {
        super(task);
    }

    @Override
    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;
        var maxShown = 3;
        var rowHeight = 16;
        var margin = 10;

        var xp = task.rewards.experience;
        var items = task.rewards.items;
        var rows = Math.min(maxShown, items.size());
        var showEllipsis = items.size() > maxShown;

        if (xp > 0 || !items.isEmpty()) {
            guiGraphics.drawString(font, Resources.REWARD_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
            calcHeight += margin;

            // Items
            for (var i = 0; i < rows; i++) {
                var item = items.get(i);
                calcWidth = Math.max(calcWidth, renderItemInTooltip(guiGraphics, item.stack(), Component.literal("" + item.total()), x, y + calcHeight + (i * rowHeight)));
            }

            if (showEllipsis) {
                renderEllipsisInTooltip(guiGraphics, items.size() - maxShown, x, y + calcHeight + (rows * rowHeight));
                rows += 1;
            }

            // XP
            if (xp > 0) {
                var component = Component.translatable("gui.charmony.villager_tasks.experience_levels", task.rewards.experience);
                calcWidth = Math.max(calcWidth, renderItemInTooltip(guiGraphics, new ItemStack(Items.EXPERIENCE_BOTTLE), component, x, y + calcHeight + (rows * rowHeight), false));
                rows += 1;
            }

            calcHeight += (rows * rowHeight) + margin;
        }

        return Pair.of(calcWidth, calcHeight);
    }

    @Override
    public Pair<Integer, Integer> renderPanel(GuiGraphics guiGraphics, int x, int y, int xx, int yy, int maxWidth, int mouseX, int mouseY) {
        var rewards = task.rewards;
        if (rewards.isEmpty()) {
            return Pair.of(xx, yy);
        }

        var boxMargin = 3;

        // Reward XP
        if (rewards.experience > 0) {
            List<Component> tooltips = List.of(
                Resources.YOU_RECEIVE,
                Component.translatable("gui.charmony.villager_tasks.experience_levels", rewards.experience)
            );

            var box = new AspectBoxBuilder()
                .withText(Component.literal("" + rewards.experience))
                .withItemStack(new ItemStack(Items.EXPERIENCE_BOTTLE))
                .withFillColor(new Color(0x40b0b0))
                .withTooltipText(tooltips);

            box.render(guiGraphics, font, x + xx, y + yy, mouseX, mouseY);
            xx += box.width() + boxMargin;
        }

        // Reward items
        if (!rewards.items.isEmpty()) {
            for (var i = 0; i < rewards.items.size(); i++) {
                var item = rewards.items.get(i);
                var stack = item.stack();

                // Reconstruct the item tooltip
                var itemTooltip = itemTooltip(stack);
                List<Component> tooltip = new ArrayList<>(List.of(
                    Resources.YOU_RECEIVE,
                    nameAndTotal(itemTooltip.getFirst(), item.total())
                ));
                tooltip.addAll(itemTooltip.subList(1, itemTooltip.size()));

                var box = new AspectBoxBuilder()
                    .withText(item.total())
                    .withItemStack(item.stack())
                    .withFillColor(new Color(0x4090c0))
                    .withTooltipText(tooltip);

                box.render(guiGraphics, font, x + xx, y + yy, mouseX, mouseY);
                var width = box.width();
                var height = box.height();

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
