package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.api.core.Color;
import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import charmony.villager_tasks.common.features.villager_tasks.Tasks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AvailableTasksScreen extends BaseScreen {
    private final Handlers handlers; // Reference for easy access to handler functions.

    private int midX;
    private int textColor;
    private int epicTextColor;
    private int rewardsTextColor;
    private int rowHeight;
    private boolean hasRenderedTaskButtons = false;

    @Nullable private Tasks availableTasks = null;
    @Nullable private Tasks activeTasks = null;

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
        this.activeTasks = handlers.getActiveTasks().orElse(null);

        midX = width / 2;
        rowHeight = 45;
        textColor = new Color(0xffffff).getArgbColor();
        epicTextColor = new Color(0xffff00).getArgbColor();
        rewardsTextColor = new Color(0x80e0ff).getArgbColor();
        hasRenderedTaskButtons = false;

        if (availableTasks != null && availableTasks.uuid().equals(merchant)) {
            this.availableTasks = availableTasks; // This allows the renderer to use the tasks.
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
        if (minecraft == null) return;
        if (availableTasks != null) {
            var top = 46;

            for (var i = 0; i < availableTasks.tasks().size(); i++) {
                var task = availableTasks.tasks().get(i);
                var titleColor = task.isEpic() ? epicTextColor : textColor;
                var distanceBetweenRewards = 3;
                var distanceBetweenReqs = 3;

                var bx0 = midX - 160;
                var bx1 = midX + 156;
                var by0 = top - 10 + (i * rowHeight);
                var by1 = by0 + 75;
                guiGraphics.fill(RenderPipelines.GUI, bx0, by0, bx1, by1, ARGB.color(150, 0, 0, 0));

                // Level scroll icon.
                var scroll = new LevelScroll(font, task.level, false);
                scroll.render(guiGraphics, midX - 152, top + (i * rowHeight) - 4, mouseX, mouseY);

                // Task title label
                var title = MutableComponent.create(task.getTitle().getContents());
                guiGraphics.drawString(font, title.withStyle(ChatFormatting.UNDERLINE), midX - 130,  top + (i * rowHeight), titleColor);

                // Accept and details buttons
                if (!hasRenderedTaskButtons) {
                    var detailsButton = new Buttons.DetailsButton(midX + 108, top - 4 + (i * rowHeight),
                        b -> {
                            minecraft.setScreen(null);
                        });

                    var acceptButton = new Buttons.AcceptButton(midX + 130, top - 4 + (i * rowHeight),
                        b -> {
                            handlers.acceptTask(task);
                            minecraft.setScreen(null);
                        });

                    if (activeTasks != null) {
                        // Set accept button disabled if the player already has this task.
                        activeTasks.getTaskByDefinition(task.getDefinitionId()).ifPresent(
                            activeTask -> acceptButton.active = false);
                    }

                    addRenderableWidget(detailsButton);
                    addRenderableWidget(acceptButton);
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

                // Rewards
                top += 21;
                var rewardsText = Resources.REWARDS_LABEL;
                var rewardsX = 0; // track how much horizontal space used for rewards
                guiGraphics.drawString(font, rewardsText, midX - 150,  top + (i * rowHeight) + 1, rewardsTextColor);

                // Reward XP
                if (task.rewards.experience > 0) {
                    var box = new RewardXpBox("" + task.rewards.experience, font);
                    var boxX = midX - 95 + rewardsX;
                    var boxY = top - 4 + (i * rowHeight);
                    box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
                    rewardsX += box.width() + distanceBetweenRewards;
                }

                // Reward items
                for (var j = 0; j < task.rewards.items.size(); j++) {
                    var item = task.rewards.items.get(j);
                    var box = new RewardItemBox(item.stack(), item.total(), font);
                    var boxX = midX - 95 + rewardsX;
                    var boxY = top - 4 + (i * rowHeight);
                    box.render(guiGraphics, boxX, boxY, mouseX, mouseY);
                    rewardsX += box.width() + distanceBetweenRewards;
                }
            }
        } else {
            TextComponentHelper.drawCenteredString(guiGraphics, font, Resources.NO_AVAILABLE_TASKS, midX, 40, textColor);
        }

        hasRenderedTaskButtons = true;
    }

    public static class ItemBox {
        protected final Minecraft minecraft;
        protected final Font font;
        protected final ItemStack stack;
        protected final String text;
        protected final int textColor;
        protected final int fillColor;

        protected boolean drawOutline = true;

        public ItemBox(Font font, ItemStack stack, String text, int textColor, int fillColor) {
            this.minecraft = Minecraft.getInstance();
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
            var ix = x + 2;
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
            if (mouseX > ix && mouseX < ix + width() - 1 && mouseY > iy && mouseY < iy + height() - 1) {
                renderTooltip(guiGraphics, mouseX, mouseY);
            }

            // Show the text
            guiGraphics.drawString(font, text, tx, ty, textColor);
        }

        public int width() {
            var textWidth = font.width(text);
            return 16 + textWidth + 7;
        }

        public int height() {
            return 18;
        }

        public int alpha() {
            return 50;
        }

        protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            var stackTooltip = new ArrayList<>(Screen.getTooltipFromItem(minecraft, stack));
            modifyStackTooltip(stackTooltip);
            var tooltips = stackTooltip.stream().map(Component::getVisualOrderText).toList();
            guiGraphics.setTooltipForNextFrame(font, tooltips, mouseX, mouseY);
        }

        protected void modifyStackTooltip(List<Component> tooltips) {
            // Override to modify the tooltip shown for the item stack.
        }
    }

    public static class CollectItemBox extends ItemBox {
        private final int count;

        public CollectItemBox(ItemStack stack, int count, Font font) {
            super(font, stack, "" + count, new Color(0xffffff).getArgbColor(), new Color(0xff8080).getArgbColor());
            this.count = count;
        }

        @Override
        protected void modifyStackTooltip(List<Component> tooltips) {
            var itemName = tooltips.getFirst().getString();
            tooltips.clear();
            tooltips.add(Resources.YOU_COLLECT);
            tooltips.add(Component.literal(itemName + ": " + count));
        }
    }

    public static class RewardItemBox extends ItemBox {
        private final int count;

        public RewardItemBox(ItemStack stack, int count, Font font) {
            super(font, stack, "" + count, new Color(0xffffff).getArgbColor(), new Color(0x80e0ff).getArgbColor());
            this.count = count;
        }

        @Override
        protected void modifyStackTooltip(List<Component> tooltips) {
            var itemName = tooltips.getFirst().getString();
            tooltips.clear();
            tooltips.addFirst(Resources.YOU_RECEIVE);
            tooltips.add(Component.literal(itemName + ": " + count));
        }
    }

    public static class RewardXpBox extends ItemBox {
        public RewardXpBox(String text, Font font) {
            super(font, new ItemStack(Items.EXPERIENCE_BOTTLE), text, new Color(0xffffff).getArgbColor(), new Color(0x80ffc0).getArgbColor());
        }

        @Override
        protected void modifyStackTooltip(List<Component> tooltips) {
            tooltips.clear();
            tooltips.add(Resources.YOU_RECEIVE);
            tooltips.add(Component.translatable("gui.charmony.villager_tasks.experience_levels", text));
        }
    }

    public static class LevelScroll {
        private final Font font;
        public final int level;
        public final boolean isActive;

        public LevelScroll(Font font, int level, boolean isActive) {
            this.font = font;
            this.level = level;
            this.isActive = isActive;
        }

        public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
            ResourceLocation texture;

            if (isActive) {
                texture = Resources.ACTIVE_LEVELS.getOrDefault(level, Resources.ACTIVE_LEVELS.get(1));
            } else {
                texture = Resources.AVAILABLE_LEVELS.getOrDefault(level, Resources.AVAILABLE_LEVELS.get(1));
            }

            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width(), height());
            if (mouseX > x && mouseX < x + width() && mouseY > y && mouseY < y + height()) {
                renderTooltip(guiGraphics, mouseX, mouseY);
            }
        }

        public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
            var components = List.of(
                Component.translatable("gui.charmony.villager_tasks.level_info", level).getVisualOrderText(),
                Component.translatable("gui.charmony.villager_tasks.level_info.level" + level).getVisualOrderText()
            );
            guiGraphics.setTooltipForNextFrame(font, components, mouseX, mouseY);
        }

        public int width() {
            return 16;
        }

        public int height() {
            return 16;
        }
    }
}
