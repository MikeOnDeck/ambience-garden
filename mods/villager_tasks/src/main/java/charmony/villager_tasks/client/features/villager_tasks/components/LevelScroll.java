package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.villager_tasks.common.features.villager_tasks.Resources;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record LevelScroll(Font font, int level, boolean isActive) {
    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        ResourceLocation texture;

        if (isActive) {
            texture = Resources.ACTIVE_LEVELS.getOrDefault(level, Resources.ACTIVE_LEVELS.get(1));
        } else {
            texture = Resources.AVAILABLE_LEVELS.getOrDefault(level, Resources.AVAILABLE_LEVELS.get(1));
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width(), height());
        if (mouseX > x && mouseX < x + width() && mouseY > y && mouseY < y + height()) {
            renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var components = List.of(
            Component.translatable("gui.charmony.villager_tasks.level_info", level).getVisualOrderText(),
            Component.translatable("gui.charmony.villager_tasks.level_info.level" + level).getVisualOrderText()
        );
        guiGraphics.setTooltipForNextFrame(font, components, mouseX, mouseY);
    }

    public int width() {
        return 16;
    }

    public int height() {
        return 16;
    }
}