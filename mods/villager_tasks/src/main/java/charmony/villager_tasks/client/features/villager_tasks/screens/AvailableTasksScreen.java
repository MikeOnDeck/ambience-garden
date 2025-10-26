package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.components.AvailableTaskTooltip;
import charmony.villager_tasks.client.features.villager_tasks.components.IndentedBox;
import charmony.villager_tasks.client.features.villager_tasks.components.LevelScroll;
import charmony.villager_tasks.client.features.villager_tasks.renderers.TaskRenderer;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

public class AvailableTasksScreen extends BaseScreen {
    private final Map<Task, AvailableTaskTooltip> tooltips = new HashMap<>();
    private final List<Button> buttons = new ArrayList<>();

    private boolean hasRenderedTaskButtons = false;

    public AvailableTasksScreen() {
        super(Resources.AVAILABLE_TASKS_TITLE);
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null) return;
        hasRenderedTaskButtons = false;

        handlers.onActiveTasksUpdate.put(this, t -> {
            this.buttons.forEach(this::removeWidget);
            this.buttons.clear();
            this.hasRenderedTaskButtons = false;
        });
    }

    @Override
    public Component getTitle() {
        var availableTasks = handlers.getAvailableTasks();
        if (!availableTasks.isEmpty()) {
            return Component.translatable("gui.charmony.villager_tasks.available_tasks_for_villager", availableTasks.name());
        }
        return super.getTitle();
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (minecraft == null) return;

        var top = midY - 57;
        var left = midX - 138;
        var right = midX + 137;

        if (handlers.availableTasksAreValid()) {
            var activeTasks = handlers.getActiveTasks();
            var availableTasks = handlers.getAvailableTasks();
            var rowHeight = 30;

            for (var i = 0; i < availableTasks.tasks().size(); i++) {
                var rh = i * rowHeight;
                var task = availableTasks.tasks().get(i);
                var playerIsDoingTask = activeTasks.getTaskById(task.id).isPresent();

                if (!tooltips.containsKey(task)) {
                    tooltips.put(task, new AvailableTaskTooltip(new TaskRenderer(task)));
                }

                // Background behind the task
                var taskBg = new IndentedBox();
                taskBg.render(guiGraphics, left, right, top - 9 + rh, top + 14 + rh, task.isEpic() ? epicFillColor : fillColor);

                // Level scroll icon
                var scroll = new LevelScroll(font, task.level, playerIsDoingTask);
                var sx = left + 3;
                var sy = top + rh - 6;
                scroll.render(guiGraphics, sx, sy, mouseX, mouseY);

                // Task title label
                var title = MutableComponent.create(task.getTitle().getContents());
                var tx = sx + 22;
                var ty = top + rh - 1;
                guiGraphics.drawString(font, title, tx, ty, textColor.getArgbColor(), false);

                // Task buttons
                var detailsX = right - 44;
                var acceptX = right - 22;
                var buttonY = top + rh - 6;
                if (!hasRenderedTaskButtons) {
                    var detailsButton = new Buttons.DetailsButton(detailsX, buttonY,
                        b -> {
                            minecraft.setScreen(null);
                        });

                    var acceptButton = new Buttons.AcceptButton(acceptX, buttonY,
                        playerIsDoingTask ? Resources.ALREADY_DOING_TASK : Buttons.AcceptButton.DEFAULT_TOOLTIP,
                        b -> {
                            handlers.acceptTask(task);
                        });

                    if (playerIsDoingTask) {
                        acceptButton.active = false; // Set accept button disabled if the player already has this task.
                    }

                    addRenderableWidget(detailsButton);
                    addRenderableWidget(acceptButton);
                    this.buttons.addAll(List.of(detailsButton, acceptButton));
                }

                // Mouse over title shows requirements of the task.
                if (mouseX >= tx && mouseX <= detailsX - 4 &&
                    mouseY >= top + rh - 8 && mouseY <= top + rh + 13) {
                    var titleComponent = Component.literal(title.getString());
                    guiGraphics.setTooltipForNextFrame(font, List.of(titleComponent), Optional.of(tooltips.get(task)), mouseX, mouseY);
                }

//                // Requirements
//                if (false) {
//                    top += 21;
//                    var reqsText = Resources.REQUIRES_LABEL;
//                    var reqsX = 0;
//                    guiGraphics.drawString(font, reqsText, midX - 150, top + ry + 1, textColor);
//
//                    for (var j = 0; j < task.collect.items().size(); j++) {
//                        var item = task.collect.items().get(j);
//                        var box = new CollectBox(item.stack(), item.total());
//                        var boxX = midX - 95 + reqsX;
//                        var boxY = top - 4 + ry;
//                        box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
//                        reqsX += box.width() + distanceBetweenReqs;
//                    }
//                }
//
//                // Rewards
//                if (false) {
//                    top += 21;
//                    var rewardsText = Resources.REWARDS_LABEL;
//                    var rewardsX = 0; // track how much horizontal space used for rewards
//                    guiGraphics.drawString(font, rewardsText, midX - 150, top + ry + 1, rewardsTextColor);
//
//                    // Reward XP
//                    if (task.rewards.experience > 0) {
//                        var box = new RewardXpBox(task.rewards.experience);
//                        var boxX = midX - 95 + rewardsX;
//                        var boxY = top - 4 + ry;
//                        box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
//                        rewardsX += box.width() + distanceBetweenRewards;
//                    }
//
//
//                    // Reward items
//                    for (var j = 0; j < task.rewards.items.size(); j++) {
//                        var item = task.rewards.items.get(j);
//                        var box = new RewardItemBox(item);
//                        var boxX = midX - 95 + rewardsX;
//                        var boxY = top - 4 + ry;
//                        box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
//                        rewardsX += box.width() + distanceBetweenRewards;
//                    }
//                }
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_AVAILABLE_TASKS, midX, top, textColor.getArgbColor());
        }

        hasRenderedTaskButtons = true;
    }
}
