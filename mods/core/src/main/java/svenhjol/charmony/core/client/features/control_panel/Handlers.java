package svenhjol.charmony.core.client.features.control_panel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import svenhjol.charmony.core.base.Setup;

public class Handlers extends Setup<ControlPanel> {
    public static final Component SETTINGS_TOOLTIP = Component.translatable("gui.charmony.settings.settings");
    public Handlers(ControlPanel feature) {
        super(feature);
    }

    public void clientTick(Minecraft minecraft) {
        var parent = minecraft.screen;
        var button = feature().registers.settingsButton;
        button.setTooltip(Tooltip.create(SETTINGS_TOOLTIP));

        int x = 0;
        int y = 0;

        if (feature().showButtonOnTitleScreen() && parent instanceof TitleScreen) {
            var children = parent.children();
            var accessibilityButton = children.stream()
                .filter(b -> b instanceof SpriteIconButton s && s.sprite.enabled().equals(ResourceLocation.parse("icon/accessibility")))
                .map(b -> (SpriteIconButton)b)
                .findFirst()
                .orElse(null);

            if (accessibilityButton != null) {
                x = accessibilityButton.getX() + 24;
                y = accessibilityButton.getY();
            } else {
                x = (parent.width / 2) + 128;
                y = (parent.height / 4) + 132;
            }

            if (!children.contains(button)) {
                parent.addRenderableWidget(button);
            }
        }

        if (feature().showButtonOnOptionsScreen() && parent instanceof OptionsScreen) {
            if (minecraft.player == null) return;
            var children = parent.children();
            x = parent.width - 26;
            y = parent.height - 26;

            if (!children.contains(button)) {
                parent.addRenderableWidget(button);
            }
        }

        if (x > 0 && y > 0) {
            button.setPosition(x, y);
        }
    }

    public void openSettingsScreen() {
        var minecraft = Minecraft.getInstance();
        var current = minecraft.screen;
        minecraft.setScreen(new ModsScreen(current));
    }
}
