package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.core.client.renderers.SpriteRenderer;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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

    public void renderEllipsisInTooltip(GuiGraphics guiGraphics, int extraCount, int x, int y) {
        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;
        var ellipsis = Component.translatable("gui.charmony.villager_tasks.ellipsis", extraCount);

        guiGraphics.drawString(font, ellipsis, x + 1, y + 4, new Color(0x909090).getArgbColor(), false);
    }

    public int renderItemInTooltip(GuiGraphics guiGraphics, ItemStack stack, Component component, int x, int y) {
        return renderItemInTooltip(guiGraphics, stack, component, x, y, true);
    }

    public int renderItemInTooltip(GuiGraphics guiGraphics, ItemStack stack, Component component, int x, int y, boolean showItemName) {
        var minecraft = Minecraft.getInstance();
        var itemTooltip = Screen.getTooltipFromItem(minecraft, stack);

        if (showItemName) {
            var name = itemTooltip.getFirst();
            component = Component.translatable("gui.charmony.villager_tasks.name_and_number", name, component);
        }

        guiGraphics.renderFakeItem(stack, x, y);
        guiGraphics.drawString(font, component, x + 20, y + 4, new Color(0xffffff).getArgbColor(), false);

        return font.width(component) + 24;
    }

    public int renderItemAndSpriteInTooltip(GuiGraphics guiGraphics, ItemStack stack, SpriteRenderer spriteRenderer, Component component, int x, int y, boolean showItemName) {
        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;
        var itemTooltip = Screen.getTooltipFromItem(minecraft, stack);

        if (showItemName) {
            var name = itemTooltip.getFirst();
            component = Component.translatable("gui.charmony.villager_tasks.name_and_number", name, component);
        }

        guiGraphics.renderFakeItem(stack, x, y);
        spriteRenderer.render(guiGraphics, x + 18, y - 1);
        guiGraphics.drawString(font, component, x + 40, y + 4, new Color(0xffffff).getArgbColor(), false);

        return font.width(component) + 44;
    }

    public int renderSpriteInTooltip(GuiGraphics guiGraphics, SpriteRenderer spriteRenderer, Component component, int x, int y) {
        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;

        var name = spriteRenderer.getName();
        component = Component.translatable("gui.charmony.villager_tasks.name_and_number", name, component);

        spriteRenderer.render(guiGraphics, x, y);
        guiGraphics.drawString(font, component, x + 20, y + 4, new Color(0xffffff).getArgbColor(), false);

        return font.width(component) + 24;
    }

    public Pair<Integer, Integer> renderPanel(GuiGraphics guiGraphics, int x, int y, int xx, int yy, int maxWidth, int mouseX, int mouseY) {
        return Pair.of(0, 0); // Hook
    }

    public Pair<Integer, Integer> renderTaskHoverTooltip(GuiGraphics guiGraphics, int x, int y) {
        return Pair.of(0, 0);
    }

    public List<Component> itemTooltip(ItemStack stack) {
        return Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
    }

    public Component itemNameFromTooltip(ItemStack stack) {
        return itemTooltip(stack).getFirst();
    }

    public Component nameAndTotal(Component name, String total) {
        return Component.translatable("gui.charmony.villager_tasks.name_and_number", name, total);
    }

    public Component nameAndTotal(Component name, int total) {
        return nameAndTotal(name, "" + total);
    }
}
