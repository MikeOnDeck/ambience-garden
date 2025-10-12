package charmony.tweaks.common.features.shulker_box_transferring;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

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
