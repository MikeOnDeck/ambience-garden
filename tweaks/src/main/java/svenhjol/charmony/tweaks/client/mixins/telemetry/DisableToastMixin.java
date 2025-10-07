package svenhjol.charmony.tweaks.client.mixins.telemetry;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import svenhjol.charmony.tweaks.client.features.telemetry.Telemetry;

@Mixin(ClientPacketListener.class)
public class DisableToastMixin {
    @WrapWithCondition(
        method = "handleLogin",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/toasts/ToastManager;addToast(Lnet/minecraft/client/gui/components/toasts/Toast;)V"
        )
    )
    private boolean hookDisableChatNag(ToastManager instance, Toast toast) {
        return !Telemetry.disableChatMessageVerification();
    }
}
