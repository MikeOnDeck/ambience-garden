package charmony.villager_tasks.client.features.villager_tasks;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class TaskTooltip implements ClientTooltipComponent, TooltipComponent {
    protected final Task task;
    protected final Minecraft minecraft;
    protected final Font font;
    protected int height;
    protected int width;

    public TaskTooltip(Task task) {
        this.task = task;
        this.minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
    }

    @Override
    public int getHeight(Font font) {
        return height;
    }

    @Override
    public int getWidth(Font font) {
        return width;
    }

    public float scale() {
        return 0.66f;
    }

    protected int renderItem(GuiGraphics guiGraphics, ItemStack stack, Component component, int x, int y) {
        return renderItem(guiGraphics, stack, component, x, y, true);
    }

    protected int renderItem(GuiGraphics guiGraphics, ItemStack stack, Component component, int x, int y, boolean showItemName) {
        var itemTooltip = Screen.getTooltipFromItem(minecraft, stack);

        if (showItemName) {
            var itemName = itemTooltip.getFirst();
            component = Component.translatable("gui.charmony.villager_tasks.name_and_number", itemName, component);
        }

        guiGraphics.renderFakeItem(stack, x, y);
        guiGraphics.drawString(font, component, x + 20, y + 4, new Color(0xffffff).getArgbColor(), false);

        return font.width(component) + 24;
    }

    protected void startScaling(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x * (1 - scale()), y * (1 - scale()));
        guiGraphics.pose().scale(scale());
    }

    protected void stopScaling(GuiGraphics guiGraphics) {
        guiGraphics.pose().popMatrix();
    }

    protected void recalculateDimensions(int calcWidth, int calcHeight) {
        this.width = Math.max(this.width, (int)(calcWidth * scale()));
        this.height = Math.max(this.height, (int)(calcHeight * scale()));
    }

    protected Pair<Integer, Integer> renderCollect(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;

        guiGraphics.drawString(font, Resources.COLLECT_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
        calcHeight += 10;

        var items = task.collect.items();
        var rows = items.size();

        for (var i = 0; i < Math.min(3, items.size()); i++) {
            var item = items.get(i);
            calcWidth = Math.max(calcWidth, renderItem(guiGraphics, item.stack(), Component.literal("" + item.total()), x, y + calcHeight + (i * 15)));
        }

        calcHeight += (rows * 15) + 10;
        return Pair.of(calcWidth, calcHeight);
    }

    protected Pair<Integer, Integer> renderRewards(GuiGraphics guiGraphics, int x, int y) {
        var calcHeight = 0;
        var calcWidth = 0;

        guiGraphics.drawString(font, Resources.REWARD_ASPECT, x, y + calcHeight, new Color(0xffffff).getArgbColor(), false);
        calcHeight += 10;

        var items = task.rewards.items;
        var rows = items.size();

        // Items
        for (var i = 0; i < Math.min(3, items.size()); i++) {
            var item = items.get(i);
            calcWidth = renderItem(guiGraphics, item.stack(), Component.literal("" + item.total()), x, y + calcHeight + (i * 15));
        }

        // XP
        if (task.rewards.experience > 0) {
            var component = Component.translatable("gui.charmony.villager_tasks.experience_levels", task.rewards.experience);
            calcWidth = renderItem(guiGraphics, new ItemStack(Items.EXPERIENCE_BOTTLE), component, x, y + calcHeight + (rows * 15), false);
            rows += 1;
        }

        calcHeight += (rows * 15) + 10;
        return Pair.of(calcWidth, calcHeight);
    }
}
