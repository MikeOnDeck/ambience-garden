package charmony.core.client.features.tint_background;

import net.minecraft.client.gui.GuiGraphics;
import charmony.api.core.Color;

@SuppressWarnings("unused")
public interface TintedGuiGraphics {
    GuiGraphics tint(Color color);
}
