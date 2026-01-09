package charmony.tweaks.client.features.shulker_boxes_show_contents;

import charmony.core.base.Environment;
import charmony.core.base.Setup;
import charmony.core.helpers.PlayerHelper;
import charmony.tweaks.common.features.shulker_boxes_show_contents.Networking;
import charmony.tweaks.common.features.shulker_boxes_show_contents.Networking.S2CShowContents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;

import javax.annotation.Nullable;
import java.util.*;

public class Handlers extends Setup<ShulkerBoxesShowContents> {
    private ShulkerBoxData lastShulkerBoxData = ShulkerBoxData.EMPTY;
    private long lastShulkerBoxCheck = 0;
    private boolean lookingAtShulkerBox = false;

    public Handlers(ShulkerBoxesShowContents feature) {
        super(feature);
    }

    public void hudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        feature().registers.hudRenderer.render(guiGraphics, deltaTracker);
    }

    public void clientTick(Minecraft minecraft) {
        var player = minecraft.player;
        clientTickLookingAt(player);
    }

    private void clientTickLookingAt(Player player) {
        if (player == null) return;

        if (Environment.usesCharmonyServer()) {
            feature().registers.hudRenderer.tick(player);

            var lookedAt = PlayerHelper.lookingAtBlock(player);
            lookingAtShulkerBox = checkLookingAtShulkerBox(player, lookedAt);
            if (lookingAtShulkerBox) {
                var gameTime = player.level().getGameTime();

                // First time looking at the shulker box or more than 10 ticks since last check
                if (lastShulkerBoxCheck == 0 || gameTime - lastShulkerBoxCheck >= 10) {
                    lastShulkerBoxCheck = gameTime;
                    Networking.C2SRequestContents.send(lookedAt);
                } else {
                    lastShulkerBoxCheck = 0;
                }
            }
        }
    }

    public boolean checkLookingAtShulkerBox(Player player, BlockPos pos) {
        if (!Environment.usesCharmonyServer()) return false;

        var minecraft = Minecraft.getInstance();
        if (minecraft.screen != null) return false;

        var level = player.level();
        return level.getBlockEntity(pos) instanceof ShulkerBoxBlockEntity;
    }

    public boolean isLookingAtShulkerBox() {
        return lookingAtShulkerBox;
    }

    public void handleShowContents(Player player, S2CShowContents payload) {
        var items = new ArrayList<>(payload.items());

        // Iterate through the items removing any that are empty.
        items.removeIf(ItemStack::isEmpty);

        Map<Item, Integer> itemCountMap = new HashMap<>();

        // Iterate through items, reducing the same item into a count.
        for (ItemStack itemStack : items) {
            var item = itemStack.getItem();
            var count = itemStack.getCount();

            itemCountMap.put(item, itemCountMap.getOrDefault(item, 0) + count);
        }

        setLastShulkerBoxData(itemCountMap, payload.name());
    }

    public void setLastShulkerBoxData(Map<Item, Integer> itemsAndCounts, String name) {
        lastShulkerBoxData = new ShulkerBoxData(itemsAndCounts, name);
    }

    public ShulkerBoxData getLastShulkerBoxData() {
        return lastShulkerBoxData;
    }

    /**
     * When hovering over a shulker box, remove all lines that show the contents.
     */
    public void removeLinesFromShulkerBox(ItemStack stack, List<Component> components, TooltipFlag tooltipFlag) {
        var shulkerBoxBlock = tryGetShulkerBoxBlock(stack);
        if (shulkerBoxBlock != null && !components.isEmpty()) {
            var color = shulkerBoxBlock.getColor();
            var title = components.getFirst();
            components.clear();

            // Show the shulker box name in color if applicable.
            if (color != null && title instanceof MutableComponent mutableTitle) {
                int textColor;
                if (color == DyeColor.BLACK) {
                    textColor = 0x545454;
                } else if (color == DyeColor.GRAY) {
                    textColor = 0x909090;
                } else {
                    textColor = color.getTextColor();
                }
                mutableTitle.withColor(textColor);
                components.addFirst(mutableTitle);
            } else {
                components.addFirst(title);
            }
        }
    }

    /**
     * When hovering over a shulker box, display a custom grid.
     */
    public Optional<TooltipComponent> addGridToShulkerBox(ItemStack stack) {
        var shulkerBoxBlock = tryGetShulkerBoxBlock(stack);
        var level = Minecraft.getInstance().level;
        if (level != null
            && shulkerBoxBlock != null
            && stack.has(DataComponents.CONTAINER)
        ) {
            var data = stack.get(DataComponents.CONTAINER);
            if (data != null) {
                var items = data.stream().toList();
                return Optional.of(new ShulkerBoxTooltip(items));
            }
        }

        return Optional.empty();
    }

    @Nullable
    public ShulkerBoxBlock tryGetShulkerBoxBlock(ItemStack stack) {
        if (Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock shulkerBoxBlock) {
            return shulkerBoxBlock;
        }
        return null;
    }

    public record ShulkerBoxData(
        Map<Item, Integer> itemsAndCounts,
        String name
    ) {
        public static final ShulkerBoxData EMPTY = new ShulkerBoxData(Map.of(), "");

        public boolean isEmpty() {
            return itemsAndCounts.isEmpty();
        }
    }
}
