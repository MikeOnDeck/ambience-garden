package charmony.villager_tasks.client.features.villager_tasks;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class Buttons {
    public static class ViewTasksButton extends Button {
        public static final int WIDTH = 120;
        public static final int HEIGHT = 20;
        static Component TEXT = Component.literal("View tasks"); // TODO: add lang string

        public ViewTasksButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class AcceptTaskButton extends Button {
        public static final int WIDTH = 60;
        public static final int HEIGHT = 20;
        static Component TEXT = Component.literal("Accept"); // TODO: add lang string

        public AcceptTaskButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }

    public static class TaskDetailsButton extends Button {
        public static final int WIDTH = 60;
        public static final int HEIGHT = 20;
        static Component TEXT = Component.literal("Details"); // TODO: add lang string

        public TaskDetailsButton(int x, int y, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION);
        }
    }
}
