package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.rewards.RewardItem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class RewardsRenderer extends BaseRenderer {
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
            var box = renderXpBox(guiGraphics, rewards.experience, x + xx, y + yy, mouseX, mouseY);
            xx += box.getFirst() + boxMargin;
        }

        // Reward items
        for (var i = 0; i < rewards.items.size(); i++) {
            var item = rewards.items.get(i);
            var box = renderItemBox(guiGraphics, item, x + xx, y + yy, mouseX, mouseY);

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

    public Pair<Integer, Integer> renderItemBox(GuiGraphics guiGraphics, RewardItem rewardItem, int x, int y, int mouseX, int mouseY) {
        var fillColor = new Color(0x4090c0);
        var text = "" + rewardItem.total();
        var box = renderRequirementBox(guiGraphics, Component.literal(text), x, y, fillColor);
        var width = box.getFirst();
        var height = box.getSecond();

        // Item x and y
        var ix = x + 3;
        var iy = y + 1;

        renderItemStack(guiGraphics, rewardItem.stack(), List.of(), ix, iy, mouseX, mouseY);

        // Text x and y
        var tx = ix + 19;
        var ty = iy + 5;

        guiGraphics.drawString(font, text, tx, ty, DEFAULT_TEXT_COLOR.getArgbColor());

        var itemTooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), rewardItem.stack());
        var tooltip = new ArrayList<Component>();
        tooltip.add(Resources.YOU_RECEIVE);
        tooltip.add(Component.translatable("gui.charmony.villager_tasks.name_and_number", itemTooltip.getFirst(), text));

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }

        return box;
    }

    public Pair<Integer, Integer> renderXpBox(GuiGraphics guiGraphics, int experience, int x, int y, int mouseX, int mouseY) {
        var fillColor = new Color(0x40b0b0);
        var text = "" + experience;
        var box = renderRequirementBox(guiGraphics, Component.literal(text), x, y, fillColor);
        var width = box.getFirst();
        var height = box.getSecond();

        // Item x and y
        var ix = x + 2;
        var iy = y + 1;

        renderItemStack(guiGraphics, new ItemStack(Items.EXPERIENCE_BOTTLE), List.of(), ix, iy, mouseX, mouseY);

        // Text x and y
        var tx = ix + 19;
        var ty = iy + 5;

        guiGraphics.drawString(font, text, tx, ty, DEFAULT_TEXT_COLOR.getArgbColor(), false);

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Resources.YOU_RECEIVE);
        tooltip.add(Component.translatable("gui.charmony.villager_tasks.experience_levels", experience));

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }

        return box;
    }
}
