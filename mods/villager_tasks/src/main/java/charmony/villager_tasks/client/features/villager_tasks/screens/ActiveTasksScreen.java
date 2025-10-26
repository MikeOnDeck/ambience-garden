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

public class ActiveTasksScreen extends BaseScreen {
    private boolean hasRenderedTaskButtons = false;

    private final Map<Task, AvailableTaskTooltip> tooltips = new HashMap<>();
    private final List<Button> buttons = new ArrayList<>();

    public ActiveTasksScreen() {
        super(Resources.ACTIVE_TASKS_TITLE);
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
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (minecraft == null) return;

        var top = midY - 57;
        var left = midX - 138;
        var right = midX + 137;

        if (!handlers.getActiveTasks().isEmpty()) {
            var activeTasks = handlers.getActiveTasks();
            var rowHeight = 30;

            for (var i = 0; i < activeTasks.tasks().size(); i++) {
                var rh = i * rowHeight;
                var task = activeTasks.tasks().get(i);

                if (!tooltips.containsKey(task)) {
                    tooltips.put(task, new AvailableTaskTooltip(new TaskRenderer(task)));
                }

                // Background behind the task
                var taskBg = new IndentedBox();
                taskBg.render(guiGraphics, left, right, top - 9 + rh, top + 14 + rh, task.isEpic() ? epicFillColor : fillColor);

                // Level scroll icon
                var scroll = new LevelScroll(font, task.level, true);
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
                var abandonX = right - 22;
                var buttonY = top + rh - 6;
                if (!hasRenderedTaskButtons) {
                    var detailsButton = new Buttons.DetailsButton(detailsX, buttonY,
                        b -> {
                            minecraft.setScreen(new TaskDetailsScreen(task));
                        });

                    var abandonButton = new Buttons.AbandonButton(abandonX, buttonY,
                        b -> {
                            handlers.abandonTask(task);
                            this.tooltips.remove(task);
                        });

                    addRenderableWidget(detailsButton);
                    addRenderableWidget(abandonButton);
                    this.buttons.addAll(List.of(detailsButton, abandonButton));
                }

                // Mouse over title shows requirements of the task.
                if (mouseX >= tx && mouseX <= detailsX - 4 &&
                    mouseY >= top + rh - 8 && mouseY <= top + rh + 13) {
                    var titleComponent = Component.literal(title.getString());
                    guiGraphics.setTooltipForNextFrame(font, List.of(titleComponent), Optional.of(tooltips.get(task)), mouseX, mouseY);
                }
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_ACTIVE_TASKS, midX, top, textColor.getArgbColor());
        }

        hasRenderedTaskButtons = true;
    }

    @Override
    public void onClose() {
        handlers.onActiveTasksUpdate.remove(this);
        super.onClose();
    }
}
