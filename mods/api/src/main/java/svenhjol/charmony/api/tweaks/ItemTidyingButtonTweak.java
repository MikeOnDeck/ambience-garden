package svenhjol.charmony.api.tweaks;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screens.Screen;

/**
 * Definition for a screen and its container offset.
 */
@SuppressWarnings("unused")
public interface ItemTidyingButtonTweak {
    Class<? extends Screen> getScreen();

    /**
     * Provide hardcoded container inventory X and Y base coordinates for the given screen.
     */
    default Pair<Integer, Integer> getContainerXYOffset() {
        return Pair.of(0, 0);
    }

    /**
     * Provide hardcoded player inventory X and Y base coordinates for the given screen.
     */
    default Pair<Integer, Integer> getPlayerXYOffset() {
        return Pair.of(0, 0);
    }

    /**
     * True if the screen should respond to recipe button toggle.
     */
    default boolean hasRecipeButton() {
        return false;
    }
}
