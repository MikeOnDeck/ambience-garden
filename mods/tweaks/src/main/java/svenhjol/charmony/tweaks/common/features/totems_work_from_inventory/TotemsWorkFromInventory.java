package svenhjol.charmony.tweaks.common.features.totems_work_from_inventory;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Common, description = """
    A Totem of Undying will work from anywhere in the player's inventory as well as held in the main or offhand.""")
public final class TotemsWorkFromInventory extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Advancements advancements;
    public final TotemInventoryProviders totemInventoryProviders;

    public TotemsWorkFromInventory(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
        advancements = new Advancements(this);
        totemInventoryProviders = new TotemInventoryProviders(this);
    }

    public static TotemsWorkFromInventory feature() {
        return Mod.getSidedFeature(TotemsWorkFromInventory.class);
    }
}
