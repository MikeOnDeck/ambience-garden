package svenhjol.charmony.tweaks.client.features.shulker_box_menu_colors;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import svenhjol.charmony.api.Api;
import svenhjol.charmony.api.tweaks.DarkModeProvider;
import svenhjol.charmony.core.base.Setup;
import svenhjol.charmony.core.helpers.ColorHelper;

import java.util.function.Predicate;

public class DarkModeProviders extends Setup<ShulkerBoxMenuColors> implements DarkModeProvider {
    public DarkModeProviders(ShulkerBoxMenuColors feature) {
        super(feature);
        Api.registerProvider(this);
    }

    @Override
    public Predicate<Screen> usesDarkMode() {
        return screen -> {
            var color = feature().handlers.color;
            if (screen instanceof ShulkerBoxScreen && color != null) {
                return ColorHelper.DARK_MODE_COLORS.contains(color);
            }
            return false;
        };
    }
}
