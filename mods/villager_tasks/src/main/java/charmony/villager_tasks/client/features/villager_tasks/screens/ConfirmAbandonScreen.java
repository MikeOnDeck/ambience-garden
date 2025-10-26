package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.gui.GuiGraphics;

public class ConfirmAbandonScreen extends BaseScreen {
    private final Task task;

    public ConfirmAbandonScreen(Task task) {
        super(task.getTitle());
        this.task = task;
    }

    @Override
    protected void init() {
        super.init();

        if (minecraft == null) return;

        var confirmButton = new Buttons.ConfirmButton(midX - 80, midY + 10, b -> {
            handlers.abandonTask(task);
            minecraft.setScreen(new ActiveTasksScreen());
        });
        var cancelButton = new Buttons.CancelButton(midX + 10, midY + 10, b -> {
            minecraft.setScreen(new ActiveTasksScreen());
        });

        addRenderableWidget(confirmButton);
        addRenderableWidget(cancelButton);
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.CONFIRM_ABANDON, midX, midY - 20, titleColor.getArgbColor());
    }
}
