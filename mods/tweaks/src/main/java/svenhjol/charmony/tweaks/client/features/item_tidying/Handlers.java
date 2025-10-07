package svenhjol.charmony.tweaks.client.features.item_tidying;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import svenhjol.charmony.core.base.Setup;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Handlers extends Setup<ItemTidying> {
    private static final int LEFT = 159;
    private static final int TOP = 12;

    private final Map<ButtonType, ImageButton> sortingButtons = new HashMap<>();
    private List<Slot> playerSlots = new LinkedList<>();
    private List<Slot> containerSlots = new LinkedList<>();
    private @Nullable AbstractContainerScreen<?> containerScreen;
    private Pair<Integer, Integer> containerXY;
    private Pair<Integer, Integer> playerXY;

    public Handlers(ItemTidying feature) {
        super(feature);
    }

    public void handleKeypress(KeyEvent keyEvent) {
        if (containerScreen == null || !feature().registers.tidyInventoryKey.matches(keyEvent)) {
            return;
        }

        if (feature().keybindSortsMultipleInventories()) {
            sortContainer();
            sortPlayer();
            return;
        }

        if (containerScreen instanceof InventoryScreen) {
            sortPlayer();
        } else {
            sortContainer();
        }
    }

    public void setupScreen(Screen screen) {
        this.containerScreen = null;

        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (player == null) return;

        if (!(screen instanceof AbstractContainerScreen<?> acs)) return;

        this.containerScreen = acs;
        var clazz = acs.getClass();
        if (feature().registers.blacklisted.contains(clazz)) return;

        sortingButtons.clear();
        var menu = containerScreen.getMenu();
        var baseX = containerScreen.leftPos + LEFT;
        var baseY = containerScreen.topPos - TOP;

        processButtonCoordinates(acs, baseX, baseY);

        // Gather slot types.
        var slots = menu.slots;

        playerSlots = new LinkedList<>();
        containerSlots = new LinkedList<>();

        for (var slot : slots) {
            if (slot.container == player.getInventory()) {
                playerSlots.add(slot);
            } else {
                containerSlots.add(slot);
            }
        }

        var containerXY = containerXY();
        var playerXY = playerXY();

        if (!containerSlots.isEmpty() && feature().registers.whitelisted.contains(clazz)) {
            addSortingButton(screen, ButtonType.Container, containerXY.getFirst(), containerXY.getSecond() + containerSlots.getFirst().y,
                click -> sortContainer());
        }

        if (!playerSlots.isEmpty()) {
            addSortingButton(screen, ButtonType.Player, playerXY.getFirst(), playerXY.getSecond() + playerSlots.getFirst().y,
                click -> sortPlayer());
        }

        sortingButtons.values().forEach(containerScreen::addRenderableWidget);
    }

    public void sortPlayer() {
        if (!playerSlots.isEmpty() && containerScreen != null) {
            mergeAndSort(playerSlots, containerScreen, 9, -1);
        }
    }

    public void sortContainer() {
        if (!containerSlots.isEmpty() && containerScreen != null) {
            mergeAndSort(containerSlots, containerScreen, -1, -1);
        }
    }

    public void mergeAndSort(List<Slot> slots, AbstractContainerScreen<?> screen, int startIndex, int endIndex) {
        // Get a map of all the unique items and their stack size in each slot.
        Map<Item, CopyOnWriteArrayList<Pair<Slot, Integer>>> merge = new HashMap<>();

        if (screen instanceof InventoryScreen) {
            startIndex = 9;
            endIndex = 35;
        }

        for (Slot slot : slots) {
            if (!canUseSlotIndex(slot, startIndex, endIndex)) continue;

            var stack = slot.getItem();
            var item = stack.getItem();
            var count = stack.getCount();
            var max = stack.getMaxStackSize();

            if (stack.isEmpty()) continue;
            if (count == max) continue;

            merge.computeIfAbsent(item, l -> new CopyOnWriteArrayList<>());
            var holders = merge.get(item);

            if (count > 0) {
                holders.add(Pair.of(slot, count));
            }
        }

        // Merge each slot to the next one of the same item.
        try {
            for (Item item : merge.keySet()) {
                var max = item.getDefaultMaxStackSize();
                var holders = merge.get(item);
                if (holders.size() == 1) continue; // No other slots have this item, it's unique.

                for (int i = 0; i < holders.size() - 1; i++) {
                    var holder = holders.get(i);
                    var slot = holder.getFirst();
                    var count = holder.getSecond();

                    // We need to deplete this holder by adding it to the next ones.
                    var pointer = i + 1;
                    while (pointer < holders.size() && count > 0) {
                        var nextHolder = holders.get(pointer);
                        var nextSlot = nextHolder.getFirst();
                        var nextCount = nextHolder.getSecond();
                        if (nextCount == max) {
                            pointer++;
                            continue;
                        }

                        var min = nextCount;
                        for (var n = min; n < max; n++) {
                            screen.slotClicked(slot, slot.getContainerSlot(), 0, ClickType.PICKUP);
                            screen.slotClicked(nextSlot, nextSlot.getContainerSlot(), 1, ClickType.PICKUP);
                            screen.slotClicked(slot, slot.getContainerSlot(), 0, ClickType.PICKUP);
                            nextCount++;
                            if (--count == 0) {
                                break;
                            }
                        }

                        holders.set(i, Pair.of(slot, count));
                        holders.set(pointer, Pair.of(nextSlot, nextCount));
                    }
                }
            }
        } catch (Exception e) {
            feature().log().error(e.getMessage());
        }

        List<Slot> occupied = new LinkedList<>();

        for (Slot slot : slots) {
            if (!canUseSlotIndex(slot, startIndex, endIndex)) continue;

            var stack = slot.getItem();
            if (stack.isEmpty()) continue;
            occupied.add(slot);
        }

        var current = 0;
        var max = occupied.size();

        try {
            for (int i = 0; i < slots.size(); i++) {
                var slot = slots.get(i);
                if (!canUseSlotIndex(slot, startIndex, endIndex)) continue;

                var stack = slot.getItem();
                if (stack.isEmpty() && current < max) {
                    var occupiedSlot = occupied.get(current);
                    screen.slotClicked(occupiedSlot, occupiedSlot.index, 0, ClickType.PICKUP);
                    screen.slotClicked(slot, slot.index, 0, ClickType.PICKUP);
                }
                current++;
            }
        } catch (Exception e) {
            feature().log().error(e.getMessage());
        }
    }

    public void renderScreen(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Re-render when recipe is opened/closed.
        var clazz = screen.getClass();
        if (feature().registers.blacklisted.contains(clazz)) return;
        var tweak = feature().registers.tweaks.get(clazz);

        if (tweak != null && tweak.hasRecipeButton()) {
            // Recalculate the button X offsets.
            processButtonCoordinates(screen, screen.leftPos + LEFT, 0);

            var playerXY = playerXY();
            var containerXY = containerXY();

            if (sortingButtons.containsKey(ButtonType.Container)) {
                var imageButton = sortingButtons.get(ButtonType.Container);
                imageButton.setPosition(containerXY.getFirst(), imageButton.getY());
            }
            if (sortingButtons.containsKey(ButtonType.Player)) {
                var imageButton = sortingButtons.get(ButtonType.Player);
                imageButton.setPosition(playerXY.getFirst(), imageButton.getY());
            }
        }

        updateButtonSprites(screen);
    }

    public void addSortingButton(Screen screen, ButtonType type, int x, int y, Button.OnPress callback) {
        sortingButtons.put(type, new ImageButton(x, y, 10, 10, feature().registers.lightModeTidyButton, callback));
        updateButtonSprites(screen);
    }

    private void updateButtonSprites(Screen screen) {
        var useDarkMode = feature().registers.darkModeTests.stream().anyMatch(pred -> pred.test(screen));

        if (sortingButtons.containsKey(ButtonType.Container)) {
            var imageButton = sortingButtons.get(ButtonType.Container);
            imageButton.sprites = useDarkMode ? feature().registers.darkModeTidyButton : feature().registers.lightModeTidyButton;
        }
        if (sortingButtons.containsKey(ButtonType.Player)) {
            var imageButton = sortingButtons.get(ButtonType.Player);
            imageButton.sprites = useDarkMode ? feature().registers.darkModeTidyButton : feature().registers.lightModeTidyButton;
        }
    }

    private boolean canUseSlotIndex(Slot slot, int start, int end) {
        return (start == -1 || (start > -1 && slot.getContainerSlot() >= start))
            && (end == -1 || (end > -1 && slot.getContainerSlot() <= end));
    }

    private void processButtonCoordinates(AbstractContainerScreen<?> screen, int baseX, int baseY) {
        var clazz = screen.getClass();
        var tweak = feature().registers.tweaks.get(clazz);

        // Set new bases for modification using tweak provider.
        var containerX = baseX;
        var containerY = baseY;
        var playerX = baseX;
        var playerY = baseY;

        if (tweak != null) {
            var containerOffset = tweak.getContainerXYOffset();
            var playerOffset = tweak.getPlayerXYOffset();

            containerX = baseX + containerOffset.getFirst();
            containerY = baseY + containerOffset.getSecond();

            playerX = baseX + playerOffset.getFirst();
            playerY = baseY + playerOffset.getSecond();
        }

        this.containerXY = Pair.of(containerX, containerY);
        this.playerXY = Pair.of(playerX, playerY);
    }

    public Pair<Integer, Integer> containerXY() {
        return containerXY;
    }

    public Pair<Integer, Integer> playerXY() {
        return playerXY;
    }

    public enum ButtonType {
        Container,
        Player
    }
}
