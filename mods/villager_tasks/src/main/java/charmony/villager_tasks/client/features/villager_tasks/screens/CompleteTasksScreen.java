package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.renderers.TaskRenderer;
import charmony.villager_tasks.client.features.villager_tasks.tooltips.AvailableTaskTooltip;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteTasksScreen extends BaseScreen {
    private boolean hasRenderedTaskButtons = false;

    private final Map<Task, AvailableTaskTooltip> tooltips = new HashMap<>();
    private final Map<Task, TaskRenderer> renderers = new HashMap<>();
    private final List<Button> buttons = new ArrayList<>();

    public CompleteTasksScreen() {
        super(Resources.COMPLETE_TASKS_TITLE);
    }

    @Override
    protected void init() {
        super.init();
        addButtons();
        refresh();
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var top = midY - 67;
        var right = midX + 137;

        if (!handlers.getActiveTasks().isEmpty()) {
            var satisfiedTasks = handlers.getActiveTasks().tasks().stream().filter(Task::isSatisfied).toList();
            var rowHeight = 30;

            for (var i = 0; i < satisfiedTasks.size(); i++) {
                var rh = i * rowHeight;
                var task = satisfiedTasks.get(i);

                var renderer = renderers.computeIfAbsent(task, TaskRenderer::new);
                var tooltip = tooltips.computeIfAbsent(task, t -> new AvailableTaskTooltip(renderer));

                renderer.updateTask(task);
                renderer.renderSimpleTaskRow(guiGraphics, midX, top + rh, mouseX, mouseY, tooltip);

                if (!hasRenderedTaskButtons) {
                    var buttonY = top + rh + 2;
                    buttons.addAll(List.of(
                        new Buttons.DetailsButton(right - 44, buttonY,
                            b -> minecraft.setScreen(new TaskDetailsScreen(task, this))),
                        new Buttons.CompleteButton(right - 22, buttonY,
                            b -> {
                                handlers.completeTask(task);
                                minecraft.setScreen(null);
                            })
                    ));
                    buttons.forEach(this::addRenderableWidget);
                }
            }
        } else {
            this.onClose();
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
