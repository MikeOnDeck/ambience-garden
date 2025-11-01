package charmony.villager_tasks.client.features.villager_tasks.screens;

import charmony.api.core.Color;
import charmony.core.helpers.TextComponentHelper;
import charmony.villager_tasks.client.features.villager_tasks.Buttons;
import charmony.villager_tasks.client.features.villager_tasks.Handlers;
import charmony.villager_tasks.client.features.villager_tasks.VillagerTasks;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public abstract class BaseScreen extends Screen {
    protected final Handlers handlers; // Reference for easy access to handler functions.

    protected int midX;
    protected int midY;

    protected Color titleColor;
    protected Color textColor;
    protected Color fillColor;
    protected Color epicFillColor;

    public BaseScreen(Component component) {
        super(component);
        this.handlers = VillagerTasks.feature().handlers;
    }

    @Override
    protected void init() {
        super.init();
        if (minecraft == null) return;

        midX = width / 2;
        midY = height / 2;

        titleColor = new Color(0x454545);
        textColor = new Color(0x202020);
        fillColor = new Color(0x909090);
        epicFillColor = new Color(0xa0a060);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        renderBg(guiGraphics);
        renderTitle(guiGraphics);
        renderContent(guiGraphics, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, tickDelta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void refresh() {
        // Hook
    }

    protected void renderBg(GuiGraphics guiGraphics) {
        var width = 298;
        var height = 177;
        var midX = (this.width - width) / 2;
        var midY = (this.height - height) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Resources.TASKS_BACKGROUND, midX, midY, 0.0f, 0.0f, width, height, 512, 256);
    }

    protected void renderTitle(GuiGraphics guiGraphics) {
        TextComponentHelper.drawCenteredString(guiGraphics, font, getTitle(), midX, midY - 80, titleColor.getArgbColor());
    }

    protected void addCloseButton() {
        if (minecraft == null) return;

        var closeButton = new Buttons.CloseButton(midX - (Buttons.CloseButton.WIDTH / 2), midY + 94,
            b -> minecraft.setScreen(null));

        addRenderableWidget(closeButton);
    }

    protected abstract void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY);
}
