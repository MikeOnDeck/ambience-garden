package svenhjol.charmony.tweaks.common.mixins.wandering_trader_tiers;

import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.tweaks.common.features.wandering_trader_tiers.WanderingTraderTiers;

@Mixin(WanderingTrader.class)
public class WanderingTraderMixin {
    @Inject(
        method = "updateTrades",
        at = @At("TAIL")
    )
    private void hookUpdateTrades(CallbackInfo ci) {
        WanderingTraderTiers.feature().handlers.addTierToTrader((WanderingTrader)(Object)this);
    }
}
