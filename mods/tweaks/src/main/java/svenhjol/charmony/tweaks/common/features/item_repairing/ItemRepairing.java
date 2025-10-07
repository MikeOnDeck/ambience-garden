package svenhjol.charmony.tweaks.common.features.item_repairing;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    More ways to repair items using different materials.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class ItemRepairing extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;

    @Configurable(name = "Repair tridents using prismarine", description = "Use prismarine shards to repair trident damage.")
    private static boolean repairTridents = true;

    @Configurable(name = "Repair elytra using leather", description = "Use leather to repair elytra when insomnia is disabled.")
    private static boolean repairElytra = true;

    @Configurable(name = "Repair netherite using scrap", description = "Use netherite scrap to repair netherite item damage.")
    private static boolean repairNetheriteItems = true;

    public ItemRepairing(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
    }

    public boolean repairElytra() {
        return repairElytra;
    }

    public boolean repairNetheriteItems() {
        return repairNetheriteItems;
    }

    public boolean repairTridents() {
        return repairTridents;
    }
}
