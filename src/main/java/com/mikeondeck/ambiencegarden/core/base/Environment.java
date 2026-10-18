package com.mikeondeck.ambiencegarden.core.base;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Minimal Environment helper for checking runtime environment.
 */
public class Environment {
    public static boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static boolean isDebugMode() {
        // Always false for production build
        return false;
    }
}
