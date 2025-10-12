package charmony.core.client.mixins.tint_background;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import charmony.api.core.Color;
import charmony.core.client.features.tint_background.TintBackground;
import charmony.core.client.features.tint_background.TintedGuiGraphics;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements TintedGuiGraphics {
    @Override
    public GuiGraphics tint(Color color) {
        TintBackground.feature().handlers.setTintHolder(color);
        return (GuiGraphics)(Object)this;
    }
}

