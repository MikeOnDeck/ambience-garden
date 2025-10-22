package charmony.villager_tasks.client.features.villager_tasks.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemBox {
    protected final Minecraft minecraft;
    protected final Font font;
    protected final ItemStack stack;
    protected final String text;
    protected final int textColor;
    protected final int fillColor;

    protected boolean drawOutline = true;

    public ItemBox(Font font, ItemStack stack, String text, int textColor, int fillColor) {
        this.minecraft = Minecraft.getInstance();
        this.stack = stack;
        this.text = text;
        this.font = font;
        this.textColor = textColor;
        this.fillColor = fillColor;
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        var lineColor = ARGB.color(Math.min(255, alpha() * 3), fillColor);
        var bgColor = ARGB.color(alpha(), fillColor);

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
        if (drawOutline) {
            guiGraphics.hLine(x, x1, y, lineColor);
            guiGraphics.vLine(x1, y, y1, lineColor);
            guiGraphics.hLine(x, x1, y1, lineColor);
            guiGraphics.vLine(x, y, y1, lineColor);
            guiGraphics.fill(x + 1, y + 1, x1, y1, bgColor);
        } else {
            guiGraphics.fill(x, y, x1 + 1, y1 + 1, bgColor);
        }

        // Render item and tooltip
        guiGraphics.renderFakeItem(stack, ix, iy);
        if (mouseX > ix && mouseX < ix + width() - 1 && mouseY > iy && mouseY < iy + height() - 1) {
            renderTooltip(guiGraphics, mouseX, mouseY);
        }

        // Show the text
        guiGraphics.drawString(font, text, tx, ty, textColor);
    }

    public int width() {
        var textWidth = font.width(text);
        return 16 + textWidth + 7;
    }

    public int height() {
        return 18;
    }

    public int alpha() {
        return 50;
    }

    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var stackTooltip = new ArrayList<>(Screen.getTooltipFromItem(minecraft, stack));
        modifyStackTooltip(stackTooltip);
        var tooltips = stackTooltip.stream().map(Component::getVisualOrderText).toList();
        guiGraphics.setTooltipForNextFrame(font, tooltips, mouseX, mouseY);
    }

    protected void modifyStackTooltip(List<Component> tooltips) {
        // Override to modify the tooltip shown for the item stack.
    }
}