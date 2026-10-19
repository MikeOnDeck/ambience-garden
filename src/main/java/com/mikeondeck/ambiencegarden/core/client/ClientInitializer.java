package com.mikeondeck.ambiencegarden.core.client;

import net.fabricmc.api.ClientModInitializer;
import com.mikeondeck.ambiencegarden.api.core.Side;
import com.mikeondeck.ambiencegarden.core.Charmony;

public final class ClientInitializer implements ClientModInitializer {
    private static boolean initialized = false;

    @Override
    public void onInitializeClient() {
        init();
    }

    /**
     * We expose init() so that child mods can ensure that the core framework gets launched first.
     */
    public static void init() {
        if (initialized) return;

        // Setup and run the core framework (minimal, just for ambient sounds).
        var charmony = Charmony.instance();
        charmony.run(Side.Client);

        initialized = true;
    }
}
