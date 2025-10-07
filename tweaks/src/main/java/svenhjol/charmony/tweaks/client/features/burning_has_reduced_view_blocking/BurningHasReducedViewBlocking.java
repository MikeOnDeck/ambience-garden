package svenhjol.charmony.tweaks.client.features.burning_has_reduced_view_blocking;

import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    Reduces the size and position of the burning animation to prevent blocking the player's view.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class BurningHasReducedViewBlocking extends SidedFeature {
    public final Handlers handlers;

    @Configurable(
        name = "Vertical position",
        description = "0.0 is default, negative values move the fire down, positive values move the fire up.",
        requireRestart = false
    )
    private static double verticalPosition = -0.16d;

    @Configurable(
        name = "Scaled size",
        description = "1.0 is default, negative values shrink the fire, positive values enlarge the fire.",
        requireRestart = false
    )
    private static double scaledSize = 0.55d;

    public BurningHasReducedViewBlocking(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
    }

    public static BurningHasReducedViewBlocking feature() {
        return Mod.getSidedFeature(BurningHasReducedViewBlocking.class);
    }

    public double verticalPosition() {
        return Mth.clamp(verticalPosition, -1.0d, 1.0d);
    }

    public float scaledSize() {
        return Mth.clamp((float)scaledSize, 0.1f, 2.0f);
    }
}
