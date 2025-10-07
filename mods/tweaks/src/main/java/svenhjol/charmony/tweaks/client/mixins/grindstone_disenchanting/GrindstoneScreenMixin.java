package svenhjol.charmony.tweaks.client.mixins.grindstone_disenchanting;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.GrindstoneScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.core.base.Environment;
import svenhjol.charmony.tweaks.client.features.grindstone_disenchanting.GrindstoneDisenchanting;

@SuppressWarnings("UnreachableCode")
@Mixin(GrindstoneScreen.class)
public abstract class GrindstoneScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    @Unique
    private final ThreadLocal<Player> player = new ThreadLocal<>();

    public GrindstoneScreenMixin(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        player.remove();
        player.set(inventory.player);
    }

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void hookInit(GrindstoneMenu menu, Inventory inventory, Component title, CallbackInfo ci) {
        player.set(inventory.player);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (Environment.usesCharmonyServer()) {
            var screen = (GrindstoneScreen) (Object) this;
            GrindstoneDisenchanting.feature().handlers.updateGrindstoneCost(screen, player.get(), guiGraphics, font, imageWidth);
        }
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }
}
