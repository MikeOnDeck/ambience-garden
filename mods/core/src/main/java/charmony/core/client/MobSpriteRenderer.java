package charmony.core.client;

import charmony.core.Charmony;
import charmony.core.client.renderers.SpriteRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public class MobSpriteRenderer extends SpriteRenderer {
    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    public MobSpriteRenderer(Identifier id) {
        super(id);
    }

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return HEIGHT;
    }

    public Identifier getTexture() {
        return Charmony.id("mob/" + id.getNamespace() + "/" + id.getPath());
    }

    @Override
    public Component getName() {
        return Component.translatable("entity." + id.getNamespace() + "." + id.getPath());
    }
}
