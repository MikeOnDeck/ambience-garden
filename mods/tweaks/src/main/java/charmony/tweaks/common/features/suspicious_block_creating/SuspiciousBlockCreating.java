package charmony.tweaks.common.features.suspicious_block_creating;

import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = "Use a piston to push an item into sand or gravel, making it suspicious.")
public final class SuspiciousBlockCreating extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;

    public SuspiciousBlockCreating(Mod mod) {
        super(mod);

        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
    }

    public static SuspiciousBlockCreating feature() {
        return Mod.getSidedFeature(SuspiciousBlockCreating.class);
    }
}
