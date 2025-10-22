package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.api.core.Color;
import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.client.features.villager_tasks.components.CollectItemBox;
import charmony.villager_tasks.client.features.villager_tasks.components.LevelScroll;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;

public class ActiveTasksScreen extends BaseScreen {
    private final Handlers handlers; // Reference for easy access to handler functions.

    private int midX;
    private int textColor;
    private int epicTextColor;
    private boolean hasRenderedTaskButtons = false;
    private Tasks activeTasks = null;

    public ActiveTasksScreen() {
        super(Resources.ACTIVE_TASKS_TITLE);
        this.handlers = VillagerTasks.feature().handlers;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null) return;

        var activeTasks = handlers.getActiveTasks().orElse(null);

        midX = width / 2;
        textColor = new Color(0xffffff).getArgbColor();
        epicTextColor = new Color(0xffff00).getArgbColor();
        hasRenderedTaskButtons = false;

        if (activeTasks != null ) {
            this.activeTasks = activeTasks; // This allows the renderer to use the tasks.
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        renderTitle(guiGraphics);
        renderTasks(guiGraphics, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, tickDelta);
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        TextComponentHelper.drawCenteredString(guiGraphics, font, getTitle(), midX, 14, textColor);
    }

    private void renderTasks(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (minecraft == null) return;

        if (activeTasks != null) {
            var rowHeight = 40;
            var top = 46;

            for (var i = 0; i < activeTasks.tasks().size(); i++) {
                var task = activeTasks.tasks().get(i);
                var titleColor = task.isEpic() ? epicTextColor : textColor;
                var distanceBetweenReqs = 3;

                var bx0 = midX - 158;
                var bx1 = midX + 156;
                var by0 = top - 9 + (i * rowHeight);
                var by1 = by0 + 52;
                guiGraphics.fill(RenderPipelines.GUI, bx0, by0, bx1, by1, ARGB.color(150, 0, 0, 0));

                // Level scroll icon.
                var scroll = new LevelScroll(font, task.level, true);
                scroll.render(guiGraphics, midX - 152, top + (i * rowHeight) - 4, mouseX, mouseY);

                // Task title label
                var title = MutableComponent.create(task.getTitle().getContents());
                guiGraphics.drawString(font, title.withStyle(ChatFormatting.UNDERLINE), midX - 130,  top + (i * rowHeight), titleColor);

                // Task buttons
                if (!hasRenderedTaskButtons) {
                    var detailsButton = new Buttons.DetailsButton(midX + 108, top - 4 + (i * rowHeight),
                        b -> {
                            minecraft.setScreen(null);
                        });

                    var abandonButton = new Buttons.AbandonButton(midX + 130, top - 4 + (i * rowHeight),
                        b -> {
                            minecraft.setScreen(null);
                        });

                    addRenderableWidget(detailsButton);
                    addRenderableWidget(abandonButton);
                }

                // Requirements
                top += 21;
                var reqsText = Resources.REQUIRES_LABEL;
                var reqsX = 0;
                guiGraphics.drawString(font, reqsText, midX - 150,  top + (i * rowHeight) + 1, textColor);

                for (var j = 0; j < task.collect.items().size(); j++) {
                    var item = task.collect.items().get(j);
                    var box = new CollectItemBox(item.stack(), item.total(), font);
                    var boxX = midX - 95 + reqsX;
                    var boxY = top - 4 + (i * rowHeight);
                    box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
                    reqsX += box.width() + distanceBetweenReqs;
                }
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_ACTIVE_TASKS, midX, 40, textColor);
        }

        hasRenderedTaskButtons = true;
    }
}
