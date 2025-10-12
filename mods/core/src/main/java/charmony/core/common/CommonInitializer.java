package charmony.core.common;

import net.fabricmc.api.ModInitializer;
import charmony.api.core.Side;
import charmony.core.Charmony;
import charmony.core.base.Environment;
import charmony.core.common.features.advancements.Advancements;
import charmony.core.common.features.conditional_recipes.ConditionalRecipes;
import charmony.core.common.features.core.Core;
import charmony.core.common.features.teleport.Teleport;
import charmony.core.common.features.test_feature.TestFeature;
import charmony.core.common.features.wood.Wood;

public class CommonInitializer implements ModInitializer {
    private static boolean initialized = false;

    @Override
    public void onInitialize() {
        init();
    }

    /**
     * We expose init() so that child mods can ensure that Charmony gets launched first.
     */
    public static void init() {
        if (initialized) return;

        var charmony = Charmony.instance();
        charmony.addSidedFeature(Core.class);
        charmony.addSidedFeature(Advancements.class);
        charmony.addSidedFeature(ConditionalRecipes.class);
        charmony.addSidedFeature(Teleport.class);
        charmony.addSidedFeature(Wood.class);

        if (Environment.isDevEnvironment()) {
            charmony.addSidedFeature(TestFeature.class);
        }

        charmony.run(Side.Common);

        initialized = true;
    }
}
