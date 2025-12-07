package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.component.IndentedBoxBuilder;
import charmony.villager_tasks.client.features.villager_tasks.renderers.TaskRenderer;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class TaskDetailsScreen extends BaseScreen {
    private final Screen parent;
    private final Task task;
    private final TaskRenderer taskRenderer;
    private Button pinButton;

    public TaskDetailsScreen(Task task, Screen parent) {
        super(task.getTitle());
        this.task = task;
        this.parent = parent;
        this.taskRenderer = new TaskRenderer(task);
    }

    @Override
    protected void init() {
        super.init();
        addButtons();
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var reqs = task.requirements();
        var rewards = task.rewards();

        var top = midY - 69;
        var left = midX - 138;
        var right = midX + 138;

        if (reqs.isEmpty()) {
            // Can't render task with no requirements - just show missingno and return
            guiGraphics.drawString(font, Resources.MISSINGNO, left, top + 5, titleColor.getArgbColor(), false);
            return;
        }

        // Title for requirements section
        guiGraphics.drawString(font, Resources.REQUIREMENTS, left, top + 5, titleColor.getArgbColor(), false);

        top += 14;
        var reqbox = new IndentedBoxBuilder()
            .withDimensions(right - left, 70)
            .withColor(fillColor);

        reqbox.render(guiGraphics, left, top);

        var px = left + 5;
        var py = top + 5;
        var maxWidth = 240;
        Pair<Integer, Integer> box = Pair.of(0, 0);

        for (var renderer : taskRenderer.requirementRenderers) {
            box = renderer.renderPanel(guiGraphics, px, py, box.getFirst(), box.getSecond(), maxWidth, mouseX, mouseY);
        }

        top += 70;
        if (rewards.isEmpty()) {
            // Sadface.
            guiGraphics.drawString(font, Resources.MISSINGNO, left, top + 5, titleColor.getArgbColor(), false);
            return;
        }

        // Title for rewards section
        guiGraphics.drawString(font, Resources.REWARDS, left, top + 5, titleColor.getArgbColor(), false);

        top += 14;
        var rewardbox = new IndentedBoxBuilder()
            .withDimensions(right - left, 49)
            .withColor(fillColor);

        rewardbox.render(guiGraphics, left, top);

        px = left + 5;
        py = top + 5;

        for (var renderer : taskRenderer.rewardRenderers) {
            renderer.renderPanel(guiGraphics, px, py, 0, 0, maxWidth, mouseX, mouseY);
        }
    }

    @Override
    protected void addButtons() {
        if (task.isStarted()) {
            addPinButton();
        }

        var closeButton = new Buttons.BackToTaskButton(midX + (task.isStarted() ? 5 : -(Buttons.BackToTaskButton.WIDTH / 2)), midY + bottomButtonY,
                b -> minecraft.setScreen(parent));

        addRenderableWidget(closeButton);
    }

    protected void addPinButton() {
        if (pinButton != null) {
            removeWidget(pinButton);
        }

        if (handlers.isPinnedTask(task)) {
            pinButton = new Buttons.UnpinButton(midX - (5 + Buttons.UnpinButton.WIDTH), midY + bottomButtonY,
                b -> {
                    handlers.clearPinnedTask();
                    addPinButton();
                });
        } else {
            pinButton = new Buttons.PinButton(midX - (5 + Buttons.PinButton.WIDTH), midY + bottomButtonY,
                b -> {
                    handlers.setPinnedTask(task);
                    addPinButton();
                });
        }

        addRenderableWidget(pinButton);
    }
}
