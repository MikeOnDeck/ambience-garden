package svenhjol.charmony.tweaks.client.features.item_frame_hiding;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

import java.util.function.Supplier;

@FeatureDefinition(side = Side.Client, canBeDisabledInConfig = false)
public final class ItemFrameHiding extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Supplier<Common> common;

    public ItemFrameHiding(Mod mod) {
        super(mod);
        common = Common::new;
        handlers = new Handlers(this);
        registers = new Registers(this);
    }
}
