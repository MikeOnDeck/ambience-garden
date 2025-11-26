package charmony.api.tweaks;

import net.minecraft.world.entity.npc.villager.VillagerTrades;

import java.util.List;

@SuppressWarnings("unused")
public interface WandererTradeTier {
    String id();

    List<VillagerTrades.ItemListing> trades();
}
