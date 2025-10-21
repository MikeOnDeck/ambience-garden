package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.api.core.Color;
import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class AvailableTasksScreen extends BaseScreen {
    private final Handlers handlers; // Reference for easy access to handler functions.

    private int midX;
    private int textColor;
    private int epicTextColor;

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
        epicTextColor = new Color(0xffff00).getArgbColor();

        if (availableTasks != null && availableTasks.uuid().equals(merchant)) {
            this.availableTasks = availableTasks; // This allows the renderer to use the tasks.

            for (var i = 0; i < this.availableTasks.tasks().size(); i++) {
                var task = availableTasks.tasks().get(i);
                var infoButton = new Buttons.TaskInfoButton(midX + 53, 44 + (i * 25),
                    b -> {
                        minecraft.setScreen(null);
                    });

                var acceptButton = new Buttons.AcceptTaskButton(midX + 105, 44 + (i * 25),
                    b -> {
                        handlers.acceptTask(task);
                        minecraft.setScreen(null);
                    });

                if (activeTasks != null) {
                    // Set accept button disabled if the player already has this task.
                    activeTasks.getTaskByDefinition(task.getDefinitionId()).ifPresent(
                        activeTask -> acceptButton.active = false);
                }

                addRenderableWidget(infoButton);
                addRenderableWidget(acceptButton);
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

        renderTitle(guiGraphics);
        renderTasks(guiGraphics);
    }

    @Override
    public Component getTitle() {
        if (availableTasks != null) {
            return Component.translatable("gui.charmony.villager_tasks.available_tasks_for_villager", availableTasks.name());
        }
        return super.getTitle();
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        TextComponentHelper.drawCenteredString(guiGraphics, font, getTitle(), midX, 19, textColor);
    }

    private void renderTasks(GuiGraphics guiGraphics) {
        if (availableTasks != null) {
            for (var i = 0; i < availableTasks.tasks().size(); i++) {
                var task = availableTasks.tasks().get(i);
                var titleColor = task.isEpic() ? epicTextColor : textColor;

                // Title
                TextComponentHelper.drawCenteredString(guiGraphics, font, task.getTitle(), midX - 105,  50 + (i * 25), titleColor);



            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_AVAILABLE_TASKS, midX, 40, textColor);
        }
    }
}
