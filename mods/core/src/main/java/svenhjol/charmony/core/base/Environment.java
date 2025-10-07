package svenhjol.charmony.core.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import svenhjol.charmony.core.common.features.core.Core;

@SuppressWarnings("unused")
public final class Environment {
    public static final String DEBUG_MODE = "Debug mode";
    public static final String MIXIN_DISABLE_MODE = "Mixin disable mode";

    public static boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static boolean isModLoaded(String id) {
        FabricLoader instance = FabricLoader.getInstance();
        return instance.isModLoaded(id);
    }

    public static boolean isDebugMode() {
        return Mod.tryGetSidedFeature(Core.class)
            .map(Core::debugMode).orElse(false);
    }

    public static boolean isMixinDisableMode() {
        return Mod.tryGetSidedFeature(Core.class)
            .map(Core::mixinsDisabled).orElse(false);
    }

    /**
     * Checks if the client is using a charmony server.
     * Don't call this on the server!
     */
    @net.fabricmc.api.Environment(EnvType.CLIENT)
    public static boolean usesCharmonyServer() {
        return Mod.tryGetSidedFeature(svenhjol.charmony.core.client.features.core.Core.class)
            .map(c -> c.handlers.usesCharmonyServer()).orElse(false);
    }
}
