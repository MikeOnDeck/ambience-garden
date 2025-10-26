package charmony.villager_tasks.client.features.villager_tasks.components;

import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record LevelScroll(Task task, Font font) {
    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        ResourceLocation texture;
        var level = task.level;

        if (task.isStarted()) {
            texture = Resources.ACTIVE_LEVELS.getOrDefault(level, Resources.ACTIVE_LEVELS.get(level));
        } else {
            texture = Resources.AVAILABLE_LEVELS.getOrDefault(level, Resources.AVAILABLE_LEVELS.get(level));
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width(), height());
        if (mouseX > x && mouseX < x + width() && mouseY > y && mouseY < y + height()) {
            renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<Component> components = new ArrayList<>();
        components.add(Component.translatable("gui.charmony.villager_tasks.level_info", task.level));

        if (task.isNotStarted()) {
            components.add(Component.translatable("gui.charmony.villager_tasks.level_info.level" + task.level));
        } else if (task.isSatisfied()) {
            components.add(Resources.DONE_TASK);
        } else if (task.isStarted()) {
            components.add(Resources.DOING_TASK);
        }

        guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
    }

    public int width() {
        return 16;
    }

    public int height() {
        return 16;
    }
}