package svenhjol.charmony.tweaks.common.features.trade_improvements;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = "Adds extra villager and wandering trader trades.")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class TradeImprovements extends SidedFeature {
    public final Registers registers;
    public final WanderingTraderTiersProvider wanderingTraderTiersProvider;

    @Configurable(name = "Enchanted books", description = "If true, librarians will buy any enchanted book in return for emeralds.")
    private static boolean enchantedBooks = true;

    @Configurable(name = "Repaired anvils", description = "If true, armorers, weaponsmiths and toolsmiths will buy chipped or damaged anvils along with iron ingots in return for repaired anvils.")
    private static boolean repairedAnvils = true;

    @Configurable(name = "Leather for rotten flesh", description = "If true, leatherworkers will sell leather in return for rotten flesh.")
    private static boolean leatherForRottenFlesh = true;

    @Configurable(name = "Beef for rotten flesh", description = "If true, butchers will sell beef in return for rotten flesh.")
    private static boolean beefForRottenFlesh = true;

    @Configurable(name = "Phantom membrane", description = "If true, clerics will sell phantom membrane in return for emeralds.")
    private static boolean phantomMembrane = true;

    @Configurable(name = "Colored wools", description = "If true, wandering traders can sell all colors of wool in return for emeralds.")
    private static boolean coloredWools = true;

    @Configurable(name = "Concrete powders", description = "If true, wandering traders can sell all colors of concrete powder in return for emeralds.")
    private static boolean concretePowders = true;

    @Configurable(name = "Colored concrete", description = "If true, wandering traders can sell all colors of concrete in return for emeralds.")
    private static boolean coloredConcretes = true;

    @Configurable(name = "Glazed terracotta", description = "If true, wandering traders can sell all variants of glazed terracotta in return for emeralds.")
    private static boolean glazedTerracottas = true;

    @Configurable(name = "Colored terracotta", description = "If true, wandering traders can sell all colors of terracotta in return for emeralds.")
    private static boolean coloredTerracottas = true;

    @Configurable(name = "Colored glass", description = "If true, wandering traders can sell all colors of stained glass in return for emeralds.")
    private static boolean coloredGlass = true;

    @Configurable(name = "Colored bundles", description = "If true, wandering traders can sell all colors of bundles in return for emeralds.")
    private static boolean coloredBundles = false;

    @Configurable(name = "Colored candles", description = "If true, wandering traders can sell all colors of candles in return for emeralds.")
    private static boolean coloredCandles = false;

    @Configurable(name = "Charmony mod items", description = "If true, wandering traders have a chance to sell various items from Charmony mods.")
    private static boolean charmonyModItems = true;

    public TradeImprovements(Mod mod) {
        super(mod);
        registers = new Registers(this);
        wanderingTraderTiersProvider = new WanderingTraderTiersProvider(this);
    }

    public boolean enchantedBooks() {
        return enchantedBooks;
    }

    public boolean repairedAnvils() {
        return repairedAnvils;
    }

    public boolean leatherForRottenFlesh() {
        return leatherForRottenFlesh;
    }

    public boolean beefForRottenFlesh() {
        return beefForRottenFlesh;
    }

    public boolean phantomMembrane() {
        return phantomMembrane;
    }

    public boolean coloredWools() {
        return coloredWools;
    }

    public boolean concretePowders() {
        return concretePowders;
    }

    public boolean coloredConcretes() {
        return coloredConcretes;
    }

    public boolean glazedTerracottas() {
        return glazedTerracottas;
    }

    public boolean coloredTerracottas() {
        return coloredTerracottas;
    }

    public boolean coloredGlass() {
        return coloredGlass;
    }

    public boolean coloredBundles() {
        return coloredBundles;
    }

    public boolean coloredCandles() {
        return coloredCandles;
    }

    public boolean charmonyModItems() {
        return charmonyModItems;
    }
}
