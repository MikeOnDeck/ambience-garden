package svenhjol.charmony.core.client.features.control_panel;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ModsScreen extends SettingsScreen {
    private final Screen parent;
    private ModsList list;

    protected ModsScreen(Screen parent) {
        super(Component.translatable("gui.charmony.settings.settings"));
        this.parent = parent;
    }

    @Override
    protected void addContents() {
        list = layout().addToContents(new ModsList(minecraft, this));
    }

    @Override
    protected void addFooter() {
        layout().addToFooter(Button.builder(CommonComponents.GUI_BACK, button -> back())
            .width(200).build());
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        if (list != null) {
            list.updateSize(width, layout());
        }
    }

    public void back() {
        if (minecraft == null) return;
        minecraft.setScreen(parent);
    }
}
