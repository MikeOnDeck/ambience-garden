package svenhjol.charmony.core.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import svenhjol.charmony.api.core.Sprite;
import svenhjol.charmony.core.Charmony;

import java.util.List;

@SuppressWarnings("unused")
public interface ItemContainerTooltip extends TooltipComponent {
    ResourceLocation BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(Charmony.ID, "item_container/background");
    int MARGIN_Y = 6;
    int SLOT_SIZE_X = 18;
    int SLOT_SIZE_Y = 18;

    int gridSizeX();

    int gridSizeY();

    List<ItemStack> getItems();

    default int backgroundWidth() {
        return this.gridSizeX() * SLOT_SIZE_X;
    }

    default int backgroundHeight() {
        return this.gridSizeY() * SLOT_SIZE_Y;
    }

    default int marginY() {
        return MARGIN_Y;
    }

    default ResourceLocation backgroundSprite() {
        return BACKGROUND_SPRITE;
    }

    default Sprite slotSprite() {
        return SlotSprite.Slot;
    }

    default void defaultRenderImage(GuiGraphics guiGraphics, Font font, Sprite texture, int x, int y) {
        var gx = this.gridSizeX();
        var gy = this.gridSizeY();
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, backgroundSprite(), x, y, backgroundWidth(), backgroundHeight());
        int index = 0;

        for (int yy = 0; yy < gy; ++yy) {
            for (int xx = 0; xx < gx; ++xx) {
                int slotX = x + xx * SLOT_SIZE_X + 1;
                int slotY = y + yy * SLOT_SIZE_Y + 1;
                renderSlotWithIndex(guiGraphics, font, texture, index++, slotX, slotY);
            }
        }
    }

    default void renderSlotWithIndex(GuiGraphics guiGraphics, Font font, Sprite texture, int index, int x, int y) {
        if (index >= getItems().size()) {
            renderSlot(guiGraphics, texture, x, y);
        } else {
            var itemStack = getItems().get(index);
            renderSlot(guiGraphics, texture, x, y);
            guiGraphics.renderItem(itemStack, x + 1, y + 1, index);
            guiGraphics.renderItemDecorations(font, itemStack, x + 1, y + 1);
        }
    }

    default void renderSlot(GuiGraphics guiGraphics, Sprite texture, int x, int y) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture.sprite(), x, y, texture.width(), texture.height());
    }
}
