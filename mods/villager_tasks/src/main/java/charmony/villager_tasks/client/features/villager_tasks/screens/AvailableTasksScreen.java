package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.api.core.Color;
import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;

import javax.annotation.Nullable;

public class AvailableTasksScreen extends BaseScreen {
    private final Handlers handlers; // Reference for easy access to handler functions.

    private int midX;
    private int textColor;
    private int epicTextColor;
    private int rewardsTextColor;
    private int rowHeight;

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
        rowHeight = 30;
        textColor = new Color(0xffffff).getArgbColor();
        epicTextColor = new Color(0xffff00).getArgbColor();
        rewardsTextColor = new Color(0x80e0ff).getArgbColor();

        if (availableTasks != null && availableTasks.uuid().equals(merchant)) {
            this.availableTasks = availableTasks; // This allows the renderer to use the tasks.

            for (var i = 0; i < this.availableTasks.tasks().size(); i++) {
                var task = availableTasks.tasks().get(i);
                var infoButton = new Buttons.TaskInfoButton(midX + 53, 36 + (i * rowHeight),
                    b -> {
                        minecraft.setScreen(null);
                    });

                var acceptButton = new Buttons.AcceptTaskButton(midX + 105, 36 + (i * rowHeight),
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
        renderTasks(guiGraphics, mouseX, mouseY);
    }

    @Override
    public Component getTitle() {
        if (availableTasks != null) {
            return Component.translatable("gui.charmony.villager_tasks.available_tasks_for_villager", availableTasks.name());
        }
        return super.getTitle();
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        TextComponentHelper.drawCenteredString(guiGraphics, font, getTitle(), midX, 14, textColor);
    }

    private void renderTasks(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (availableTasks != null) {
            var top = 40;

            for (var i = 0; i < availableTasks.tasks().size(); i++) {
                var task = availableTasks.tasks().get(i);
                var titleColor = task.isEpic() ? epicTextColor : textColor;
                var distanceBetweenRewards = 5;

                // title of the task
                var title = MutableComponent.create(task.getTitle().getContents());
                guiGraphics.drawString(font, title.withStyle(ChatFormatting.UNDERLINE), midX - 150,  top + (i * rowHeight), titleColor);

                // rewards label
                var rewards = Resources.REWARDS_LABEL;
                var rewardsTextWidth = font.width(rewards);
                var fillColor = rewardsTextColor;

                top += 20;
                guiGraphics.drawString(font, rewards, midX - 150,  top + (i * rowHeight), rewardsTextColor);

                var rewardsX = 0; // track how much horizontal space used for rewards
                for (var j = 0; j < task.rewards.items.size(); j++) {
                    var item = task.rewards.items.get(j);
                    var stack = item.stack();
                    var count = "" + item.total();
                    var countTextWidth = font.width(count);

                    // background box x and y bounds
                    var x1 = midX - 150 + rewardsTextWidth + 8 + rewardsX;
                    var x2 = x1 + 16 + countTextWidth + 5;
                    var y1 = top - 4 + (i * rowHeight);
                    var y2 = y1 + 16;

                    // ttem x and y
                    var ix = midX - 150 + rewardsTextWidth + 8 + rewardsX;
                    var iy = top - 4 + (i * rowHeight);

                    // count x and y
                    var cx = ix + 18;
                    var cy = iy + 4;

                    // Draw a background fill for the item icon and count
                    guiGraphics.fill(x1, y1, x2, y2, ARGB.color(40, fillColor));

                    // Render the item icon and its tooltip
                    guiGraphics.renderFakeItem(stack, ix, iy);

                    if (mouseX > ix && mouseX < ix + 16 && mouseY > iy && mouseY < iy + 16) {
                        guiGraphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);
                    }

                    // Render the count
                    guiGraphics.drawString(font, count, cx, cy, textColor);
                    rewardsX += (x2 - x1) + distanceBetweenRewards;
                }
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_AVAILABLE_TASKS, midX, 40, textColor);
        }
    }
}
