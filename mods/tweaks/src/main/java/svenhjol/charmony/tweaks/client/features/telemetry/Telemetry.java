package svenhjol.charmony.tweaks.client.features.telemetry;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.api.core.Side;

/**
 * This feature exposes its configuration via static methods.
 * The reason for this is that the mixins need direct access to these values on the thread they run.
 */
@FeatureDefinition(side = Side.Client, enabledByDefault = false, description = """
    Disables telemetry and hides some nag messages.
    Some data removal may be considered controversial so this feature is disabled by default.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class Telemetry extends SidedFeature {
    @Configurable(name = "Disable telemetry", description = """
        If true, prevents the client telemetry manager from ever sending any messages back to the mothership.
        Telemetry includes your game session, game version, operating system and launcher.""")
    private static boolean disableTelemetry = true;

    @Configurable(name = "Disable experimental screen dialog", description = """
        If true, disables the 'Experimental' warning dialog that appears when opening a world with experimental features enabled.""")
    private static boolean disableExperimental = true;

    @Configurable(name = "Disable chat message verification dialog", description = """
        If true, disables the 'Chat messages can't be verified' dialog message when the server does not enforce secure profile.
        This only applies if you set 'enforce-secure-profile' to true in server.properties""")
    private static boolean disableChatMessageVerification = true;

    @Configurable(name = "Disable development mode connections", description = """
        If true, disables realms and other API connections when running in the development environment.
        Setting this to true doesn't do anything if you are playing in a launcher.""")
    private static boolean disableDevEnvironmentConnections = true;

    public Telemetry(Mod mod) {
        super(mod);
    }

    public static boolean disableChatMessageVerification() {
        return disableChatMessageVerification;
    }

    public static boolean disableDevEnvironmentConnections() {
        return disableDevEnvironmentConnections
            && Environment.isDevEnvironment();
    }

    public static boolean disableTelemetry() {
        return disableTelemetry;
    }

    public static boolean disableExperimental() {
        return disableExperimental;
    }
}
