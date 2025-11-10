package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.core.client.renderers.SpriteRenderer;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.client.features.villager_tasks.tooltips.MapTooltip;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseRenderer {
    protected static final Color DEFAULT_FILL_COLOR = new Color(0x808080);
    protected static final Color DEFAULT_TEXT_COLOR = new Color(0xffffff);
    protected static final Color INDENT_COLOR = new Color(0xffffff);
    protected static final Color OUTDENT_COLOR = new Color(0x000000);

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

    public void renderIndentedBox(GuiGraphics guiGraphics, int x0, int x1, int y0, int y1, Color color) {
        renderIndentedBox(guiGraphics, x0, x1, y0, y1, color, 255, false);
    }

    public void renderIndentedBox(GuiGraphics guiGraphics, int x0, int x1, int y0, int y1, Color color, int opacity, boolean overlay) {
        var bgColor = ARGB.color(opacity, color.getArgbColor());

        guiGraphics.hLine(x0, x1 - 1, y0, OUTDENT_COLOR.getArgbColor());
        guiGraphics.vLine(x1, y0, y1 + 1, INDENT_COLOR.getArgbColor());
        guiGraphics.hLine(x0 + 1, x1, y1, INDENT_COLOR.getArgbColor());
        guiGraphics.vLine(x0, y0, y1, OUTDENT_COLOR.getArgbColor());

        int bx0, bx1, by0, by1;
        if (overlay) {
            bx0 = x0;
            bx1 = x1 + 1;
            by0 = y0;
            by1 = y1 + 1;
        } else {
            bx0 = x0 + 1;
            bx1 = x1;
            by0 = y0 + 1;
            by1 = y1;
        }

        guiGraphics.fill(RenderPipelines.GUI, bx0, by0, bx1, by1, bgColor);
    }

    public Pair<Integer, Integer> renderRequirementBox(GuiGraphics guiGraphics, Component text, int x, int y, int width, Color fillColor) {
        width += font.width(text);
        var height = 19;
        var alpha = 120;

        var x1 = x + width;
        var y1 = y + height;

        renderIndentedBox(guiGraphics, x, x1, y, y1, fillColor, alpha, true);

        return Pair.of(width, height);
    }

    public Pair<Integer, Integer> renderItemBox(GuiGraphics guiGraphics, Satisfiable req, ItemStack stack, MutableComponent title, int x, int y, int mouseX, int mouseY, boolean showProgress) {
        var textColor = textColor(req);
        var fillColor = fillColor(req);
        var text = showProgress ? (req.total() - req.remaining()) + "/" + req.total() : "" + req.total();
        var box = renderRequirementBox(guiGraphics, Component.literal(text), x, y, 25, fillColor);
        var width = box.getFirst();
        var height = box.getSecond();

        // Item x and y
        var ix = x + 2;
        var iy = y + 1;

        renderItemStack(guiGraphics, stack, List.of(), ix, iy, mouseX, mouseY);

        // Text x and y
        var tx = ix + 19;
        var ty = iy + 5;

        guiGraphics.drawString(font, text, tx, ty, textColor.getArgbColor());

        // Tooltip on item box hover
        var itemTooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
        var tooltip = new ArrayList<Component>();
        tooltip.add(title);
        tooltip.add(Component.translatable("gui.charmony.villager_tasks.name_and_number", itemTooltip.getFirst(), req.total()));

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }

        return box;
    }

    public Pair<Integer, Integer> renderCustomItemBox(GuiGraphics guiGraphics, ItemStack stack, Component text, List<Component> tooltip, int x, int y, int mouseX, int mouseY) {
        var box = renderRequirementBox(guiGraphics, text, x, y, 25, fillColor());
        var width = box.getFirst();
        var height = box.getSecond();

        // Item x and y
        var ix = x + 2;
        var iy = y + 1;

        renderItemStack(guiGraphics, stack, List.of(), ix, iy, mouseX, mouseY);

        // Text x and y
        var tx = ix + 19;
        var ty = iy + 5;

        guiGraphics.drawString(font, text, tx, ty, textColor().getArgbColor(), false);

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }

        return box;
    }

    public Pair<Integer, Integer> renderSpriteBox(GuiGraphics guiGraphics, Satisfiable req, SpriteRenderer spriteRenderer, MutableComponent title, int x, int y, int mouseX, int mouseY, boolean showProgress) {
        var textColor = textColor(req);
        var fillColor = fillColor(req);
        var text = showProgress ? (req.total() - req.remaining()) + "/" + req.total() : "" + req.total();
        var box = renderRequirementBox(guiGraphics, Component.literal(text), x, y, 25, fillColor);
        var width = box.getFirst();
        var height = box.getSecond();

        // Sprite x and y
        var sx = x + 2;
        var sy = y + 2;

        spriteRenderer.render(guiGraphics, sx, sy);

        // Text x and y
        var tx = sx + 20;
        var ty = sy + 4;

        guiGraphics.drawString(font, text, tx, ty, textColor.getArgbColor());

        // Tooltip on sprite box hover
        var name = spriteRenderer.getName();
        var tooltip = new ArrayList<Component>();
        tooltip.add(title);
        tooltip.add(Component.translatable("gui.charmony.villager_tasks.name_and_number", name, req.total()));

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }

        return box;
    }

    public Pair<Integer, Integer> renderSpriteAndMapBox(GuiGraphics guiGraphics, Satisfiable req, SpriteRenderer spriteRenderer, ItemStack map, MutableComponent title, int x, int y, int mouseX, int mouseY, boolean showProgress) {
        var textColor = textColor(req);
        var fillColor = fillColor(req);
        var text = showProgress ? (req.total() - req.remaining()) + "/" + req.total() : "" + req.total();
        var box = renderRequirementBox(guiGraphics, Component.literal(text), x, y, 46, fillColor);
        var width = box.getFirst();
        var height = box.getSecond();

        // Sprite x and y
        var sx = x + 2;
        var sy = y + 2;

        spriteRenderer.render(guiGraphics, sx, sy);

        // Map item x and y
        var ix = sx + 18;
        var iy = sy - 1;

        renderItemStack(guiGraphics, map, List.of(), ix, iy, mouseX, mouseY);

        // Text x and y
        var tx = ix + 22;
        var ty = iy + 5;

        guiGraphics.drawString(font, text, tx, ty, textColor.getArgbColor());

        // Tooltip on sprite box hover
        var name = spriteRenderer.getName();
        var tooltip = new ArrayList<Component>();
        tooltip.add(title);
        tooltip.add(Component.translatable("gui.charmony.villager_tasks.name_and_number", name, req.total()));

        var mapTooltip = new MapTooltip(map);

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltip, Optional.of(mapTooltip), mouseX, mouseY);
        }

        return box;
    }

    public Pair<Integer, Integer> renderItemAndSpriteBox(GuiGraphics guiGraphics, Satisfiable req, ItemStack stack, SpriteRenderer spriteRenderer, MutableComponent title, int x, int y, int mouseX, int mouseY, boolean showProgress) {
        var textColor = textColor(req);
        var fillColor = fillColor(req);
        var text = showProgress ? (req.total() - req.remaining()) + "/" + req.total() : "" + req.total();
        var box = renderRequirementBox(guiGraphics, Component.literal(text), x, y, 46, fillColor);
        var width = box.getFirst();
        var height = box.getSecond();

        // Item x and y
        var ix = x + 2;
        var iy = y + 2;

        renderItemStack(guiGraphics, stack, List.of(), ix, iy, mouseX, mouseY);

        // Sprite x and y
        var sx = ix + 18;
        var sy = iy - 1;

        spriteRenderer.render(guiGraphics, sx, sy);

        // Text x and y
        var tx = sx + 22;
        var ty = sy + 5;

        guiGraphics.drawString(font, text, tx, ty, textColor.getArgbColor());

        // Tooltip on box hover
        var itemTooltip = Screen.getTooltipFromItem(Minecraft.getInstance(), stack);
        var tooltip = new ArrayList<Component>();
        tooltip.add(title);
        tooltip.add(Component.translatable("gui.charmony.villager_tasks.name_and_number", itemTooltip.getFirst(), text));
        tooltip.add(spriteRenderer.getDescription());

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }

        return box;
    }

    public void renderItemStack(GuiGraphics guiGraphics, ItemStack itemStack, List<Component> tooltip, int x, int y, int mouseX, int mouseY) {
        guiGraphics.renderFakeItem(itemStack, x, y);
        if (!tooltip.isEmpty() && mouseX > x && mouseX < x + 16 - 1 && mouseY > y && mouseY < y + 16 - 1) {
            guiGraphics.setTooltipForNextFrame(font, tooltip.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }
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

    public Color fillColor() {
        return DEFAULT_FILL_COLOR;
    }

    public Color fillColor(Satisfiable item) {
        var satisfied = item.isSatisfied();
        var none = item.remaining() == item.total();
        var some = item.remaining() < item.total() && !satisfied;

        if (satisfied) {
            return getCompleteColor();
        } else if (some) {
            return getProgressColor();
        } else if (none) {
            return getMissingColor();
        } else {
            return DEFAULT_FILL_COLOR;
        }
    }

    public Color textColor() {
        return DEFAULT_TEXT_COLOR;
    }

    public Color textColor(Satisfiable item) {
        return DEFAULT_TEXT_COLOR;
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
