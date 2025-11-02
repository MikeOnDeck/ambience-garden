package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.client.features.villager_tasks.tooltips.BaseTooltip;
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

public final class TaskRenderer extends BaseRenderer {
    public static final int SCROLL_WIDTH = 16;
    public static final int SCROLL_HEIGHT = 16;

    private final Font font;

    public final CollectRenderer collect;
    public final HuntRenderer hunt;
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
        this.hunt = new HuntRenderer(task);
        this.rewards = new RewardsRenderer(task);
        this.task = task;
        this.font = Minecraft.getInstance().font;

        this.titleColor = new Color(0x454545);
        this.textColor = new Color(0x202020);
        this.epicTextColor = new Color(0x604514);
        this.completeTextColor = new Color(0x004000);
        this.fillColor = new Color(0x909090);
        this.epicFillColor = new Color(0xb09660);
        this.completeFillColor = new Color(0x70a070);

        this.villagerOwnsTask = task.belongsTo(handlers.getLastVillagerInteraction());
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        this.task = task;
        this.collect.updateTask(task);
        this.rewards.updateTask(task);
        this.villagerOwnsTask = task.belongsTo(handlers.getLastVillagerInteraction());
    }

    /**
     * Render a task as a single row.
     * This is common to the Available, Active and Completed tasks screens.
     * Use villagerOwnsTask to adjust colors when rendering tasks for the villager that owns them.
     */
    public void renderSimpleTaskRow(GuiGraphics guiGraphics, int midX, int top, int mouseX, int mouseY, BaseTooltip tooltip) {
        var left = midX - 138;
        var right = midX + 137;

        var isSatisfied = task.isSatisfied();
        var isEpic = task.isEpic();

        var textColor = isSatisfied && villagerOwnsTask ? completeTextColor : this.textColor;
        var fillColor = isSatisfied && villagerOwnsTask ? completeFillColor : this.fillColor;

        // Background behind the task
        var taskHeight = task.isStarted() ? 23 : 21;
        renderIndentedBox(guiGraphics, left, right, top, top + taskHeight, fillColor);

        // Level scroll icon
        var sx = left + 3;
        var sy = top + 3;
        renderScroll(guiGraphics, sx, sy, mouseX, mouseY);

        // Star icon for epic tasks
        if (isEpic) {
            renderStar(guiGraphics, left - 2, top - 2, mouseX, mouseY);
        }

        // Task title label
        var title = MutableComponent.create(task.getTitle().getContents());
        var tx = sx + 22;
        var ty = top + 7;
        guiGraphics.drawString(font, title, tx, ty, textColor.getArgbColor(), false);

        // Mouse over title shows requirements of the task.
        if (mouseX >= tx && mouseX <= right - 44 && mouseY >= top + 1 && mouseY <= top + 20) {
            var titleComponent = Component.literal(title.getString());
            guiGraphics.setTooltipForNextFrame(font, List.of(titleComponent), Optional.of(tooltip), mouseX, mouseY);
        }

        // Progress bar
        if (task.isStarted()) {
            var pWidth = right - left - 1;
            var pHeight = 2;
            var pLeft = left + 1;
            var pTop = top + 21;
            renderProgressBar(guiGraphics, pLeft, pTop, pWidth, pHeight, mouseX, mouseY);
        }
    }

    public void renderStar(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        var texture = Resources.STAR;
        var width = 8;
        var height = 8;
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height);

        if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height) {
            List<Component> components = new ArrayList<>();
            components.add(Resources.EPIC_TASK_TITLE);
            components.add(Resources.EPIC_TASK_DESCRIPTION);
            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }

    public void renderProgressBar(GuiGraphics guiGraphics, int left, int top, int width, int height, int mouseX, int mouseY) {
        var bgColor = new Color(0x000000);
        var progressColor = new Color(0xffd000);
        var completeColor = new Color(0x00ff00);
        var total = task.total();
        var remaining = task.remaining();

        var x0 = left;
        var y0 = top;
        var x1 = left + width;
        var y1 = top + height;

        guiGraphics.fill(RenderPipelines.GUI, x0, y0, x1, y1, bgColor.getArgbColor());

        var completed = total - remaining;
        var percent = ((double) completed /total) * 100d;
        var completion = remaining == 0 ? width : (int)(((double)width / 100d) * percent);
        var lineColor = remaining == 0 ? completeColor : progressColor;

        guiGraphics.fill(RenderPipelines.GUI, left, top, left + completion, top + 2, lineColor.getArgbColor());

        if (mouseX >= x0 && mouseX <= x1 && mouseY >= y0 && mouseY <= y1) {
            var component = Component.translatable("gui.charmony.villager_tasks.requirements_remaining", completed, total);
            guiGraphics.setTooltipForNextFrame(component, mouseX, mouseY);
        }
    }

    public void renderScroll(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        ResourceLocation texture;
        var level = task.level;

        if (task.isStarted()) {
            texture = Resources.ACTIVE_SCROLL_LEVELS.getOrDefault(level, Resources.ACTIVE_SCROLL_LEVELS.get(level));
        } else {
            texture = Resources.AVAILABLE_SCROLL_LEVELS.getOrDefault(level, Resources.AVAILABLE_SCROLL_LEVELS.get(level));
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, SCROLL_WIDTH, SCROLL_HEIGHT);
        if (mouseX > x && mouseX < x + SCROLL_WIDTH && mouseY > y && mouseY < y + SCROLL_HEIGHT) {
            renderScrollTooltip(guiGraphics, mouseX, mouseY, villagerOwnsTask);
        }
    }

    private void renderScrollTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean villagerOwnsTask) {
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
