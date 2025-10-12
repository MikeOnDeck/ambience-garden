package charmony.core.client.mixins.test_feature;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import org.spongepowered.asm.mixin.Mixin;
import charmony.core.client.features.test_feature.TestFeature;

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
        var result = TestFeature.feature().handlers.tryRenderBackground(guiGraphics, width, height, imageWidth, imageHeight);
        if (result) {
            return;
        }
        original.call(guiGraphics, ticks, mouseX, mouseY);
    }
}
