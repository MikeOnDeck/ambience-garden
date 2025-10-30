package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.villager_tasks.client.features.villager_tasks.components.CollectItemBox;
import charmony.villager_tasks.client.features.villager_tasks.components.IndentedBox;
import charmony.villager_tasks.client.features.villager_tasks.components.RewardItemBox;
import charmony.villager_tasks.client.features.villager_tasks.components.RewardXpBox;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.gui.GuiGraphics;

public class TaskDetailsScreen extends BaseScreen {
    private final Task task;

    public TaskDetailsScreen(Task task) {
        super(task.getTitle());
        this.task = task;
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
        var taskBg = new IndentedBox();
        taskBg.render(guiGraphics, left, right, top, top + 70, fillColor);

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

        renderCollectPanel(guiGraphics, reqPanelX, reqPanelY, reqPanelMaxWidth, mouseX, mouseY);

        top += 70;
        if (rewards.isEmpty()) {
            // Sadface.
            guiGraphics.drawString(font, Resources.MISSINGNO, left, top + 5, titleColor.getArgbColor(), false);
            return;
        }

        // Title for rewards section
        guiGraphics.drawString(font, Resources.REWARDS, left, top + 5, titleColor.getArgbColor(), false);

        top += 14;
        taskBg = new IndentedBox();
        taskBg.render(guiGraphics, left, right, top, top + 49, fillColor);

        var rewardsPanelX = left + 5;
        var rewardsPanelY = top + 5;
        var rewardsPanelMaxWidth = 240;

        renderRewardsPanel(guiGraphics, rewardsPanelX, rewardsPanelY, rewardsPanelMaxWidth, mouseX, mouseY);
    }

    protected void renderCollectPanel(GuiGraphics guiGraphics, int x, int y, int maxWidth, int mouseX, int mouseY) {
        var collect = task.collect;
        if (collect.isEmpty()) {
            return;
        }

        var xx = 0;
        var yy = 0;
        var boxMargin = 3;

        for (var i = 0; i < collect.items().size(); i++) {
            var item = collect.items().get(i);
            var box = new CollectItemBox(item, task.isStarted());
            box.render(guiGraphics, x + xx, y + yy, mouseX, mouseY);
            xx += box.width() + boxMargin;
            if (xx >= maxWidth) {
                // Move to next row
                xx = 0;
                yy += box.height() + boxMargin;
            }
        }
    }

    protected void renderRewardsPanel(GuiGraphics guiGraphics, int x, int y, int maxWidth, int mouseX, int mouseY) {
        var rewards = task.rewards;
        if (rewards.isEmpty()) {
            return;
        }

        var xx = 0;
        var yy = 0;
        var boxMargin = 3;

        // Reward XP
        if (rewards.experience > 0) {
            var xpBox = new RewardXpBox(rewards.experience);
            xpBox.render(guiGraphics, x + xx, y + yy, mouseX, mouseY);
            xx += xpBox.width() + boxMargin;
        }

        // Reward items
        for (var i = 0; i < rewards.items.size(); i++) {
            var item = rewards.items.get(i);
            var box = new RewardItemBox(item);
            box.render(guiGraphics, x + xx, y + yy, mouseX, mouseY);
            xx += box.width() + boxMargin;
            if (xx >= maxWidth) {
                // Move to next row
                xx = 0;
                yy += box.height() + boxMargin;
            }
        }
    }
}
