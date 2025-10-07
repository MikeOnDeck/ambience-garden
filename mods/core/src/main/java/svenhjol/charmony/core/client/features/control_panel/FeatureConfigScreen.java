package svenhjol.charmony.core.client.features.control_panel;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import svenhjol.charmony.core.base.Feature;

public class FeatureConfigScreen extends SettingsScreen {
    private static final Component DEFAULTS = Component.translatable("gui.charmony.settings.defaults");
    private static final Component SAVE = Component.translatable("gui.charmony.settings.save");
    private final Feature feature;
    private final FeaturesScreen parent;

    private FeatureConfigList list;

    public FeatureConfigScreen(Feature feature, FeaturesScreen parent) {
        super(Component.translatable("gui.charmony.settings.feature.title", feature.name()));
        this.feature = feature;
        this.parent = parent;
    }

    @Override
    protected void addContents() {
        list = layout().addToContents(new FeatureConfigList(feature, minecraft, width, this));
    }

    @Override
    protected void addFooter() {
        var linearLayout = layout().addToFooter(LinearLayout.horizontal().spacing(8));
        linearLayout.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).width(90).build());
        linearLayout.addChild(Button.builder(DEFAULTS, button -> defaults()).width(90).build());
        linearLayout.addChild(Button.builder(SAVE, button -> save()).width(90).build());
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        if (list != null) {
            list.updateSize(width, layout());
        }
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            parent.refreshState();
            minecraft.setScreen(parent);
        }
    }

    protected void save() {
        list.writeConfig();

        if (list.requiresRestart()) {
            parent.requiresRestart();
        }

        onClose();
    }

    protected void defaults() {
        list.defaults();
    }
}
