package svenhjol.charmony.tweaks.client.features.shulker_box_menu_colors;

import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    Shulker box menus are tinted to the color of the shulker box.""")
public final class ShulkerBoxMenuColors extends SidedFeature {
    public final Handlers handlers;
    public final Registers registers;
    public final DarkModeProviders darkModeProviders;

    public ShulkerBoxMenuColors(Mod mod) {
        super(mod);
        handlers = new Handlers(this);
        registers = new Registers(this);
        darkModeProviders = new DarkModeProviders(this);
    }

    public static ShulkerBoxMenuColors feature() {
        return Mod.getSidedFeature(ShulkerBoxMenuColors.class);
    }
}
