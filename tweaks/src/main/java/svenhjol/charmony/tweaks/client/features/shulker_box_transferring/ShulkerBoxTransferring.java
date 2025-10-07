package svenhjol.charmony.tweaks.client.features.shulker_box_transferring;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

import java.util.function.Supplier;

/**
 * This is the client-side processing of the ShulkerBoxTransferring common feature.
 */
@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public final class ShulkerBoxTransferring extends SidedFeature {
    public final Supplier<Common> common;

    public ShulkerBoxTransferring(Mod mod) {
        super(mod);
        common = Common::new;
    }

    public static ShulkerBoxTransferring feature() {
        return Mod.getSidedFeature(ShulkerBoxTransferring.class);
    }
}
