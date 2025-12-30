package charmony.tweaks.client.mixins.telemetry_disable;

import charmony.tweaks.client.features.telemetry_disable.TelemetryDisable;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
        return !TelemetryDisable.disableChatMessageVerification();
    }
}
