package svenhjol.charmony.tweaks.client.mixins.shulker_box_menu_colors;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charmony.tweaks.client.features.shulker_box_menu_colors.ShulkerBoxMenuColors;

@Mixin(ShulkerBoxScreen.class)
public abstract class ShulkerBoxScreenMixin extends AbstractContainerScreen<ShulkerBoxMenu> {
    public ShulkerBoxScreenMixin(ShulkerBoxMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    /**
     * Try calling a custom render function that has a reference to the last clicked block color.
     */
    @WrapMethod(
        method = "renderBg"
    )
    private void hookRenderBg(GuiGraphics guiGraphics, float ticks, int mouseX, int mouseY, Operation<Void> original) {
        var result = ShulkerBoxMenuColors.feature().handlers.tryRenderBackground(guiGraphics, width, height, imageWidth, imageHeight);
        if (result) {
            return;
        }
        original.call(guiGraphics, ticks, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        var result = ShulkerBoxMenuColors.feature().handlers.tryRenderLabels(guiGraphics, font, title, titleLabelX, titleLabelY, playerInventoryTitle, inventoryLabelX, inventoryLabelY);
        if (result) {
            return;
        }

        // Default behaviour
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }
}
