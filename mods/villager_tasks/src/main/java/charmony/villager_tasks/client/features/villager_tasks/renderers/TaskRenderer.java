package charmony.villager_tasks.client.features.villager_tasks.renderers;

import charmony.api.core.Color;
import charmony.villager_tasks.client.features.villager_tasks.components.IndentedBox;
import charmony.villager_tasks.client.features.villager_tasks.components.LevelScroll;
import charmony.villager_tasks.client.features.villager_tasks.components.TaskTooltip;
import charmony.villager_tasks.common.features.villager_tasks.Task;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.Optional;

public class TaskRenderer {
    private final Task task;
    private final Font font;

    public final CollectRenderer collect;
    public final RewardsRenderer rewards;

    public Color titleColor;
    public Color textColor;
    public Color epicTextColor;
    public Color completeTextColor;
    public Color fillColor;
    public Color epicFillColor;
    public Color completeFillColor;

    public TaskRenderer(Task task) {
        this.collect = new CollectRenderer(task);
        this.rewards = new RewardsRenderer(task);
        this.task = task;
        this.font = Minecraft.getInstance().font;

        this.titleColor = new Color(0x454545);
        this.textColor = new Color(0x202020);
        this.epicTextColor = new Color(0x606020);
        this.completeTextColor = new Color(0x004000);
        this.fillColor = new Color(0x909090);
        this.epicFillColor = new Color(0xa0a060);
        this.completeFillColor = new Color(0x70a070);
    }

    public void simpleTaskRow(GuiGraphics guiGraphics, int midX, int top, int mouseX, int mouseY, TaskTooltip tooltip) {
        var left = midX - 138;
        var right = midX + 137;

        var textColor = task.isSatisfied() ? completeTextColor : (task.isEpic() ? epicTextColor : this.textColor);
        var fillColor = task.isSatisfied() ? completeFillColor : (task.isEpic() ? epicFillColor : this.fillColor);

        // Background behind the task
        var taskBg = new IndentedBox();
        taskBg.render(guiGraphics, left, right, top, top + 21, fillColor);

        // Level scroll icon
        var scroll = new LevelScroll(task, font);
        var sx = left + 3;
        var sy = top + 3;
        scroll.render(guiGraphics, sx, sy, mouseX, mouseY);

        // Task title label
        var title = MutableComponent.create(task.getTitle().getContents());
        var tx = sx + 22;
        var ty = top + 7;
        guiGraphics.drawString(font, title, tx, ty, textColor.getArgbColor(), false);

        // Mouse over title shows requirements of the task.
        if (mouseX >= tx && mouseX <= right - 44 &&
            mouseY >= top + 1 && mouseY <= top + 20) {
            var titleComponent = Component.literal(title.getString());
            guiGraphics.setTooltipForNextFrame(font, List.of(titleComponent), Optional.of(tooltip), mouseX, mouseY);
        }
    }
}
