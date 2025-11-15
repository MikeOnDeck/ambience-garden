package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.renderers.TaskRenderer;
import charmony.villager_tasks.client.features.villager_tasks.tooltips.AvailableTaskTooltip;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailableTasksScreen extends BaseScreen {
    private final Map<Task, AvailableTaskTooltip> tooltips = new HashMap<>();
    private final Map<Task, TaskRenderer> renderers = new HashMap<>();
    private final List<Button> buttons = new ArrayList<>();

    private boolean hasRenderedTaskButtons = false;

    public AvailableTasksScreen() {
        super(Resources.AVAILABLE_TASKS_TITLE);
    }

    @Override
    protected void init() {
        super.init();
        addCloseButton();
        refresh();
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
        if (minecraft.level == null) return;

        var top = midY - 67;
        var right = midX + 137;

        if (handlers.availableTasksAreValid()) {
            var activeTasks = handlers.getActiveTasks().copy();
            var availableTasks = handlers.getAvailableTasks().copy();
            var rowHeight = 30;

            for (var i = 0; i < availableTasks.tasks().size(); i++) {
                var rh = i * rowHeight;
                var task = availableTasks.tasks().get(i);
                if (task.isEmpty()) continue;

                var activeTask = activeTasks.getTaskById(task.id).orElse(null);

                var playerIsDoingTask = activeTask != null;
                var playerHasDoneTask = activeTask != null && activeTask.isSatisfied();

                Task taskCopy = playerIsDoingTask ? activeTask : task; // have to do this for Java's lambdas, mumble mumble

                var renderer = renderers.computeIfAbsent(taskCopy, TaskRenderer::new);
                var tooltip = tooltips.computeIfAbsent(taskCopy, t -> new AvailableTaskTooltip(renderer));

                renderer.updateTask(taskCopy);
                renderer.renderSimpleTaskRow(guiGraphics, midX, top + rh, mouseX, mouseY, tooltip);

                // Task buttons
                if (!hasRenderedTaskButtons) {
                    var buttonY = top + rh + 2;

                    var detailsButton = new Buttons.DetailsButton(right - 44, buttonY,
                        b -> minecraft.setScreen(new TaskDetailsScreen(taskCopy, this)));

                    var acceptButton = new Buttons.AcceptButton(right - 22, buttonY,
                        playerIsDoingTask ? Resources.DOING_TASK : Buttons.AcceptButton.DEFAULT_TOOLTIP,
                        b -> handlers.acceptTask(taskCopy));

                    var completeButton = new Buttons.CompleteButton(right - 22, buttonY,
                        Buttons.CompleteButton.DEFAULT_TOOLTIP,
                        b -> {
                            handlers.completeTask(taskCopy);
                            minecraft.setScreen(null);
                        });

                    completeButton.visible = false;

                    if (playerIsDoingTask) {
                        acceptButton.active = false;
                    }
                    if (playerHasDoneTask) {
                        acceptButton.visible = false;
                        completeButton.visible = true;
                    }

                    buttons.addAll(List.of(detailsButton, acceptButton, completeButton));
                    buttons.forEach(this::addRenderableWidget);
                }
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_AVAILABLE_TASKS, midX, top + 20, textColor.getArgbColor());
        }

        hasRenderedTaskButtons = true;
    }

    @Override
    public void onClose() {
        handlers.clearLastVillagerInteraction();
        super.onClose();
    }

    public void refresh() {
        this.hasRenderedTaskButtons = false;
        this.clearWidgets();
        this.buttons.clear();
        this.tooltips.clear();
        this.renderers.clear();
    }
}
