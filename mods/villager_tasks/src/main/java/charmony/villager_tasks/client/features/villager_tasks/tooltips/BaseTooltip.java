package charmony.villager_tasks.client.features.villager_tasks.tooltips;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public abstract class BaseTooltip implements ClientTooltipComponent, TooltipComponent {
    protected final Minecraft minecraft;
    protected final Font font;
    protected int height;
    protected int width;

    public BaseTooltip() {
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
}
