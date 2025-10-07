package svenhjol.charmony.core.common.features.conditional_recipes;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import svenhjol.charmony.core.base.Setup;

public class Registers extends Setup<ConditionalRecipes> {
    public Registers(ConditionalRecipes feature) {
        super(feature);
    }

    @Override
    public Runnable boot() {
        return () -> {
            // Conditional recipe manager.
            ResourceManagerHelper.get(PackType.SERVER_DATA)
                .registerReloadListener(ConditionalRecipeManager.ID, ConditionalRecipeManager::new);
        };
    }
}
