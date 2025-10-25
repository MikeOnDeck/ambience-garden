package charmony.villager_tasks.client.features.villager_tasks;

import charmony.api.core.Color;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

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

    protected int renderItem(GuiGraphics guiGraphics, ItemStack stack, String text, int x, int y) {
        var itemTooltip = Screen.getTooltipFromItem(minecraft, stack);
        var itemName = itemTooltip.getFirst();
        var component = Component.translatable("gui.charmony.villager_tasks.name_and_number", itemName, text);

        guiGraphics.renderFakeItem(stack, x, y);
        guiGraphics.drawString(font, component, x + 20, y + 4, new Color(0xffffff).getArgbColor(), false);

        return font.width(component) + 24;
    }

    protected void startScale(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x * (1 - scale()), y * (1 - scale()));
        guiGraphics.pose().scale(scale());
    }

    protected void stopScale(GuiGraphics guiGraphics) {
        guiGraphics.pose().popMatrix();
    }

    protected void recalculateDimensions(int calcWidth, int calcHeight) {
        this.width = Math.max(this.width, (int)(calcWidth * scale()));
        this.height = Math.max(this.height, (int)(calcHeight * scale()));
    }
}
