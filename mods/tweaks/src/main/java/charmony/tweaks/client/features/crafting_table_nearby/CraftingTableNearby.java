package charmony.tweaks.client.features.crafting_table_nearby;

import charmony.api.core.Configurable;
import charmony.api.core.FeatureDefinition;
import charmony.core.base.Mod;
import charmony.core.base.SidedFeature;
import charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    Open the 3x3 crafting grid using a hotkey when within 4 blocks of any crafting table.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class CraftingTableNearby extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    @Configurable(
        name = "Show crafting icon",
        description = "If true, displays a crafting table icon in the bottom right of the screen when in range of a crafting table.",
        requireRestart = false
    )
    private static boolean showCraftingIcon = true;

    public CraftingTableNearby(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public static CraftingTableNearby feature() {
        return Mod.getSidedFeature(CraftingTableNearby.class);
    }

    public boolean showCraftingIcon() {
        return showCraftingIcon;
    }
}
