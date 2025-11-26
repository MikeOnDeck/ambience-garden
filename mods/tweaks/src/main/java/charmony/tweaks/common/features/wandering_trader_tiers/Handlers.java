package charmony.tweaks.common.features.wandering_trader_tiers;

import charmony.core.base.Setup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;

public class Handlers extends Setup<WanderingTraderTiers> {
    public Handlers(WanderingTraderTiers feature) {
        super(feature);
    }

    public void addTierToTrader(WanderingTrader trader) {
        if (!feature().enabled()) return;
        if (!(trader.level() instanceof ServerLevel level)) return;

        var tiers = Registers.WANDERING_TRADER_TIERS;
        if (tiers.isEmpty()) return;

        var random = trader.getRandom();
        var listings = tiers.values().stream().toList().get(random.nextInt(tiers.size()));
        var offers = trader.getOffers();

        trader.addOffersFromItemListings(level, offers, listings.toArray(new VillagerTrades.ItemListing[0]), listings.size());
    }
}
