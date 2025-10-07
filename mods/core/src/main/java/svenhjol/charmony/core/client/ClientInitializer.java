package svenhjol.charmony.core.client;

import net.fabricmc.api.ClientModInitializer;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.Charmony;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.core.client.features.control_panel.ControlPanel;
import svenhjol.charmony.core.client.features.core.Core;
import svenhjol.charmony.core.client.features.hud_item_scaling.HudItemScaling;
import svenhjol.charmony.core.client.features.test_feature.TestFeature;
import svenhjol.charmony.core.client.features.tint_background.TintBackground;
import svenhjol.charmony.core.client.features.wood.Wood;

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
