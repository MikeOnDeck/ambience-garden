package svenhjol.charmony.tweaks.common.features.shulker_box_transferring;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = "Drag and drop items into a shulkerbox from within your inventory.")
public final class ShulkerBoxTransferring extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;
    public final Networking networking;

    public ShulkerBoxTransferring(Mod mod) {
        super(mod);

        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
        networking = new Networking(this);
    }

    public static ShulkerBoxTransferring feature() {
        return Mod.getSidedFeature(ShulkerBoxTransferring.class);
    }
}
