package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.api.core.Color;
import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import net.minecraft.client.gui.GuiGraphics;

import javax.annotation.Nullable;

public class AvailableTasksScreen extends BaseScreen {
    private final Handlers handlers; // Reference for easy access to handler functions.

    private int midX;
    private int textColor;

    @Nullable private Tasks availableTasks = null;

    public AvailableTasksScreen() {
        super(Resources.AVAILABLE_TASKS);
        this.handlers = VillagerTasks.feature().handlers;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null) return;

        var merchant = handlers.getLastVillagerInteraction().orElse(null);
        var availableTasks = handlers.getAvailableTasks().orElse(null);
        var activeTasks = handlers.getActiveTasks().orElse(null);

        midX = width / 2;
        textColor = new Color(0xffffff).getArgbColor();

        if (availableTasks != null && availableTasks.uuid().equals(merchant)) {
            this.availableTasks = availableTasks; // This allows the renderer to use the tasks.

            for (var i = 0; i < this.availableTasks.tasks().size(); i++) {
                var task = availableTasks.tasks().get(i);
                var details = new Buttons.TaskDetailsButton(midX + 20, 50 + (i * 25),
                    b -> {
                        minecraft.setScreen(null);
                    });

                var accept = new Buttons.AcceptTaskButton(midX + 85, 50 + (i * 25),
                    b -> {
                        handlers.acceptTask(task);
                        minecraft.setScreen(null);
                    });

                if (activeTasks != null) {
                    // Set accept button disabled if the player already has this task.
                    activeTasks.getTaskByDefinition(task.getDefinitionId()).ifPresent(
                        activeTask -> accept.active = false);
                }

                addRenderableWidget(details);
                addRenderableWidget(accept);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        super.render(guiGraphics, mouseX, mouseY, tickDelta);

        if (availableTasks != null) {
            for (var i = 0; i < availableTasks.tasks().size(); i++) {
                var task = availableTasks.tasks().get(i);
                var name = task.getActiveBehaviorNames().getFirst();
                TextComponentHelper.drawCenteredString(guiGraphics, font, name, midX - 100,  56 + (i * 25), textColor);
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_AVAILABLE_TASKS, midX, 40, textColor);
        }
    }
}
