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
import net.minecraft.world.item.trading.Merchant;

import javax.annotation.Nullable;

public class AvailableTasksScreen extends BaseScreen {
    /**
     * Reference for easy access to client handler functions.
     */
    private final Handlers handlers;

    private int midX;
    private int textColor;

    @Nullable private Tasks tasks = null;

    public AvailableTasksScreen(Merchant merchant) {
        super(Resources.AVAILABLE_TASKS);
        this.handlers = VillagerTasks.feature().handlers;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null) return;

        var merchant = handlers.getLastMerchantInteraction().orElse(null);
        var tasks = handlers.getAvailableTasks().orElse(null);

        midX = width / 2;
        textColor = new Color(0xffffff).getArgbColor();

        if (tasks != null && tasks.uuid().equals(merchant)) {
            this.tasks = tasks; // This allows the renderer to use the tasks.

            for (var i = 0; i < this.tasks.tasks().size(); i++) {
                var task = tasks.tasks().get(i);
                var details = new Buttons.TaskDetailsButton(midX + 20, 50 + (i * 25),
                    b -> {
                        minecraft.setScreen(null);
                    });

                var accept = new Buttons.AcceptTaskButton(midX + 85, 50 + (i * 25),
                    b -> {
                        handlers.acceptTask(task);
                        minecraft.setScreen(null);
                    });

                // TODO: disable button if task already accepted.

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

        if (tasks != null) {
            for (var i = 0; i < tasks.tasks().size(); i++) {
                var task = tasks.tasks().get(i);
                var name = task.getActiveBehaviorNames().getFirst();
                TextComponentHelper.drawCenteredString(guiGraphics, font, name, midX - 100,  56 + (i * 25), textColor);
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Component.literal("No available tasks."), midX, 40, textColor);
        }
    }
}
