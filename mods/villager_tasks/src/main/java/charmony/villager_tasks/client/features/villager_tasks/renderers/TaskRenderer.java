package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.client.features.villager_tasks.components.IndentedBox;
import charmony.villager_tasks.client.features.villager_tasks.components.ProgressBox;
import charmony.villager_tasks.client.features.villager_tasks.components.TaskTooltip;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskRenderer extends BaseRenderer {
    public static final int SCROLL_WIDTH = 16;
    public static final int SCROLL_HEIGHT = 16;

    protected final Font font;

    public final CollectRenderer collect;
    public final RewardsRenderer rewards;

    public Color titleColor;
    public Color textColor;
    public Color epicTextColor;
    public Color completeTextColor;
    public Color fillColor;
    public Color epicFillColor;
    public Color completeFillColor;

    public boolean villagerOwnsTask;

    public TaskRenderer(Task task) {
        super(task);
        this.collect = new CollectRenderer(task);
        this.rewards = new RewardsRenderer(task);
        this.task = task;
        this.font = Minecraft.getInstance().font;

        this.titleColor = new Color(0x454545);
        this.textColor = new Color(0x202020);
        this.epicTextColor = new Color(0x606020);
        this.completeTextColor = new Color(0x004000);
        this.fillColor = new Color(0x909090);
        this.epicFillColor = new Color(0xa0a060);
        this.completeFillColor = new Color(0x70a070);

        this.villagerOwnsTask = task.villager.equals(handlers.getLastVillagerInteraction());
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        this.task = task;
        this.collect.updateTask(task);
        this.rewards.updateTask(task);
        this.villagerOwnsTask = task.villager.equals(handlers.getLastVillagerInteraction());
    }

    /**
     * Render a task as a single row.
     * This is common to the Available, Active and Completed tasks screens.
     * Use villagerOwnsTask to adjust colors when rendering tasks for the villager that owns them.
     */
    public void renderSimpleTaskRow(GuiGraphics guiGraphics, int midX, int top, int mouseX, int mouseY, TaskTooltip tooltip) {
        var left = midX - 138;
        var right = midX + 137;

        var textColor = task.isSatisfied() && villagerOwnsTask ? completeTextColor : (task.isEpic() ? epicTextColor : this.textColor);
        var fillColor = task.isSatisfied() && villagerOwnsTask ? completeFillColor : (task.isEpic() ? epicFillColor : this.fillColor);

        // Background behind the task
        var taskBg = new IndentedBox();
        var taskHeight = task.isStarted() ? 23 : 21;
        taskBg.render(guiGraphics, left, right, top, top + taskHeight, fillColor);

        // Level scroll icon
        var sx = left + 3;
        var sy = top + 3;
        renderScroll(guiGraphics, sx, sy, mouseX, mouseY);

        // Task title label
        var title = MutableComponent.create(task.getTitle().getContents());
        var tx = sx + 22;
        var ty = top + 7;
        guiGraphics.drawString(font, title, tx, ty, textColor.getArgbColor(), false);

        // Mouse over title shows requirements of the task.
        if (mouseX >= tx && mouseX <= right - 44
            && mouseY >= top + 1 && mouseY <= top + 20) {
            var titleComponent = Component.literal(title.getString());
            guiGraphics.setTooltipForNextFrame(font, List.of(titleComponent), Optional.of(tooltip), mouseX, mouseY);
        }

        // Progress bar
        if (task.isStarted()) {
            var pWidth = right - left - 1;
            var pHeight = 2;
            var pLeft = left + 1;
            var pTop = top + 21;
            var progress = new ProgressBox(task.remaining(), task.total());
            progress.render(guiGraphics, pLeft, pTop, pWidth, pHeight, mouseX, mouseY);
        }
    }

    public void renderScroll(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        ResourceLocation texture;
        var level = task.level;

        if (task.isStarted()) {
            texture = Resources.ACTIVE_LEVELS.getOrDefault(level, Resources.ACTIVE_LEVELS.get(level));
        } else {
            texture = Resources.AVAILABLE_LEVELS.getOrDefault(level, Resources.AVAILABLE_LEVELS.get(level));
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, SCROLL_WIDTH, SCROLL_HEIGHT);
        if (mouseX > x && mouseX < x + SCROLL_WIDTH && mouseY > y && mouseY < y + SCROLL_HEIGHT) {
            renderScrollTooltip(guiGraphics, mouseX, mouseY, villagerOwnsTask);
        }
    }

    protected void renderScrollTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean villagerOwnsTask) {
        List<Component> components = new ArrayList<>();
        components.add(Component.translatable("gui.charmony.villager_tasks.level_info", task.level));

        if (task.isNotStarted()) {
            components.add(Component.translatable("gui.charmony.villager_tasks.level_info.level" + task.level));
        } else if (task.isSatisfied() && !villagerOwnsTask) {
            components.add(Resources.DONE_TASK);
        } else if (task.isSatisfied() && villagerOwnsTask) {
            components.add(Resources.DONE_TASK_WITH_LOYALTY);
        } else if (task.isStarted()) {
            components.add(Resources.DOING_TASK);
        }

        guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
    }
}
