package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public abstract class BaseRenderer {
    protected final Font font;
    protected final Handlers handlers;
    protected Task task;

    public BaseRenderer(Task task) {
        this.task = task;
        this.font = Minecraft.getInstance().font;
        this.handlers = VillagerTasks.feature().handlers;
    }

    public Task task() {
        return task;
    }

    public void updateTask(Task task) {
        this.task = task;
    }

    public int renderTooltipItem(GuiGraphics guiGraphics, ItemStack stack, Component component, int x, int y) {
        return renderTooltipItem(guiGraphics, stack, component, x, y, true);
    }

    public int renderTooltipItem(GuiGraphics guiGraphics, ItemStack stack, Component component, int x, int y, boolean showItemName) {
        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;
        var itemTooltip = Screen.getTooltipFromItem(minecraft, stack);

        if (showItemName) {
            var itemName = itemTooltip.getFirst();
            component = Component.translatable("gui.charmony.villager_tasks.name_and_number", itemName, component);
        }

        guiGraphics.renderFakeItem(stack, x, y);
        guiGraphics.drawString(font, component, x + 20, y + 4, new Color(0xffffff).getArgbColor(), false);

        return font.width(component) + 24;
    }
}
