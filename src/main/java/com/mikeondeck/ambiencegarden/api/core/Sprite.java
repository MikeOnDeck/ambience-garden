package com.mikeondeck.ambiencegarden.api.core;

import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public interface Sprite {
    Identifier sprite();

    int width();

    int height();
}
