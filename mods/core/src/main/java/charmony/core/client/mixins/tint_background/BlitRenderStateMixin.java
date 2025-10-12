package charmony.core.client.mixins.tint_background;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import charmony.core.client.features.tint_background.TintBackground;

@Mixin(BlitRenderState.class)
public class BlitRenderStateMixin {
    @WrapOperation(
        method = "buildVertices",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            ordinal = 0
        )
    )
    private VertexConsumer hook0(VertexConsumer instance, int color, Operation<VertexConsumer> original) {
        if (TintBackground.feature().enabled()) {
            return alterColor(instance, color, false);
        }
        return original.call(instance, color);
    }

    @WrapOperation(
        method = "buildVertices",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            ordinal = 1
        )
    )
    private VertexConsumer hook1(VertexConsumer instance, int color, Operation<VertexConsumer> original) {
        if (TintBackground.feature().enabled()) {
            return alterColor(instance, color, false);
        }
        return original.call(instance, color);
    }

    @WrapOperation(
        method = "buildVertices",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            ordinal = 2
        )
    )
    private VertexConsumer hook2(VertexConsumer instance, int color, Operation<VertexConsumer> original) {
        if (TintBackground.feature().enabled()) {
            return alterColor(instance, color, false);
        }
        return original.call(instance, color);
    }

    @WrapOperation(
        method = "buildVertices",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
            ordinal = 3
        )
    )
    private VertexConsumer hook3(VertexConsumer instance, int color, Operation<VertexConsumer> original) {
        if (TintBackground.feature().enabled()) {
            return alterColor(instance, color, true);
        }
        return original.call(instance, color);
    }

    @Unique
    private VertexConsumer alterColor(VertexConsumer instance, int defaultColor, boolean lastVertex) {
        return TintBackground.feature().handlers.setTint((GuiElementRenderState)this, instance, defaultColor, lastVertex);
    }
}
