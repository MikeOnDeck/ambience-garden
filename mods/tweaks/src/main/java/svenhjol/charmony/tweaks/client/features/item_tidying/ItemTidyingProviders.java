package svenhjol.charmony.tweaks.client.features.item_tidying;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.*;
import svenhjol.charmony.api.tweaks.ItemTidyingBlacklistProvider;
import svenhjol.charmony.api.tweaks.ItemTidyingButtonTweak;
import svenhjol.charmony.api.tweaks.ItemTidyingButtonTweakProvider;
import svenhjol.charmony.api.tweaks.ItemTidyingWhitelistProvider;
import svenhjol.charmony.core.Api;
import svenhjol.charmony.core.base.Setup;

import java.util.List;

public class ItemTidyingProviders extends Setup<ItemTidying> implements
    ItemTidyingWhitelistProvider,
    ItemTidyingBlacklistProvider,
    ItemTidyingButtonTweakProvider {

    public ItemTidyingProviders(ItemTidying feature) {
        super(feature);
        Api.registerProvider(this);
    }

    @Override
    public List<ItemTidyingButtonTweak> getItemTidyingButtonTweaks() {
        // Offset the base button X and Y coordinates on these screens.
        return List.of(
            new ItemTidyingButtonTweak() {
                @Override
                public Class<? extends Screen> getScreen() {
                    return MerchantScreen.class;
                }

                @Override
                public Pair<Integer, Integer> getPlayerXYOffset() {
                    return Pair.of(100, 0);
                }
            },
            new ItemTidyingButtonTweak() {
                @Override
                public Class<? extends Screen> getScreen() {
                    return InventoryScreen.class;
                }

                @Override
                public Pair<Integer, Integer> getPlayerXYOffset() {
                    return Pair.of(0, 76);
                }

                @Override
                public boolean hasRecipeButton() {
                    return true;
                }
            }
        );
    }

    @Override
    public List<Class<? extends Screen>> getItemTidyingBlacklistedScreens() {
        // Don't show the button on these screens.
        return List.of(
            CreativeModeInventoryScreen.class,
            BeaconScreen.class
        );
    }

    @Override
    public List<Class<? extends Screen>> getItemTidyingWhitelistedScreens() {
        // Add two sorting buttons to screens with a container.
        return List.of(
            ContainerScreen.class,
            HopperScreen.class,
            ShulkerBoxScreen.class,
            DispenserScreen.class
        );
    }
}
