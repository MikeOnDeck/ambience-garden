package svenhjol.charmony.tweaks.client.features.item_tidying;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    Button to automatically stack and arrange items in inventory and containers.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class ItemTidying extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final ItemTidyingProviders itemTidyingProviders;

    @Configurable(
        name = "Keybind sorts multiple inventories",
        description = """
            If true, the sorting key will sort player and non-player inventories in the same keypress.
            If false, the sorting key only sorts non-player inventories unless viewing the player's inventory screen.""",
        requireRestart = false
    )
    private static boolean keybindSortsMultipleInventories = false;

    public ItemTidying(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        registers = new Registers(this);
        itemTidyingProviders = new ItemTidyingProviders(this);
    }

    public static ItemTidying feature() {
        return Mod.getSidedFeature(ItemTidying.class);
    }

    public boolean keybindSortsMultipleInventories() {
        return keybindSortsMultipleInventories;
    }
}
