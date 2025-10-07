package svenhjol.charmony.api.tweaks;

import java.util.List;

/**
 * Register screens that have their containers in a non-standard place such as the MerchantScreen.
 * This is used by features that place buttons near the container inventory.
 */
@SuppressWarnings("unused")
public interface ItemTidyingButtonTweakProvider {
    List<ItemTidyingButtonTweak> getItemTidyingButtonTweaks();
}
