package charmony.tweaks.common.mixins.wandering_trader_tiers;

import charmony.tweaks.common.features.wandering_trader_tiers.WanderingTraderTiers;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
