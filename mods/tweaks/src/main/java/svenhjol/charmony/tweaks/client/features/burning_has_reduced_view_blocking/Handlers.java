package svenhjol.charmony.tweaks.client.features.burning_has_reduced_view_blocking;

import com.mojang.blaze3d.vertex.PoseStack;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<BurningHasReducedViewBlocking> {
    public Handlers(BurningHasReducedViewBlocking feature) {
        super(feature);
    }

    public void changeFireSize(PoseStack poseStack) {
        var pos = feature().verticalPosition();
        var scale = feature().scaledSize();
        poseStack.translate(0d, pos, 0d);
        poseStack.scale(scale, scale, scale);
    }
}
