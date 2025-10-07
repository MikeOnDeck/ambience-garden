package svenhjol.charmony.core.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
public final class CoreButtons {
    public static class CloseButton extends Button {
        public static int WIDTH = 110;
        public static int HEIGHT = 20;
        static Component TEXT = CoreResources.CLOSE;

        public CloseButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class BackButton extends Button {
        public static int WIDTH = 100;
        public static int HEIGHT = 20;
        static Component TEXT = CoreResources.BACK;

        public BackButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class SaveButton extends Button {
        public static int WIDTH = 100;
        public static int HEIGHT = 20;
        static Component TEXT = CoreResources.SAVE;

        public SaveButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class CancelButton extends Button {
        public static int WIDTH = 100;
        public static int HEIGHT = 20;
        static Component TEXT = CoreResources.CANCEL;

        public CancelButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class DeleteButton extends Button {
        public static int WIDTH = 100;
        public static int HEIGHT = 20;
        static Component TEXT = CoreResources.DELETE;

        public DeleteButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class EditButton extends Button {
        public static int HEIGHT = 20;

        public EditButton(int x, int y, int width, OnPress onPress, Component text) {
            super(x, y, width, HEIGHT, text, onPress, DEFAULT_NARRATION);
        }
    }
}
