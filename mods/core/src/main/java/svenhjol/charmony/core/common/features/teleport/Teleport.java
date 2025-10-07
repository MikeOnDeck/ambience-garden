package svenhjol.charmony.core.common.features.teleport;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, canBeDisabled = false, description = """
    Handles extended teleportation functions.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class Teleport extends SidedFeature {
    public final Handlers handlers;
    public final Registers registers;

    @Configurable(
        name = "Teleport after ticks",
        description = "How many ticks to wait before teleporting the player.",
        requireRestart = false
    )
    private static int teleportAfterTicks = 10;

    @Configurable(
        name = "Reposition after ticks",
        description = "How many ticks to wait before repositioning the player.",
        requireRestart = false
    )
    private static int repositionAfterTicks = 30;

    @Configurable(
        name = "Play sound after ticks",
        description = "How many ticks to wait before playing the teleport warmup sound.",
        requireRestart = false
    )
    private static int playSoundAfterTicks = 5;

    @Configurable(
        name = "Protection duration",
        description = "How many seconds of protection (resistance, regeneration) while teleporting the player.",
        requireRestart = false
    )
    private static int protectionDuration = 10;

    public Teleport(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        registers = new Registers(this);
    }

    public static Teleport feature() {
        return Mod.getSidedFeature(Teleport.class);
    }

    public int teleportAfterTicks() {
        return teleportAfterTicks;
    }
    public int repositionAfterTicks() {
        return repositionAfterTicks;
    }

    public int playSoundAfterTicks() {
        return playSoundAfterTicks;
    }

    public int protectionDurationTicks() {
        return protectionDuration * 20;
    }
}
