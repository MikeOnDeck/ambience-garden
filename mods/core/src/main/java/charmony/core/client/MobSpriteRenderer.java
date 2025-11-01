package charmony.core.client;

import charmony.core.Charmony;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class MobSpriteRenderer {
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    private final ResourceLocation mob;
    private ResourceLocation texture;

    public MobSpriteRenderer(ResourceLocation mob) {
        this.mob = mob;
    }

    public void render(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if (texture == null) {
            texture = getTexture();
        }
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, WIDTH, HEIGHT);

        if (mouseX >= x && mouseX < x + WIDTH && mouseY >= y && mouseY < y + HEIGHT) {
            guiGraphics.setTooltipForNextFrame(getName(), mouseX, mouseY);
        }
    }

    public ResourceLocation getTexture() {
        return Charmony.id("mob/" + mob.getNamespace() + "/" + mob.getPath());
    }

    public Component getName() {
        return Component.translatable("entity." + mob.getNamespace() + "." + mob.getPath());
    }
}
