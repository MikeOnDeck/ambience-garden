package svenhjol.charmony.api.core;

import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public interface Sprite {
    ResourceLocation sprite();

    int width();

    int height();
}
