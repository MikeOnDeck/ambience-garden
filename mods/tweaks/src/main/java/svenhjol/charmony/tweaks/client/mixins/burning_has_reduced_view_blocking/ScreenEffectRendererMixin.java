package svenhjol.charmony.tweaks.client.mixins.burning_has_reduced_view_blocking;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charmony.tweaks.client.features.burning_has_reduced_view_blocking.BurningHasReducedViewBlocking;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    @Inject(
        method = "renderFire",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private static void hookRenderFire(PoseStack poseStack, MultiBufferSource multiBufferSource, TextureAtlasSprite textureAtlasSprite, CallbackInfo ci) {
        BurningHasReducedViewBlocking.feature().handlers.changeFireSize(poseStack);
    }
}
