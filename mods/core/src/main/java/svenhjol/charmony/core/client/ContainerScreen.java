package svenhjol.charmony.core.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@SuppressWarnings("unused")
public abstract class ContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected final ResourceLocation texture;

    public ContainerScreen(T menu, Inventory inv, Component title, ResourceLocation texture) {
        super(menu, inv, title);
        this.texture = texture;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        if (minecraft != null) {
            var x = (width - imageWidth) / 2;
            var y = (height - imageHeight) / 2;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
        }
    }
}
