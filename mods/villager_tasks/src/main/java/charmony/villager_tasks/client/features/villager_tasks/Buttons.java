package charmony.villager_tasks.client.features.villager_tasks;

import charmony.core.Charmony;
import charmony.villager_tasks.common.features.villager_tasks.Resources;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;

public final class Buttons {
    public static final WidgetSprites ACCEPT_BUTTON = makeButtonWithDisabled("accept");
    public static final WidgetSprites DETAILS_BUTTON = makeButton("details");

    public static class ViewTasksButton extends Button {
        public static final int WIDTH = 120;
        public static final int HEIGHT = 20;
        static Component TEXT = Resources.VIEW_TASKS;

        public ViewTasksButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class AcceptButton extends ImageButton {
        public static int WIDTH = 20;
        public static int HEIGHT = 18;
        static WidgetSprites SPRITES = ACCEPT_BUTTON;
        static Component TEXT = Resources.ACCEPT;

        public AcceptButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, SPRITES, onPress);
            setTooltip(Tooltip.create(TEXT));
        }
    }

    public static class DetailsButton extends ImageButton {
        public static int WIDTH = 20;
        public static int HEIGHT = 18;
        static WidgetSprites SPRITES = DETAILS_BUTTON;
        static Component TEXT = Resources.DETAILS;

        public DetailsButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, SPRITES, onPress);
            setTooltip(Tooltip.create(TEXT));
        }
    }

    static WidgetSprites makeButton(String name) {
        return new WidgetSprites(
            Charmony.id("widget/buttons/" + name + "_button_normal"),
            Charmony.id("widget/buttons/" + name + "_button_highlighted"));
    }

    static WidgetSprites makeButtonWithDisabled(String name) {
        return new WidgetSprites(
            Charmony.id("widget/buttons/" + name + "_button_normal"),
            Charmony.id("widget/buttons/" + name + "_button_disabled"),
            Charmony.id("widget/buttons/" + name + "_button_highlighted"));
    }
}
