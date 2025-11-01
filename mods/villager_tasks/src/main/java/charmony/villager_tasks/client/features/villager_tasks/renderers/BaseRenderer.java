package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.core.client.MobSpriteRenderer;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.client.features.villager_tasks.components.IndentedBox;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class BaseRenderer {
    protected static final Color DEFAULT_FILL_COLOR = new Color(0x808080);
    protected static final Color DEFAULT_TEXT_COLOR = new Color(0xffffff);
    protected static final Map<ResourceLocation, MobSpriteRenderer> CACHED_MOB_SPRITE_RENDERERS = new WeakHashMap<>();

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

    public Pair<Integer, Integer> renderRequirementBox(GuiGraphics guiGraphics, Component text, int x, int y, Color fillColor) {
        // Dimensions of box
        var width = font.width(text) + 24;
        var height = 18;
        var alpha = 120;

        var x1 = x + width;
        var y1 = y + height;

        // Draw box outline and background
        var box = new IndentedBox(alpha);
        box.render(guiGraphics, x, x1, y, y1, fillColor);

        return Pair.of(width, height);
    }

    public void renderItemStack(GuiGraphics guiGraphics, ItemStack itemStack, List<Component> customToooltip, int x, int y, int mouseX, int mouseY) {
        guiGraphics.renderFakeItem(itemStack, x, y);
        if (mouseX > x && mouseX < x + 16 - 1 && mouseY > y && mouseY < y + 16 - 1) {
            List<Component> finalTooltip = new ArrayList<>();

            var itemTooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), itemStack);
            if (customToooltip.isEmpty()) {
                finalTooltip.addAll(itemTooltip);
            } else {
                var first = itemTooltip.getFirst();
                finalTooltip.add(first);
                finalTooltip.addAll(customToooltip);
            }

            guiGraphics.setTooltipForNextFrame(font, finalTooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }
    }

    public int renderItemTooltip(GuiGraphics guiGraphics, ItemStack stack, Component component, int x, int y) {
        return renderItemTooltip(guiGraphics, stack, component, x, y, true);
    }

    public int renderItemTooltip(GuiGraphics guiGraphics, ItemStack stack, Component component, int x, int y, boolean showItemName) {
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

    public int renderMobTooltip(GuiGraphics guiGraphics, ResourceLocation mob, Component component, int x, int y) {
        var minecraft = Minecraft.getInstance();
        var font = minecraft.font;
        var spriteRenderer = CACHED_MOB_SPRITE_RENDERERS.computeIfAbsent(mob, r -> new MobSpriteRenderer(mob));

        spriteRenderer.render(guiGraphics, x, y, 16, 16);
        guiGraphics.drawString(font, component, x + 20, y + 4, new Color(0xffffff).getArgbColor(), false);

        return font.width(component) + 24;
    }

    public Color getMissingColor() {
        return new Color(0xb00000);
    }

    public Color getProgressColor() {
        return new Color(0xc09000);
    }

    public Color getCompleteColor() {
        return new Color(0x00a020);
    }
}
