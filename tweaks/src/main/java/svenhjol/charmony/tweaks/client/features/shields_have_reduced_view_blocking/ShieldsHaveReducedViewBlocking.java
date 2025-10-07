package svenhjol.charmony.tweaks.client.features.shields_have_reduced_view_blocking;

import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    Reduces the size and position of the shield to prevent blocking the player's view.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class ShieldsHaveReducedViewBlocking extends SidedFeature {
    public final Handlers handlers;

    @Configurable(
        name = "Vertical position",
        description = "0.0 is default, negative values move the shield down, positive values move the shield up.",
        requireRestart = false
    )
    private static double verticalPosition = -0.16d;

    @Configurable(
        name = "Scaled size",
        description = "1.0 is default, negative values shrink the shield, positive values enlarge the shield.",
        requireRestart = false
    )
    private static double scaledSize = 0.9d;

    public ShieldsHaveReducedViewBlocking(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
    }

    public static ShieldsHaveReducedViewBlocking feature() {
        return Mod.getSidedFeature(ShieldsHaveReducedViewBlocking.class);
    }

    public double verticalPosition() {
        return Mth.clamp(verticalPosition, -1.0d, 1.0d);
    }

    public float scaledSize() {
        return Mth.clamp((float)scaledSize, 0.5f, 2.0f);
    }
}
