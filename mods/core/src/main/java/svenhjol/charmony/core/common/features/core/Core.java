package svenhjol.charmony.core.common.features.core;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.api.core.Side;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.core.base.SidedFeature;

@FeatureDefinition(side = Side.Common, canBeDisabled = false, description = """
    Core functionality and settings.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class Core extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;
    public final Networking networking;

    @Configurable(
        name = "Debug mode",
        description = "Enable debugging mode. Produces more logging output and adds some testing code."
    )
    private static boolean debugMode = false;

    @Configurable(
        name = "Mixin disable mode",
        description = """
            Enable mixin disable mode. All Charmony mod mixins will be disabled and behavior will be inconsistent.
            Use this only for testing if mods are crashing to determine if Charmony might be the cause."""
    )
    private static boolean mixinsDisableMode = false;

    @Configurable(
        name = "Custom anvil on-take behavior",
        description = """
            Enable custom on-take behavior for anvils. This changes the vanilla anvil behavior to avoid destroying entire stacks.
            There is a small chance that a mod depends on the default behavior, in which case you should disable this config option."""
    )
    private static boolean customAnvilOnTakeBehavior = true;

    public Core(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
        networking = new Networking(this);
    }

    public static Core feature() {
        return Mod.getSidedFeature(Core.class);
    }

    public boolean debugMode() {
        return debugMode;
    }

    public boolean mixinsDisabled() {
        return mixinsDisableMode;
    }

    public boolean customAnvilOnTakeBehavior() {
        return customAnvilOnTakeBehavior;
    }

    @Override
    public void run() {
        if (mixinsDisableMode) {
            log().warn("""
            
            
            -------------------------------------------------------------------
                         CHARMONY IS RUNNING IN MIXIN DISABLE MODE
            -------------------------------------------------------------------
            
            No Charmony-related mods will work as expected! This mode is used
            to determine if Charmony mods have mixin conflicts with another mod.
            
            Eliminate mods from your mod pack one by one, testing mixin disable
            mode on and off, to find out where a conflict is happening.
            Then come to Charmony's discord and talk to us.
            
            """);
        }
    }
}
