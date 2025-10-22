package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.api.core.Color;
import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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

                // Task title label
                var title = MutableComponent.create(task.getTitle().getContents());
                guiGraphics.drawString(font, title.withStyle(ChatFormatting.UNDERLINE), midX - 150,  top + (i * rowHeight), titleColor);

                // Rewards label
                top += 20;
                var rewards = Resources.REWARDS_LABEL;
                var rewardsTextWidth = font.width(rewards);

                var rewardsX = 0; // track how much horizontal space used for rewards
                guiGraphics.drawString(font, rewards, midX - 150,  top + (i * rowHeight) + 1, rewardsTextColor);

                // Reward XP
                if (task.rewards.experience > 0) {
                    var box = new RewardXpBox("" + task.rewards.experience, font);
                    var boxX = midX - 150 + rewardsTextWidth + 8 + rewardsX;
                    var boxY = top - 4 + (i * rowHeight);
                    box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
                    rewardsX += box.width() + distanceBetweenRewards;
                }

                // Reward items
                for (var j = 0; j < task.rewards.items.size(); j++) {
                    var item = task.rewards.items.get(j);
                    var box = new RewardItemBox(item.stack(), "" + item.total(), font);
                    var boxX = midX - 150 + rewardsTextWidth + 8 + rewardsX;
                    var boxY = top - 4 + (i * rowHeight);
                    box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
                    rewardsX += box.width() + distanceBetweenRewards;
                }
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_AVAILABLE_TASKS, midX, 40, textColor);
        }
    }

    public static class ItemBox {
        protected final Font font;
        protected final ItemStack stack;
        protected final String text;
        protected final int textColor;
        protected final int fillColor;

        protected boolean drawOutline = true;

        public ItemBox(Font font, ItemStack stack, String text, int textColor, int fillColor) {
            this.stack = stack;
            this.text = text;
            this.font = font;
            this.textColor = textColor;
            this.fillColor = fillColor;
        }

        public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
            var lineColor = ARGB.color(Math.min(255, alpha() * 3), fillColor);
            var bgColor = ARGB.color(alpha(), fillColor);

            // Dimensions of box
            var x1 = x + width();
            var y1 = y + height();

            // Item x and y
            var ix = x + 1;
            var iy = y + 1;

            // Text x and y
            var tx = ix + 18;
            var ty = iy + 5;

            // Draw box outline and background
            if (drawOutline) {
                guiGraphics.hLine(x, x1, y, lineColor);
                guiGraphics.vLine(x1, y, y1, lineColor);
                guiGraphics.hLine(x, x1, y1, lineColor);
                guiGraphics.vLine(x, y, y1, lineColor);
                guiGraphics.fill(x + 1, y + 1, x1, y1, bgColor);
            } else {
                guiGraphics.fill(x, y, x1 + 1, y1 + 1, bgColor);
            }

            // Render item and tooltip
            guiGraphics.renderFakeItem(stack, ix, iy);
            if (mouseX > ix && mouseX < ix + 16 && mouseY > iy && mouseY < iy + 16) {
                renderTooltip(guiGraphics, mouseX, mouseY);
            }

            // Show the text
            guiGraphics.drawString(font, text, tx, ty, textColor);
        }

        public int width() {
            var textWidth = font.width(text);
            return 16 + textWidth + 6;
        }

        public int height() {
            return 18;
        }

        public int alpha() {
            return 50;
        }

        public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            guiGraphics.setTooltipForNextFrame(font, stack, mouseX, mouseY);
        }
    }

    public static class RewardItemBox extends ItemBox {
        public RewardItemBox(ItemStack stack, String text, Font font) {
            super(font, stack, text, new Color(0xffffff).getArgbColor(), new Color(0x80e0ff).getArgbColor());
        }
    }

    public static class RewardXpBox extends ItemBox {
        public RewardXpBox(String text, Font font) {
            super(font, new ItemStack(Items.EXPERIENCE_BOTTLE), text, new Color(0xffffff).getArgbColor(), new Color(0x80ffc0).getArgbColor());
        }

        @Override
        public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            var component = Component.translatable("gui.charmony.villager_tasks.experience_levels", text);
            guiGraphics.setTooltipForNextFrame(component, mouseX, mouseY);
        }
    }
}
