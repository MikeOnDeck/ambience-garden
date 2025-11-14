package charmony.api.core;

import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public interface Sprite {
    Identifier sprite();

    int width();

    int height();
}
