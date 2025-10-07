package svenhjol.charmony.core.client.features.control_panel;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import svenhjol.charmony.core.base.Mod;

public class FeaturesScreen extends SettingsScreen {
    public static final WidgetSprites CONFIG_BUTTON = makeButton("config");
    public static final WidgetSprites DISABLE_BUTTON = makeButton("disable");
    public static final WidgetSprites ENABLE_BUTTON = makeButton("enable");
    public static final WidgetSprites SETTINGS_BUTTON = makeButton("settings");

    private final Screen parent;
    private final Mod mod;

    private FeaturesList list;

    public FeaturesScreen(Mod mod, Screen parent) {
        super(Component.translatable("gui.charmony.settings.title", mod.name()));
        this.parent = parent;
        this.mod = mod;
    }

    @Override
    protected void addFooter() {
        layout().addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> done())
            .width(200).build());
    }

    @Override
    protected void addContents() {
        list = layout().addToContents(new FeaturesList(minecraft, mod, this));
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        if (list != null) {
            list.updateSize(width, layout());
        }
    }

    /**
     * Updates feature data after being modified by a child screen.
     * Typically this is called by the feature config screen to inform this screen
     * that one or more config items are no longer defaults.
     */
    public void refreshState() {
        list.refreshState();
    }

    public void done() {
        if (minecraft == null) return;

        var screen = requiresRestart ? new RestartScreen() : parent;
        minecraft.setScreen(screen);
    }
}
