package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.api.core.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

@Deprecated
public record ProgressBox(double remaining, double total) {

    public void render(GuiGraphics guiGraphics, int left, int top, int width, int height, int mouseX, int mouseY) {
        var bgColor = new Color(0x000000);
        var progressColor = new Color(0xffd000);
        var completeColor = new Color(0x00ff00);

        var x0 = left;
        var y0 = top;
        var x1 = left + width;
        var y1 = top + height;

        guiGraphics.fill(RenderPipelines.GUI, x0, y0, x1, y1, bgColor.getArgbColor());

        var completed = (int)(total - remaining);
        var percent = (completed/total) * 100d;
        var completion = remaining == 0 ? width : (int)(((double)width / 100d) * percent);
        var lineColor = remaining == 0 ? completeColor : progressColor;

        guiGraphics.fill(RenderPipelines.GUI, left, top, left + completion, top + 2, lineColor.getArgbColor());

        if (mouseX >= x0 && mouseX <= x1 && mouseY >= y0 && mouseY <= y1) {
            var component = Component.translatable("gui.charmony.villager_tasks.requirements_remaining", completed, (int)total);
            guiGraphics.setTooltipForNextFrame(component, mouseX, mouseY);
        }
    }
}
