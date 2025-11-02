package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;

public final class RewardsRenderer extends BaseRenderer {
    private Color fillColor;

    public RewardsRenderer(Task task) {
        super(task);
    }

    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;

        var xp = task.rewards.experience;
        var items = task.rewards.items;
        var rows = items.size();

        if (xp > 0 || !items.isEmpty()) {
            guiGraphics.drawString(font, Resources.REWARD_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
            calcHeight += 10;

            // Items
            for (var i = 0; i < Math.min(3, items.size()); i++) {
                var item = items.get(i);
                calcWidth = renderItemTooltip(guiGraphics, item.stack(), Component.literal("" + item.total()), x, y + calcHeight + (i * 15));
            }

            // XP
            if (xp > 0) {
                var component = Component.translatable("gui.charmony.villager_tasks.experience_levels", task.rewards.experience);
                calcWidth = renderItemTooltip(guiGraphics, new ItemStack(Items.EXPERIENCE_BOTTLE), component, x, y + calcHeight + (rows * 15), false);
                rows += 1;
            }

            calcHeight += (rows * 15) + 15;
        }

        return Pair.of(calcWidth, calcHeight);
    }

    public void renderPanel(GuiGraphics guiGraphics, int x, int y, int maxWidth, int mouseX, int mouseY) {
        var rewards = task.rewards;
        if (rewards.isEmpty()) {
            return;
        }

        var xx = 0;
        var yy = 0;
        var boxMargin = 3;

        // Reward XP
        if (rewards.experience > 0) {
            fillColor = new Color(0x40b0b0);

            var stack = new ItemStack(Items.EXPERIENCE_BOTTLE);
            var component = Component.literal("" + rewards.experience);
            var tooltip = new ArrayList<Component>();
            tooltip.add(Resources.YOU_RECEIVE);
            tooltip.add(Component.translatable("gui.charmony.villager_tasks.experience_levels", rewards.experience));

            var box = renderCustomItemBox(guiGraphics, stack, component, tooltip, x + xx, y + yy, mouseX, mouseY);
            xx += box.getFirst() + boxMargin;
        }

        // Reward items
        if (!rewards.items.isEmpty()) {
            fillColor = new Color(0x4090c0);

            for (var i = 0; i < rewards.items.size(); i++) {
                var item = rewards.items.get(i);
                var stack = item.stack();
                var total = "" + item.total();
                var component = Component.literal(total);

                // Reconstruct the item tooltip
                var itemTooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
                var tooltip = new ArrayList<Component>();
                tooltip.add(Resources.YOU_RECEIVE);
                tooltip.add(Component.translatable("gui.charmony.villager_tasks.name_and_number", itemTooltip.getFirst(), total));
                tooltip.addAll(itemTooltip.subList(1, itemTooltip.size()));

                var box = renderCustomItemBox(guiGraphics, stack, component, tooltip, x + xx, y + yy, mouseX, mouseY);
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
    }

    @Override
    public Color fillColor() {
        return fillColor;
    }
}
