package svenhjol.charmony.core.helpers;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import svenhjol.charmony.core.base.Log;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.world.entity.npc.VillagerTrades.TRADES;

public final class VillagerHelper {
    public static final Log LOGGER = new Log("VillagerHelper");

    /**
     * Helper method for the registry to add new trades to a villager profession.
     */
    public static Int2ObjectMap<List<VillagerTrades.ItemListing>> getMutableTrades(ResourceKey<VillagerProfession> profession) {
        var fixedTrades = TRADES.get(profession);
        Int2ObjectMap<List<VillagerTrades.ItemListing>> mutable = new Int2ObjectOpenHashMap<>();

        for (int i = 1; i <= 5; i++) {
            mutable.put(i, NonNullList.create());
        }

        fixedTrades.int2ObjectEntrySet().forEach(e
            -> Arrays.stream(e.getValue())
            .forEach(a -> mutable.get(e.getIntKey()).add(a)));

        return mutable;
    }

    /**
     * Helper method for the registry to reassemble trades after adding a custom trade to a profession.
     */
    public static void reassembleTrades(ResourceKey<VillagerProfession> profession, Int2ObjectMap<List<VillagerTrades.ItemListing>> trades) {
        Int2ObjectMap<VillagerTrades.ItemListing[]> mappedTrades = new Int2ObjectOpenHashMap<>();
        trades.int2ObjectEntrySet().forEach(e
            -> mappedTrades.put(e.getIntKey(), e.getValue().toArray(new VillagerTrades.ItemListing[0])));

        LOGGER.warnIfDebug("Reassembling trades for profession " + profession.location());
        TRADES.put(profession, mappedTrades);
    }
}
