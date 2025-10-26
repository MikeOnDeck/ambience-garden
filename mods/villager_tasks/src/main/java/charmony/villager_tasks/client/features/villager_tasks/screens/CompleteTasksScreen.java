package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.components.AvailableTaskTooltip;
import charmony.villager_tasks.client.features.villager_tasks.renderers.TaskRenderer;
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
        if (minecraft == null) return;

        this.hasRenderedTaskButtons = false;
        this.buttons.clear();
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (minecraft == null) return;

        var top = midY - 67;
        var right = midX + 137;

        if (!handlers.getActiveTasks().isEmpty()) {
            var activeTasks = handlers.getActiveTasks();
            var rowHeight = 30;

            for (var i = 0; i < activeTasks.tasks().size(); i++) {
                var rh = i * rowHeight;
                var task = activeTasks.tasks().get(i);
                if (!task.isSatisfied()) continue;

                var renderer = renderers.computeIfAbsent(task, TaskRenderer::new);
                var tooltip = tooltips.computeIfAbsent(task, t -> new AvailableTaskTooltip(renderer));

                renderer.simpleTaskRow(guiGraphics, midX, top + rh, mouseX, mouseY, tooltip);

                if (!hasRenderedTaskButtons) {
                    var buttonY = top + rh + 2;
                    buttons.addAll(List.of(
                        new Buttons.DetailsButton(right - 44, buttonY,
                            b -> minecraft.setScreen(new TaskDetailsScreen(task))),
                        new Buttons.CompleteButton(right - 22, buttonY,
                            b -> minecraft.setScreen(null))
                    ));
                    buttons.forEach(this::addRenderableWidget);
                }
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_SATISFIED_TASKS, midX, top + 20, textColor.getArgbColor());
        }

        hasRenderedTaskButtons = true;
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}
