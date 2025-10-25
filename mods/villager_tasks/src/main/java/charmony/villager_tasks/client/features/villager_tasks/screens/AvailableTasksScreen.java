package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.api.core.Color;
import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.AvailableTaskTooltip;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.client.features.villager_tasks.components.*;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvailableTasksScreen extends BaseScreen {
    private final Handlers handlers; // Reference for easy access to handler functions.

    private int midX;
    private int textColor;
    private int epicTextColor;
    private int rewardsTextColor;
    private boolean hasRenderedTaskButtons = false;

    private Tasks availableTasks = Tasks.EMPTY;
    private Tasks activeTasks = Tasks.EMPTY;

    private List<AvailableTaskTooltip> tooltips = new ArrayList<>();

    public AvailableTasksScreen() {
        super(Resources.AVAILABLE_TASKS_TITLE);
        this.handlers = VillagerTasks.feature().handlers;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null) return;

        var villager = handlers.getLastVillagerInteraction();
        var availableTasks = handlers.getAvailableTasks();
        this.activeTasks = handlers.getActiveTasks();
        this.tooltips = new ArrayList<>();

        midX = width / 2;
        textColor = new Color(0xffffff).getArgbColor();
        epicTextColor = new Color(0xffff00).getArgbColor();
        rewardsTextColor = new Color(0x80e0ff).getArgbColor();
        hasRenderedTaskButtons = false;

        // We check that the available tasks are for the last interacted villager.
        if (availableTasks != null && availableTasks.uuid().equals(villager)) {
            this.availableTasks = availableTasks;

            for (var task : availableTasks.tasks()) {
                this.tooltips.add(new AvailableTaskTooltip(task));
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        renderTitle(guiGraphics);
        renderTasks(guiGraphics, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, tickDelta);
    }

    @Override
    public Component getTitle() {
        if (!availableTasks.isEmpty()) {
            return Component.translatable("gui.charmony.villager_tasks.available_tasks_for_villager", availableTasks.name());
        }
        return super.getTitle();
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        TextComponentHelper.drawCenteredString(guiGraphics, font, getTitle(), midX, 14, textColor);
    }

    private void renderTasks(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (minecraft == null) return;

        if (!availableTasks.isEmpty()) {
            var rowHeight = 90;
            var top = 46;

            for (var i = 0; i < availableTasks.tasks().size(); i++) {
                var task = availableTasks.tasks().get(i);
                var playerIsDoingTask = activeTasks != null && activeTasks.getTaskByDefinition(task.getDefinitionId()).isPresent();
                var titleColor = task.isEpic() ? epicTextColor : textColor;
                var distanceBetweenRewards = 3;
                var distanceBetweenReqs = 3;

                // Background behind the task
                var taskBg = new BorderedBox(130);
                taskBg.render(guiGraphics, midX - 158, midX + 156, top - 9 + (i * rowHeight), top + 65 + (i * rowHeight), new Color(0x000000));

                // Level scroll icon
                var scroll = new LevelScroll(font, task.level, playerIsDoingTask);
                scroll.render(guiGraphics, midX - 152, top + (i * rowHeight) - 4, mouseX, mouseY);

                // Task title label
                var title = MutableComponent.create(task.getTitle().getContents());
                var titleWidth = font.width(title);
                guiGraphics.drawString(font, title.withStyle(ChatFormatting.UNDERLINE), midX - 130,  top + (i * rowHeight), titleColor);

                // Mouse over shows requirements of the task.
                if (mouseX >= midX - 130 && mouseX <= midX - 130 + titleWidth &&
                    mouseY >= top + (i * rowHeight) && mouseY <= top + 10 + (i * rowHeight)) {

                    guiGraphics.setTooltipForNextFrame(font, List.of(
                        Component.literal(title.getString())
                    ), Optional.of(tooltips.get(i)), mouseX, mouseY);
                }

                // Task buttons
                if (!hasRenderedTaskButtons) {
                    var detailsButton = new Buttons.DetailsButton(midX + 108, top - 4 + (i * rowHeight),
                        b -> {
                            minecraft.setScreen(null);
                        });

                    var acceptButton = new Buttons.AcceptButton(midX + 130, top - 4 + (i * rowHeight),
                        playerIsDoingTask ? Resources.ALREADY_DOING_TASK : Buttons.AcceptButton.DEFAULT_TOOLTIP,
                        b -> {
                            handlers.acceptTask(task);
                            minecraft.setScreen(null);
                        });

                    if (playerIsDoingTask) {
                        // Set accept button disabled if the player already has this task.
                        acceptButton.active = false;
                    }

                    addRenderableWidget(detailsButton);
                    addRenderableWidget(acceptButton);
                }

                // Requirements
                if (false) {
                    top += 21;
                    var reqsText = Resources.REQUIRES_LABEL;
                    var reqsX = 0;
                    guiGraphics.drawString(font, reqsText, midX - 150, top + (i * rowHeight) + 1, textColor);

                    for (var j = 0; j < task.collect.items().size(); j++) {
                        var item = task.collect.items().get(j);
                        var box = new CollectBox(item.stack(), item.total());
                        var boxX = midX - 95 + reqsX;
                        var boxY = top - 4 + (i * rowHeight);
                        box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
                        reqsX += box.width() + distanceBetweenReqs;
                    }
                }

                // Rewards
                if (false) {
                    top += 21;
                    var rewardsText = Resources.REWARDS_LABEL;
                    var rewardsX = 0; // track how much horizontal space used for rewards
                    guiGraphics.drawString(font, rewardsText, midX - 150, top + (i * rowHeight) + 1, rewardsTextColor);

                    // Reward XP
                    if (task.rewards.experience > 0) {
                        var box = new RewardXpBox(task.rewards.experience);
                        var boxX = midX - 95 + rewardsX;
                        var boxY = top - 4 + (i * rowHeight);
                        box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
                        rewardsX += box.width() + distanceBetweenRewards;
                    }


                    // Reward items
                    for (var j = 0; j < task.rewards.items.size(); j++) {
                        var item = task.rewards.items.get(j);
                        var box = new RewardItemBox(item);
                        var boxX = midX - 95 + rewardsX;
                        var boxY = top - 4 + (i * rowHeight);
                        box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
                        rewardsX += box.width() + distanceBetweenRewards;
                    }
                }
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_AVAILABLE_TASKS, midX, 40, textColor);
        }

        hasRenderedTaskButtons = true;
    }
}
