package svenhjol.charmony.core.client.features.control_panel;

import svenhjol.charmony.api.core.Configurable;
import svenhjol.charmony.api.core.FeatureDefinition;
import svenhjol.charmony.core.base.SidedFeature;
import svenhjol.charmony.core.base.Mod;
import svenhjol.charmony.api.core.Side;

@FeatureDefinition(side = Side.Client, description = """
    In-game control panel for Charmony mods.""")
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public final class ControlPanel extends SidedFeature {
    public final Registers registers;
    public final Handlers handlers;

    @Configurable(
        name = "Show button on title screen",
        description = "If true, the control panel button will be shown on the title screen.",
        requireRestart = false
    )
    private static boolean showButtonOnTitleScreen = true;

    @Configurable(
        name = "Show button on in-game options",
        description = "If true, the control panel button will be shown on the options screen after joining a world.",
        requireRestart = false
    )
    private static boolean showButtonOnOptionsScreen = true;

    public ControlPanel(Mod mod) {
        super(mod);
        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    public boolean showButtonOnTitleScreen() {
        return showButtonOnTitleScreen;
    }

    public boolean showButtonOnOptionsScreen() {
        return showButtonOnOptionsScreen;
    }
}
