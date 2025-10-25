package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.api.core.Color;
import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.client.features.villager_tasks.components.AvailableTaskTooltip;
import charmony.villager_tasks.client.features.villager_tasks.components.IndentedBox;
import charmony.villager_tasks.client.features.villager_tasks.components.LevelScroll;
import charmony.villager_tasks.client.features.villager_tasks.renderers.TaskRenderer;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvailableTasksScreen extends BaseScreen {
    private final Handlers handlers; // Reference for easy access to handler functions.

    private int midX;
    private int midY;
    private Color titleColor;
    private Color textColor;
    private Color fillColor;
    private Color epicFillColor;
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
        midY = height / 2;

        titleColor = new Color(0x454545);
        textColor = new Color(0x202020);
        fillColor = new Color(0x909090);
        epicFillColor = new Color(0xa0a060);

        hasRenderedTaskButtons = false;

        // We check that the available tasks are for the last interacted villager.
        if (availableTasks != null && availableTasks.uuid().equals(villager)) {
            this.availableTasks = availableTasks;

            for (var task : availableTasks.tasks()) {
                var renderer = new TaskRenderer(task);
                this.tooltips.add(new AvailableTaskTooltip(renderer));
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        renderBg(guiGraphics);
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

    public void renderBg(GuiGraphics guiGraphics) {
        var width = 298;
        var height = 177;
        var midX = (this.width - width) / 2;
        var midY = (this.height - height) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Resources.TASKS_BACKGROUND, midX, midY, 0.0f, 0.0f, width, height, 512, 256);
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        TextComponentHelper.drawCenteredString(guiGraphics, font, getTitle(), midX, midY - 80, titleColor.getArgbColor());
    }

    private void renderTasks(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (minecraft == null) return;
        var top = midY - 55;
        var left = midX - 138;
        var right = midX + 137;

        if (!availableTasks.isEmpty()) {
            var rowHeight = 30;

            for (var i = 0; i < availableTasks.tasks().size(); i++) {
                var rh = i * rowHeight;
                var task = availableTasks.tasks().get(i);
                var playerIsDoingTask = activeTasks != null && activeTasks.getTaskById(task.id).isPresent();

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
                            minecraft.setScreen(null);
                        });

                    if (playerIsDoingTask) {
                        acceptButton.active = false; // Set accept button disabled if the player already has this task.
                    }

                    addRenderableWidget(detailsButton);
                    addRenderableWidget(acceptButton);
                }

                // Mouse over title shows requirements of the task.
                if (mouseX >= tx && mouseX <= detailsX - 4 &&
                    mouseY >= top + rh - 8 && mouseY <= top + rh + 13) {

                    guiGraphics.setTooltipForNextFrame(font, List.of(
                        Component.literal(title.getString())
                    ), Optional.of(tooltips.get(i)), mouseX, mouseY);
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
