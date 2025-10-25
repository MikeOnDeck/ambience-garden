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
    public static final WidgetSprites ABANDON_BUTTON = makeButton("abandon");
    public static final WidgetSprites COMPLETE_BUTTON = makeButtonWithDisabled("complete");
    public static final WidgetSprites DETAILS_BUTTON = makeButton("details");

    public static class ActiveTasksButton extends Button {
        public static final int WIDTH = 120;
        public static final int HEIGHT = 20;
        static Component TEXT = Resources.ACTIVE_TASKS_BUTTON;

        public ActiveTasksButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class AvailableTasksButton extends Button {
        public static final int WIDTH = 120;
        public static final int HEIGHT = 20;
        static Component TEXT = Resources.AVAILABLE_TASKS_BUTTON;

        public AvailableTasksButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class AcceptButton extends ImageButton {
        public static int WIDTH = 20;
        public static int HEIGHT = 18;
        public static Component DEFAULT_TOOLTIP = Resources.ACCEPT;
        static WidgetSprites SPRITES = ACCEPT_BUTTON;

        public AcceptButton(int x, int y, OnPress onPress) {
            this(x, y, DEFAULT_TOOLTIP, onPress);
        }

        public AcceptButton(int x, int y, Component tooltip, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, SPRITES, onPress);
            setTooltip(Tooltip.create(tooltip));
        }
    }

    public static class AbandonButton extends ImageButton {
        public static int WIDTH = 20;
        public static int HEIGHT = 18;
        static WidgetSprites SPRITES = ABANDON_BUTTON;
        static Component TEXT = Resources.ABANDON;

        public AbandonButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, SPRITES, onPress);
            setTooltip(Tooltip.create(TEXT));
        }
    }

    public static class CompleteButton extends ImageButton {
        public static int WIDTH = 20;
        public static int HEIGHT = 18;
        public static Component DEFAULT_TOOLTIP = Resources.COMPLETE;
        static WidgetSprites SPRITES = COMPLETE_BUTTON;

        public CompleteButton(int x, int y, OnPress onPress) {
            this(x, y, DEFAULT_TOOLTIP, onPress);
        }

        public CompleteButton(int x, int y, Component tooltip, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, SPRITES, onPress);
            setTooltip(Tooltip.create(tooltip));
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
