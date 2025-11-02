package charmony.core.client.renderers;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public abstract class SpriteRenderer {
    protected final ResourceLocation id;
    protected ResourceLocation texture;

    public SpriteRenderer(ResourceLocation id) {
        this.id = id;
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (texture == null) {
            texture = getTexture();
        }

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width(), height());

        var name = getName().orElse(null);
        if (name != null && mouseX >= x && mouseX < x + width() && mouseY >= y && mouseY < y + height()) {
            guiGraphics.setTooltipForNextFrame(name, mouseX, mouseY);
        }
    }

    public abstract ResourceLocation getTexture();

    public ResourceLocation getId() {
        return id;
    }

    public Optional<Component> getName() {
        return Optional.empty();
    }

    public abstract int width();

    public abstract int height();
}
