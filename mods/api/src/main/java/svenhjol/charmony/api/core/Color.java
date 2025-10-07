package svenhjol.charmony.api.core;

import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public final class Color {
    private final int color;

    public Color(int color) {
        this.color = color;
    }

    public Color(DyeColor color) {
        this.color = color.getTextureDiffuseColor();
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
        return ARGB.color(Vec3.fromRGB24(color));
    }
}
