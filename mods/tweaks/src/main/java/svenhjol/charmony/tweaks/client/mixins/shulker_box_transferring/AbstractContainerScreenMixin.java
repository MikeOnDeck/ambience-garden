package svenhjol.charmony.tweaks.client.mixins.shulker_box_transferring;

import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.tweaks.client.features.shulker_box_transferring.ShulkerBoxMouseActions;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Shadow protected abstract void addItemSlotMouseAction(ItemSlotMouseAction itemSlotMouseAction);

    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    private void hookInit(CallbackInfo ci) {
        if (!Environment.usesCharmonyServer()) return;
        this.addItemSlotMouseAction(new ShulkerBoxMouseActions(this.minecraft));
    }
}
