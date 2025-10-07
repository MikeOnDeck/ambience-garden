package svenhjol.charmony.tweaks.client.features.shulker_boxes_show_contents_when_hovering;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import svenhjol.charmony.api.core.Sprite;
import svenhjol.charmony.core.client.ItemContainerTooltip;
import svenhjol.charmony.core.client.SlotSprite;
import svenhjol.charmony.tweaks.client.features.shulker_box_transferring.ShulkerBoxTransferring;

import java.util.List;

public record ShulkerBoxTooltip(List<ItemStack> items) implements ClientTooltipComponent, ItemContainerTooltip {
    @Override
    public int gridSizeX() {
        return 9;
    }

    @Override
    public int gridSizeY() {
        return 3;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getHeight(Font font) {
        return gridSizeY() * SLOT_SIZE_Y + MARGIN_Y;
    }

    @Override
    public int getWidth(Font font) {
        return gridSizeX() * SLOT_SIZE_X + 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, int k, int l, GuiGraphics guiGraphics) {
        defaultRenderImage(guiGraphics, font, SlotSprite.Slot, x, y);
    }

    @Override
    public void renderSlotWithIndex(GuiGraphics guiGraphics, Font font, Sprite texture, int index, int x, int y) {
        if (index == 0 && ShulkerBoxTransferring.feature().enabled()) {
            texture = SlotSprite.SelectedSlot;
        }
        ItemContainerTooltip.super.renderSlotWithIndex(guiGraphics, font, texture, index, x, y);
    }
}