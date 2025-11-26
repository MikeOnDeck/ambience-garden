package charmony.tweaks.common.features.wandering_trader_tiers;

import charmony.api.tweaks.WandererTradeProvider;
import charmony.core.Api;
import charmony.core.base.Setup;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.npc.villager.VillagerTrades;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registers extends Setup<WanderingTraderTiers> {
    public static final Map<String, List<VillagerTrades.ItemListing>> WANDERING_TRADER_TIERS = new HashMap<>();

    public Registers(WanderingTraderTiers feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            ServerLifecycleEvents.SERVER_STARTING.register(this::serverStarting);
        };
    }

    /**
     * MUST be run late to resolve tags. Add a whole tier of items to a wandering trader's trades.
     * Ideally this should be done using the WandererTradeProvider API.
     */
    public void addTier(String id, List<VillagerTrades.ItemListing> trades) {
        if (WANDERING_TRADER_TIERS.containsKey(id)) {
            feature().log().info("Wandering trader tier " + id + " has already been added, not adding again.");
            return;
        }

        WANDERING_TRADER_TIERS.put(id, trades);
    }

    private void serverStarting(MinecraftServer server) {
        Api.consume(WandererTradeProvider.class,
            provider -> {
                for (var tier : provider.getWandererTradeTiers()) {
                    addTier(tier.id(), tier.trades());
                }
            });
    }

}
