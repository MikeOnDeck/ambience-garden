package svenhjol.charmony.core.common.features.advancements;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@SuppressWarnings("unused")
@FeatureDefinition(side = Side.Common, description = """
    Triggers advancements for Charmony features.
    If this feature is disabled, no Charmony advancements will be awarded.""")
public final class Advancements extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    public Advancements(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public static void trigger(String id, Player player) {
        var advancements = Mod.getSidedFeature(Advancements.class);
        var res = Charmony.id(id);

        // Only trigger advancements if on the server-side, advancements is enabled and client-mode is disabled.
        var canTrigger = !player.level().isClientSide()
            && advancements.enabled();

        if (canTrigger) {
            advancements.registers.actionPerformed.trigger(res, (ServerPlayer)player);
        }
    }
}
