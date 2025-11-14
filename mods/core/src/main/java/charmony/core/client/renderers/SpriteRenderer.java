package charmony.core.client.renderers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class SpriteRenderer {
    protected final Identifier id;
    protected Identifier texture;

    public SpriteRenderer(Identifier id) {
        this.id = id;
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
        render(guiGraphics, x, y, -1, -1);
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (texture == null) {
            texture = getTexture();
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width(), height());

        if (mouseX >= x && mouseX < x + width() && mouseY >= y && mouseY < y + height()) {
            guiGraphics.setTooltipForNextFrame(getName(), mouseX, mouseY);
        }
    }

    public abstract Identifier getTexture();

    public abstract Component getName();

    public Component getDescription() {
        return Component.empty();
    }

    public Identifier getId() {
        return id;
    }

    public abstract int width();

    public abstract int height();
}
