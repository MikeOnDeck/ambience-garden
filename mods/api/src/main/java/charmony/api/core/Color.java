package charmony.api.core;

import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;

@SuppressWarnings("unused")
public record Color(int color) {
    public Color(DyeColor color) {
        this(color.getTextureDiffuseColor());
    }

    public float getRed() {
        return (float) ARGB.red(color) / 255.0f;
    }

    public float getGreen() {
        return (float) ARGB.green(color) / 255.0f;
    }

    public float getBlue() {
        return (float) ARGB.blue(color) / 255.0f;
    }

    public float getAlpha() {
        return (float) ARGB.alpha(color) / 255.0f;
    }

    public int getIntColor() {
        return color;
    }

    public int getArgbColor() {
        return ARGB.opaque(color);
    }
}
