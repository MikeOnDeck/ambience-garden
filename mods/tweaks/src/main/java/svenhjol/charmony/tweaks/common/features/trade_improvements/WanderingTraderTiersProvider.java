package svenhjol.charmony.tweaks.common.features.trade_improvements;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.npc.VillagerTrades;
import svenhjol.charmony.api.tweaks.WandererTradeProvider;
import svenhjol.charmony.api.tweaks.WandererTradeTier;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.common.GenericTrades;
import svenhjol.charmony.core.helpers.TagHelper;

import java.util.ArrayList;
import java.util.List;

public class WanderingTraderTiersProvider extends Setup<TradeImprovements> implements WandererTradeProvider {
    public WanderingTraderTiersProvider(TradeImprovements feature) {
        super(feature);
        Api.registerProvider(this);
    }

    @Override
    public List<WandererTradeTier> getWandererTradeTiers() {
        List<WandererTradeTier> tiers = new ArrayList<>();

        if (feature().coloredWools()) {
            tiers.add(new WandererTradeTier() {
                @Override
                public String id() {
                    return "colored_wools";
                }

                @Override
                public List<VillagerTrades.ItemListing> trades() {
                    return TagHelper.getValues(BuiltInRegistries.BLOCK, BlockTags.WOOL).stream()
                        .map(block -> (VillagerTrades.ItemListing) new GenericTrades.ItemsForEmeralds(block, 3, 2, 8, 0, 0, 2))
                        .toList();
                }
            });
        }

        if (feature().concretePowders()) {
            tiers.add(new WandererTradeTier() {
                @Override
                public String id() {
                    return "concrete_powders";
                }

                @Override
                public List<VillagerTrades.ItemListing> trades() {
                    return TagHelper.getValues(BuiltInRegistries.BLOCK, BlockTags.CONCRETE_POWDER).stream()
                        .map(block -> (VillagerTrades.ItemListing) new GenericTrades.ItemsForEmeralds(block, 3, 2, 8, 0, 0, 2))
                        .toList();
                }
            });
        }

        if (feature().coloredConcretes()) {
            tiers.add(new WandererTradeTier() {
                @Override
                public String id() {
                    return "colored_concretes";
                }

                @Override
                public List<VillagerTrades.ItemListing> trades() {
                    return TagHelper.getValues(BuiltInRegistries.BLOCK, ConventionalBlockTags.CONCRETES).stream()
                        .map(block -> (VillagerTrades.ItemListing) new GenericTrades.ItemsForEmeralds(block, 5, 2, 8, 0, 0, 2))
                        .toList();
                }
            });
        }

        if (feature().glazedTerracottas()) {
            tiers.add(new WandererTradeTier() {
                @Override
                public String id() {
                    return "glazed_terracottas";
                }

                @Override
                public List<VillagerTrades.ItemListing> trades() {
                    return TagHelper.getValues(BuiltInRegistries.BLOCK, ConventionalBlockTags.GLAZED_TERRACOTTAS).stream()
                        .map(block -> (VillagerTrades.ItemListing) new GenericTrades.ItemsForEmeralds(block, 9, 2, 8, 0, 0, 2))
                        .toList();
                }
            });
        }

        if (feature().coloredTerracottas()) {
            tiers.add(new WandererTradeTier() {
                @Override
                public String id() {
                    return "colored_terracottas";
                }

                @Override
                public List<VillagerTrades.ItemListing> trades() {
                    return TagHelper.getValues(BuiltInRegistries.BLOCK, Tags.COLORED_TERRACOTTAS).stream()
                        .map(block -> (VillagerTrades.ItemListing) new GenericTrades.ItemsForEmeralds(block, 3, 2, 8, 0, 0, 2))
                        .toList();
                }
            });
        }

        if (feature().coloredGlass()) {
            tiers.add(new WandererTradeTier() {
                @Override
                public String id() {
                    return "colored_glass";
                }

                @Override
                public List<VillagerTrades.ItemListing> trades() {
                    return TagHelper.getValues(BuiltInRegistries.BLOCK, Tags.COLORED_GLASS).stream()
                        .map(block -> (VillagerTrades.ItemListing) new GenericTrades.ItemsForEmeralds(block, 3, 2, 8, 0, 0, 2))
                        .toList();
                }
            });
        }

        if (feature().coloredBundles()) {
            tiers.add(new WandererTradeTier() {
                @Override
                public String id() {
                    return "colored_bundles";
                }

                @Override
                public List<VillagerTrades.ItemListing> trades() {
                    return TagHelper.getValues(BuiltInRegistries.ITEM, Tags.COLORED_BUNDLES).stream()
                        .map(block -> (VillagerTrades.ItemListing) new GenericTrades.ItemsForEmeralds(block, 28, 8, 1, 0, 0, 1))
                        .toList();
                }
            });
        }

        if (feature().coloredCandles()) {
            tiers.add(new WandererTradeTier() {
                @Override
                public String id() {
                    return "colored_candles";
                }

                @Override
                public List<VillagerTrades.ItemListing> trades() {
                    return TagHelper.getValues(BuiltInRegistries.ITEM, Tags.COLORED_CANDLES).stream()
                        .map(block -> (VillagerTrades.ItemListing) new GenericTrades.ItemsForEmeralds(block, 3, 2, 8, 0, 0, 2))
                        .toList();
                }
            });
        }

        return tiers;
    }
}
