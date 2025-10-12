package charmony.core.client.features.tint_background;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import charmony.api.core.Color;
import charmony.core.base.Setup;

import java.util.Map;
import java.util.WeakHashMap;

public class Handlers extends Setup<TintBackground> {
    private Color tintHolder = null;
    private final Map<GuiElementRenderState, Color> targets = new WeakHashMap<>();

    public Handlers(TintBackground feature) {
        super(feature);
    }

    public void setTintHolder(Color tint) {
        this.tintHolder = tint;
    }

    public boolean hasTint(GuiElementRenderState state) {
        return targets.containsKey(state);
    }

    public void trySetState(GuiElementRenderState target) {
        if (tintHolder != null) {
            targets.put(target, tintHolder);
            tintHolder = null;
        }
    }

    public Color getTint(GuiElementRenderState state) {
        return targets.get(state);
    }

    public VertexConsumer setTint(GuiElementRenderState state, VertexConsumer vertexConsumer, int defaultColor, boolean lastVertex) {
        var hasTint = hasTint(state);
        if (hasTint) {
            var tint = getTint(state);
            var color = tint.getArgbColor();
            vertexConsumer.setColor(color);

            if (lastVertex) {
                targets.remove(state);
            }
        } else {
            vertexConsumer.setColor(defaultColor);
        }

        return vertexConsumer;
    }
}
