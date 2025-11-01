package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AspectBox {
    public static final Color DEFAULT_FILL_COLOR = new Color(0x808080);
    public static final Color DEFAULT_TEXT_COLOR = new Color(0xffffff);

    protected final Minecraft minecraft;

    public AspectBox() {
        this.minecraft = Minecraft.getInstance();
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        // Dimensions of box
        var x1 = x + width();
        var y1 = y + height();

        // Item x and y
        var ix = x + 2;
        var iy = y + 1;

        // Text x and y
        var tx = ix + 18;
        var ty = iy + 5;

        // Draw box outline and background
        var box = new IndentedBox(fillAlpha());
        box.render(guiGraphics, x, x1, y, y1, fillColor());

        var itemStack = itemStack().orElse(null);
        var mob = mob().orElse(null);
        var text = text();

        if (itemStack != null) {
            renderItemStack(guiGraphics, itemStack, ix, iy, mouseX, mouseY);
        }

        if (mob != null) {

        }

        if (!text.isEmpty()) {
            renderText(guiGraphics, text, tx, ty, mouseX, mouseY);
        }
    }


    public int width() {
        int width = 0;
        width += font().width(text());
        width += itemStack().isPresent() ? 16 : 0;
        width += mob().isPresent() ? 16 : 0;
        return width + 7; // padding
    }

    public int height() {
        return 18;
    }

    public int fillAlpha() {
        return 120;
    }

    public String text() {
        return "";
    }

    public Optional<ItemStack> itemStack() {
        return Optional.empty();
    }

    public Optional<ResourceLocation> mob() {
        return Optional.empty();
    }

    public Font font() {
        return minecraft.font;
    }

    public Color fillColor() {
        return DEFAULT_FILL_COLOR;
    }

    public Color textColor() {
        return DEFAULT_TEXT_COLOR;
    }

    private void renderText(GuiGraphics guiGraphics, String text, int x, int y, int mouseX, int mouseY) {
        guiGraphics.drawString(font(), text, x, y, textColor().getArgbColor());
    }

    private void renderItemStack(GuiGraphics guiGraphics, ItemStack itemStack, int x, int y, int mouseX, int mouseY) {
        guiGraphics.renderFakeItem(itemStack, x, y);
        if (mouseX > x && mouseX < x + width() - 1 && mouseY > y && mouseY < y + height() - 1) {
            renderItemStackTooltip(guiGraphics, itemStack, mouseX, mouseY);
        }
    }

    private void renderMob(GuiGraphics guiGraphics, ResourceLocation mob, int x, int y, int mouseX, int mouseY) {

    }

    protected void renderItemStackTooltip(GuiGraphics guiGraphics, ItemStack itemStack, int mouseX, int mouseY) {
        var stackTooltip = new ArrayList<>(Screen.getTooltipFromItem(minecraft, itemStack));
        modifyItemStackTooltip(stackTooltip);
        var tooltips = stackTooltip.stream().map(Component::getVisualOrderText).toList();
        guiGraphics.setTooltipForNextFrame(font(), tooltips, mouseX, mouseY);
    }

    protected void modifyItemStackTooltip(List<Component> tooltips) {
        // Override to modify the tooltip shown for the itemstack.
    }
}