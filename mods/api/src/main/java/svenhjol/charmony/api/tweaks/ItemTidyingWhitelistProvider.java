package svenhjol.charmony.api.tweaks;

import net.minecraft.client.gui.screens.Screen;

import java.util.List;

/**
 * Specify a list of screens that will show the ItemTidying button.
 */
@SuppressWarnings("unused")
public interface ItemTidyingWhitelistProvider {
    List<Class<? extends Screen>> getItemTidyingWhitelistedScreens();
}
