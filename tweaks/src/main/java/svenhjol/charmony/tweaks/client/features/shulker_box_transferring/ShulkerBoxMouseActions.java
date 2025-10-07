package svenhjol.charmony.tweaks.client.features.shulker_box_transferring;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.tweaks.common.features.shulker_box_transferring.Networking;

@SuppressWarnings("unused")
public class ShulkerBoxMouseActions implements ItemSlotMouseAction {
    private final ScrollWheelHandler scrollWheelHandler;

    public ShulkerBoxMouseActions(Minecraft minecraft) {
        this.scrollWheelHandler = new ScrollWheelHandler();
    }

    @Override
    public boolean matches(Slot slot) {
        return slot.getItem().is(ItemTags.SHULKER_BOXES);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, int slot, ItemStack stack) {
        if (!Environment.usesCharmonyServer()) {
            return false;
        }

        var vector = scrollWheelHandler.onMouseScroll(mouseX, mouseY);
        var direction = vector.y == 0 ? -vector.x : vector.y;
        var common = ShulkerBoxTransferring.feature().common.get();

        if (common.handlers.reorderShulkerBoxItems(stack, direction)) {
            // Sync with server.
            Networking.C2SReorderShulkerBoxItems.send(slot, direction);
        }

        return stack.is(ItemTags.SHULKER_BOXES);
    }

    @Override
    public void onStopHovering(Slot slot) {
        // no op
    }

    @Override
    public void onSlotClicked(Slot slot, ClickType clickType) {
        // no op
    }
}
