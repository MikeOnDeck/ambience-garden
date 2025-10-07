package svenhjol.charmony.tweaks.common.features.torchflowers_emit_light;

import net.minecraft.util.Mth;
import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = "Torchflowers emit ambient light.")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class TorchflowersEmitLight extends SidedFeature {
    public final Handlers handlers;

    @Configurable(
        name = "Light level",
        description = "Amount of light emitted by a Torchflower. Valid values between 0-15."
    )
    private static int lightLevel = 8;

    public TorchflowersEmitLight(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
    }

    public static TorchflowersEmitLight feature() {
        return Mod.getSidedFeature(TorchflowersEmitLight.class);
    }

    public int lightLevel() {
        return Mth.clamp(lightLevel, 0, 15);
    }
}
