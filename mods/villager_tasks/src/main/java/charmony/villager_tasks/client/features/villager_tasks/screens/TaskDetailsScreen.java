package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.renderers.TaskRenderer;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nullable;

public class TaskDetailsScreen extends BaseScreen {
    private final @Nullable Screen parent;
    private final Task task;
    private final TaskRenderer taskRenderer;

    public TaskDetailsScreen(Task task) {
        this(task, null);
    }

    public TaskDetailsScreen(Task task, @Nullable Screen parent) {
        super(task.getTitle());
        this.task = task;
        this.parent = parent;
        this.taskRenderer = new TaskRenderer(task);
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null) return;

        if (parent != null) {
            var backToTask = new Buttons.BackToTaskButton(midX - (Buttons.BackToTaskButton.WIDTH / 2), midY + 94,
                b -> minecraft.setScreen(parent));

            addRenderableWidget(backToTask);
        } else {
            addCloseButton();
        }

    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var reqs = task.requirements();
        var rewards = task.rewards();

        var top = midY - 69;
        var left = midX - 138;
        var right = midX + 138;

        if (reqs.isEmpty()) {
            // Can't render task with no requirements - just show missingno and return
            guiGraphics.drawString(font, Resources.MISSINGNO, left, top + 5, titleColor.getArgbColor(), false);
            return;
        }

        // Title for requirements section
        guiGraphics.drawString(font, Resources.REQUIREMENTS, left, top + 5, titleColor.getArgbColor(), false);

        top += 14;
        taskRenderer.renderIndentedBox(guiGraphics, left, right, top, top + 70, fillColor);

        int reqPanelX;
        int reqPanelY = top + 5;
        int reqPanelMaxWidth;

        if (reqs.size() == 1) {
            // render layout for single objective
            reqPanelMaxWidth = 240;
            reqPanelX = left + 5;
        } else {
            // render layout for multiple objectives
            reqPanelMaxWidth = 180;
            reqPanelX = left + 60;
        }

        taskRenderer.collect.renderPanel(guiGraphics, reqPanelX, reqPanelY, reqPanelMaxWidth, mouseX, mouseY);

        top += 70;
        if (rewards.isEmpty()) {
            // Sadface.
            guiGraphics.drawString(font, Resources.MISSINGNO, left, top + 5, titleColor.getArgbColor(), false);
            return;
        }

        // Title for rewards section
        guiGraphics.drawString(font, Resources.REWARDS, left, top + 5, titleColor.getArgbColor(), false);

        top += 14;
        taskRenderer.renderIndentedBox(guiGraphics, left, right, top, top + 49, fillColor);

        var rewardsPanelX = left + 5;
        var rewardsPanelY = top + 5;
        var rewardsPanelMaxWidth = 240;

        taskRenderer.rewards.renderPanel(guiGraphics, rewardsPanelX, rewardsPanelY, rewardsPanelMaxWidth, mouseX, mouseY);
    }
}
