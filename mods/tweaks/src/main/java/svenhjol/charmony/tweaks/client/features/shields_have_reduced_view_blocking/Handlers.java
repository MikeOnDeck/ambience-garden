package svenhjol.charmony.tweaks.client.features.shields_have_reduced_view_blocking;

import com.mojang.blaze3d.vertex.PoseStack;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<ShieldsHaveReducedViewBlocking> {
    public Handlers(ShieldsHaveReducedViewBlocking feature) {
        super(feature);
    }

    public void changeShieldSize(PoseStack poseStack) {
        var pos = feature().verticalPosition();
        var scale = feature().scaledSize();
        poseStack.translate(0d, pos, 0d);
        poseStack.scale(scale, scale, scale);
    }
}
