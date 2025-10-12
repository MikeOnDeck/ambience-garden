package charmony.core.client;

import net.fabricmc.api.ClientModInitializer;
import charmony.api.core.Side;
import charmony.core.Charmony;
import charmony.core.base.Environment;
import charmony.core.client.features.control_panel.ControlPanel;
import charmony.core.client.features.core.Core;
import charmony.core.client.features.hud_item_scaling.HudItemScaling;
import charmony.core.client.features.test_feature.TestFeature;
import charmony.core.client.features.tint_background.TintBackground;
import charmony.core.client.features.wood.Wood;

public final class ClientInitializer implements ClientModInitializer {
    private static boolean initialized = false;

    @Override
    public void onInitializeClient() {
        init();
    }

    /**
     * We expose init() so that child mods can ensure that Charmony gets launched first.
     */
    public static void init() {
        if (initialized) return;

        // Setup and run the mod.
        var charmony = Charmony.instance();
        charmony.addSidedFeature(Core.class);
        charmony.addSidedFeature(ControlPanel.class);
        charmony.addSidedFeature(HudItemScaling.class);
        charmony.addSidedFeature(TintBackground.class);
        charmony.addSidedFeature(Wood.class);

        if (Environment.isDevEnvironment()) {
            charmony.addSidedFeature(TestFeature.class);
        }

        charmony.run(Side.Client);

        initialized = true;
    }
}
