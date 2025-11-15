package charmony.villager_tasks.client.features.villager_tasks.component;

import charmony.api.core.Color;
import charmony.core.client.renderers.SpriteRenderer;
import charmony.villager_tasks.common.features.villager_tasks.interfaces.Satisfiable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AspectBoxBuilder {
    private static final Color DEFAULT_FILL_COLOR = new Color(0x808080);
    private static final Color DEFAULT_TEXT_COLOR = new Color(0xffffff);

    private Component text;
    private ItemStack stack;
    private SpriteRenderer spriteRenderer;
    private GraphicOrder graphicOrder = GraphicOrder.ITEM_STACK_FIRST;
    private List<Component> tooltipText = new ArrayList<>();
    private Satisfiable requirement;
    private boolean showProgress = true;
    private Color textColor;
    private Color fillColor;
    private int width;
    private int height;

    public AspectBoxBuilder withText(Component text) {
        this.text = text;
        return this;
    }

    public AspectBoxBuilder withText(String text) {
        this.text = Component.literal(text);
        return this;
    }

    public AspectBoxBuilder withText(int number) {
        this.text = Component.literal("" + number);
        return this;
    }

    public AspectBoxBuilder withFillColor(Color color) {
        this.fillColor = color;
        return this;
    }

    public AspectBoxBuilder withTextColor(Color color) {
        this.textColor = color;
        return this;
    }

    public AspectBoxBuilder showProgress(boolean flag) {
        this.showProgress = flag;
        return this;
    }

    public AspectBoxBuilder withItemStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public AspectBoxBuilder withSpriteRenderer(SpriteRenderer spriteRenderer) {
        this.spriteRenderer = spriteRenderer;
        return this;
    }

    public AspectBoxBuilder withGraphicOrder(GraphicOrder graphicOrder) {
        this.graphicOrder = graphicOrder;
        return this;
    }

    public AspectBoxBuilder withTooltipText(List<Component> components) {
        this.tooltipText = components;
        return this;
    }

    public AspectBoxBuilder withRequirement(Satisfiable req) {
        this.requirement = req;
        return this;
    }

    public void render(GuiGraphics guiGraphics, Font font, int x, int y, int mouseX, int mouseY) {
        var textColor = textColor();
        var fillColor = fillColor();

        var hasStackAndSprite = stack != null && spriteRenderer != null;
        var hasOneGraphic = stack != null || spriteRenderer != null;

        Component text;
        if (this.text != null) {
            text = this.text;
        } else {
            text = Component.literal(showProgress ? (requirement.total() - requirement.remaining()) + "/" + requirement.total() : "" + requirement.total());
        }

        var initialWidth = hasStackAndSprite ? 42 : hasOneGraphic ? 26 : 7;
        this.width = initialWidth + font.width(text);
        this.height = 19;

        new IndentedBoxBuilder()
            .withDimensions(width, height)
            .withOpacity(0.5d)
            .withOverlay(true)
            .withColor(fillColor)
            .render(guiGraphics, x, y);

        int tx;
        int ty = y + 6;

        if (hasStackAndSprite) {
            if (graphicOrder == GraphicOrder.ITEM_STACK_FIRST) {
                guiGraphics.renderItem(stack, x + 2, y + 2);
                spriteRenderer.render(guiGraphics, x + 19, y + 2, 16, 16);
            } else {
                spriteRenderer.render(guiGraphics, x + 2, y + 2, 16, 16);
                guiGraphics.renderItem(stack, x + 19, y + 3);
            }
            tx = x + 39;
        } else if (stack != null) {
            guiGraphics.renderItem(stack, x + 2, y + 2);
            tx = x + 22;
        } else if (spriteRenderer != null) {
            spriteRenderer.render(guiGraphics, x + 2, y + 2, 16, 16);
            tx = x + 22;
        } else {
            tx = 0;
        }

        guiGraphics.drawString(font, text, tx, ty, textColor.getArgbColor());

        if (!tooltipText.isEmpty() && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            guiGraphics.setTooltipForNextFrame(font, tooltipText.stream().map(Component::getVisualOrderText).toList(), mouseX, mouseY);
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean showProgress() {
        return showProgress;
    }

    public Color fillColor() {
        if (fillColor != null) {
            return fillColor;
        }

        var satisfied = requirement.isSatisfied();
        var none = requirement.remaining() == requirement.total();
        var some = requirement.remaining() < requirement.total() && !satisfied;

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
        if (textColor != null) {
            return textColor;
        }
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

    public enum GraphicOrder {
        ITEM_STACK_FIRST,
        SPRITE_FIRST;
    }
}
