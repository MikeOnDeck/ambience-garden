package svenhjol.charmony.api.tweaks;

import java.util.List;

/**
 * Specify a list of trade definitions that will be added to the wandering trader's trades.
 * @see WandererTrade
 */
@SuppressWarnings("unused")
public interface WandererTradeProvider {
    /**
     * Trade definitions to be added to the common (slot 1) trades.
     */
    default List<WandererTrade> getWandererTrades() {
        return List.of();
    }

    /**
     * Trade definitions to be added to the rare (slot 2) trades.
     */
    default List<WandererTrade> getRareWandererTrades() {
        return List.of();
    }

    /**
     * A trade tier will be added as a complete set.
     */
    default List<WandererTradeTier> getWandererTradeTiers() {
        return List.of();
    }
}
