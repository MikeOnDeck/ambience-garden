package svenhjol.charmony.api.tweaks;

import net.minecraft.client.gui.screens.Screen;

import java.util.function.Predicate;

/**
 * Allow a provider to check if a custom screen should use "dark mode".
 * This helps consumers that render a button to a screen to use the appropriate graphic.
 */
@SuppressWarnings("unused")
public interface DarkModeProvider {
    default Predicate<Screen> usesDarkMode() {
        return screen -> false;
    }
}
