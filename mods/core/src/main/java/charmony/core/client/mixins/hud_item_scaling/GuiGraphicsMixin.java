package charmony.core.client.mixins.hud_item_scaling;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiItemRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import charmony.core.client.features.hud_item_scaling.HudItemScaling;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @WrapOperation(
        method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;submitItem(Lnet/minecraft/client/gui/render/state/GuiItemRenderState;)V"
        )
    )
    private void hookSubmitItem(GuiRenderState instance, GuiItemRenderState guiItemRenderState, Operation<Void> original) {
        HudItemScaling.feature().handlers.setGuiItemRenderState(guiItemRenderState);
        original.call(instance, guiItemRenderState);
    }
}
